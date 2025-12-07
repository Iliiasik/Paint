package bookeditor.data;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;

public final class NbtSizeUtils {
    private NbtSizeUtils() {}

    public static final int CLIENT_SAFE_NBT_MARGIN = 64;
    public static final int MAX_NBT_SIZE = 131072;

    public static int getAllowedMax() {
        return MAX_NBT_SIZE - CLIENT_SAFE_NBT_MARGIN;
    }

    public static int getNbtByteSize(NbtCompound compound) {
        if (compound == null) return 1;
        try {
            PacketByteBuf tmp = new PacketByteBuf(Unpooled.buffer());
            tmp.writeNbt(compound);
            int size = tmp.readableBytes();
            tmp.release();
            return size;
        } catch (RuntimeException e) {
            return -1;
        }
    }

    public static int measureNbtInPacket(Hand hand, NbtCompound compound) {
        if (compound == null) return 1;
        try {
            PacketByteBuf tmp = new PacketByteBuf(Unpooled.buffer());
            tmp.writeEnumConstant(hand);
            int afterEnum = tmp.readableBytes();
            tmp.writeNbt(compound);
            int afterNbt = tmp.readableBytes();
            tmp.release();
            return afterNbt - afterEnum;
        } catch (RuntimeException e) {
            return getNbtByteSize(compound);
        }
    }
}
