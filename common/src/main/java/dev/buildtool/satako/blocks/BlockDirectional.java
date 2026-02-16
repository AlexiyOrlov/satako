package dev.buildtool.satako.blocks;

import com.mojang.serialization.MapCodec;
import dev.buildtool.satako.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * Created on 5/29/18.
 */
public class BlockDirectional extends DirectionalBlock {
    private boolean dropItems=true;
    private static final MapCodec<BlockDirectional> MAP_CODEC=simpleCodec(BlockDirectional::new);

    public BlockDirectional(Properties p_52591_, boolean dropItems) {
        super(p_52591_);
        this.dropItems = dropItems;
    }

    public BlockDirectional(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(FACING);
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
        if (dropItems && !state.is(newState.getBlock()) && state.hasBlockEntity()) {
            Services.PLATFORM.dropItemsIfAny(worldIn, pos, state);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
        return state.hasBlockEntity() && worldIn.getBlockEntity(pos).triggerEvent(id, param);
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return MAP_CODEC;
    }
}
