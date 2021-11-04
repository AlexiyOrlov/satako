package dev.buildtool.satako;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * Convenience class
 */
public class BlockData
{
    public Block block;
    public BlockPos position;
    public TileEntity tile;
    public BlockState blockState;

    public BlockData(BlockState iBlockState, int x_, int y_, int z_, @Nullable TileEntity tileEntity)
    {
        block = iBlockState.getBlock();
        blockState = iBlockState;
        position = new BlockPos(x_, y_, z_);
        tile = tileEntity;
    }

    public BlockData(BlockState iBlockState, BlockPos blockPos, @Nullable TileEntity tileEntity)
    {
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
