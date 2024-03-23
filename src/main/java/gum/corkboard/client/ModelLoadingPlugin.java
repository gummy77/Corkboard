package gum.corkboard.client;

import gum.corkboard.main.CorkBoard;
import net.minecraft.util.Identifier;

public class ModelLoadingPlugin implements net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin {
    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        pluginContext.addModels(new Identifier(CorkBoard.MODID, "block/note"));
    }
}
