package bookeditor.client.gui.widget.editor;

import bookeditor.client.editor.interaction.ImageInteraction;
import bookeditor.client.editor.input.EditorInputHandler;
import bookeditor.client.editor.input.EditorMouseHandler;
import bookeditor.client.editor.mode.EditorMode;
import bookeditor.client.editor.render.EditorRenderer;
import bookeditor.client.editor.textbox.TextBoxCaret;
import bookeditor.client.editor.tools.TextBoxCreationTool;
import bookeditor.client.editor.textbox.TextBoxEditOps;
import bookeditor.client.editor.interaction.TextBoxInteraction;
import bookeditor.client.editor.render.TextBoxRenderer;
import bookeditor.client.editor.tools.AdvancedDrawingTool;
import bookeditor.client.editor.tools.EraserTool;
import bookeditor.data.BookData;
import net.minecraft.client.font.TextRenderer;
import java.util.function.Consumer;

public class EditorState {
    public static final int PAD_OUT = 8;
    public static final int PAD_IN = 8;
    public static final int LOGICAL_W = 960;
    public static final int LOGICAL_H = 600;
    public static final int MAX_TEXTBOX_CHARS = 5000;
    public final TextRenderer textRenderer;
    public final Consumer<String> onImageUrlSeen;
    public final Runnable onDirty;
    public boolean editable;
    public BookData.Page page;
    public EditorMode mode = EditorMode.OBJECT_MODE;
    public boolean bold;
    public boolean italic;
    public boolean underline;
    public int argb = 0xFF202020;
    public float size = 1.0f;
    public int textBoxBgColor = 0x00FFFFFF;
    public String plankType = "minecraft:dark_oak_planks";
    public int scrollY = 0;
    public String clipboard = "";
    public float userZoom = 1.0f;
    public int panOffsetX = 0;
    public int panOffsetY = 0;
    public boolean isPanning = false;
    public int panStartMouseX = 0;
    public int panStartMouseY = 0;
    public int panStartOffsetX = 0;
    public int panStartOffsetY = 0;

    private float smoothZoom = 1.0f;
    private float smoothPanX = 0.0f;
    private float smoothPanY = 0.0f;
    private static final float SMOOTH_FACTOR = 0.15f;

    private int cachedWidgetX = -1;
    private int cachedWidgetY = -1;
    private int cachedWidgetW = -1;
    private int cachedWidgetH = -1;
    private double cachedBaseScale = -1;
    private int cachedVisualW = -1;
    private int cachedVisualH = -1;
    private int cachedVisualLeft = -1;
    private int cachedVisualTop = -1;
    public final TextBoxCaret textBoxCaret = new TextBoxCaret();
    public final ImageInteraction imageInteraction = new ImageInteraction();
    public final TextBoxInteraction textBoxInteraction = new TextBoxInteraction();
    public final TextBoxCreationTool textBoxCreationTool = new TextBoxCreationTool();
    public final EraserTool eraserTool = new EraserTool();
    public final AdvancedDrawingTool drawingTool = new AdvancedDrawingTool();
    public final TextBoxRenderer textBoxRenderer = new TextBoxRenderer();
    public final TextBoxEditOps textBoxOps = new TextBoxEditOps();
    public final EditorRenderer editorRenderer = new EditorRenderer();
    public final EditorInputHandler inputHandler = new EditorInputHandler(this);
    public final EditorMouseHandler mouseHandler = new EditorMouseHandler();
    private final EditorWidget widget;

    private String transientMessage = null;
    private long transientExpiryMillis = 0L;

    public EditorState(TextRenderer textRenderer, boolean editable, Consumer<String> onImageUrlSeen, Runnable onDirty, EditorWidget widget) {
        this.textRenderer = textRenderer;
        this.editable = editable;
        this.onImageUrlSeen = onImageUrlSeen;
        this.onDirty = onDirty;
        this.widget = widget;
    }

    public void setHeight(int height) {
        widget.setWidgetHeight(Math.max(40, height));
    }
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    public void setContent(BookData.Page page) {
        this.page = page;
        mode = EditorMode.OBJECT_MODE;
        textBoxCaret.reset();
        textBoxInteraction.clearSelection();
        imageInteraction.clearSelection();
        textBoxCreationTool.deactivate();
        drawingTool.setActive(false);
        eraserTool.setActive(false);
        scrollY = 0;
    }


    private void updateCache() {
        int wx = widget.getX();
        int wy = widget.getY();
        int ww = widget.getWidth();
        int wh = widget.getHeight();
        if (wx != cachedWidgetX || wy != cachedWidgetY || ww != cachedWidgetW || wh != cachedWidgetH) {
            cachedWidgetX = wx;
            cachedWidgetY = wy;
            cachedWidgetW = ww;
            cachedWidgetH = wh;
            int iw = Math.max(0, ww - PAD_OUT * 2);
            int ih = Math.max(0, wh - PAD_OUT * 2);
            double sw = (double) iw / (LOGICAL_W + PAD_IN * 2.0);
            double sh = (double) ih / (LOGICAL_H + PAD_IN * 2.0);
            cachedBaseScale = Math.max(0.1, Math.min(sw, sh));
            cachedVisualW = (int) Math.floor(cachedBaseScale * (LOGICAL_W + PAD_IN * 2));
            cachedVisualH = (int) Math.floor(cachedBaseScale * (LOGICAL_H + PAD_IN * 2));
            cachedVisualLeft = (wx + PAD_OUT) + (iw - cachedVisualW) / 2;
            cachedVisualTop = (wy + PAD_OUT) + (ih - cachedVisualH) / 2;
        }
    }

    public void updateSmooth() {
        smoothZoom += (userZoom - smoothZoom) * SMOOTH_FACTOR;
        smoothPanX += (panOffsetX - smoothPanX) * SMOOTH_FACTOR;
        smoothPanY += (panOffsetY - smoothPanY) * SMOOTH_FACTOR;
        if (Math.abs(smoothZoom - userZoom) < 0.001f) smoothZoom = userZoom;
        if (Math.abs(smoothPanX - panOffsetX) < 0.5f) smoothPanX = panOffsetX;
        if (Math.abs(smoothPanY - panOffsetY) < 0.5f) smoothPanY = panOffsetY;
    }

    public double baseScale() {
        updateCache();
        return cachedBaseScale;
    }
    public double scale() {
        return baseScale() * smoothZoom;
    }
    public int canvasVisualWidth() {
        updateCache();
        return cachedVisualW;
    }
    public int canvasVisualHeight() {
        updateCache();
        return cachedVisualH;
    }
    public int canvasVisualLeft() {
        updateCache();
        return cachedVisualLeft;
    }
    public int canvasVisualTop() {
        updateCache();
        return cachedVisualTop;
    }
    public int canvasScreenLeft() {
        double sc = scale();
        int zoomedW = (int) Math.floor(sc * (LOGICAL_W + PAD_IN * 2));
        int centerOffsetX = (canvasVisualWidth() - zoomedW) / 2;
        return canvasVisualLeft() + centerOffsetX + (int) smoothPanX;
    }
    public int canvasScreenTop() {
        double sc = scale();
        int zoomedH = (int) Math.floor(sc * (LOGICAL_H + PAD_IN * 2));
        int centerOffsetY = (canvasVisualHeight() - zoomedH) / 2;
        return canvasVisualTop() + centerOffsetY + (int) smoothPanY;
    }
    public int contentScreenLeft() {
        return canvasScreenLeft() + (int) Math.round(scale() * PAD_IN);
    }
    public int contentScreenTop() {
        return canvasScreenTop() + (int) Math.round(scale() * PAD_IN);
    }
    public void setUserZoom(float zoom) {
        this.userZoom = Math.max(1.0f, Math.min(5.0f, zoom));
        clampPanOffset();
    }
    public void clampPanOffset() {
        if (userZoom <= 1.0f) {
            panOffsetX = 0;
            panOffsetY = 0;
            return;
        }
        int zoomedW = (int) Math.floor(scale() * (LOGICAL_W + PAD_IN * 2));
        int zoomedH = (int) Math.floor(scale() * (LOGICAL_H + PAD_IN * 2));
        int maxPanX = Math.max(0, (zoomedW - canvasVisualWidth()) / 2);
        int maxPanY = Math.max(0, (zoomedH - canvasVisualHeight()) / 2);
        panOffsetX = Math.max(-maxPanX, Math.min(maxPanX, panOffsetX));
        panOffsetY = Math.max(-maxPanY, Math.min(maxPanY, panOffsetY));
    }
    public boolean canPan() {
        return userZoom > 1.0f && !drawingTool.isActive() && !eraserTool.isActive() && !textBoxCreationTool.isActive();
    }
    public EditorWidget getWidget() {
        return widget;
    }

    public void setPlankType(String plankType) {
        this.plankType = plankType != null ? plankType : "minecraft:dark_oak_planks";
    }

    public void showTransientMessage(String msg, long durationMillis) {
        this.transientMessage = msg;
        this.transientExpiryMillis = System.currentTimeMillis() + durationMillis;
    }

    public String getTransientMessage() {
        if (transientMessage == null) return null;
        if (System.currentTimeMillis() > transientExpiryMillis) {
            transientMessage = null;
            return null;
        }
        return transientMessage;
    }
}