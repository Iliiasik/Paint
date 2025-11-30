package bookeditor.client.gui.widget.button;

import bookeditor.client.gui.widget.button.colorpicker.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class ColorPickerDropdown extends ClickableWidget {
    private final ColorPickerState state;
    private final ColorPickerRenderer renderer;
    private final ColorPickerInputHandler inputHandler;
    private final java.util.function.Consumer<Integer> onColorChange;

    public ColorPickerDropdown(int x, int y, java.util.function.Consumer<Integer> onColorChange, int initialArgb) {
        super(x, y, 20, 18, Text.literal(""));
        this.onColorChange = onColorChange;
        this.state = new ColorPickerState(initialArgb);
        this.renderer = new ColorPickerRenderer();
        this.inputHandler = new ColorPickerInputHandler(state, onColorChange);
    }

    public void setArgb(int argb) {
        state.updateFromArgb(argb);
    }

    public int getArgb() {
        return state.argb;
    }

    public boolean isExpanded() {
        return state.expanded;
    }

    @Override
    protected void renderButton(DrawContext ctx, int mouseX, int mouseY, float delta) {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - state.lastFrameTime) / 1000.0f;
        state.lastFrameTime = currentTime;

        state.updateHoverProgress(this.isHovered(), deltaTime);
        renderer.renderButton(ctx, getX(), getY(), width, height, state);
    }

    public void renderDropdown(DrawContext ctx, int mouseX, int mouseY) {
        if (!state.expanded) return;

        state.updateCursorBlink();
        int dropX = getX();
        int dropY = getY() + height + 2;
        renderer.renderDropdown(ctx, dropX, dropY, state, mouseX, mouseY);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (!state.expanded) {
            state.expanded = true;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!state.expanded) {
            if (isMouseOver(mouseX, mouseY)) {
                state.expanded = true;
                return true;
            }
            return false;
        }

        int dropX = getX();
        int dropY = getY() + height + 2;
        return inputHandler.handleMouseClick(mouseX, mouseY, dropX, dropY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!state.expanded) return false;

        int dropX = getX();
        int dropY = getY() + height + 2;
        return inputHandler.handleMouseDrag(mouseX, mouseY, dropX, dropY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        inputHandler.handleMouseRelease();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return inputHandler.handleKeyPress(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char chr, int modifiers) {
        return inputHandler.handleCharTyped(chr, modifiers);
    }

    @Override
    protected void appendClickableNarrations(net.minecraft.client.gui.screen.narration.NarrationMessageBuilder builder) {
        builder.put(net.minecraft.client.gui.screen.narration.NarrationPart.TITLE, Text.translatable("narrator.select.color"));
    }
}