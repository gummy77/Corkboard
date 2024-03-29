package gum.corkboard.client;

import gum.corkboard.client.renderer.CorkboardEntityRenderer;
import gum.corkboard.client.screen.NoteScreen;
import gum.corkboard.main.registries.BlockRegistry;
import gum.corkboard.main.registries.ScreenRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.accessor.AccessBlockModelRenderer;
import net.fabricmc.fabric.impl.client.model.ModelLoadingRegistryImpl;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.util.registry.Registry;


@Environment(EnvType.CLIENT)
public class CorkBoardClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        ModelLoadingPlugin.onInitializeModelLoader();
        BlockEntityRendererRegistry.register(BlockRegistry.CORKBOARD_ENTITY, CorkboardEntityRenderer::new);

        HandledScreens.register(ScreenRegistry.NOTE_SCREEN_HANDLER, NoteScreen::new);

    }
}
