package bookeditor.client.gui.widget.zoom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class ZoomSliderRenderer {
    private static final int BORDER_NORMAL = 0xFF8B8B8B;
    private static final int BORDER_HOVER = 0xFFE8B84C;
    private static final int BG_COLOR = 0xFFF0E8DC;
    private static final int TRACK_COLOR = 0xFFD4C8B8;
    private static final int THUMB_COLOR = 0xFFF0E8DC;
    private static final int TEXT_COLOR = 0xFF000000;

    public void render(DrawContext ctx, int x, int y, int width, int height, ZoomSliderState state) {
        int borderColor = interpolateColor(BORDER_NORMAL, BORDER_HOVER, state.hoverProgress);

        ctx.fill(x - 1, y - 1, x + width + 1, y + height + 1, borderColor);
        ctx.fill(x, y, x + width, y + height, BG_COLOR);

        int trackX = x + width / 2 - 2;
        int trackWidth = 4;
        int trackPadding = 20;
        int trackY = y + trackPadding;
        int trackHeight = height - trackPadding * 2 - 16;

        ctx.fill(trackX - 1, trackY - 1, trackX + trackWidth + 1, trackY + trackHeight + 1, BORDER_NORMAL);
        ctx.fill(trackX, trackY, trackX + trackWidth, trackY + trackHeight, TRACK_COLOR);

        float normalized = 1f - state.getNormalizedPosition();
        int thumbY = trackY + (int)(normalized * (trackHeight - 8));
        int thumbX = x + 2;
        int thumbWidth = width - 4;
        int thumbHeight = 8;

        ctx.fill(thumbX - 1, thumbY - 1, thumbX + thumbWidth + 1, thumbY + thumbHeight + 1, borderColor);
        ctx.fill(thumbX, thumbY, thumbX + thumbWidth, thumbY + thumbHeight, THUMB_COLOR);

        int topHighlight = addAlpha(0xFFFFFFFF, 0.3f);
        ctx.fill(thumbX + 1, thumbY + 1, thumbX + thumbWidth - 1, thumbY + 2, topHighlight);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        String percentText = String.format("%.0f%%", state.getZoomPercent());
        int textWidth = textRenderer.getWidth(percentText);
        int textX = x + (width - textWidth) / 2;
        int textY = y + height - 14;
        ctx.drawText(textRenderer, percentText, textX, textY, TEXT_COLOR, false);

        String maxLabel = "500%";
        int maxLabelWidth = textRenderer.getWidth(maxLabel);
        ctx.drawText(textRenderer, maxLabel, x + (width - maxLabelWidth) / 2, y + 4, 0xFF6B5F53, false);
    }

    private int interpolateColor(int color1, int color2, float t) {
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int a = (int) (a1 + (a2 - a1) * t);
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int addAlpha(int color, float alpha) {
        int a = (int) (255 * alpha);
        return (a << 24) | (color & 0x00FFFFFF);
    }
}

