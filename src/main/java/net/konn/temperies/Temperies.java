package net.konn.temperies;

import net.konn.temperies.attachment.Temperies_Attachments;
import net.konn.temperies.block.Temperies_Blocks;
import net.konn.temperies.component.Temperies_DataComponents;
import net.konn.temperies.event.TemperatureHandler;
import net.konn.temperies.item.Temperies_ArmorMaterials;
import net.konn.temperies.item.Temperies_CreativeModeTabs;
import net.konn.temperies.item.Temperies_Items;
import net.konn.temperies.menu.Temperies_Menus;
import net.konn.temperies.network.TemperiesNetworking;
import net.konn.temperies.temperature.TemperiesHeatSources;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Temperies.MOD_ID)
public class Temperies {
    public static final String MOD_ID = "temperies";

    public Temperies(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(TemperiesNetworking::registerPayloads);

        Temperies_CreativeModeTabs.register(modEventBus);
        Temperies_Items.register(modEventBus);
        Temperies_Blocks.register(modEventBus);
        Temperies_Attachments.register(modEventBus);
        Temperies_ArmorMaterials.register(modEventBus);
        Temperies_DataComponents.register(modEventBus);
        Temperies_Menus.register(modEventBus);


        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new TemperatureHandler());

    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(
                TemperiesHeatSources::registerDefaults
        );
    }
}
