package net.konn.temperies.temperature;

import net.konn.temperies.component.Temperies_DataComponents;
import net.konn.temperies.util.Temperies_Tags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public final class ThermalLining {

    private ThermalLining() {
    }
    public static boolean canApplyMaterial(
            ItemStack material
    ) {
        return material.is(ItemTags.WOOL)
                || material.is(
                Temperies_Tags.Items.COOLING_MATERIALS
        );
    }
    public static boolean applyMaterial(
            ItemStack armorStack,
            ArmorItem armor,
            ItemStack material
    ) {
        if (material.is(ItemTags.WOOL)) {

            applyWarming(
                    armorStack,
                    armor
            );

            return true;
        }

        if (material.is(
                Temperies_Tags.Items.COOLING_MATERIALS
        )) {

            applyCooling(
                    armorStack,
                    armor
            );

            return true;
        }

        return false;
    }

    public static int getStrength(
            ArmorItem.Type type
    ) {
        return switch (type) {
            case CHESTPLATE -> 60;
            case LEGGINGS -> 50;
            case HELMET, BOOTS -> 40;

            default -> 0;
        };
    }
    public static int getStrength(
            ArmorItem armor
    ) {
        return getStrength(
                armor.getType()
        );
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
