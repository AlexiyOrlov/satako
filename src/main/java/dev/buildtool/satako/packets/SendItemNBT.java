package dev.buildtool.satako.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;

/**
 * For setting NBT on a stack in the specified hand.
 * Created on 3/16/20.
 */
public class SendItemNBT {
    public final CompoundTag compoundNBT;
    public final InteractionHand toHand;

    public SendItemNBT(CompoundTag compoundNBT, InteractionHand toHand_) {
        this.compoundNBT = compoundNBT;
        toHand = toHand_;
    }
}
