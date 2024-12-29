package dev.buildtool.satako;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface ItemContainer {

    CompoundTag serializeNBT(HolderLookup.Provider provider);

    void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt);

    int getSlotCount();

    ItemStack getStackInSlot(int slot);

    NonNullList<ItemStack> getItems();

    void setSize(int size);

    boolean isEmpty();

    ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

    ItemStack extractItem(int slot, int amount, boolean simulate);

    boolean isItemValid(int slot, ItemStack stack);
}
