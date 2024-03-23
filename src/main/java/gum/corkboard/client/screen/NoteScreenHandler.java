package gum.corkboard.client.screen;

import gum.corkboard.main.registries.ScreenRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.*;

public class NoteScreenHandler extends ScreenHandler {
    public NoteScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ScreenRegistry.NOTE_SCREEN_HANDLER, syncId);
    }

    public boolean writeItemStackText(ItemStack stack, String[] text){
        if(text != null && stack != null) {
            NbtCompound nbt = stack.getOrCreateSubNbt("text");
            for(int i = 0; i < text.length; i++){
                if(text[i] == ""){
                    text[i] = " ";
                }
            }

            nbt.putString("0", text[0]);
            nbt.putString("1", text[1]);
            nbt.putString("2", text[2]);
            nbt.putString("3", text[3]);
            return true;

        } else {
            return false;
        }
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
