package bookeditor.client.gui.widget.button.colorpicker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ColorPickerRenderer {

    public void renderButton(DrawContext ctx, int x, int y, int width, int height, ColorPickerState state) {
        ctx.fill(x + 1, y + height, x + width - 1, y + height + 1, 0x33000000);
        ctx.fill(x + 1, y + 1, x + width - 1, y + height - 1, state.argb);

        int borderColor = ColorConverter.interpolateColor(
                ColorPickerConstants.NORMAL_BORDER,
                ColorPickerConstants.HOVER_BORDER,
                state.hoverProgress
        );

        ctx.fill(x, y, x + width, y + 1, borderColor);
        ctx.fill(x, y + height - 1, x + width, y + height, borderColor);
        ctx.fill(x, y, x + 1, y + height, borderColor);
        ctx.fill(x + width - 1, y, x + width, y + height, borderColor);

        int topGradient = ColorConverter.addAlpha(0xFFFFFFFF, 0.2f * (1 + state.hoverProgress * 0.3f));
        ctx.fill(x + 1, y + 1, x + width - 1, y + height / 2, topGradient);
    }

    public void renderDropdown(DrawContext ctx, int dropX, int dropY, ColorPickerState state, int mouseX, int mouseY) {
        int totalWidth = ColorPickerConstants.calculateTotalWidth();
        int totalHeight = ColorPickerConstants.calculateTotalHeight();

        ctx.getMatrices().push();
        ctx.getMatrices().translate(0, 0, 300);

        ctx.fill(dropX - 1, dropY - 1, dropX + totalWidth + 1, dropY + totalHeight + 1, ColorPickerConstants.DARK_BORDER);
        ctx.fill(dropX, dropY, dropX + totalWidth, dropY + totalHeight, ColorPickerConstants.BG_COLOR);

        int paletteX = dropX + ColorPickerConstants.GAP;
        int paletteY = dropY + ColorPickerConstants.GAP;

        renderSatBrightPalette(ctx, paletteX, paletteY, state);

        int hueX = paletteX + ColorPickerConstants.PALETTE_WIDTH + ColorPickerConstants.GAP;
        int hueY = paletteY;
        renderHueBar(ctx, hueX, hueY, state);

        int hexY = paletteY + ColorPickerConstants.PALETTE_HEIGHT + ColorPickerConstants.GAP;
        renderHexField(ctx, paletteX, hexY, state);
        renderPreview(ctx, paletteX + 70, hexY, state);

        int presetsY = hexY + ColorPickerConstants.HEX_FIELD_HEIGHT + ColorPickerConstants.GAP;
        renderPresets(ctx, paletteX, presetsY, mouseX, mouseY);

        ctx.getMatrices().pop();
    }

    private void renderSatBrightPalette(DrawContext ctx, int x, int y, ColorPickerState state) {
        for (int py = 0; py < ColorPickerConstants.PALETTE_HEIGHT; py++) {
            for (int px = 0; px < ColorPickerConstants.PALETTE_WIDTH; px++) {
                float sat = (float) px / (ColorPickerConstants.PALETTE_WIDTH - 1);
                float bright = 1f - (float) py / (ColorPickerConstants.PALETTE_HEIGHT - 1);
                int color = ColorConverter.hsbToArgb(state.hue, sat, bright);
                ctx.fill(x + px, y + py, x + px + 1, y + py + 1, color);
            }
        }

        ctx.fill(x - 1, y - 1, x + ColorPickerConstants.PALETTE_WIDTH + 1, y, ColorPickerConstants.DARK_BORDER);
        ctx.fill(x - 1, y + ColorPickerConstants.PALETTE_HEIGHT, x + ColorPickerConstants.PALETTE_WIDTH + 1, y + ColorPickerConstants.PALETTE_HEIGHT + 1, ColorPickerConstants.DARK_BORDER);
        ctx.fill(x - 1, y, x, y + ColorPickerConstants.PALETTE_HEIGHT, ColorPickerConstants.DARK_BORDER);
        ctx.fill(x + ColorPickerConstants.PALETTE_WIDTH, y, x + ColorPickerConstants.PALETTE_WIDTH + 1, y + ColorPickerConstants.PALETTE_HEIGHT, ColorPickerConstants.DARK_BORDER);

        int markerX = x + (int) (state.saturation * (ColorPickerConstants.PALETTE_WIDTH - 1));
        int markerY = y + (int) ((1f - state.brightness) * (ColorPickerConstants.PALETTE_HEIGHT - 1));

        ctx.fill(markerX - 3, markerY - 1, markerX + 4, markerY, 0xFFFFFFFF);
        ctx.fill(markerX - 3, markerY + 1, markerX + 4, markerY + 2, 0xFFFFFFFF);
        ctx.fill(markerX - 1, markerY - 3, markerX, markerY + 4, 0xFFFFFFFF);
        ctx.fill(markerX + 1, markerY - 3, markerX + 2, markerY + 4, 0xFFFFFFFF);

        ctx.fill(markerX - 4, markerY - 1, markerX - 3, markerY + 2, 0xFF000000);
        ctx.fill(markerX + 4, markerY - 1, markerX + 5, markerY + 2, 0xFF000000);
        ctx.fill(markerX - 1, markerY - 4, markerX + 2, markerY - 3, 0xFF000000);
        ctx.fill(markerX - 1, markerY + 4, markerX + 2, markerY + 5, 0xFF000000);
    }

    private void renderHueBar(DrawContext ctx, int x, int y, ColorPickerState state) {
        for (int py = 0; py < ColorPickerConstants.HUE_BAR_HEIGHT; py++) {
            float h = (float) py / (ColorPickerConstants.HUE_BAR_HEIGHT - 1);
            int color = ColorConverter.hsbToArgb(h, 1f, 1f);
            ctx.fill(x, y + py, x + ColorPickerConstants.HUE_BAR_WIDTH, y + py + 1, color);
        }

        ctx.fill(x - 1, y - 1, x + ColorPickerConstants.HUE_BAR_WIDTH + 1, y, ColorPickerConstants.DARK_BORDER);
        ctx.fill(x - 1, y + ColorPickerConstants.HUE_BAR_HEIGHT, x + ColorPickerConstants.HUE_BAR_WIDTH + 1, y + ColorPickerConstants.HUE_BAR_HEIGHT + 1, ColorPickerConstants.DARK_BORDER);
        ctx.fill(x - 1, y, x, y + ColorPickerConstants.HUE_BAR_HEIGHT, ColorPickerConstants.DARK_BORDER);
        ctx.fill(x + ColorPickerConstants.HUE_BAR_WIDTH, y, x + ColorPickerConstants.HUE_BAR_WIDTH + 1, y + ColorPickerConstants.HUE_BAR_HEIGHT, ColorPickerConstants.DARK_BORDER);

        int markerY = y + (int) (state.hue * (ColorPickerConstants.HUE_BAR_HEIGHT - 1));
        ctx.fill(x - 2, markerY - 1, x + ColorPickerConstants.HUE_BAR_WIDTH + 2, markerY + 2, 0xFFFFFFFF);
        ctx.fill(x - 3, markerY - 2, x + ColorPickerConstants.HUE_BAR_WIDTH + 3, markerY - 1, 0xFF000000);
        ctx.fill(x - 3, markerY + 2, x + ColorPickerConstants.HUE_BAR_WIDTH + 3, markerY + 3, 0xFF000000);
    }

    private void renderHexField(DrawContext ctx, int x, int y, ColorPickerState state) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        int borderColor = state.hexFieldFocused ? ColorPickerConstants.HOVER_BORDER : ColorPickerConstants.NORMAL_BORDER;
        int bgColor = state.hexFieldFocused ? 0xFFFFFFFF : 0xFFE8E8E8;

        ctx.fill(x, y, x + ColorPickerConstants.HEX_FIELD_WIDTH, y + ColorPickerConstants.HEX_FIELD_HEIGHT, borderColor);
        ctx.fill(x + 1, y + 1, x + ColorPickerConstants.HEX_FIELD_WIDTH - 1, y + ColorPickerConstants.HEX_FIELD_HEIGHT - 1, bgColor);

        String displayText = "#" + state.hexInput;
        ctx.drawText(textRenderer, Text.literal(displayText), x + 4, y + 5, 0xFF000000, false);

        if (state.hexFieldFocused && state.cursorVisible) {
            String beforeCursor = "#" + state.hexInput.substring(0, Math.min(state.cursorPos, state.hexInput.length()));
            int cursorX = x + 4 + textRenderer.getWidth(beforeCursor);
            ctx.fill(cursorX, y + 3, cursorX + 1, y + ColorPickerConstants.HEX_FIELD_HEIGHT - 3, 0xFF000000);
        }
    }

    private void renderPreview(DrawContext ctx, int x, int y, ColorPickerState state) {
        ctx.fill(x, y, x + ColorPickerConstants.PREVIEW_WIDTH, y + ColorPickerConstants.HEX_FIELD_HEIGHT, ColorPickerConstants.DARK_BORDER);
        ctx.fill(x + 1, y + 1, x + ColorPickerConstants.PREVIEW_WIDTH - 1, y + ColorPickerConstants.HEX_FIELD_HEIGHT - 1, state.argb);
    }

    private void renderPresets(DrawContext ctx, int x, int y, int mouseX, int mouseY) {
        for (int i = 0; i < ColorPickerConstants.PRESET_COLORS.length; i++) {
            int col = i % ColorPickerConstants.COLS;
            int row = i / ColorPickerConstants.COLS;
            int cx = x + col * (ColorPickerConstants.COLOR_SIZE + ColorPickerConstants.GAP);
            int cy = y + row * (ColorPickerConstants.COLOR_SIZE + ColorPickerConstants.GAP);

            ctx.fill(cx, cy, cx + ColorPickerConstants.COLOR_SIZE, cy + ColorPickerConstants.COLOR_SIZE, ColorPickerConstants.PRESET_COLORS[i]);

            if (mouseX >= cx && mouseX < cx + ColorPickerConstants.COLOR_SIZE && mouseY >= cy && mouseY < cy + ColorPickerConstants.COLOR_SIZE) {
                ctx.fill(cx, cy, cx + ColorPickerConstants.COLOR_SIZE, cy + 1, ColorPickerConstants.HOVER_BORDER);
                ctx.fill(cx, cy + ColorPickerConstants.COLOR_SIZE - 1, cx + ColorPickerConstants.COLOR_SIZE, cy + ColorPickerConstants.COLOR_SIZE, ColorPickerConstants.HOVER_BORDER);
                ctx.fill(cx, cy, cx + 1, cy + ColorPickerConstants.COLOR_SIZE, ColorPickerConstants.HOVER_BORDER);
                ctx.fill(cx + ColorPickerConstants.COLOR_SIZE - 1, cy, cx + ColorPickerConstants.COLOR_SIZE, cy + ColorPickerConstants.COLOR_SIZE, ColorPickerConstants.HOVER_BORDER);
            }
        }
    }
}


