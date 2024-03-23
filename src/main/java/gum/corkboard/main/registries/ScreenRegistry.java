package gum.corkboard.main.registries;

import gum.corkboard.client.screen.NoteScreenHandler;
import gum.corkboard.main.CorkBoard;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ScreenRegistry {
    public static final ScreenHandlerType<NoteScreenHandler> NOTE_SCREEN_HANDLER;
    public static void registerScreens() {
    }

    static {
        NOTE_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(CorkBoard.MODID, "note_screen"), NoteScreenHandler::new);
    }
}
