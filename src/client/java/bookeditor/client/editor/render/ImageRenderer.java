package bookeditor.client.editor.render;

import bookeditor.client.editor.interaction.ImageInteraction;
import bookeditor.client.util.ImageCache;
import bookeditor.data.BookData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public class ImageRenderer {

    public void render(DrawContext ctx,
                       BookData.Page page,
                       ImageInteraction imageInteraction,
                       int startScreenX,
                       int startScreenY,
                       int canvasScreenTop,
                       double scale,
                       int logicalW,
                       int logicalH) {
        if (page == null) {
            return;
        }

        for (int i = 0; i < page.nodes.size(); i++) {
            BookData.Node n = page.nodes.get(i);
            if (n instanceof BookData.ImageNode img) {
                int drawW = Math.max(8, img.w);
                int drawH = Math.max(8, img.h);
                int imgX = startScreenX + (int)Math.round(scale * img.x);
                int imgY = startScreenY + (int)Math.round(scale * img.y);

                Identifier tex = ImageCache.getTexture(img.url);
                int sw = Math.max(1, (int)Math.round(scale * drawW));
                int sh = Math.max(1, (int)Math.round(scale * drawH));

                if (imgY < (canvasScreenTop + (int)Math.floor(scale * (logicalH + 8*2))) && imgY + sh > canvasScreenTop) {
                    if (tex != null) {
                        AbstractTexture abstractTexture = net.minecraft.client.MinecraftClient.getInstance().getTextureManager().getTexture(tex);
                        if (abstractTexture instanceof NativeImageBackedTexture nativeTexture && nativeTexture.getImage() != null) {
                            int texW = nativeTexture.getImage().getWidth();
                            int texH = nativeTexture.getImage().getHeight();
                            ctx.drawTexture(tex, imgX, imgY, 0, 0, sw, sh, sw, sh);
                        } else {
                            ctx.drawTexture(tex, imgX, imgY, 0, 0, sw, sh, sw, sh);
                        }
                    } else {
                        boolean loading = ImageCache.isLoading(img.url);
                        ctx.fill(imgX, imgY, imgX + sw, imgY + sh, loading ? 0xFFEEEEEE : 0xFFF6EEE3);
                        String text = loading ? "..." : "X";
                        int textColor = loading ? 0xFF888888 : 0xFFCC0000;
                        ctx.drawCenteredTextWithShadow(
                            net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                            text,
                            imgX + sw / 2,
                            imgY + sh / 2 - 4,
                            textColor
                        );
                    }
                }
                imageInteraction.addImageRect(imgX, imgY, sw, sh, i);
            }
        }
    }
}