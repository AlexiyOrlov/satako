package dev.buildtool.satako;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Related to {@link net.minecraft.block.AbstractBlock#triggerEvent(BlockState, World, BlockPos, int, int)}
 */
public interface SCSync {
    boolean onDataReceived(BlockState state, World worldIn, BlockPos pos, byte id, byte value);
}
