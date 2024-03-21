package gum.corkboard.main.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.joml.Vector2d;

public class Corkboard extends BlockWithEntity {
    public static final MapCodec<Corkboard> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(createSettingsCodec()).apply(instance, Corkboard::new);
    });
    public static final DirectionProperty FACING;

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public Corkboard(AbstractBlock.Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        Direction dir = state.get(FACING);
        return switch (dir) {
            case NORTH -> VoxelShapes.cuboid(0.0f, 0.0f, 0.9375f, 1.0f, 1.0f, 1.0f);
            case SOUTH -> VoxelShapes.cuboid(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0625f);
            case EAST -> VoxelShapes.cuboid(0.0f, 0.0f, 0.0f, 0.0625f, 1.0f, 1.0f);
            case WEST -> VoxelShapes.cuboid(0.9375f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            default -> VoxelShapes.fullCube();
        };
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        CorkboardEntity blockEntity = (CorkboardEntity) world.getBlockEntity(blockPos);

        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        Vec3d itemPosition = blockPos.toCenterPos().subtract(blockHitResult.getPos());
        double yPos = itemPosition.y;
        double xPos = 0;

        Direction dir = blockState.get(FACING);
        switch (dir) {
            case NORTH -> xPos = -itemPosition.x;
            case EAST -> xPos = -itemPosition.z;
            case WEST -> xPos = itemPosition.z;
            case SOUTH ->  xPos = itemPosition.x;
        }

        if (!player.getStackInHand(hand).isEmpty()) {
            if (!blockEntity.isFull()) {
                ItemStack item = player.getStackInHand(hand).copyWithCount(1);
                NbtCompound nbt = item.getNbt();

                if(nbt == null) nbt = new NbtCompound();

                nbt.putDouble("scr_pos_y", (double)(Math.round(yPos * 16.0)) / 16);
                nbt.putDouble("scr_pos_x", (double)(Math.round(xPos * 16.0)) / 16);
                item.setNbt(nbt);

                blockEntity.addStack(item);
                player.getStackInHand(hand).decrement(1);

                world.updateListeners(blockPos, blockState, blockState, 2);
            }
        } else {
            if (!blockEntity.isEmpty()) {
                DefaultedList<ItemStack> items = blockEntity.getItems();

                int nearestStack = -1;
                double nearestDist = 10;
                Vector2d newPos = new Vector2d(yPos, xPos);
                for (int i = 0; i < items.size(); i++) {
                    ItemStack curStack = items.get(i);
                    NbtCompound nbt = curStack.getNbt();
                    if(nbt != null) {
                        Vector2d curPos = new Vector2d(nbt.getDouble("scr_pos_y"), nbt.getDouble("scr_pos_x"));

                        double dist = curPos.distance(newPos);
                        if (dist <= nearestDist) {
                            nearestDist = dist;
                            nearestStack = i;
                        }
                    }
                }

                ItemStack item = blockEntity.getStack(nearestStack);
                NbtCompound nbt = item.getNbt();

                if (nbt != null) {
                    if (nbt.contains("scr_pos_x")) {
                        nbt.remove("scr_pos_y");
                        nbt.remove("scr_pos_x");
                    }
                }

                blockEntity.setStack(nearestStack, ItemStack.EMPTY);
                player.getInventory().offerOrDrop(item);

                world.updateListeners(blockPos, blockState, blockState, 2);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CorkboardEntity) {
                for (int i = 0; i < ((CorkboardEntity) blockEntity).getItems().size(); i++) {
                    ItemStack item = ((CorkboardEntity) blockEntity).getItems().get(i);
                    NbtCompound nbt = item.getNbt();
                    if (nbt != null) {
                        if (nbt.contains("scr_pos_x")) {
                            nbt.remove("scr_pos_y");
                            nbt.remove("scr_pos_x");

                        }
                    }
                }
                ItemScatterer.spawn(world, pos, (CorkboardEntity) blockEntity);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CorkboardEntity(pos, state);
    }

    static {
        FACING = Properties.HORIZONTAL_FACING;
    }
}
