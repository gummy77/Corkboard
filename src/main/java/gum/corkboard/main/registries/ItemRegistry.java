package gum.corkboard.main.registries;

import gum.corkboard.main.CorkBoard;
import gum.corkboard.main.item.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static gum.corkboard.main.registries.BlockRegistry.*;

public class ItemRegistry {
    public static Item NOTE;
    public static Item CORKBOARD_ITEM;

    //public static final ItemGroup MODGROUP;

    public static void registerItems () {
        //Registry.register(Registry.ITEM_GROUP, new Identifier(CorkBoard.MODID, "corkboardgroup"), MODGROUP);
    }

    public static Item register (String path, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(CorkBoard.MODID, path), item);
    }

    static {
        NOTE = register("note", new Note());
        CORKBOARD_ITEM = register("corkboard_item", new BlockItem(CORKBOARD, new FabricItemSettings()));

//        MODGROUP = FabricItemGroup.builder()
//                .displayName(Text.translatable("itemGroup.corkboard.corkboardgroup"))
//                .icon(() -> new ItemStack(CORKBOARD_ITEM))
//                .entries(((displayContext, entries) -> {
//                    entries.add(NOTE);
//                    entries.add(CORKBOARD_ITEM);
//                }))
//                .build();
    }

}
