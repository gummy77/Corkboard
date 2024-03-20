package gum.corkboard.client;

import gum.corkboard.client.renderer.CorkboardEntityRenderer;
import gum.corkboard.client.screen.NoteScreen;
import gum.corkboard.main.registries.BlockRegistry;
import gum.corkboard.main.registries.ScreenRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(EnvType.CLIENT)
public class CorkBoardClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(BlockRegistry.CORKBOARD_ENTITY, CorkboardEntityRenderer::new);
        HandledScreens.register(ScreenRegistry.NOTE_SCREEN_HANDLER, NoteScreen::new);
    }
}
