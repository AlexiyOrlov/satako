package dev.buildtool.satako;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Related to {@link net.minecraft.world.level.block.Block#triggerEvent(BlockState, Level, BlockPos, int, int)}
 */
public interface SCSync {
    boolean onDataReceived(BlockState state, Level worldIn, BlockPos pos, byte id, byte value);
}
