package net.konn.temperies.client.screen;

import net.konn.temperies.Temperies;
import net.konn.temperies.menu.LiningMenu;
import net.konn.temperies.network.SwitchLoomTabPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class LiningScreen
        extends AbstractContainerScreen<LiningMenu> {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(
                    Temperies.MOD_ID,
                    "textures/gui/container/loom_lining.png"
            );

    public LiningScreen(
            LiningMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(
                menu,
                inventory,
                Component.translatable(
                        "container.temperies.lining"
                )
        );

        this.imageWidth = 176;
        this.imageHeight = 166;

        this.inventoryLabelY = 72;
    }

    @Override
    protected void init() {
        super.init();

        addTabs();
    }

    private void addTabs() {

        /*
         * Vanilla-вкладка.
         */
        this.addRenderableWidget(
                Button.builder(
                                Component.translatable(
                                        "gui.temperies.loom.patterns"
                                ),
                                button ->
                                        PacketDistributor.sendToServer(
                                                new SwitchLoomTabPayload(
                                                        menu.getLoomPos(),
                                                        false
                                                )
                                        )
                        )
                        .bounds(
                                leftPos,
                                topPos - 20,
                                55,
                                20
                        )
                        .build()
        );

        /*
         * Текущая вкладка — отключённая кнопка.
         */
        Button liningTab =
                Button.builder(
                                Component.translatable(
                                        "gui.temperies.loom.lining"
                                ),
                                button -> {
                                }
                        )
                        .bounds(
                                leftPos + 57,
                                topPos - 20,
                                55,
                                20
                        )
                        .build();

        liningTab.active = false;

        this.addRenderableWidget(
                liningTab
        );
    }

    @Override
    public void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        renderBackground(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );

        super.render(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );

        renderTooltip(
                guiGraphics,
                mouseX,
                mouseY
        );
    }

    @Override
    protected void renderBg(
            GuiGraphics guiGraphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {

        guiGraphics.blit(
                BACKGROUND,
                leftPos,
                topPos,
                0,
                0,
                imageWidth,
                imageHeight,
                256,
                256
        );
    }
}
