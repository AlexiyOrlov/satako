package dev.buildtool.satako;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Convenience class
 */
public class BlockData
{
    public Block block;
    public BlockPos position;
    public BlockEntity tile;
    public BlockState blockState;

    public BlockData(BlockState iBlockState, int x_, int y_, int z_, @Nullable BlockEntity tileEntity) {
        block = iBlockState.getBlock();
        blockState = iBlockState;
        position = new BlockPos(x_, y_, z_);
        tile = tileEntity;
    }

    public BlockData(BlockState iBlockState, BlockPos blockPos, @Nullable BlockEntity tileEntity) {
        block = iBlockState.getBlock();
        blockState = iBlockState;
        position = blockPos;
        tile = tileEntity;
    }

    public int getX()
    {
        return position.getX();
    }

    public int getY()
    {
        return position.getY();
    }

    public int getZ()
    {
        return position.getZ();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof BlockData)
        {
            BlockData blockData = (BlockData) obj;
            return blockData.position.equals(position);
        }
        return false;
    }

    @Override
    public String toString()
    {
        return blockState.toString() + " / " + position + " " + (tile == null ? "" : tile.toString());
    }
}
