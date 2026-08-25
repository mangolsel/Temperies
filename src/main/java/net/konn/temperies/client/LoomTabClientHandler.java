package net.konn.temperies.client;

import net.konn.temperies.Temperies;
import net.konn.temperies.network.SwitchLoomTabPayload;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
        modid = Temperies.MOD_ID,
        value = Dist.CLIENT
)
public final class LoomTabClientHandler {

    private static BlockPos lastLoomPos;

    private LoomTabClientHandler() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(
            PlayerInteractEvent.RightClickBlock event
    ) {

        if (!event.getLevel().isClientSide()) {
            return;
        }

        if (event.getLevel()
                .getBlockState(event.getPos())
                .is(Blocks.LOOM)) {

            lastLoomPos =
                    event.getPos().immutable();
        }
    }

    @SubscribeEvent
    public static void onScreenInit(
            ScreenEvent.Init.Post event
    ) {

        if (!(event.getScreen()
                instanceof LoomScreen screen)) {

            return;
        }

        if (lastLoomPos == null) {
            return;
        }

        int left =
                screen.getGuiLeft();

        int top =
                screen.getGuiTop();

        /*
         * Активная vanilla-вкладка.
         */
        Button patternTab =
                Button.builder(
                                Component.translatable(
                                        "gui.temperies.loom.patterns"
                                ),
                                button -> {
                                }
                        )
                        .bounds(
                                left,
                                top - 20,
                                55,
                                20
                        )
                        .build();

        patternTab.active = false;

        event.addListener(
                patternTab
        );


        /*
         * Вкладка Temperies.
         */
        Button liningTab =
                Button.builder(
                                Component.translatable(
                                        "gui.temperies.loom.lining"
                                ),
                                button ->
                                        PacketDistributor.sendToServer(
                                                new SwitchLoomTabPayload(
                                                        lastLoomPos,
                                                        true
                                                )
                                        )
                        )
                        .bounds(
                                left + 57,
                                top - 20,
                                55,
                                20
                        )
                        .build();

        event.addListener(
                liningTab
        );
    }
}
