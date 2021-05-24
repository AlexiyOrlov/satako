package dev.buildtool.satako.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class BlockConnectable extends SixWayBlock {
    public BlockConnectable(float thickness, Properties properties) {
        super(thickness, properties);
        assert thickness <= 0.5;
        registerDefaultState(defaultBlockState().setValue(NORTH, false).setValue(SOUTH, false).setValue(UP, false).setValue(DOWN, false).setValue(EAST, false).setValue(WEST, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> blockStateBuilder) {
        super.createBlockStateDefinition(blockStateBuilder);
        blockStateBuilder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext useContext) {
        return makeConnections(useContext.getLevel(), useContext.getClickedPos());
    }

    /**
     * Define block connections at given positions
     *
     * @return blockstate with appropriate connections
     */
    protected abstract BlockState makeConnections(IBlockReader blockReader, BlockPos pos);

    protected boolean doConnectTo(IWorld world, BlockState blockState, BlockState to, BlockPos pos, BlockPos toPos, Direction direction) {
        return to.getBlock() == this;
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction on, BlockState state, IWorld world, BlockPos blockPos, BlockPos pos) {
        return blockState.setValue(PROPERTY_BY_DIRECTION.get(on), doConnectTo(world, blockState, state, blockPos, pos, on));
    }

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
