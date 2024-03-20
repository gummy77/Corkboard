package gum.corkboard.client.screen;

import gum.corkboard.main.block.Corkboard;
import gum.corkboard.main.registries.ScreenRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;

public class NoteScreenHandler extends ScreenHandler {
    private final Inventory noteInventory;
    public ItemStack stack;
    public NoteScreenHandler(int syncId, PlayerInventory inventory) {
        super(ScreenRegistry.NOTE_SCREEN_HANDLER, syncId);
        this.noteInventory = new SimpleInventory(1);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
