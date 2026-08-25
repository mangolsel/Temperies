package net.konn.temperies.client;

import net.konn.temperies.Temperies;
import net.konn.temperies.client.screen.LiningScreen;
import net.konn.temperies.client.widget.ItemIconButton;
import net.konn.temperies.item.Temperies_Items;
import net.konn.temperies.network.SwitchLoomTabPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;


@EventBusSubscriber(
        modid = Temperies.MOD_ID,
        value = Dist.CLIENT
)
public final class LoomTabClientHandler {
    private static double savedCursorX;
    private static double savedCursorY;

    private static boolean restoreCursor;

    private static BlockPos lastLoomPos;

    private LoomTabClientHandler() {
    }
    public static void switchTab(
            BlockPos loomPos,
            boolean liningTab
    ) {
        Minecraft minecraft =
                Minecraft.getInstance();

        savedCursorX =
                minecraft.mouseHandler.xpos();

        savedCursorY =
                minecraft.mouseHandler.ypos();

        restoreCursor = true;

        PacketDistributor.sendToServer(
                new SwitchLoomTabPayload(
                        loomPos,
                        liningTab
                )
        );
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
        if (restoreCursor
                && (event.getScreen() instanceof LoomScreen
                || event.getScreen() instanceof LiningScreen)) {

            double cursorX = savedCursorX;
            double cursorY = savedCursorY;

            restoreCursor = false;

            Minecraft minecraft =
                    Minecraft.getInstance();

            minecraft.execute(() -> {
                GLFW.glfwSetCursorPos(
                        minecraft.getWindow().getWindow(),
                        cursorX,
                        cursorY
                );
            });
        }

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
        ItemIconButton patternTab =
                new ItemIconButton(
                        left,
                        top - 22,
                        28,
                        22,

                        new ItemStack(
                                Items.WHITE_BANNER
                        ),

                        Component.translatable(
                                "gui.temperies.loom.patterns"
                        ),

                        button -> {
                        }
                );

        patternTab.active = false;

        event.addListener(
                patternTab
        );


        /*
         * Вкладка подкладок.
         */
        ItemIconButton liningTab =
                new ItemIconButton(
                        left + 30,
                        top - 22,
                        28,
                        22,

                        new ItemStack(
                                Temperies_Items.FLAX_FABRIC.get()
                        ),

                        Component.translatable(
                                "gui.temperies.loom.lining"
                        ),

                        button ->
                                switchTab(
                                        lastLoomPos,
                                        true
                                )
                );

        event.addListener(
                liningTab
        );
    }
}
