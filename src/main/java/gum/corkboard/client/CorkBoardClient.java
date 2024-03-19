package gum.corkboard.client;

import gum.corkboard.client.renderer.CorkboardEntityRenderer;
import gum.corkboard.main.registries.BlockRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class CorkBoardClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(new CorkboardModelLoadingPlugin());
        BlockEntityRendererRegistry.register(BlockRegistry.CORKBOARD_ENTITY, CorkboardEntityRenderer::new);
    }
}
