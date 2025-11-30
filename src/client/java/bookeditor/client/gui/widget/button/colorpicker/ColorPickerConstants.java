package bookeditor.client.gui.widget.button.colorpicker;

public class ColorPickerConstants {
    public static final int[] PRESET_COLORS = {
            0xFF000000, 0xFF404040, 0xFF808080, 0xFFC0C0C0, 0xFFFFFFFF,
            0xFFFF0000, 0xFFFF8000, 0xFFFFFF00, 0xFF80FF00, 0xFF00FF00,
            0xFF00FF80, 0xFF00FFFF, 0xFF0080FF, 0xFF0000FF, 0xFF8000FF,
            0xFFFF00FF, 0xFFFF0080, 0xFF800000, 0xFF808000, 0xFF008000,
            0xFF008080, 0xFF000080, 0xFF800080, 0xFF8B4513, 0xFFFFA500
    };

    public static final int COLOR_SIZE = 14;
    public static final int GAP = 2;
    public static final int COLS = 5;

    public static final int PALETTE_WIDTH = 100;
    public static final int PALETTE_HEIGHT = 80;
    public static final int HUE_BAR_WIDTH = 12;
    public static final int HUE_BAR_HEIGHT = PALETTE_HEIGHT;

    public static final int HEX_FIELD_WIDTH = 65;
    public static final int HEX_FIELD_HEIGHT = 18;
    public static final int PREVIEW_WIDTH = 42;

    public static final int NORMAL_BORDER = 0xFF8B8B8B;
    public static final int HOVER_BORDER = 0xFFE8B84C;
    public static final int DARK_BORDER = 0xFF373737;
    public static final int BG_COLOR = 0xFFF0E8DC;

    public static int calculateTotalWidth() {
        return GAP + PALETTE_WIDTH + GAP + HUE_BAR_WIDTH + GAP;
    }

    public static int calculateTotalHeight() {
        int presetRows = (PRESET_COLORS.length + COLS - 1) / COLS;
        int presetHeight = presetRows * COLOR_SIZE + (presetRows + 1) * GAP;
        return GAP + PALETTE_HEIGHT + GAP + HEX_FIELD_HEIGHT + GAP + presetHeight + GAP;
    }
}
