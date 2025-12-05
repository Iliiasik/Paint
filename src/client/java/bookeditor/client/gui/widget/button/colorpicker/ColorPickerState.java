package bookeditor.client.gui.widget.button.colorpicker;

public class ColorPickerState {
    public int argb;
    public float hue = 0f;
    public float saturation = 1f;
    public float brightness = 1f;
    public String hexInput = "";
    public boolean hexFieldFocused = false;
    public int cursorPos = 0;
    public long lastBlinkTime = 0;
    public boolean cursorVisible = true;
    public boolean draggingPalette = false;
    public boolean draggingHue = false;
    public boolean expanded = false;
    public float hoverProgress = 0.0f;
    public long lastFrameTime = System.currentTimeMillis();

    public ColorPickerState(int initialArgb) {
        this.argb = initialArgb;
        updateFromArgb(initialArgb);
    }

    public void updateFromArgb(int argb) {
        this.argb = argb;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        float[] hsb = ColorConverter.rgbToHsb(r, g, b);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
        this.hexInput = String.format("%02X%02X%02X", r, g, b);
    }

    public void updateFromHSB() {
        this.argb = ColorConverter.hsbToArgb(hue, saturation, brightness);
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        this.hexInput = String.format("%02X%02X%02X", r, g, b);
    }

    public void updateHoverProgress(boolean isHovering, float deltaTime) {
        if (isHovering && hoverProgress < 1.0f) {
            hoverProgress = Math.min(1.0f, hoverProgress + deltaTime * 10.0f);
        } else if (!isHovering && hoverProgress > 0.0f) {
            hoverProgress = Math.max(0.0f, hoverProgress - deltaTime * 10.0f);
        }
    }

    public void updateCursorBlink() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBlinkTime > 500) {
            cursorVisible = !cursorVisible;
            lastBlinkTime = currentTime;
        }
    }
}
