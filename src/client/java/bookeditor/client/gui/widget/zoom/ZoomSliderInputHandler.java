package bookeditor.client.gui.widget.zoom;

import java.util.function.Consumer;

public class ZoomSliderInputHandler {
    private final ZoomSliderState state;
    private final Consumer<Float> onZoomChange;

    public ZoomSliderInputHandler(ZoomSliderState state, Consumer<Float> onZoomChange) {
        this.state = state;
        this.onZoomChange = onZoomChange;
    }

    public boolean handleMouseClick(double mouseX, double mouseY, int x, int y, int width, int height) {
        int trackPadding = 20;
        int trackY = y + trackPadding;
        int trackHeight = height - trackPadding * 2 - 16;

        if (mouseX >= x && mouseX < x + width && mouseY >= trackY && mouseY < trackY + trackHeight) {
            state.dragging = true;
            updateZoomFromMouse(mouseY, trackY, trackHeight);
            return true;
        }
        return false;
    }

    public boolean handleMouseDrag(double mouseY, int y, int height) {
        if (!state.dragging) return false;

        int trackPadding = 20;
        int trackY = y + trackPadding;
        int trackHeight = height - trackPadding * 2 - 16;

        updateZoomFromMouse(mouseY, trackY, trackHeight);
        return true;
    }

    public void handleMouseRelease() {
        state.dragging = false;
    }

    public boolean handleMouseScroll(double amount, int x, int y, int width, int height, double mouseX, double mouseY) {
        if (mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
            float delta = (float)(amount * 0.1f);
            state.setZoom(state.zoomLevel + delta);
            onZoomChange.accept(state.zoomLevel);
            return true;
        }
        return false;
    }

    private void updateZoomFromMouse(double mouseY, int trackY, int trackHeight) {
        float normalized = 1f - (float)((mouseY - trackY) / (trackHeight - 8));
        normalized = Math.max(0f, Math.min(1f, normalized));
        state.setFromNormalizedPosition(normalized);
        onZoomChange.accept(state.zoomLevel);
    }
}

