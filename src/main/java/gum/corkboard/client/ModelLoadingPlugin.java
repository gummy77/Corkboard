package gum.corkboard.client;

import gum.corkboard.client.model.NoteModel;
import gum.corkboard.main.CorkBoard;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class ModelLoadingPlugin implements net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin {
    public static final ModelIdentifier NOTE_MODEL = new ModelIdentifier(CorkBoard.MODID, "note_model", "");
    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        pluginContext.modifyModelOnLoad().register((original, context) -> {
            // This is called for every model that is loaded, so make sure we only target ours
            if(context.id().equals(NOTE_MODEL)) {
                return new NoteModel();
            } else {
                // If we don't modify the model we just return the original as-is
                return original;
            }
        });
        pluginContext.addModels(new Identifier(CorkBoard.MODID, "block/note"));
    }
}
