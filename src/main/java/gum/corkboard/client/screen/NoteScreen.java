package gum.corkboard.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import gum.corkboard.main.CorkBoard;
import gum.corkboard.main.registries.PacketRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class NoteScreen extends HandledScreen<NoteScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(CorkBoard.MODID, "textures/gui/note.png");
    private String[] messages;
    private int currentRow;
    @Nullable
    private SelectionManager selectionManager;

    public NoteScreen(NoteScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 100;
        this.backgroundWidth = 100;
        this.messages = new String[]{"", "", "", ""};
    }

    @Override
    protected void init() {
        ItemStack stack = this.client.player.getMainHandStack();
        NbtCompound initialNbt = stack.getOrCreateSubNbt("text");

        //System.out.println("Stared Editing: \n" + stack.getName() + "\n" +initialNbt);
        if(initialNbt != null) {
            this.messages = new String[]{
                    initialNbt.getString("0"),
                    initialNbt.getString("1"),
                    initialNbt.getString("2"),
                    initialNbt.getString("3")};
        }

        for(int i = 0; i < messages.length; i++){
            if(messages[i] == " "){
                messages[i] = "";
            }
        }

        this.selectionManager = new SelectionManager(
            () -> this.messages[this.currentRow],
            this::setCurrentRowMessage,
            SelectionManager.makeClipboardGetter(this.client),
            SelectionManager.makeClipboardSetter(this.client),
            (string) -> this.client.textRenderer.getWidth(string) <= 90
        );
    }



    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        matrixStack.translate(-2000, 0, 0);
        super.render(matrixStack, mouseX, mouseY, delta);
        matrixStack.translate(2000, 0, 0);

        matrixStack.push();

        this.scaleScreen(matrixStack);
        drawBackground(matrixStack, delta, mouseX, mouseY);
        this.renderSignText(matrixStack);

        matrixStack.pop();
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
            return this.selectionManager.handleSpecialKey(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
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
    protected void drawBackground(MatrixStack matrixStack, float delta, int mouseX, int mouseY) {
        matrixStack.push();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        //(TEXTURE, -backgroundWidth/2, -backgroundHeight/2, 0, 0, backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);
        matrixStack.pop();
    }

    private void scaleScreen(MatrixStack matrixStack) {
        matrixStack.translate((float)this.width / 2.0F, 120.0F, 50.0F);
        matrixStack.scale(2f, 2f, 2f);
    }

    private void renderSignText(MatrixStack matrixStack) {
        matrixStack.translate(0, -20.0F, 0);
        VertexConsumerProvider.Immediate immediate = this.client.getBufferBuilders().getEntityVertexConsumers();

        int j = this.selectionManager.getSelectionStart();
        int k = this.selectionManager.getSelectionEnd();
        int l = 15;
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
                this.client.textRenderer.draw(string, o, n * l, 0, false, matrixStack.peek().getPositionMatrix(), immediate, false, 0, 15728880, false);
                if (n == this.currentRow && j >= 0) {
                    p = this.textRenderer.getWidth(string.substring(0, Math.min(j, string.length())));
                    q = p - this.textRenderer.getWidth(string) / 2;
                    if (j >= string.length()) {
                        this.client.textRenderer.draw("_", q, m, 0, false, matrixStack.peek().getPositionMatrix(), immediate, false, 0, 15728880, false);
                    }
                }
            }
        }

        for(n = 0; n < this.messages.length; ++n) {
            string = this.messages[n];
            if (string != null && n == this.currentRow && j >= 0) {
                o = this.textRenderer.getWidth(string.substring(0, Math.min(j, string.length())));
                p = o - this.textRenderer.getWidth(string) / 2;
                if (j < string.length()) {
                    fill(matrixStack, p, m - 1, p + 1, m + 10, -16777216 | 0);

                }

//                if (k != j) {
//                    q = Math.min(j, k);
//                    int r = Math.max(j, k);
//                    int s = this.textRenderer.getWidth(string.substring(0, q)) - this.textRenderer.getWidth(string) / 2;
//                    int t = this.textRenderer.getWidth(string.substring(0, r)) - this.textRenderer.getWidth(string) / 2;
//                    int u = Math.min(s, t);
//                    int v = Math.max(s, t);
//                    fill(RenderLayer.tex.getGuiTextHighlight(), u, m, v, m + 10, -16776961);
//                }
            }
        }

    }

    @Override
    public void close() {
        this.finishEditing();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void setCurrentRowMessage(String message) {
        this.messages[this.currentRow] = message;
    }

    private void saveNbtText (){
        ItemStack stack = client.player.getMainHandStack();

        if(this.handler.writeItemStackText(stack, this.messages)){

            PacketByteBuf buf = PacketByteBufs.create();
            String joinedText =
                    this.messages[0] + "\n" +
                    this.messages[1] + "\n" +
                    this.messages[2] + "\n" +
                    this.messages[3] + "\n";

            buf.writeString(joinedText);

            ClientPlayNetworking.send(PacketRegistry.SET_NOTE_NBT_PACKET_ID, buf);
        }
        //System.out.println("Finished Editing: \n"+stack.getName() + "\n" + stack.getNbt());
    }

    private void finishEditing() {
        saveNbtText();
        super.close();
    }
}
