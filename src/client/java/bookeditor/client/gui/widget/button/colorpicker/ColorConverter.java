package bookeditor.client.gui.widget.button.colorpicker;

public class ColorConverter {
    public static int hsbToArgb(float h, float s, float b) {
        int rgb = java.awt.Color.HSBtoRGB(h, s, b);
        return 0xFF000000 | (rgb & 0xFFFFFF);
    }

    public static float[] rgbToHsb(int r, int g, int b) {
        float[] hsb = new float[3];
        java.awt.Color.RGBtoHSB(r, g, b, hsb);
        return hsb;
    }

    public static int interpolateColor(int c1, int c2, float progress) {
        int a1 = (c1 >> 24) & 0xFF;
        int r1 = (c1 >> 16) & 0xFF;
        int g1 = (c1 >> 8) & 0xFF;
        int b1 = c1 & 0xFF;
        int a2 = (c2 >> 24) & 0xFF;
        int r2 = (c2 >> 16) & 0xFF;
        int g2 = (c2 >> 8) & 0xFF;
        int b2 = c2 & 0xFF;
        int a = (int) (a1 + (a2 - a1) * progress);
        int r = (int) (r1 + (r2 - r1) * progress);
        int g = (int) (g1 + (g2 - g1) * progress);
        int b = (int) (b1 + (b2 - b1) * progress);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int addAlpha(int color, float alpha) {
        int a = (int) (255 * alpha);
        return (a << 24) | (color & 0xFFFFFF);
    }
}
