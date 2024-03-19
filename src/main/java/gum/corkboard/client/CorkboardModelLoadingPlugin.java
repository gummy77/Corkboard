package gum.corkboard.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

public class CorkboardModelLoadingPlugin implements ModelLoadingPlugin {
    //public static final ModelIdentifier CORKBOARD_MODEL = new ModelIdentifier(CorkBoard.MODID, "corkboard_model", "");

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
//        pluginContext.modifyModelOnLoad().register((original, context) -> {
//            if(context.id().equals(CORKBOARD_MODEL)) {
//                return new CorkboardModel();
//            } else {
//                return original;
//            }
//        });
    }
}
