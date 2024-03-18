package gum.corkboard.client.renderer;

import gum.corkboard.main.block.Corkboard;
import gum.corkboard.main.block.CorkboardEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;


public class CorkboardEntityRenderer implements BlockEntityRenderer<CorkboardEntity> {
    private final ItemRenderer itemRenderer;

    public CorkboardEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(CorkboardEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction direction = (Direction)blockEntity.getCachedState().get(Corkboard.FACING);
        DefaultedList<ItemStack> defaultedList = blockEntity.getItems();
        ItemStack itemStack = defaultedList.get(0);
        int k = (int)blockEntity.getPos().asLong();

        if (itemStack != ItemStack.EMPTY) {
            matrices.push();
            matrices.translate(0.5, 0.5, 0.5);

            MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), k);

            matrices.pop();
        }
    }


}
