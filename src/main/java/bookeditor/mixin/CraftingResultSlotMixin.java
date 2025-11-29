package bookeditor.mixin;

import bookeditor.Bookeditor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingResultSlot.class)
public abstract class CraftingResultSlotMixin {

    @Shadow @Final private RecipeInputInventory input;

    @Inject(method = "onTakeItem", at = @At("HEAD"))
    private void onTakeCreativeBook(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (!stack.isOf(Bookeditor.CREATIVE_BOOK)) {
            return;
        }

        String plankType = findPlankInCrafting();
        if (plankType != null) {
            NbtCompound root = stack.getOrCreateNbt();
            NbtCompound bookData;
            if (root.contains("CreativeBook", NbtElement.COMPOUND_TYPE)) {
                bookData = root.getCompound("CreativeBook");
            } else {
                bookData = new NbtCompound();
            }
            bookData.putString("plankType", plankType);
            root.put("CreativeBook", bookData);
            stack.setNbt(root);
        }
    }

    @Unique
    private String findPlankInCrafting() {
        for (int i = 0; i < input.size(); i++) {
            ItemStack slotStack = input.getStack(i);
            if (!slotStack.isEmpty() && slotStack.isIn(ItemTags.PLANKS)) {
                Item item = slotStack.getItem();
                Identifier id = Registries.ITEM.getId(item);
                return id.toString();
            }
        }
        return null;
    }
}

