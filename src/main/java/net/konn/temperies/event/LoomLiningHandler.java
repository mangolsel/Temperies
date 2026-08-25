package net.konn.temperies.event;

import net.konn.temperies.temperature.ThermalLining;
import net.konn.temperies.util.Temperies_Tags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class LoomLiningHandler {
    @SubscribeEvent
    public void onRightClickLoom(
            PlayerInteractEvent.RightClickBlock event
    ) {
        Player player =
                event.getEntity();

        /*
         * Срабатываем только один раз —
         * от основной руки.
         */
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        /*
         * Не ломаем обычный интерфейс ткацкого станка.
         *
         * Обычный ПКМ -> vanilla loom.
         * Shift + ПКМ -> система Temperies.
         */
        if (!player.isShiftKeyDown()) {
            return;
        }

        if (!event.getLevel()
                .getBlockState(event.getPos())
                .is(Blocks.LOOM)) {

            return;
        }

        ItemStack armorStack =
                player.getMainHandItem();

        ItemStack materialStack =
                player.getOffhandItem();

        /*
         * Поддерживаем любую обычную броню,
         * включая vanilla и большинство modded armor.
         */
        if (!(armorStack.getItem()
                instanceof ArmorItem armor)) {

            return;
        }

        boolean warmingMaterial =
                materialStack.is(ItemTags.WOOL);

        boolean coolingMaterial =
                materialStack.is(
                        Temperies_Tags.Items.COOLING_MATERIALS
                );

        if (!warmingMaterial
                && !coolingMaterial) {

            return;
        }

        /*
         * Останавливаем стандартное открытие Loom.
         */
        event.setCanceled(true);
        event.setCancellationResult(
                InteractionResult.SUCCESS
        );

        /*
         * Само изменение предмета выполняем
         * только на сервере.
         */
        if (event.getLevel().isClientSide) {
            return;
        }

        int newModifier;

        if (warmingMaterial) {

            newModifier =
                    ThermalLining.getStrength(armor);

        } else {

            newModifier =
                    -ThermalLining.getStrength(armor);
        }

        int existingModifier =
                ThermalLining.getModifier(
                        armorStack
                );

        /*
         * Если такая подкладка уже установлена,
         * материал повторно не тратим.
         */
        if (existingModifier == newModifier) {

            player.displayClientMessage(
                    Component.translatable(
                            "message.temperies.lining.already_applied"
                    ),
                    true
            );

            return;
        }

        if (warmingMaterial) {

            ThermalLining.applyWarming(
                    armorStack,
                    armor
            );

        } else {

            ThermalLining.applyCooling(
                    armorStack,
                    armor
            );
        }

        /*
         * Тратим один материал.
         */
        if (!player.getAbilities().instabuild) {
            materialStack.shrink(1);
        }

        event.getLevel().playSound(
                null,
                event.getPos(),
                SoundEvents.WOOL_PLACE,
                SoundSource.BLOCKS,
                1.0F,
                1.0F
        );

        player.displayClientMessage(
                Component.translatable(
                        warmingMaterial
                                ? "message.temperies.lining.warming"
                                : "message.temperies.lining.cooling"
                ),
                true
        );
    }
    @SubscribeEvent
    public void onTooltip(
            ItemTooltipEvent event
    ) {
        ItemStack stack =
                event.getItemStack();

        int modifier =
                ThermalLining.getModifier(stack);

        if (modifier > 0) {

            event.getToolTip().add(
                    Component.translatable(
                            "tooltip.temperies.lining.warming",
                            modifier
                    ).withStyle(
                            ChatFormatting.GOLD
                    )
            );

        } else if (modifier < 0) {

            event.getToolTip().add(
                    Component.translatable(
                            "tooltip.temperies.lining.cooling",
                            Math.abs(modifier)
                    ).withStyle(
                            ChatFormatting.AQUA
                    )
            );
        }
    }
}
