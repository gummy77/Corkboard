package gum.corkboard.main.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gum.corkboard.main.CorkBoard;
import gum.corkboard.main.registries.BlockRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

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
        switch(dir) {
            case NORTH:
                return VoxelShapes.cuboid(0.0f, 0.0f, 0.875f, 1.0f, 1.0f, 1.0f);
            case SOUTH:
                return VoxelShapes.cuboid(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.875f);
            case EAST:
                return VoxelShapes.cuboid(0.0f, 0.0f, 0.0f, 0.875f, 1.0f, 1.0f);
            case WEST:
                return VoxelShapes.cuboid(0.875f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            default:
                return VoxelShapes.fullCube();
        }
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


        if (!player.getStackInHand(hand).isEmpty()) {
            if (blockEntity.getStack(0).isEmpty()) {

                blockEntity.setStack(0, player.getStackInHand(hand).copy());
                player.getStackInHand(hand).setCount(0);

                world.updateListeners(blockPos, blockState, blockState, 2);
                System.out.println("ADDED");

            }
        } else {
            if (!blockEntity.getStack(0).isEmpty()) {
                player.getInventory().offerOrDrop(blockEntity.getStack(0));
                blockEntity.setStack(0, ItemStack.EMPTY);

                world.updateListeners(blockPos, blockState, blockState, 2);
                System.out.println("REMOVED");
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CorkboardEntity) {
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
