package gum.corkboard.main;

import gum.corkboard.main.registries.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.Entity;

public class CorkBoard implements ModInitializer {
    public static final String MODID = "corkboard";

    public void onInitialize () {
        EntityRegistry.registerEntites();
        PacketRegistry.registerPackets();
        ScreenRegistry.registerScreens();
        ItemRegistry.registerItems();
        BlockRegistry.registerBlocks();

    }
}

/*
TODO:
- add crafting recipes
- add sounds
 */
