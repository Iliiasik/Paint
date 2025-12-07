package bookeditor.net;

import bookeditor.Bookeditor;
import bookeditor.data.BookData;
import bookeditor.data.NbtSizeUtils;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public final class BookNetworking {
    public static final Identifier UPDATE_BOOK_ID = Identifier.of(Bookeditor.MODID, "update_book");
    public static final Identifier UPDATE_BOOK_TOO_LARGE_ID = Identifier.of(Bookeditor.MODID, "update_book_too_large");

    public record UpdateBookPayload(Hand hand, NbtCompound nbt) implements CustomPayload {
        public static final Id<UpdateBookPayload> ID = new Id<>(UPDATE_BOOK_ID);
        public static final PacketCodec<RegistryByteBuf, UpdateBookPayload> CODEC = PacketCodec.of(
                (value, buf) -> {
                    buf.writeEnumConstant(value.hand);
                    buf.writeNbt(value.nbt);
                },
                buf -> new UpdateBookPayload(buf.readEnumConstant(Hand.class), buf.readNbt())
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record UpdateBookTooLargePayload(String message) implements CustomPayload {
        public static final Id<UpdateBookTooLargePayload> ID = new Id<>(UPDATE_BOOK_TOO_LARGE_ID);
        public static final PacketCodec<RegistryByteBuf, UpdateBookTooLargePayload> CODEC = PacketCodec.of(
                (value, buf) -> buf.writeString(value.message),
                buf -> new UpdateBookTooLargePayload(buf.readString(32767))
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    private BookNetworking() {}

    public static void registerServerReceivers() {
        PayloadTypeRegistry.playC2S().register(UpdateBookPayload.ID, UpdateBookPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(UpdateBookTooLargePayload.ID, UpdateBookTooLargePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(UpdateBookPayload.ID, (payload, context) -> {
            Hand hand = payload.hand();
            NbtCompound nbt = payload.nbt();
            ServerPlayerEntity player = context.player();

            if (nbt == null) {
                ServerPlayNetworking.send(player, new UpdateBookTooLargePayload("book_nbt_too_large"));
                return;
            }

            int nbtSize = NbtSizeUtils.getNbtByteSize(nbt);
            int allowed = NbtSizeUtils.getAllowedMax();
            if (nbtSize > allowed) {
                ServerPlayNetworking.send(player, new UpdateBookTooLargePayload("book_nbt_too_large"));
                return;
            }

            context.server().execute(() -> applyOnServer(player, hand, nbt));
        });
    }

    private static void applyOnServer(ServerPlayerEntity player, Hand hand, NbtCompound nbt) {
        if (nbt == null) return;
        ItemStack stack = player.getStackInHand(hand);
        if (!stack.isEmpty() && stack.getItem() == Bookeditor.CREATIVE_BOOK) {
            NbtCompound root = Bookeditor.getCustomData(stack);
            root.put(BookData.ROOT, nbt);
            Bookeditor.setCustomData(stack, root);
        }
    }
}