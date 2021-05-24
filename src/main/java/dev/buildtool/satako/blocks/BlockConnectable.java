package dev.buildtool.satako.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public abstract class BlockConnectable extends SixWayBlock {
    public BlockConnectable(float thickness, Properties properties) {
        super(thickness, properties);
        assert thickness < 1;
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
}
