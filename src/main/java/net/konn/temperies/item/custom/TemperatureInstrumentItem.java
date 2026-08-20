package net.konn.temperies.item.custom;

import net.minecraft.world.item.Item;

public class TemperatureInstrumentItem extends Item {

    private final boolean fragile;

    public TemperatureInstrumentItem(
            boolean fragile,
            Properties properties
    ) {
        super(properties);

        this.fragile = fragile;
    }

    public boolean isFragile() {
        return fragile;
    }
}
