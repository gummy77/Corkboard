package gum.corkboard.main;

import gum.corkboard.main.registries.*;
import net.fabricmc.api.ModInitializer;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor;

public class CorkBoard implements ModInitializer {
    public static final String MODID = "corkboard";

    public void onInitialize () {
        ItemRegistry.registerItems();
        BlockRegistry.registerBlocks();
    }
}
