package bookeditor.client.gui.widget.button;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CustomButton extends ButtonWidget {
    private float hoverProgress = 0.0f;
    private long lastFrameTime = System.currentTimeMillis();

    private static final int PRIMARY_COLOR = 0xFFF0E8DC;
    private static final int BORDER_NORMAL = 0xFF8B8B8B;
    private static final int BORDER_HOVER = 0xFFE8B84C;
    private static final int TEXT_COLOR = 0xFF000000;
    private static final int SHADOW_COLOR = 0x441E1E1E;

    public CustomButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
    }

    public CustomButton(int x, int y, int width, int height, Text message, Text tooltip, PressAction onPress) {
        this(x, y, width, height, message, onPress);
        this.setTooltip(Tooltip.of(tooltip));
    }

    @Override
    protected void renderButton(DrawContext ctx, int mouseX, int mouseY, float delta) {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastFrameTime) / 1000.0f;
        lastFrameTime = currentTime;

        boolean isHovering = this.isHovered();

        if (isHovering && hoverProgress < 1.0f) {
            hoverProgress = Math.min(1.0f, hoverProgress + deltaTime * 6.0f);
        } else if (!isHovering && hoverProgress > 0.0f) {
            hoverProgress = Math.max(0.0f, hoverProgress - deltaTime * 6.0f);
        }

        int x = getX();
        int y = getY();
        int w = width;
        int h = height;

        if (active) {
            ctx.fill(x + 1, y + h, x + w, y + h + 2, SHADOW_COLOR);

            ctx.fill(x, y, x + w, y + h, PRIMARY_COLOR);

            int topHighlight = addAlpha(0xFFFFFFFF, 0.3f);
            ctx.fill(x + 1, y + 1, x + w - 1, y + 2, topHighlight);

            int bottomShadow = addAlpha(0xFF000000, 0.15f);
            ctx.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, bottomShadow);

            int borderColor = interpolateColor(BORDER_NORMAL, BORDER_HOVER, hoverProgress);

            ctx.fill(x, y, x + w, y + 1, borderColor);
            ctx.fill(x, y + h - 1, x + w, y + h, borderColor);
            ctx.fill(x, y, x + 1, y + h, borderColor);
            ctx.fill(x + w - 1, y, x + w, y + h, borderColor);
        } else {
            ctx.fill(x, y, x + w, y + h, 0xFFA0A0A0);
        }

        int textX = x + w / 2;
        int textY = y + (h - 8) / 2;
        int textWidth = this.getTextRenderer().getWidth(this.getMessage());
        ctx.drawText(this.getTextRenderer(), this.getMessage(), textX - textWidth / 2, textY,
                active ? TEXT_COLOR : 0xFFA0A0A0, false);
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
        return (a << 24) | (color & 0xFFFFFF);
    }

    private net.minecraft.client.font.TextRenderer getTextRenderer() {
        return net.minecraft.client.MinecraftClient.getInstance().textRenderer;
    }
}