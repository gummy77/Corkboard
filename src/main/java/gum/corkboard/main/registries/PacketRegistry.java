package gum.corkboard.main.registries;

import gum.corkboard.main.CorkBoard;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class PacketRegistry {
    public static Identifier SET_NOTE_NBT_PACKET_ID = new Identifier(CorkBoard.MODID, "set_note_nbt_packet");

    public static void registerPackets(){
        ServerPlayNetworking.registerGlobalReceiver(SET_NOTE_NBT_PACKET_ID, (client, player, handler, buf, responseSender) -> {
            client.execute(() -> {
                ItemStack stack = player.getStackInHand(player.getActiveHand());
                String[] messages = buf.readString().split("~\n");

                NbtCompound nbt = stack.getOrCreateSubNbt("text");
                if (nbt == null) nbt = new NbtCompound();
                nbt.putString("0", messages[0]);
                nbt.putString("1", messages[1]);
                nbt.putString("2", messages[2]);
                nbt.putString("3", messages[3]);
                stack.setNbt(nbt);
            });
        });
    }
}
