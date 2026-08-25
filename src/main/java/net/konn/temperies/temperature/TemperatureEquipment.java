package net.konn.temperies.temperature;

import net.konn.temperies.component.Temperies_DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class TemperatureEquipment {

    private TemperatureEquipment() {
    }

    public static int getStackModifier(
            ItemStack stack
    ) {
        int modifier = 0;


        if (stack.getItem()
                instanceof TemperatureWearable wearable) {

            modifier +=
                    wearable.getTemperatureModifier(
                            stack
                    );
        }

        modifier += stack.getOrDefault(
                Temperies_DataComponents.THERMAL_LINING.get(),
                0
        );

        return modifier;
    }
    public record Modifiers(
            int total,
            int warming,
            int cooling
    ) {
    }
    public static Modifiers calculate(
            LivingEntity entity
    ) {
        int total = 0;
        int warming = 0;
        int cooling = 0;

        for (ItemStack stack : entity.getArmorSlots()) {

            int modifier =
                    getStackModifier(stack);

            total += modifier;

            if (modifier > 0) {
                warming += modifier;
            } else if (modifier < 0) {
                cooling -= modifier;
            }
        }

        return new Modifiers(
                Mth.clamp(
                        total,
                        -TemperatureConstants.MAX_EXPOSURE,
                        TemperatureConstants.MAX_EXPOSURE
                ),

                Mth.clamp(
                        warming,
                        0,
                        TemperatureConstants.MAX_EXPOSURE
                ),

                Mth.clamp(
                        cooling,
                        0,
                        TemperatureConstants.MAX_EXPOSURE
                )
        );
    }
}
