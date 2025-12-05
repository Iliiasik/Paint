package bookeditor.client.gui.widget.zoom;

public class ZoomSliderState {
    public static final float MIN_ZOOM = 1.0f;
    public static final float MAX_ZOOM = 5.0f;
    public static final float DEFAULT_ZOOM = 1.0f;

    public float zoomLevel = DEFAULT_ZOOM;
    public boolean dragging = false;
    public float hoverProgress = 0.0f;
    public long lastFrameTime = System.currentTimeMillis();

    public void setZoom(float zoom) {
        this.zoomLevel = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));
    }

    public float getZoomPercent() {
        return zoomLevel * 100f;
    }

    public float getNormalizedPosition() {
        return (zoomLevel - MIN_ZOOM) / (MAX_ZOOM - MIN_ZOOM);
    }

    public void setFromNormalizedPosition(float normalized) {
        normalized = Math.max(0f, Math.min(1f, normalized));
        this.zoomLevel = MIN_ZOOM + normalized * (MAX_ZOOM - MIN_ZOOM);
    }

    public void updateHoverProgress(boolean isHovering, float deltaTime) {
        if (isHovering && hoverProgress < 1.0f) {
            hoverProgress = Math.min(1.0f, hoverProgress + deltaTime * 6.0f);
        } else if (!isHovering && hoverProgress > 0.0f) {
            hoverProgress = Math.max(0.0f, hoverProgress - deltaTime * 6.0f);
        }
    }

    public void reset() {
        this.zoomLevel = DEFAULT_ZOOM;
    }
}

