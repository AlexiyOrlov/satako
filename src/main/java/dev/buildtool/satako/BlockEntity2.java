package dev.buildtool.satako;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * Main Tile class. Override
 * {@link TileEntity#save(CompoundNBT)}, {@link TileEntity#load(BlockState, CompoundNBT)}
 * to store data
 */
public abstract class BlockEntity2 extends TileEntity
{
    protected final static String ITEMS = "Items";

    public BlockEntity2(TileEntityType<?> tileEntityType)
    {
        super(tileEntityType);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        //server side
        BlockPos blockPos = getBlockPos();
        return new SUpdateTileEntityPacket(blockPos, 0, getUpdateTag());
    }


    @Override
    public CompoundNBT getUpdateTag()
    {
        //server side
        CompoundNBT supertag = super.getUpdateTag();
        save(supertag);
        return supertag;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        CompoundNBT nbtTagCompound = pkt.getTag();
        load(getBlockState(),nbtTagCompound);
    }

    public int getX()
    {
        return getBlockPos().getX();
    }

    public int getY()
    {
        return getBlockPos().getY();
    }

    public int getZ()
    {
        return getBlockPos().getZ();
    }

}
