package bookeditor.client.util;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class PlankTextureUtil {

    private static final Map<String, Identifier> PLANK_TEXTURES = new HashMap<>();

    static {
        registerPlank("minecraft:oak_planks", "minecraft", "textures/block/oak_planks.png");
        registerPlank("minecraft:spruce_planks", "minecraft", "textures/block/spruce_planks.png");
        registerPlank("minecraft:birch_planks", "minecraft", "textures/block/birch_planks.png");
        registerPlank("minecraft:jungle_planks", "minecraft", "textures/block/jungle_planks.png");
        registerPlank("minecraft:acacia_planks", "minecraft", "textures/block/acacia_planks.png");
        registerPlank("minecraft:dark_oak_planks", "minecraft", "textures/block/dark_oak_planks.png");
        registerPlank("minecraft:mangrove_planks", "minecraft", "textures/block/mangrove_planks.png");
        registerPlank("minecraft:cherry_planks", "minecraft", "textures/block/cherry_planks.png");
        registerPlank("minecraft:bamboo_planks", "minecraft", "textures/block/bamboo_planks.png");
        registerPlank("minecraft:crimson_planks", "minecraft", "textures/block/crimson_planks.png");
        registerPlank("minecraft:warped_planks", "minecraft", "textures/block/warped_planks.png");
    }

    private static void registerPlank(String plankId, String namespace, String texturePath) {
        PLANK_TEXTURES.put(plankId, Identifier.of(namespace, texturePath));
    }

    public static Identifier getTextureForPlank(String plankType) {
        if (plankType == null || plankType.isEmpty()) {
            return getDefaultTexture();
        }

        Identifier texture = PLANK_TEXTURES.get(plankType);
        if (texture != null) {
            return texture;
        }

        try {
            Identifier plankId = Identifier.tryParse(plankType);
            if (plankId != null) {
                return Identifier.of(plankId.getNamespace(), "textures/block/" + plankId.getPath() + ".png");
            }
        } catch (Exception ignored) {
        }

        return getDefaultTexture();
    }

    public static Identifier getDefaultTexture() {
        return Identifier.of("minecraft", "textures/block/dark_oak_planks.png");
    }
}

