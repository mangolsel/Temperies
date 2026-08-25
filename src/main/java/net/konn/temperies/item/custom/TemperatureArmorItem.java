package net.konn.temperies.item.custom;

import net.konn.temperies.temperature.TemperatureWearable;
import net.minecraft.core.Holder;
import net.minecraft.world.item.*;

public class TemperatureArmorItem extends ArmorItem implements TemperatureWearable {

    private final int temperatureModifier;

    public TemperatureArmorItem(Holder<ArmorMaterial> material, Type type, int temperatureModifier, Item.Properties properties) {
        super(material, type, properties);

        this.temperatureModifier =
                temperatureModifier;
    }

    @Override
    public int getTemperatureModifier(ItemStack stack)
    {
        return temperatureModifier;
    }
}
