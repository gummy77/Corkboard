package gum.corkboard.main;

import gum.corkboard.main.registries.*;
import net.fabricmc.api.ModInitializer;

public class CorkBoard implements ModInitializer {
    public static final String MODID = "corkboard";

    public void onInitialize () {
        ItemRegistry.registerItems();
        BlockRegistry.registerBlocks();
    }
}

/*
TODO:
- Clean up Code
- Add connected Textures for Cork board
- add text functionality for notes
- add crafting recipes
- add sounds
- fix corkboard edge textures
 */
