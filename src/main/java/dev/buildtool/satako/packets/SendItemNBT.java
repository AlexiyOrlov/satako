package dev.buildtool.satako.packets;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;

/**
 * For setting NBT on a stack in the specified hand.
 * Created on 3/16/20.
 */
public class SendItemNBT
{
    public final CompoundNBT compoundNBT;
    public final Hand toHand;

    public SendItemNBT(CompoundNBT compoundNBT, Hand toHand_)
    {
        this.compoundNBT = compoundNBT;
        toHand = toHand_;
    }
}
