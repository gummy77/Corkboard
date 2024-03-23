package gum.corkboard.main.registries;

import gum.corkboard.main.CorkBoard;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class PacketRegistry {
    public static Identifier SET_NOTE_NBT_PACKET_ID = new Identifier(CorkBoard.MODID, "set_note_nbt_packet");

    public static void registerPackets(){
        ServerPlayNetworking.registerGlobalReceiver(SET_NOTE_NBT_PACKET_ID, (client, player, handler, buf, responseSender) -> client.execute(() -> {

            ItemStack stack = player.getStackInHand(player.getActiveHand());
            String messagesString = buf.readString();
            String[] messages = messagesString.split("\n");

            for(int i = 0; i < messages.length; i++){
                if(messages[i] == ""){
                    messages[i] = " ";
                }
            }

            //System.out.println("Recieved Update Request:\n"+stack.getName()+"\n"+messages.length+"\n"+messagesString);

            NbtCompound nbt = stack.getOrCreateSubNbt("text");
            //if (nbt == null) nbt = new NbtCompound();
            if(messages.length >= 1) nbt.putString("0", messages[0]);
            if(messages.length >= 2) nbt.putString("1", messages[1]);
            if(messages.length >= 3) nbt.putString("2", messages[2]);
            if(messages.length >= 4) nbt.putString("3", messages[3]);

            //System.out.println("Finished Editing Request: \n"+stack.getName() + "\n" + stack.getNbt());
        }));
    }
}
