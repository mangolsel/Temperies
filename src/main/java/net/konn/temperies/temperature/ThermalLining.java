package net.konn.temperies.temperature;

import net.konn.temperies.component.Temperies_DataComponents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public final class ThermalLining {

    private ThermalLining() {
    }

    public static int getStrength(
            ArmorItem armor
    ) {
        ArmorItem.Type type =
                armor.getType();

        if (type == ArmorItem.Type.CHESTPLATE) {
            return 60;
        }

        if (type == ArmorItem.Type.LEGGINGS) {
            return 50;
        }

        if (type == ArmorItem.Type.HELMET) {
            return 40;
        }

        if (type == ArmorItem.Type.BOOTS) {
            return 40;
        }

        return 0;
    }

    public static void applyWarming(
            ItemStack armorStack,
            ArmorItem armor
    ) {
        int strength =
                getStrength(armor);

        armorStack.set(
                Temperies_DataComponents.THERMAL_LINING.get(),
                strength
        );
    }

    public static void applyCooling(
            ItemStack armorStack,
            ArmorItem armor
    ) {
        int strength =
                getStrength(armor);

        armorStack.set(
                Temperies_DataComponents.THERMAL_LINING.get(),
                -strength
        );
    }

    public static int getModifier(
            ItemStack stack
    ) {
        return stack.getOrDefault(
                Temperies_DataComponents.THERMAL_LINING.get(),
                0
        );
    }

    public static boolean hasLining(
            ItemStack stack
    ) {
        return stack.has(
                Temperies_DataComponents.THERMAL_LINING.get()
        );
    }

    public static void remove(
            ItemStack stack
    ) {
        stack.remove(
                Temperies_DataComponents.THERMAL_LINING.get()
        );
    }
}
