package net.konn.temperies.item;

import net.konn.temperies.Temperies;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Temperies_CreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Temperies.MOD_ID);

    public static final Supplier<CreativeModeTab> TEMPERIES_TAB =
            CREATIVE_MODE_TABS.register("temperies_tab",() -> CreativeModeTab.builder()
                    .title(Component.translatable("itemsGroup.temperies.temperies_tab"))
                    .icon(()->new ItemStack(Temperies_Items.THERMOMETER.get()))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(Temperies_Items.WOOL_HELMET);
                        output.accept(Temperies_Items.WOOL_CHESTPLATE);
                        output.accept(Temperies_Items.WOOL_LEGGINGS);
                        output.accept(Temperies_Items.WOOL_BOOTS);
                        output.accept(Temperies_Items.THERMOSCOPE);
                        output.accept(Temperies_Items.THERMOMETER);
                        output.accept(Temperies_Items.FLAX_FABRIC);
                        output.accept(Temperies_Items.FLAX_HELMET);
                        output.accept(Temperies_Items.FLAX_CHESTPLATE);
                        output.accept(Temperies_Items.FLAX_LEGGINGS);
                        output.accept(Temperies_Items.FLAX_BOOTS);
                    })
                    .build());

    public static void  register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
