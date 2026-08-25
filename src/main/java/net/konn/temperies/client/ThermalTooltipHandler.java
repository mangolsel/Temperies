package net.konn.temperies.client;

import net.konn.temperies.Temperies;
import net.konn.temperies.temperature.TemperatureEquipment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(
        modid = Temperies.MOD_ID,
        value = Dist.CLIENT
)
public final class ThermalTooltipHandler {

    private ThermalTooltipHandler() {
    }

    @SubscribeEvent
    public static void onItemTooltip(
            ItemTooltipEvent event
    ) {
        int modifier =
                TemperatureEquipment.getStackModifier(
                        event.getItemStack()
                );

        if (modifier > 0) {

            event.getToolTip().add(
                    Component.translatable(
                            "tooltip.temperies.temperature_modifier.positive"
                    ).withStyle(
                            ChatFormatting.GOLD
                    )
            );

        } else if (modifier < 0) {

            event.getToolTip().add(
                    Component.translatable(
                            "tooltip.temperies.temperature_modifier.negative"
                    ).withStyle(
                            ChatFormatting.AQUA
                    )
            );
        }
    }
}
