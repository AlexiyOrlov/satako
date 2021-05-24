package dev.buildtool.satako.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.state.StateContainer;

public class BlockConnectable extends SixWayBlock {
    public BlockConnectable(float thickness, Properties properties) {
        super(thickness, properties);
        registerDefaultState(defaultBlockState().setValue(NORTH, false).setValue(SOUTH, false).setValue(UP, false).setValue(DOWN, false).setValue(EAST, false).setValue(WEST, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> blockStateBuilder) {
        super.createBlockStateDefinition(blockStateBuilder);
        blockStateBuilder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }
}
