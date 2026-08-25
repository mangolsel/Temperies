package net.konn.temperies.event;

import net.konn.temperies.attachment.Temperies_Attachments;
import net.konn.temperies.item.custom.TemperatureInstrumentItem;
import net.konn.temperies.network.TemperatureSyncPayload;
import net.konn.temperies.temperature.*;
import net.konn.temperies.util.Temperies_Tags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public final class TemperatureHandler {
    private static final int COLD_DAMAGE_INTERVAL_TICKS = 40;
    private static final float COLD_DAMAGE = 1.0F;
    private static final int UPDATE_INTERVAL_TICKS = 10;
    private static final int THERMOSCOPE_BREAK_THRESHOLD = 90;
    private static final int MAX_HEAT = TemperatureConstants.MAX_EXPOSURE;
    private static final int WATER_COOLING_STEP = 3;
    private static final int MAX_COLD = TemperatureConstants.MAX_EXPOSURE;
    private static final int EXPOSURE_STEP = 1;
    private static final int RECOVERY_STEP = 3;
    private static final int HEAT_DAMAGE_INTERVAL_TICKS = 40;
    private static final float HEAT_DAMAGE = 1.0F;
    private static final int ROOM_HORIZONTAL_RADIUS = 10;
    private static final int ROOM_DOWN_RADIUS = 4;
    private static final int ROOM_UP_RADIUS = 6;
    private static final int MAX_VISITED_ROOM_BLOCKS = 4096;

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.tickCount % UPDATE_INTERVAL_TICKS == 0) {
            updateTemperature(player);

            tryBreakFragileTemperatureInstruments(player);
        }

        maintainCustomFreezing(player);
        applyColdDamage(player);
    }

    private void tryBreakFragileTemperatureInstruments(
            ServerPlayer player
    ) {
        if (!player.isAlive()
                || player.isCreative()
                || player.isSpectator()) {
            return;
        }

        int rawHeat = player.getData(
                Temperies_Attachments.HEAT_EXPOSURE
        );

        int rawCold = player.getData(
                Temperies_Attachments.COLD_EXPOSURE
        );

        ExposureState effectiveExposure =
                getEffectiveExposure(
                        rawHeat,
                        rawCold
                );

        int extremeExposure = Math.max(
                effectiveExposure.heat(),
                effectiveExposure.cold()
        );

        if (extremeExposure < THERMOSCOPE_BREAK_THRESHOLD) {
            return;
        }

        boolean brokeSomething = false;

        for (int slot = 0;
             slot < player.getInventory().getContainerSize();
             slot++) {

            ItemStack stack =
                    player.getInventory().getItem(slot);

            if (stack.isEmpty()) {
                continue;
            }

            if (stack.getItem()
                    instanceof TemperatureInstrumentItem instrument
                    && instrument.isFragile()) {

                stack.shrink(stack.getCount());

                brokeSomething = true;
            }
        }

        if (brokeSomething) {
            player.getInventory().setChanged();

            player.serverLevel().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.GLASS_BREAK,
                    SoundSource.PLAYERS,
                    1.0F,
                    0.9F
                            + player.getRandom().nextFloat()
                            * 0.2F
            );
        }
    }

    private void applyColdDamage(ServerPlayer player) {
        if (!player.isAlive()
                || player.isCreative()
                || player.isSpectator()) {
            return;
        }

        if (player.tickCount
                % COLD_DAMAGE_INTERVAL_TICKS != 0) {
            return;
        }

        int rawHeat = player.getData(
                Temperies_Attachments.HEAT_EXPOSURE
        );

        int rawCold = player.getData(
                Temperies_Attachments.COLD_EXPOSURE
        );

        ExposureState effectiveExposure =
                getEffectiveExposure(
                        rawHeat,
                        rawCold
                );

        if (effectiveExposure.cold()
                < TemperatureConstants.DAMAGE_THRESHOLD) {
            return;
        }

        if (!player.canFreeze()) {
            return;
        }

        if (player.getInBlockState().is(Blocks.POWDER_SNOW)) {
            return;
        }

        player.hurt(
                player.damageSources().freeze(),
                COLD_DAMAGE
        );
    }
    private ExposureState applyHeatSource(
            int heat,
            int cold,
            HeatSource source
    ) {
        int remainingChange =
                source.changePerUpdate();

        if (cold > 0) {
            int removedCold = Math.min(
                    cold,
                    remainingChange
            );

            cold -= removedCold;
            remainingChange -= removedCold;
        }


        if (remainingChange > 0
                && heat < source.targetExposure()) {
            heat = Math.min(
                    source.targetExposure(),
                    heat + remainingChange
            );
        }

        return new ExposureState(
                heat,
                cold
        );
    }

    private void updateTemperature(ServerPlayer player) {
        ServerLevel level = player.serverLevel();

        int oldHeat = player.getData(
                Temperies_Attachments.HEAT_EXPOSURE
        );

        int oldCold = player.getData(
                Temperies_Attachments.COLD_EXPOSURE
        );

        int heat = oldHeat;
        int cold = oldCold;

        if (!player.isAlive()
                || player.isCreative()
                || player.isSpectator()) {

            heat = 0;
            cold = 0;

            player.setTicksFrozen(0);

            finishUpdate(
                    player,
                    oldHeat,
                    heat,
                    oldCold,
                    cold
            );

            return;
        }

        int maximumColdExposure =
                getMaximumColdExposure(player);

        Holder<Biome> biome = level.getBiome(
                player.blockPosition()
        );

        boolean neutralBiome =
                biome.is(Temperies_Tags.Biomes.NEUTRAL)
                        || biome.is(BiomeTags.IS_OCEAN);

        if (neutralBiome) {
            heat = recoverExposure(heat);
            cold = recoverExposure(cold);

        } else if (biome.is(Temperies_Tags.Biomes.COLD)) {
            boolean enclosed = isInsideEnclosedRoom(
                    level,
                    player.blockPosition()
            );

            if (enclosed) {
                heat = recoverExposure(heat);
                cold = recoverExposure(cold);

            } else if (heat > 0) {
                heat = recoverExposure(heat);

            } else if (cold < maximumColdExposure) {
                cold = Math.min(
                        maximumColdExposure,
                        cold + EXPOSURE_STEP
                );

            } else if (cold > maximumColdExposure) {
                cold = Math.max(
                        maximumColdExposure,
                        cold - RECOVERY_STEP
                );
            }

        } else if (biome.is(Temperies_Tags.Biomes.HOT)) {
            boolean exposedToSun =
                    level.isDay()
                            && !player.isInWaterOrRain()
                            && !hasShadeAbove(level, player);

            if (!exposedToSun) {
                heat = recoverExposure(heat);
                cold = recoverExposure(cold);

            } else if (cold > 0) {

                cold = recoverExposure(cold);

            } else {
                heat = Math.min(
                        MAX_HEAT,
                        heat + EXPOSURE_STEP
                );
            }

        } else {
            heat = recoverExposure(heat);
            cold = recoverExposure(cold);
        }

        HeatSource nearbyHeatSource =
                findStrongestHeatSource(
                        level,
                        player.blockPosition()
                );

        if (nearbyHeatSource != null) {
            ExposureState warmedState = applyHeatSource(
                    heat,
                    cold,
                    nearbyHeatSource
            );

            heat = warmedState.heat();
            cold = warmedState.cold();
        }



        if (player.isInWater()) {
            heat = Math.max(
                    0,
                    heat - WATER_COOLING_STEP
            );
        }

        finishUpdate(
                player,
                oldHeat,
                heat,
                oldCold,
                cold
        );
    }

    private HeatSource findStrongestHeatSource(
            ServerLevel level,
            BlockPos center
    ) {
        int maximumRadius =
                HeatSourceRegistry.getMaximumRadius();

        if (maximumRadius <= 0) {
            return null;
        }

        if (!level.isAreaLoaded(center, maximumRadius)) {
            return null;
        }

        HeatSource strongest = null;
        int strongestDistanceSquared = Integer.MAX_VALUE;

        BlockPos minimum = center.offset(
                -maximumRadius,
                -maximumRadius,
                -maximumRadius
        );

        BlockPos maximum = center.offset(
                maximumRadius,
                maximumRadius,
                maximumRadius
        );

        for (BlockPos sourcePos :
                BlockPos.betweenClosed(minimum, maximum)) {

            BlockState state =
                    level.getBlockState(sourcePos);

            HeatSource candidate =
                    HeatSourceRegistry.getActive(state);

            if (candidate == null) {
                continue;
            }

            int dx = sourcePos.getX() - center.getX();
            int dy = sourcePos.getY() - center.getY();
            int dz = sourcePos.getZ() - center.getZ();

            int distanceSquared =
                    dx * dx + dy * dy + dz * dz;

            int candidateRadiusSquared =
                    candidate.radius() * candidate.radius();

            if (distanceSquared > candidateRadiusSquared) {
                continue;
            }

            boolean strongerTarget =
                    strongest == null
                            || candidate.targetExposure()
                            > strongest.targetExposure();

            boolean fasterAtSameTarget =
                    strongest != null
                            && candidate.targetExposure()
                            == strongest.targetExposure()
                            && candidate.changePerUpdate()
                            > strongest.changePerUpdate();

            boolean closerAtSamePower =
                    strongest != null
                            && candidate.targetExposure()
                            == strongest.targetExposure()
                            && candidate.changePerUpdate()
                            == strongest.changePerUpdate()
                            && distanceSquared
                            < strongestDistanceSquared;

            if (strongerTarget
                    || fasterAtSameTarget
                    || closerAtSamePower) {
                strongest = candidate;
                strongestDistanceSquared = distanceSquared;
            }
        }

        return strongest;
    }
    private void maintainCustomFreezing(ServerPlayer player) {
        if (player.getInBlockState().is(Blocks.POWDER_SNOW)) {
            return;
        }

        int rawHeat = player.getData(
                Temperies_Attachments.HEAT_EXPOSURE
        );

        int rawCold = player.getData(
                Temperies_Attachments.COLD_EXPOSURE
        );

        ExposureState effectiveExposure =
                getEffectiveExposure(
                        rawHeat,
                        rawCold
                );

        int effectiveCold =
                effectiveExposure.cold();

        int requiredTicks =
                player.getTicksRequiredToFreeze();

        float freezeProgress = Mth.clamp(
                effectiveCold
                        / (float) TemperatureConstants.DAMAGE_THRESHOLD,
                0.0F,
                1.0F
        );

        int targetFrozenTicks = Math.round(
                freezeProgress * requiredTicks
        );

        if (player.getTicksFrozen() < targetFrozenTicks) {
            player.setTicksFrozen(targetFrozenTicks);
        }
    }

    private int recoverExposure(int exposure) {
        return Math.max(
                0,
                exposure - RECOVERY_STEP
        );
    }

    private void finishUpdate(
            ServerPlayer player,
            int oldHeat,
            int newHeat,
            int oldCold,
            int newCold
    ) {

        if (newHeat != oldHeat) {
            player.setData(
                    Temperies_Attachments.HEAT_EXPOSURE,
                    newHeat
            );
        }

        if (newCold != oldCold) {
            player.setData(
                    Temperies_Attachments.COLD_EXPOSURE,
                    newCold
            );
        }

        ExposureState effectiveExposure =
                getEffectiveExposure(
                        newHeat,
                        newCold
                );

        PacketDistributor.sendToPlayer(
                player,
                new TemperatureSyncPayload(
                        effectiveExposure.heat(),
                        effectiveExposure.cold()
                )
        );

        if (effectiveExposure.heat()
                >= TemperatureConstants.DAMAGE_THRESHOLD
                && player.tickCount
                % HEAT_DAMAGE_INTERVAL_TICKS == 0) {

            player.hurt(
                    player.damageSources().generic(),
                    HEAT_DAMAGE
            );
        }
    }

    private boolean hasShadeAbove(
            ServerLevel level,
            ServerPlayer player
    ) {
        int x = player.getBlockX();
        int z = player.getBlockZ();

        int startY = Mth.floor(player.getEyeY()) + 1;

        int topY = level.getHeight(
                Heightmap.Types.MOTION_BLOCKING,
                x,
                z
        );

        BlockPos.MutableBlockPos mutablePos =
                new BlockPos.MutableBlockPos();

        for (int y = startY; y < topY; y++) {
            mutablePos.set(x, y, z);

            BlockState state = level.getBlockState(mutablePos);

            if (state.is(
                    Temperies_Tags.Blocks.DOES_NOT_PROVIDE_SHADE))
            {
                continue;
            }

            if (!state.getCollisionShape(level, mutablePos).isEmpty())
            {
                return true;
            }
        }

        return false;
    }

    private boolean isInsideEnclosedRoom(
            ServerLevel level,
            BlockPos playerPos
    ) {
        int minX = playerPos.getX() - ROOM_HORIZONTAL_RADIUS;
        int maxX = playerPos.getX() + ROOM_HORIZONTAL_RADIUS;

        int minZ = playerPos.getZ() - ROOM_HORIZONTAL_RADIUS;
        int maxZ = playerPos.getZ() + ROOM_HORIZONTAL_RADIUS;

        int minY = Math.max(
                level.getMinBuildHeight(),
                playerPos.getY() - ROOM_DOWN_RADIUS
        );

        int maxY = Math.min(
                level.getMaxBuildHeight() - 1,
                playerPos.getY() + ROOM_UP_RADIUS
        );

        Queue<BlockPos> open = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        open.add(playerPos.immutable());

        while (!open.isEmpty()) {
            BlockPos current = open.remove();

            if (!visited.add(current)) {
                continue;
            }

            if (visited.size() > MAX_VISITED_ROOM_BLOCKS) {
                return false;
            }

            if (!level.isAreaLoaded(current, 1)) {
                return false;
            }

            if (isAtRoomSearchBoundary(
                    current,
                    minX,
                    maxX,
                    minY,
                    maxY,
                    minZ,
                    maxZ
            )) {
                return false;
            }

            if (level.canSeeSky(current)) {
                return false;
            }

            for (Direction direction : Direction.values()) {
                BlockPos next = current.relative(direction);

                if (visited.contains(next)) {
                    continue;
                }

                if (isAirPassage(level, next)) {
                    open.add(next.immutable());
                }
            }
        }

        return true;
    }

    private boolean isAtRoomSearchBoundary(
            BlockPos pos,
            int minX,
            int maxX,
            int minY,
            int maxY,
            int minZ,
            int maxZ
    ) {
        return pos.getX() <= minX
                || pos.getX() >= maxX
                || pos.getY() <= minY
                || pos.getY() >= maxY
                || pos.getZ() <= minZ
                || pos.getZ() >= maxZ;
    }


    private boolean isAirPassage(
            ServerLevel level,
            BlockPos pos
    ) {
        BlockState state = level.getBlockState(pos);

        if (state.isAir()) {
            return true;
        }

        if (!state.getFluidState().isEmpty()) {
            return true;
        }

        if (state.hasProperty(BlockStateProperties.OPEN)) {
            return state.getValue(BlockStateProperties.OPEN);
        }

        if (state.is(
                Temperies_Tags.Blocks.DOES_NOT_SEAL_ROOM
        )) {
            return true;
        }

        return state.getCollisionShape(level, pos).isEmpty();
    }
    private record ExposureState(
            int heat,
            int cold
    ) {
    }
    private ExposureState getEffectiveExposure(
            int rawHeat,
            int rawCold
    ) {
        int signedTemperature = Mth.clamp(
                rawHeat - rawCold,
                -TemperatureConstants.MAX_EXPOSURE,
                TemperatureConstants.MAX_EXPOSURE
        );

        return new ExposureState(
                Math.max(0, signedTemperature),
                Math.max(0, -signedTemperature)
        );
    }
    private int getMaximumColdExposure(
            ServerPlayer player
    ) {
        int insulation = Mth.clamp(
                TemperatureEquipment.getTotalModifier(player),
                0,
                MAX_COLD
        );

        return MAX_COLD - insulation;
    }
}
