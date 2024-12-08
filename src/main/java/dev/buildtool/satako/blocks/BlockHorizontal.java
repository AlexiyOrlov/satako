package dev.buildtool.satako.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

public class BlockHorizontal extends HorizontalDirectionalBlock {
    public BlockHorizontal(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return null;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext itemUseContext) {
        Direction direction = itemUseContext.getHorizontalDirection();
        return defaultBlockState().setValue(FACING, direction.getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        super.createBlockStateDefinition(stateBuilder);
        stateBuilder.add(FACING);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && state.hasBlockEntity()) {
            IItemHandler iItemHandler= worldIn.getCapability(Capabilities.ItemHandler.BLOCK,pos,null);
            for (int i = 0; i < iItemHandler.getSlots(); i++) {
                ItemStack stack = iItemHandler.getStackInSlot(i);
                if (!stack.isEmpty())
                    Containers.dropItemStack(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
        return state.hasBlockEntity() && worldIn.getBlockEntity(pos).triggerEvent(id, param);
    }
}
