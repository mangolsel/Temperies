package net.konn.temperies.temperature;

import net.konn.temperies.component.Temperies_DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class TemperatureEquipment {

    private TemperatureEquipment() {
    }

    public static int getTotalModifier(
            LivingEntity entity
    ) {
        int totalModifier = 0;

        for (ItemStack armorStack : entity.getArmorSlots()) {
            totalModifier += getStackModifier(armorStack);
        }

        return Mth.clamp(
                totalModifier,
                -TemperatureConstants.MAX_EXPOSURE,
                TemperatureConstants.MAX_EXPOSURE
        );
    }

    public static int getWarmingModifier(
            LivingEntity entity
    ) {
        int total = 0;

        for (ItemStack armorStack : entity.getArmorSlots()) {

            int modifier =
                    getStackModifier(armorStack);

            if (modifier > 0) {
                total += modifier;
            }
        }

        return Mth.clamp(
                total,
                0,
                TemperatureConstants.MAX_EXPOSURE
        );
    }

    public static int getCoolingModifier(
            LivingEntity entity
    ) {
        int total = 0;

        for (ItemStack armorStack : entity.getArmorSlots()) {

            int modifier =
                    getStackModifier(armorStack);

            if (modifier < 0) {
                total += Math.abs(modifier);
            }
        }

        return Mth.clamp(
                total,
                0,
                TemperatureConstants.MAX_EXPOSURE
        );
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
}
