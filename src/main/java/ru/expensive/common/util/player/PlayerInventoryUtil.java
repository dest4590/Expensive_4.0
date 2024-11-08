package ru.expensive.common.util.player;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import ru.expensive.common.QuickImports;

import java.util.function.Predicate;

public class PlayerInventoryUtil implements QuickImports {
    public static PlayerInventoryUtil INSTANCE = new PlayerInventoryUtil();

    public static Integer findHotbarSlot(Predicate<ItemStack> predicate) {
        for (int i = 0; i < 9; i++) {
            if (predicate.test(mc.player.getInventory().getStack(i))) {
                return i;
            }
        }
        return null;
    }

    public Integer findHotbarSlot(Item item) {
        return findHotbarSlot(stack -> stack.getItem().equals(item));
    }
}
