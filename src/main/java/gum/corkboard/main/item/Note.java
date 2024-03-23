package gum.corkboard.main.item;

import gum.corkboard.client.screen.NoteScreenHandler;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class Note extends Item{
    public Note() {
        super(new FabricItemSettings());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            ItemStack stack = user.getStackInHand(hand);
            NamedScreenHandlerFactory screenHandlerFactory = createScreenHandlerFactory(stack);
            user.openHandledScreen(screenHandlerFactory);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    private NamedScreenHandlerFactory createScreenHandlerFactory(ItemStack stack) {
        return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> new NoteScreenHandler(syncId, inventory), stack.getName());
    }
}
