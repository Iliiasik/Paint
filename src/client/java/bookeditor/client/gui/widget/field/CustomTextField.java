package bookeditor.client.gui.widget.field;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

public class CustomTextField extends TextFieldWidget {
    private static final int BACKGROUND = 0xFFF0E8DC;
    private static final int BORDER = 0xFF8B8B8B;
    private static final int BORDER_FOCUSED = 0xFF373737;
    private static final int TEXT_COLOR = 0xFF000000;
    private static final int SUGGESTION_COLOR = 0xFF808080;
    private static final int PADDING = 6;

    public CustomTextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
        this.setDrawsBackground(false);
        this.setEditableColor(TEXT_COLOR);
        this.setUneditableColor(TEXT_COLOR);
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        int x = getX();
        int y = getY();
        int w = this.width;
        int h = this.height;

        context.fill(x, y, x + w, y + h, BACKGROUND);

        int borderColor = this.isFocused() ? BORDER_FOCUSED : BORDER;
        int borderWidth = 1;

        context.fill(x, y, x + w, y + borderWidth, borderColor);
        context.fill(x, y + h - borderWidth, x + w, y + h, borderColor);
        context.fill(x, y, x + borderWidth, y + h, borderColor);
        context.fill(x + w - borderWidth, y, x + w, y + h, borderColor);

        int textY = y + (h - 8) / 2;
        int innerLeft = x + PADDING;
        int innerRight = x + w - PADDING;

        context.enableScissor(innerLeft, y + 2, innerRight, y + h - 2);

        String text = this.getText();
        TextRenderer tr = net.minecraft.client.MinecraftClient.getInstance().textRenderer;

        if (text.isEmpty() && !this.isFocused()) {
            Text suggestion = this.getMessage();
            if (suggestion != null) {
                context.drawText(tr, suggestion, innerLeft, textY, SUGGESTION_COLOR, false);
            }
        } else {
            context.drawText(tr, text, innerLeft, textY, TEXT_COLOR, false);

            if (this.isFocused() && (System.currentTimeMillis() / 500) % 2 == 0) {
                int cursorPos = this.getCursor();
                int cursorX = innerLeft + tr.getWidth(text.substring(0, Math.min(cursorPos, text.length())));
                context.fill(cursorX, textY - 1, cursorX + 1, textY + 9, TEXT_COLOR);
            }
        }

        context.disableScissor();
    }
}