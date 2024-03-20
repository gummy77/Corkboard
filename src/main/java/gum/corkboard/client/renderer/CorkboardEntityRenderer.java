package gum.corkboard.client.renderer;

import gum.corkboard.main.block.Corkboard;
import gum.corkboard.main.block.CorkboardEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class CorkboardEntityRenderer implements BlockEntityRenderer<CorkboardEntity> {

    public CorkboardEntityRenderer(BlockEntityRendererFactory.Context ctx) {
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

        for(int i = 0; i < defaultedList.size(); i++){
            ItemStack itemStack = defaultedList.get(i);
            if (itemStack != ItemStack.EMPTY) {
                matrices.push();

                float dir = direction.asRotation();

                matrices.translate(0.5, 0.5, 0.5);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180F - dir));
                matrices.scale(0.25f, 0.25f, 0.25f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180F));
                matrices.translate(0.0, 0.0, -1.75);

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
}
