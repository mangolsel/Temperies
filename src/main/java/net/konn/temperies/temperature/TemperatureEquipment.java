package net.konn.temperies.temperature;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class TemperatureEquipment {

    private TemperatureEquipment() {
    }

    public static int getTotalModifier(
            LivingEntity entity
    ) {
        int totalModifier =
                getWarmingModifier(entity)
                        - getCoolingModifier(entity);

        return Mth.clamp(
                totalModifier,
                -TemperatureConstants.MAX_EXPOSURE,
                TemperatureConstants.MAX_EXPOSURE
        );
    }

    /**
     * Суммарная сила согревающей одежды.
     *
     * Например:
     * шерстяные ботинки +40
     * шерстяной нагрудник +60
     * итого = 100
     */
    public static int getWarmingModifier(
            LivingEntity entity
    ) {
        int total = 0;

        for (ItemStack armorStack : entity.getArmorSlots()) {

            if (armorStack.getItem()
                    instanceof TemperatureWearable wearable) {

                int modifier =
                        wearable.getTemperatureModifier(
                                armorStack
                        );

                if (modifier > 0) {
                    total += modifier;
                }
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

            if (armorStack.getItem()
                    instanceof TemperatureWearable wearable) {

                int modifier =
                        wearable.getTemperatureModifier(
                                armorStack
                        );

                if (modifier < 0) {
                    total += Math.abs(modifier);
                }
            }
        }

        return Mth.clamp(
                total,
                0,
                TemperatureConstants.MAX_EXPOSURE
        );
    }
}
