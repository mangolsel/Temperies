package net.konn.temperies.component;

import com.mojang.serialization.Codec;
import net.konn.temperies.Temperies;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Temperies_DataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(
                    Registries.DATA_COMPONENT_TYPE,
                    Temperies.MOD_ID
            );

    public static final Supplier<DataComponentType<Integer>> THERMAL_LINING =
            DATA_COMPONENTS.registerComponentType(
                    "thermal_lining",
                    builder -> builder.persistent(Codec.INT)
            );

    private Temperies_DataComponents() {
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}
