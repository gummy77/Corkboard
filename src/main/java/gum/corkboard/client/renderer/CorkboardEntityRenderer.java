package gum.corkboard.client.renderer;

import gum.corkboard.main.CorkBoard;
import gum.corkboard.main.block.Corkboard;
import gum.corkboard.main.block.CorkboardEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MeshBuilderImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.math.RotationAxis;

import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class CorkboardEntityRenderer implements BlockEntityRenderer<CorkboardEntity> {
    CorkboardModel model;


    public CorkboardEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        model = new CorkboardModel();
    }

    @Override
    public void render(CorkboardEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        this.renderBoard(blockEntity, tickDelta, matrices, vertexConsumers, light, overlay);
        this.renderItems(blockEntity, tickDelta, matrices, vertexConsumers, light, overlay);
        matrices.pop();
    }

    private void renderBoard(CorkboardEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        VertexConsumer vertexConsumer = new SpriteIdentifier(model.ATLAS, model.TEXTURE).getVertexConsumer(vertexConsumers, model::getLayer);

        model.render(matrices, vertexConsumer, light, overlay);

        matrices.pop();
    }

    private void renderItems(CorkboardEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction direction = blockEntity.getCachedState().get(Corkboard.FACING);
        DefaultedList<ItemStack> defaultedList = blockEntity.getItems();
        int k = (int)blockEntity.getPos().asLong();

        for(int i = 0; i < defaultedList.size(); i++){
            ItemStack itemStack = defaultedList.get(i);
            if (itemStack != ItemStack.EMPTY) {
                matrices.push();

                float dir = direction.asRotation();

                matrices.translate(0.5, 0.5, 0.5);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180F - dir));
                matrices.scale(0.25f, 0.25f, 0.25f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180F));
                matrices.translate(0.0, 0.0, -1.5);

                NbtCompound nbt = itemStack.getNbt();
                if (nbt != null) {
                    if (nbt.contains("scr_pos_y")) {
                        matrices.translate(-nbt.getDouble("scr_pos_x") * 3.999, -nbt.getDouble("scr_pos_y") * 3.999, i * 0.001);
                    }
                }

                MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), k);
                matrices.pop();
            }
        }
    }
    @Environment(EnvType.CLIENT)
    public static final class CorkboardModel extends Model {
        private Identifier ATLAS = new Identifier("minecraft:textures/atlas/blocks.png");
        private Identifier TEXTURE = new Identifier(CorkBoard.MODID, "block/corkboard_texture");

        public final ModelPart root;

        public CorkboardModel() {
            super(RenderLayer::getEntityCutoutNoCull);
            this.root = getRoot();
        }

        public ModelPart getRoot() {
            List<ModelPart.Cuboid> cubes = List.of(
                new ModelPart.Cuboid(
                    0, 0,
                    0, 0, 0,
                    16, 16, 16,
                    0, 0, 0,
                    false,
                    16, 16,
                        Set.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN)
                )
            );
            return new ModelPart(cubes, new HashMap<>());
        }

        public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
            this.root.render(matrices, vertices, light, overlay, 1, 1, 1, 1);
        }
        public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
            this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
    }
}
