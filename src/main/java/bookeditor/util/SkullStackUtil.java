package bookeditor.util;

import bookeditor.Bookeditor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public final class SkullStackUtil {
    public static ItemStack playerHeadStack(String name, UUID uuid) {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        NbtCompound root = Bookeditor.getCustomData(stack);
        NbtCompound owner = new NbtCompound();
        if (uuid != null) owner.putUuid("Id", uuid);
        if (name != null) owner.putString("Name", name);
        root.put("SkullOwner", owner);
        Bookeditor.setCustomData(stack, root);
        return stack;
    }

    private SkullStackUtil(){}
}