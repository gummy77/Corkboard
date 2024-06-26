package gum.corkboard.main.registries;

import gum.corkboard.main.CorkBoard;
import gum.corkboard.main.block.Corkboard;
import gum.corkboard.main.block.CorkboardEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlockRegistry {

    public static Block CORKBOARD;

    public static BlockEntityType<CorkboardEntity> CORKBOARD_ENTITY;

    public static void registerBlocks () {

    }

    public static Block register (String path, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(CorkBoard.MODID, path), block);
    }
    public static BlockEntityType registerEntity (String path, BlockEntityType type) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CorkBoard.MODID, path), type);
    }

    static {
        CORKBOARD = register("corkboard", new Corkboard(FabricBlockSettings.create()));

        CORKBOARD_ENTITY = registerEntity("corkboard_entity", FabricBlockEntityTypeBuilder.create(CorkboardEntity::new, CORKBOARD).build());
    }
}
