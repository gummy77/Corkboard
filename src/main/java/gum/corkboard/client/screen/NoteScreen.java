package gum.corkboard.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import gum.corkboard.main.CorkBoard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Iterator;

@Environment(EnvType.CLIENT)
public class NoteScreen extends HandledScreen<NoteScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(CorkBoard.MODID, "textures/gui/note.png");
    private final String[] messages;
    private int currentRow;
    private ItemStack savedStack;
    @Nullable
    private SelectionManager selectionManager;

    public NoteScreen(NoteScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 100;
        this.backgroundWidth = 100;
        savedStack = inventory.getMainHandStack();
        NbtCompound initialNbt = savedStack.getNbt();
        if(initialNbt != null) {
            this.messages = new String[]{
                    initialNbt.getString("text0"),
                    initialNbt.getString("text1"),
                    initialNbt.getString("text2"),
                    initialNbt.getString("text3")};
        }else {
            this.messages = new String[]{"", "", "", ""};
        }

    }

    @Override
    protected void init() {
        this.selectionManager = new SelectionManager(
            () -> { return this.messages[this.currentRow]; },
            this::setCurrentRowMessage,
            SelectionManager.makeClipboardGetter(this.client),
            SelectionManager.makeClipboardSetter(this.client),
            (string) -> { return this.client.textRenderer.getWidth(string) <= 100; }
        );
    }


    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        drawContext.getMatrices().translate(-2000, 0, 0);
        super.render(drawContext, mouseX, mouseY, delta);
        drawContext.getMatrices().translate(2000, 0, 0);

        drawContext.getMatrices().push();

        this.scaleScreen(drawContext);
        drawBackground(drawContext, delta, mouseX, mouseY);
        this.renderSignText(drawContext);

        drawContext.getMatrices().pop();
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 265) {
            this.currentRow = this.currentRow - 1 & 3;
            this.selectionManager.putCursorAtEnd();
            return true;
        }else if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            return true;
        } else if (keyCode != 264 && keyCode != 257 && keyCode != 335) {
            return this.selectionManager.handleSpecialKey(keyCode) ? true : super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            this.currentRow = this.currentRow + 1 & 3;
            this.selectionManager.putCursorAtEnd();
            return true;
        }
    }
    @Override
    public boolean charTyped(char chr, int modifiers) {
        this.selectionManager.insert(chr);
        return true;
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float delta, int mouseX, int mouseY) {
        drawContext.getMatrices().push();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width/2) - (this.backgroundWidth/2);
        int y = (height/2) - (this.backgroundHeight/2);
        drawContext.drawTexture(TEXTURE, -backgroundWidth/2, -backgroundHeight/2, 0, 0, backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);
        drawContext.getMatrices().pop();
    }

    private void scaleScreen(DrawContext drawContext) {
        drawContext.getMatrices().translate((float)this.width / 2.0F, 120.0F, 50.0F);
        drawContext.getMatrices().scale(2f, 2f, 2f);
    }

    private void renderSignText(DrawContext context) {
        int j = this.selectionManager.getSelectionStart();
        int k = this.selectionManager.getSelectionEnd();
        int l = 10;
        int m = this.currentRow * l;

        int n;
        String string;
        int o;
        int p;
        int q;
        for(n = 0; n < this.messages.length; ++n) {
            string = this.messages[n];
            if (string != null) {
                if (this.textRenderer.isRightToLeft()) {
                    string = this.textRenderer.mirror(string);
                }

                o = -this.textRenderer.getWidth(string) / 2;
                context.drawText(this.textRenderer, string, o, n * l, Colors.BLACK, false);
                if (n == this.currentRow && j >= 0) {
                    p = this.textRenderer.getWidth(string.substring(0, Math.max(Math.min(j, string.length()), 0)));
                    q = p - this.textRenderer.getWidth(string) / 2;
                    if (j >= string.length()) {
                        context.drawText(this.textRenderer, "_", q, m, Colors.BLACK, false);
                    }
                }
            }
        }

        for(n = 0; n < this.messages.length; ++n) {
            string = this.messages[n];
            if (string != null && n == this.currentRow && j >= 0) {
                o = this.textRenderer.getWidth(string.substring(0, Math.max(Math.min(j, string.length()), 0)));
                p = o - this.textRenderer.getWidth(string) / 2;
                if (j < string.length()) {
                    context.fill(p, m - 1, p + 1, m + 10, -16777216 | Colors.BLACK);
                }

                if (k != j) {
                    q = Math.min(j, k);
                    int r = Math.max(j, k);
                    int s = this.textRenderer.getWidth(string.substring(0, q)) - this.textRenderer.getWidth(string) / 2;
                    int t = this.textRenderer.getWidth(string.substring(0, r)) - this.textRenderer.getWidth(string) / 2;
                    int u = Math.min(s, t);
                    int v = Math.max(s, t);
                    //context.fill(RenderLayer.getGuiTextHighlight(), u, m, v, m + this.blockEntity.getTextLineHeight(), -16776961);
                }
            }
        }

    }

    @Override
    public void close() {
        saveNbtText(savedStack);
        this.finishEditing();
    }

    @Override
    public void removed() {
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void setCurrentRowMessage(String message) {
        this.messages[this.currentRow] = message;
    }

    private void saveNbtText (ItemStack stack){
        NbtCompound nbt = new NbtCompound();
        nbt.putString("text0", this.messages[0]);
        nbt.putString("text1", this.messages[1]);
        nbt.putString("text2", this.messages[2]);
        nbt.putString("text3", this.messages[3]);
        //stack.writeNbt(nbt);
        this.handler.getSlot(0).getStack().writeNbt(nbt);
        //this.handler.stack.writeNbt(nbt);
        System.out.println("SAVED to: " + stack.getName());
    }

    private void finishEditing() {
        saveNbtText(savedStack);
        this.client.setScreen((Screen)null);
    }
}
