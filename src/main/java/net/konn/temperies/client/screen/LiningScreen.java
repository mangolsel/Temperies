package net.konn.temperies.client.screen;

import net.konn.temperies.Temperies;
import net.konn.temperies.client.LoomTabClientHandler;
import net.konn.temperies.client.widget.ItemIconButton;
import net.konn.temperies.item.Temperies_Items;
import net.konn.temperies.menu.LiningMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class LiningScreen
        extends AbstractContainerScreen<LiningMenu> {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(
                    Temperies.MOD_ID,
                    "textures/gui/container/loom_lining.png"
            );
    private static final List<ResourceLocation> ARMOR_ICONS =
            List.of(
                    ResourceLocation.withDefaultNamespace(
                            "item/empty_armor_slot_helmet"
                    ),
                    ResourceLocation.withDefaultNamespace(
                            "item/empty_armor_slot_chestplate"
                    ),
                    ResourceLocation.withDefaultNamespace(
                            "item/empty_armor_slot_leggings"
                    ),
                    ResourceLocation.withDefaultNamespace(
                            "item/empty_armor_slot_boots"
                    )
            );

    private static final List<ResourceLocation> MATERIAL_ICONS =
            List.of(
                    ResourceLocation.fromNamespaceAndPath(
                            Temperies.MOD_ID,
                            "item/empty_slot_lining"
                    ),

                    ResourceLocation.fromNamespaceAndPath(
                            Temperies.MOD_ID,
                            "item/empty_slot_shears"
                    )
            );
    private final CyclingSlotBackground armorIcon =
            new CyclingSlotBackground(0);

    private final CyclingSlotBackground materialIcon =
            new CyclingSlotBackground(1);

    @Override
    protected void containerTick() {
        super.containerTick();

        armorIcon.tick(
                ARMOR_ICONS
        );

        materialIcon.tick(
                MATERIAL_ICONS
        );
    }

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

        // Название меню
        this.titleLabelX = 9;
        this.titleLabelY = 4;

        this.inventoryLabelY = 72;
    }

    @Override
    protected void init() {
        super.init();

        addTabs();
    }

    private void addTabs() {

        /*
         * Вкладка обычного ткацкого станка.
         */
        ItemIconButton patternsTab =
                new ItemIconButton(
                        leftPos,
                        topPos - 22,
                        28,
                        22,

                        new ItemStack(
                                Items.WHITE_BANNER
                        ),

                        Component.translatable(
                                "gui.temperies.loom.patterns"
                        ),

                        button ->
                                LoomTabClientHandler.switchTab(
                                        menu.getLoomPos(),
                                        false
                                )
                );

        this.addRenderableWidget(
                patternsTab
        );


        /*
         * Текущая вкладка подкладок.
         */
        ItemIconButton liningTab =
                new ItemIconButton(
                        leftPos + 30,
                        topPos - 22,
                        28,
                        22,

                        new ItemStack(
                                Temperies_Items.FLAX_FABRIC.get()
                        ),

                        Component.translatable(
                                "gui.temperies.loom.lining"
                        ),

                        button -> {
                        }
                );

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


        armorIcon.render(
                menu,
                guiGraphics,
                partialTick,
                leftPos,
                topPos
        );

        materialIcon.render(
                menu,
                guiGraphics,
                partialTick,
                leftPos,
                topPos
        );
    }
}
