package bookeditor.client.gui.widget.editor;

import bookeditor.client.util.PlankTextureUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EditorRendererManager {
    private final EditorState state;

    public EditorRendererManager(EditorState state, EditorToolManager toolManager, EditorStyleManager styleManager) {
        this.state = state;
    }

    private Identifier getBackgroundTexture() {
        return PlankTextureUtil.getTextureForPlank(state.plankType);
    }
    public void renderButton(DrawContext ctx, int mouseX, int mouseY, float delta) {
        state.updateSmooth();
        EditorWidget widget = state.getWidget();
        renderFrame(ctx, widget);
        renderCanvas(ctx);
        enableScissor(ctx);
        if (state.textBoxCreationTool.isActive()) {
            state.textBoxCreationTool.updatePreview(mouseX, mouseY, state.contentScreenLeft(), state.contentScreenTop(), state.scale(), state.scrollY);
        }

        state.editorRenderer.render(ctx, state.page, state.mode, state.imageInteraction, state.textBoxInteraction, state.textBoxCaret,
                state.textRenderer, widget.isFocused(), state.editable, state.contentScreenLeft(), state.contentScreenTop(),
                state.canvasScreenTop(), state.scale(), state.scrollY, EditorState.LOGICAL_W, EditorState.LOGICAL_H);

        state.drawingTool.renderStrokes(ctx, state.page, state.contentScreenLeft(), state.contentScreenTop(), state.scale(), state.scrollY);

        state.imageInteraction.renderSelectionHandles(ctx);
        state.textBoxInteraction.renderSelectionHandles(ctx);

        if (state.textBoxCreationTool.isActive()) {
            state.textBoxCreationTool.renderPreview(ctx, state.contentScreenLeft(), state.contentScreenTop(), state.scale(), state.scrollY);
        }
        if (state.eraserTool.isActive()) {
            state.eraserTool.renderPreview(ctx, mouseX, mouseY, state.contentScreenLeft(), state.contentScreenTop(), state.scale(), state.scrollY);
        }
        ctx.disableScissor();

        String msg = state.getTransientMessage();
        if (msg != null) {
            int centerX = widget.getX() + widget.getWidth() / 2;
            int textY = widget.getY() + 6;
            int textW = state.textRenderer.getWidth(msg);
            ctx.drawText(state.textRenderer, Text.literal(msg), centerX - textW / 2, textY, 0xFF000000, false);
        }
    }
    private void renderFrame(DrawContext ctx, EditorWidget widget) {
        int frame = 0xFF8B8B8B;
        int x = widget.getX();
        int y = widget.getY();
        int w = widget.getWidth();
        int h = widget.getHeight();

        ctx.fill(x - 1, y - 1, x + w + 1, y + h + 1, frame);

        Identifier backgroundTexture = getBackgroundTexture();
        int textureSize = 16;
        int tilesX = (w + textureSize - 1) / textureSize;
        int tilesY = (h + textureSize - 1) / textureSize;

        for (int tileY = 0; tileY <= tilesY; tileY++) {
            for (int tileX = 0; tileX <= tilesX; tileX++) {
                int drawX = x + tileX * textureSize;
                int drawY = y + tileY * textureSize;
                int drawW = Math.min(textureSize, x + w - drawX);
                int drawH = Math.min(textureSize, y + h - drawY);

                if (drawW > 0 && drawH > 0) {
                    ctx.drawTexture(backgroundTexture, drawX, drawY, 0, 0, drawW, drawH, textureSize, textureSize);
                }
            }
        }
    }
    private void renderCanvas(DrawContext ctx) {
        int vLeft = state.canvasVisualLeft();
        int vTop = state.canvasVisualTop();
        int vWidth = state.canvasVisualWidth();
        int vHeight = state.canvasVisualHeight();

        ctx.enableScissor(vLeft, vTop, vLeft + vWidth, vTop + vHeight);

        int cLeft = state.canvasScreenLeft();
        int cTop = state.canvasScreenTop();
        int scaledW = (int) Math.floor(state.scale() * (EditorState.LOGICAL_W + EditorState.PAD_IN * 2));
        int scaledH = (int) Math.floor(state.scale() * (EditorState.LOGICAL_H + EditorState.PAD_IN * 2));
        int bg = state.page != null ? state.page.bgArgb : 0xFFF8F8F8;
        ctx.fill(cLeft, cTop, cLeft + scaledW, cTop + scaledH, bg);

        ctx.disableScissor();

        ctx.fill(vLeft - 1, vTop - 1, vLeft + vWidth + 1, vTop, 0x33000000);
        ctx.fill(vLeft - 1, vTop + vHeight, vLeft + vWidth + 1, vTop + vHeight + 1, 0x33000000);
        ctx.fill(vLeft - 1, vTop, vLeft, vTop + vHeight, 0x33000000);
        ctx.fill(vLeft + vWidth, vTop, vLeft + vWidth + 1, vTop + vHeight, 0x33000000);
    }
    private void enableScissor(DrawContext ctx) {
        int vLeft = state.canvasVisualLeft();
        int vTop = state.canvasVisualTop();
        int vWidth = state.canvasVisualWidth();
        int vHeight = state.canvasVisualHeight();
        ctx.enableScissor(vLeft, vTop, vLeft + vWidth, vTop + vHeight);
    }
}