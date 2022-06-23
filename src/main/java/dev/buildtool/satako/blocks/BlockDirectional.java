package dev.buildtool.satako.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.Collections;
import java.util.List;

/**
 * Created on 5/29/18.
 */
public class BlockDirectional extends DirectionalBlock {

    public BlockDirectional(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(FACING);
    }

    /**
     * Correct if {@link net.minecraftforge.common.extensions.IForgeBlockState#rotate(LevelAccessor, BlockPos, Rotation)} is used
     */
    @Override
    public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && state.hasBlockEntity()) {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
                for (int i = 0; i < iItemHandler.getSlots(); i++) {
                    ItemStack stack = iItemHandler.getStackInSlot(i);
                    if (!stack.isEmpty())
                        Containers.dropItemStack(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);
                }
            });
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
        return state.hasBlockEntity() && worldIn.getBlockEntity(pos).triggerEvent(id, param);
    }

    /**
     * Drop itself by default
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder builder) {
        if (builder.getLevel().getServer().getLootTables().get(getLootTable()).getLootTableId() == null) {
            return Collections.singletonList(new ItemStack(this));
        }
        return super.getDrops(p_60537_, builder);
    }
}
