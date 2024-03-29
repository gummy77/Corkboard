package gum.corkboard.client.renderer;

import gum.corkboard.main.CorkBoard;
import gum.corkboard.main.block.Corkboard;
import gum.corkboard.main.block.CorkboardEntity;
import gum.corkboard.main.registries.ItemRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class CorkboardEntityRenderer implements BlockEntityRenderer {

    private final ItemRenderer itemRenderer;
    private final TextRenderer textRenderer;
    private final BlockEntityRenderDispatcher renderManager;
    private final MinecraftClient client = MinecraftClient.getInstance();


    public CorkboardEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = client.getItemRenderer();
        this.textRenderer = ctx.getTextRenderer();
        this.renderManager = ctx.getRenderDispatcher();
    }


    @Override
    public void render(BlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        this.renderItems((CorkboardEntity) blockEntity, matrices, vertexConsumers, light, overlay);
        matrices.pop();
    }

    private void renderItems(CorkboardEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction direction = blockEntity.getCachedState().get(Corkboard.FACING);
        DefaultedList<ItemStack> defaultedList = blockEntity.getItems();
        int k = (int)blockEntity.getPos().asLong();

        matrices.push();
        float dir = direction.asRotation();
        matrices.multiply(Direction.UP.getUnitVector().getDegreesQuaternion(-dir));

        if (direction == Direction.WEST || direction == Direction.NORTH){
            matrices.translate(0, 0, -1);
        }

        matrices.scale(0.25f, 0.25f, 0.25f);

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
                    renderNote(itemStack, matrices, vertexConsumers, light, overlay);
                } else {
                    this.itemRenderer.renderItem(itemStack, ModelTransformation.Mode.NONE, light, overlay, matrices, vertexConsumers, k);
                    //renderItem(itemStack, ModelTransformationMode.FIXED, l, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, itemFrameEntity.getWorld(), itemFrameEntity.getId());
                }
                matrices.pop();
            }
        }
        matrices.pop();
    }

    private void renderNote(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.scale(3,3,3);
        matrices.translate(-0.5, -0.5, 0);

        BlockRenderManager blockRenderManager = this.client.getBlockRenderManager();

        BakedModelManager bakedModelManager = blockRenderManager.getModels().getModelManager();
        //ModelIdentifier identifier = new ModelIdentifier(CorkBoard.MODID, "/block/note");
        UnbakedModel model = new ModelLoader(client.getResourceManager(), BlockColors.create(), client.getProfiler(), 0).getOrLoadModel(new Identifier(CorkBoard.MODID, "/block/note"));


        blockRenderManager.getModelRenderer().render(
                matrices.peek(),
                vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout()),
               null,
                model,
                1, 1, 1,
                light, overlay
        );


        matrices.pop();
        matrices.push();
        NbtCompound nbt = stack.getOrCreateSubNbt("text");
        if(nbt != null) {
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-180F));
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
                        false,
                        255,
                        light
                );
            }
        }
        matrices.pop();
    }
}
