package bookeditor.client.gui.widget.button.colorpicker;

public class ColorPickerConstants {
    public static final int GAP = 2;

    public static final int HEX_FIELD_WIDTH = 80;
    public static final int HEX_FIELD_HEIGHT = 18;
    public static final int PREVIEW_WIDTH = 42;

    public static final int PALETTE_WIDTH = HEX_FIELD_WIDTH + GAP + PREVIEW_WIDTH;
    public static final int PALETTE_HEIGHT = 80;
    public static final int HUE_BAR_WIDTH = 12;
    public static final int HUE_BAR_HEIGHT = PALETTE_HEIGHT;

    public static final int BORDER_NORMAL = 0xFF8B8B8B;
    public static final int BORDER_FOCUSED = 0xFF373737;
    public static final int HOVER_BORDER = 0xFFE8B84C;
    public static final int DARK_BORDER = 0xFF373737;
    public static final int BG_COLOR = 0xFFF0E8DC;
    public static final int INPUT_BG = 0xFFF0E8DC;

    public static int calculateTotalWidth() {
        return GAP + PALETTE_WIDTH + GAP + HUE_BAR_WIDTH + GAP;
    }

    public static int calculateTotalHeight() {
        return GAP + PALETTE_HEIGHT + GAP + HEX_FIELD_HEIGHT + GAP;
    }
}
