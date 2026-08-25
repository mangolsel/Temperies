package net.konn.temperies.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.konn.temperies.Temperies;
import net.konn.temperies.item.custom.TemperatureInstrumentItem;
import net.konn.temperies.temperature.TemperatureConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class TemperatureHudRenderer {

    private static final int TEXTURE_WIDTH = 16;
    private static final int TEXTURE_HEIGHT = 70;
    private static final int LINE_SOURCE_Y = 34;
    private static final int LINE_HEIGHT = 2;

    private static final ResourceLocation THERMOMETER_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    Temperies.MOD_ID,
                    "textures/gui/termometer.png"
            );

    private static final ResourceLocation TEMPERATURE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    Temperies.MOD_ID,
                    "textures/gui/temperature.png"
            );

    private static final ResourceLocation LINE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    Temperies.MOD_ID,
                    "textures/gui/line.png"
            );

    private static final int HOT_LINE_Y = 0;
    private static final int HOT_DAMAGE_LINE_Y = 13;

    private static final int NEUTRAL_LINE_Y = 34;

    private static final int COLD_DAMAGE_LINE_Y = 57;
    private static final int COLD_LINE_Y = 68;

    private static final float DAMAGE_POINT =
            TemperatureConstants.DAMAGE_THRESHOLD
                    / (float) TemperatureConstants.MAX_EXPOSURE;


    private static final int RIGHT_MARGIN = 6;

    private TemperatureHudRenderer() {
    }

    public static void render(GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null
                || minecraft.level == null
                || minecraft.options.hideGui) {
            return;
        }
        if (!isHoldingTemperatureInstrument(minecraft)) {
            return;
        }

        float heatPercent = Mth.clamp(
                ClientTemperatureState.getHeatPercent(),
                0.0F,
                1.0F
        );

        float coldPercent = Mth.clamp(
                ClientTemperatureState.getColdPercent(),
                0.0F,
                1.0F
        );

        float signedTemperature;

        if (heatPercent >= coldPercent) {
            signedTemperature = heatPercent;
        } else {
            signedTemperature = -coldPercent;
        }

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        int x = screenWidth - TEXTURE_WIDTH - RIGHT_MARGIN;
        int y = screenHeight / 2 - TEXTURE_HEIGHT / 2;

        int lineY = calculateLineY(signedTemperature);

        renderThermometer(
                guiGraphics,
                x,
                y,
                lineY
        );
    }
    private static boolean isHoldingTemperatureInstrument(
            Minecraft minecraft
    ) {
        if (minecraft.player == null) {
            return false;
        }

        boolean mainHand =
                minecraft.player
                        .getMainHandItem()
                        .getItem()
                        instanceof TemperatureInstrumentItem;

        boolean offHand =
                minecraft.player
                        .getOffhandItem()
                        .getItem()
                        instanceof TemperatureInstrumentItem;

        return mainHand || offHand;
    }

    private static int calculateLineY(
            float signedTemperature
    ) {
        signedTemperature = Mth.clamp(
                signedTemperature,
                -1.0F,
                1.0F
        );

        if (signedTemperature > 0.0F) {
            return calculateTwoStageLine(
                    signedTemperature,
                    HOT_DAMAGE_LINE_Y,
                    HOT_LINE_Y
            );
        }

        if (signedTemperature < 0.0F) {
            return calculateTwoStageLine(
                    -signedTemperature,
                    COLD_DAMAGE_LINE_Y,
                    COLD_LINE_Y
            );
        }

        return NEUTRAL_LINE_Y;
    }
    private static int calculateTwoStageLine(
            float exposure,
            int damageY,
            int maximumY
    ) {
        exposure = Mth.clamp(
                exposure,
                0.0F,
                1.0F
        );

        if (exposure <= DAMAGE_POINT) {
            float progress = exposure / DAMAGE_POINT;

            return Math.round(
                    Mth.lerp(
                            progress,
                            TemperatureHudRenderer.NEUTRAL_LINE_Y,
                            damageY
                    )
            );
        }

        float dangerProgress =
                (exposure - DAMAGE_POINT)
                        / (1.0F - DAMAGE_POINT);

        return Math.round(
                Mth.lerp(
                        dangerProgress,
                        damageY,
                        maximumY
                )
        );
    }
    private static void drawTemperatureLine(
            GuiGraphics guiGraphics,
            int x,
            int y
    ) {
        guiGraphics.blit(
                LINE_TEXTURE,

                x,
                y,

                TEXTURE_WIDTH,
                LINE_HEIGHT,

                0.0F,
                LINE_SOURCE_Y,

                TEXTURE_WIDTH,
                LINE_HEIGHT,

                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
    }

    private static void renderThermometer(
            GuiGraphics guiGraphics,
            int x,
            int y,
            int lineY
    ) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        guiGraphics.pose().pushPose();

        try {
            guiGraphics.setColor(
                    1.0F,
                    1.0F,
                    1.0F,
                    1.0F
            );

            blitFullTexture(
                    guiGraphics,
                    THERMOMETER_TEXTURE,
                    x,
                    y
            );

            guiGraphics.pose().translate(
                    0.0F,
                    0.0F,
                    1.0F
            );

            blitFullTexture(
                    guiGraphics,
                    TEMPERATURE_TEXTURE,
                    x,
                    y
            );

            guiGraphics.pose().translate(
                    0.0F,
                    0.0F,
                    1.0F
            );

            drawTemperatureLine(
                    guiGraphics,
                    x,
                    y + lineY
            );

        } finally {
            guiGraphics.pose().popPose();
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
        }
    }

    private static void blitFullTexture(
            GuiGraphics guiGraphics,
            ResourceLocation texture,
            int x,
            int y
    ) {
        guiGraphics.blit(
                texture,

                x,
                y,

                TEXTURE_WIDTH,
                TEXTURE_HEIGHT,

                0.0F,
                0.0F,

                TEXTURE_WIDTH,
                TEXTURE_HEIGHT,

                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
    }
}
