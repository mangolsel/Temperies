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
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(Temperies.MOD_ID)
public class Temperies {
    public static final String MOD_ID = "temperies";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Temperies(IEventBus modEventBus, ModContainer modContainer) {
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


        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(
                TemperiesHeatSources::registerDefaults
        );
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
