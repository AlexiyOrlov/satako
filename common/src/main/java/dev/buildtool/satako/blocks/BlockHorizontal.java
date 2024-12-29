package dev.buildtool.satako.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
public abstract class BlockHorizontal extends HorizontalDirectionalBlock {
    private boolean dropItems=true;
    public BlockHorizontal(Properties properties) {
        super(properties);
    }

    public BlockHorizontal(Properties p_54120_, boolean dropItems) {
        super(p_54120_);
        this.dropItems = dropItems;
    }

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
        if (dropItems && !state.is(newState.getBlock()) && state.hasBlockEntity()) {
//            PlatformService.platformSpecific.dropItemsIfAny(worldIn, pos);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
        return state.hasBlockEntity() && worldIn.getBlockEntity(pos).triggerEvent(id, param);
    }
}
