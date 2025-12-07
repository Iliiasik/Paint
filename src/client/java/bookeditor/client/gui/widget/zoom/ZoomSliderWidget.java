package bookeditor.client.gui.widget.zoom;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ZoomSliderWidget extends ClickableWidget {
    private final ZoomSliderState state;
    private final ZoomSliderRenderer renderer;
    private final ZoomSliderInputHandler inputHandler;

    public ZoomSliderWidget(int x, int y, int width, int height, Consumer<Float> onZoomChange) {
        super(x, y, width, height, Text.literal("Zoom"));
        this.state = new ZoomSliderState();
        this.renderer = new ZoomSliderRenderer();
        this.inputHandler = new ZoomSliderInputHandler(state, onZoomChange);
    }

    @Override
    public void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - state.lastFrameTime) / 1000.0f;
        state.lastFrameTime = currentTime;

        state.updateSmooth();
        state.updateHoverProgress(this.isHovered(), deltaTime);
        renderer.render(ctx, getX(), getY(), width, height, state);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOver(mouseX, mouseY)) {
            return inputHandler.handleMouseClick(mouseX, mouseY, getX(), getY(), width, height);
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return inputHandler.handleMouseDrag(mouseY, getY(), height);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        inputHandler.handleMouseRelease();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return inputHandler.handleMouseScroll(verticalAmount, getX(), getY(), width, height, mouseX, mouseY);
    }


    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, Text.translatable("narrator.zoom.slider"));
    }
}

