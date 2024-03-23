package gum.corkboard.client.renderer;

import gum.corkboard.client.ModelLoadingPlugin;
import gum.corkboard.main.CorkBoard;
import gum.corkboard.main.block.Corkboard;
import gum.corkboard.main.block.CorkboardEntity;
import gum.corkboard.main.registries.BlockRegistry;
import gum.corkboard.main.registries.ItemRegistry;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingPluginManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.data.client.ModelIds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class CorkboardEntityRenderer implements BlockEntityRenderer<CorkboardEntity> {

    private ItemRenderer itemRenderer;
    private TextRenderer textRenderer;
    private EntityRenderDispatcher entityRenderer;
    private BlockRenderManager renderManager;


    public CorkboardEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
        this.textRenderer = ctx.getTextRenderer();
        this.entityRenderer = ctx.getEntityRenderDispatcher();
        this.renderManager = ctx.getRenderManager();

    }

    @Override
    public void render(CorkboardEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        this.renderItems(blockEntity, tickDelta, matrices, vertexConsumers, light, overlay);
        matrices.pop();
    }

    private void renderItems(CorkboardEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction direction = blockEntity.getCachedState().get(Corkboard.FACING);
        DefaultedList<ItemStack> defaultedList = blockEntity.getItems();
        int k = (int)blockEntity.getPos().asLong();

        matrices.push();

        float dir = direction.asRotation();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180F - dir));
        matrices.scale(0.25f, 0.25f, 0.25f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180F));
        matrices.translate(0.0, 0.0, -1.75);

        for(int i = 0; i < defaultedList.size(); i++) {
            ItemStack itemStack = defaultedList.get(i);
            if (itemStack != ItemStack.EMPTY) {
                matrices.push();

                NbtCompound nbt = itemStack.getNbt();
                if (nbt != null) {
                    if (nbt.contains("scr_pos_y")) {
                        matrices.translate(-nbt.getDouble("scr_pos_x") * 3.999, -nbt.getDouble("scr_pos_y") * 3.999, i * 0.001);
                    }
                }
                if(itemStack.getItem() == ItemRegistry.NOTE) {
                    matrices.scale(0.8f, 0.8f, 0.8f);
                    matrices.translate(0,0,-0.075);
                    renderNote(itemStack, blockEntity, tickDelta, matrices, vertexConsumers, light, overlay);
                } else {
                    MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), k);
                }
                matrices.pop();
            }
        }
        matrices.pop();
    }

    private void renderNote(ItemStack stack, BlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.scale(3,3,3);
        matrices.translate(-0.5, -0.5, 0);

        BakedModelManager bakedModelManager = this.renderManager.getModels().getModelManager();
        Identifier identifier = ModelIdentifier.of(CorkBoard.MODID, "block/note");

        this.renderManager.getModelRenderer().render(
                matrices.peek(),
                vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout()),
                (BlockState)null,
                bakedModelManager.getModel(identifier),
                1, 1, 1,
                light, overlay
        );


        matrices.pop();
        matrices.push();
        NbtCompound nbt = stack.getOrCreateSubNbt("text");
        if(nbt != null) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-180F));
            matrices.translate(0, -0.5, -0.1876);
            matrices.scale(0.03f, 0.03f, 0.03f);

            String[] messages = new String[]{
                    nbt.getString("0"),
                    nbt.getString("1"),
                    nbt.getString("2"),
                    nbt.getString("3")
            };

            for(int i = 0; i < messages.length; i++) {
                Text text = Text.of(messages[i]);
                float textPos = ((float)-this.textRenderer.getWidth(text))/2;

                this.textRenderer.draw(
                        text,
                        textPos, 15 * i,
                        0,
                        false,
                        matrices.peek().getPositionMatrix(),
                        vertexConsumers,
                        TextRenderer.TextLayerType.NORMAL,
                        255,
                        light
                );
            }
        }
        matrices.pop();
    }

}
