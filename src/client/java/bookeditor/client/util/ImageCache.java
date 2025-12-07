package bookeditor.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ImageCache {
    private static final Map<String, Identifier> CACHE = new ConcurrentHashMap<>();
    private static final Map<String, TextureState> STATES = new ConcurrentHashMap<>();
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "BookEditor-ImageLoader");
        t.setDaemon(true);
        return t;
    });

    private enum TextureState {
        LOADING, LOADED, FAILED
    }

    public static Identifier getTexture(String url) {
        if (url == null || url.isEmpty()) return null;
        TextureState state = STATES.get(url);
        if (state != TextureState.LOADED) return null;
        return CACHE.get(url);
    }

    public static boolean isLoading(String url) {
        return STATES.get(url) == TextureState.LOADING;
    }

    public static void requestTexture(String url) {
        if (url == null || url.isEmpty()) return;
        if (STATES.containsKey(url)) return;

        STATES.put(url, TextureState.LOADING);
        EXECUTOR.submit(() -> downloadTexture(url));
    }

    private static void downloadTexture(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Minecraft BookEditor");
            connection.setRequestProperty("Accept", "image/*");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setInstanceFollowRedirects(true);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                STATES.put(url, TextureState.FAILED);
                return;
            }

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (InputStream in = connection.getInputStream()) {
                byte[] data = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(data)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }
            }

            byte[] imageBytes = buffer.toByteArray();
            if (imageBytes.length == 0) {
                STATES.put(url, TextureState.FAILED);
                return;
            }

            byte[] pngBytes = convertToPng(imageBytes);
            if (pngBytes == null) {
                STATES.put(url, TextureState.FAILED);
                return;
            }

            NativeImage img = NativeImage.read(new ByteArrayInputStream(pngBytes));
            NativeImageBackedTexture texture = new NativeImageBackedTexture(img);

            String safePath = "dynamic/book/" + Integer.toHexString(url.hashCode());
            Identifier id = Identifier.of("bookeditor", safePath);

            MinecraftClient.getInstance().execute(() -> {
                try {
                    TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
                    textureManager.registerTexture(id, texture);
                    CACHE.put(url, id);
                    STATES.put(url, TextureState.LOADED);
                } catch (Exception e) {
                    STATES.put(url, TextureState.FAILED);
                }
            });
        } catch (Exception ex) {
            STATES.put(url, TextureState.FAILED);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static byte[] convertToPng(byte[] imageBytes) {
        try {
            if (isPng(imageBytes)) {
                return imageBytes;
            }

            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (bufferedImage == null) {
                return null;
            }

            BufferedImage rgbaImage = new BufferedImage(
                bufferedImage.getWidth(),
                bufferedImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB
            );
            rgbaImage.getGraphics().drawImage(bufferedImage, 0, 0, null);

            ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
            ImageIO.write(rgbaImage, "PNG", pngOutput);
            return pngOutput.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isPng(byte[] data) {
        if (data.length < 8) return false;
        return data[0] == (byte) 0x89 &&
               data[1] == (byte) 0x50 &&
               data[2] == (byte) 0x4E &&
               data[3] == (byte) 0x47 &&
               data[4] == (byte) 0x0D &&
               data[5] == (byte) 0x0A &&
               data[6] == (byte) 0x1A &&
               data[7] == (byte) 0x0A;
    }

    public static void clearCache() {
        CACHE.clear();
        STATES.clear();
    }

    private ImageCache() {}
}