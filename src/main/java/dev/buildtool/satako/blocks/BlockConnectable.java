package dev.buildtool.satako.blocks;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class BlockConnectable extends PipeBlock {
    public BlockConnectable(float apothem, Properties properties) {
        super(apothem, properties);
        if(apothem>0.5)
            throw new IllegalArgumentException("Block is too thick");
        registerDefaultState(defaultBlockState().setValue(NORTH, false).setValue(SOUTH, false).setValue(UP, false).setValue(DOWN, false).setValue(EAST, false).setValue(WEST, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        super.createBlockStateDefinition(blockStateBuilder);
        blockStateBuilder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext useContext) {
        return makeConnections(useContext.getLevel(), useContext.getClickedPos());
    }

    /**
     * Define block connections at given positions
     *
     * @return blockstate with appropriate connections
     */
    protected abstract BlockState makeConnections(Level blockReader, BlockPos pos);

    /**
     * @return when to make connections
     */
    protected boolean doConnectTo(LevelAccessor world, BlockState blockState, BlockState to, BlockPos pos, BlockPos toPos, Direction direction) {
        return to.is(this);
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction on, BlockState state, LevelAccessor world, BlockPos blockPos, BlockPos pos) {
        return blockState.setValue(PROPERTY_BY_DIRECTION.get(on), doConnectTo(world, blockState, state, blockPos, pos, on));
    }

    /**
     * @return list of directions connected to
     */
    public List<Direction> getConnections(BlockState blockState) {
        ArrayList<Direction> directions = new ArrayList<>(6);
        if (blockState.getValue(UP))
            directions.add(Direction.UP);
        if (blockState.getValue(DOWN))
            directions.add(Direction.DOWN);
        if (blockState.getValue(SOUTH))
            directions.add(Direction.SOUTH);
        if (blockState.getValue(NORTH))
            directions.add(Direction.NORTH);
        if (blockState.getValue(WEST))
            directions.add(Direction.WEST);
        if (blockState.getValue(EAST))
            directions.add(Direction.EAST);
        return directions;
    }
}
