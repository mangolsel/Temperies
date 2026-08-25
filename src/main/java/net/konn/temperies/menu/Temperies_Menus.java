package net.konn.temperies.menu;

import net.konn.temperies.Temperies;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class Temperies_Menus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(
                    Registries.MENU,
                    Temperies.MOD_ID
            );

    public static final Supplier<MenuType<LiningMenu>> LINING_MENU =
            MENUS.register(
                    "lining_menu",
                    () -> IMenuTypeExtension.create(
                            LiningMenu::new
                    )
            );

    private Temperies_Menus() {
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
