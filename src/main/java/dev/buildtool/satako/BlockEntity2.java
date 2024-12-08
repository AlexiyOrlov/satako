package dev.buildtool.satako;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Main Tile class.
 */
public abstract class BlockEntity2 extends BlockEntity {
    public BlockEntity2(BlockEntityType<?> tileEntityType, BlockPos position, BlockState blockState) {
        super(tileEntityType, position, blockState);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        //server side
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        //server side
        CompoundTag supertag = super.getUpdateTag(provider);
        saveAdditional(supertag,provider);
        return supertag;
    }

    public int getX() {
        return getBlockPos().getX();
    }

    public int getY() {
        return getBlockPos().getY();
    }

    public int getZ()
    {
        return getBlockPos().getZ();
    }

}
