package net.konn.temperies.temperature;

import net.minecraft.core.BlockPos;

public record RoomCache(
        BlockPos position,
        boolean enclosed,
        int nextCheckTick
) {
}
