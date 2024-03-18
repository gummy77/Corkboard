package gum.corkboard.main.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface CorkboardInventory extends Inventory {
    DefaultedList<ItemStack> getItems();
    @Override
    default int size() {
        return getItems().size();
    }
    @Override
    default boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    default boolean isFull() {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);
            if (stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    @Override
    default ItemStack getStack(int slot) {
        return getItems().get(slot);
    }
    @Override
    default ItemStack removeStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        markDirty();
        return result;
    }
    @Override
    default ItemStack removeStack(int slot) {
        this.markDirty();
        return Inventories.removeStack(getItems(), slot);
    }

    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > stack.getMaxCount()) {
            stack.setCount(stack.getMaxCount());
        }
        this.markDirty();
    }

    default int addStack(ItemStack stack) {
        for (int i = 0; i < size(); i++) {
            ItemStack curStack = getStack(i);
            if (curStack.isEmpty()) {
                setStack(i, stack);
                return i;
            }
        }
        return -1;
    }

//    @Override
//    default void markDirty() {
//        // Override if you want behavior.
//    }

    @Override
    default void clear() {
        getItems().clear();
    }
    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return false;
    }
}
