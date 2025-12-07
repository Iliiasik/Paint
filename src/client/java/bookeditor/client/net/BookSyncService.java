package bookeditor.client.net;

import bookeditor.data.BookData;
import bookeditor.data.NbtSizeUtils;
import bookeditor.net.BookNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Hand;
import net.minecraft.nbt.NbtCompound;

public final class BookSyncService {
    private BookSyncService() {}

    public static boolean canSend(BookData data) {
        if (data == null) return false;
        NbtCompound nbt = BookData.toNbt(data);
        int nbtSize = NbtSizeUtils.getNbtByteSize(nbt);
        int allowed = NbtSizeUtils.getAllowedMax();
        return nbtSize > 0 && nbtSize <= allowed;
    }

    public static int getCurrentSize(BookData data) {
        if (data == null) return 0;
        NbtCompound nbt = BookData.toNbt(data);
        return NbtSizeUtils.getNbtByteSize(nbt);
    }

    public static void sendUpdate(Hand hand, BookData data) {
        if (data == null) return;
        if (!canSend(data)) return;

        NbtCompound nbt = BookData.toNbt(data);

        try {
            ClientPlayNetworking.send(new BookNetworking.UpdateBookPayload(hand, nbt));
        } catch (Exception ignored) {
        }
    }
}