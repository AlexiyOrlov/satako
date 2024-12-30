package dev.buildtool.satako;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Can be implemented by item handlers
 */
public interface ItemContainer {

    CompoundTag serializeNBT(HolderLookup.Provider provider);

    void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt);

    int getSlotCount();

    ItemStack getStackInSlot(int slot);

    List<ItemStack> getItems();

    void setSize(int size);

    boolean isEmpty();

    /**
     * @return remainder
     */
    ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

    ItemStack extractItem(int slot, int amount, boolean simulate);

    boolean isItemValid(int slot, ItemStack stack);

    default int getSizeLimit(int slot){
        return 64;
    }

    void setItem(int i,ItemStack stack);
}
