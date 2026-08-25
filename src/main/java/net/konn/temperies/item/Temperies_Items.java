package net.konn.temperies.item;

import net.konn.temperies.Temperies;
import net.konn.temperies.item.custom.TemperatureArmorItem;
import net.konn.temperies.item.custom.TemperatureInstrumentItem;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Temperies_Items {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Temperies.MOD_ID);

    // TEMPERATURE INSTRUMENTS
    public static final DeferredItem<TemperatureInstrumentItem> THERMOSCOPE =
            ITEMS.register(
                    "thermoscope",
                    () -> new TemperatureInstrumentItem(true,
                            new Item.Properties().stacksTo(1)));

    public static final DeferredItem<TemperatureInstrumentItem> THERMOMETER =
            ITEMS.register(
                    "thermometer", () -> new TemperatureInstrumentItem(false,
                            new Item.Properties().stacksTo(1)));

    //ITEMS
    public static final DeferredItem<Item> PEAT = ITEMS.registerSimpleItem("peat");
    public static final DeferredItem<Item> FLAX_FIBER = ITEMS.registerSimpleItem("flax_fiber");

    //ARMOR
    public static final DeferredItem<Item> WOOL_HELMET =
            ITEMS.register(
                    "wool_helmet",
                    () -> new TemperatureArmorItem(Temperies_ArmorMaterials.WOOL, ArmorItem.Type.HELMET, 40,
                            new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(3))));

    public static final DeferredItem<Item> WOOL_CHESTPLATE =
            ITEMS.register(
                    "wool_chestplate",
                    () -> new TemperatureArmorItem(Temperies_ArmorMaterials.WOOL, ArmorItem.Type.CHESTPLATE, 60,
                            new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(3))));

    public static final DeferredItem<Item> WOOL_LEGGINGS =
            ITEMS.register(
                    "wool_leggings",
                    () -> new TemperatureArmorItem(Temperies_ArmorMaterials.WOOL, ArmorItem.Type.LEGGINGS, 50,
                            new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(3))));

    public static final DeferredItem<Item> WOOL_BOOTS =
            ITEMS.register(
                    "wool_boots",
                    () -> new TemperatureArmorItem(Temperies_ArmorMaterials.WOOL, ArmorItem.Type.BOOTS, 40,
                            new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(3))));

    public static final DeferredItem<Item> FLAX_BOOTS =
            ITEMS.register(
                    "flax_boots",
                    () -> new TemperatureArmorItem(Temperies_ArmorMaterials.FLAX, ArmorItem.Type.BOOTS, -40,
                            new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(3))));

    public static final DeferredItem<Item> FLAX_HELMET =
            ITEMS.register(
                    "flax_helmet",
                    () -> new TemperatureArmorItem(Temperies_ArmorMaterials.FLAX, ArmorItem.Type.HELMET, -40,
                            new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(3))));

    public static final DeferredItem<Item> FLAX_CHESTPLATE =
            ITEMS.register(
                    "flax_chestplate",
                    () -> new TemperatureArmorItem(Temperies_ArmorMaterials.FLAX, ArmorItem.Type.CHESTPLATE, -60,
                            new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(3))));

    public static final DeferredItem<Item> FLAX_LEGGINGS =
            ITEMS.register(
                    "flax_leggings",
                    () -> new TemperatureArmorItem(Temperies_ArmorMaterials.FLAX, ArmorItem.Type.LEGGINGS, -50,
                            new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(3))));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
