package net.konn.temperies.client.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ItemIconButton extends Button {

    private final ItemStack icon;

    public ItemIconButton(
            int x,
            int y,
            int width,
            int height,
            ItemStack icon,
            Component narration,
            OnPress onPress
    ) {
        super(
                x,
                y,
                width,
                height,
                narration,
                onPress,
                DEFAULT_NARRATION
        );

        this.icon = icon;
    }

    @Override
    protected void renderWidget(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        /*
         * Сначала обычная minecraft-кнопка.
         */
        super.renderWidget(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );

        /*
         * Затем предмет поверх неё.
         */
        int iconX =
                getX()
                        + (getWidth() - 16) / 2;

        int iconY =
                getY()
                        + (getHeight() - 16) / 2;

        guiGraphics.renderItem(
                icon,
                iconX,
                iconY
        );
    }
}
