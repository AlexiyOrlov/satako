package dev.buildtool.satako;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ItemList implements ItemContainer{
    protected NonNullList<ItemStack> itemStacks;

    public ItemList(int size) {
        itemStacks = NonNullList.withSize(size,ItemStack.EMPTY);
    }


    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbtCompound = new CompoundTag();
        for (int i = 0; i < itemStacks.size(); i++) {
            ItemStack itemStack = itemStacks.get(i);
            nbtCompound.put("Stack#" + i, itemStack.save(provider));
        }
        nbtCompound.putInt("Size", itemStacks.size());
        return nbtCompound;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        int count = nbt.getInt("Size");
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                int finalI = i;
                ItemStack.parse(provider,nbt.getCompound("Stack#" + i)).ifPresent(itemStack ->itemStacks.set(finalI,itemStack));
            }
        }
    }

    @Override
    public int getSlotCount() {
        return itemStacks.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return itemStacks.get(slot);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return itemStacks;
    }

    @Override
    public void setSize(int size) {
        NonNullList<ItemStack> old = itemStacks;
         itemStacks = NonNullList.withSize(size, ItemStack.EMPTY);
        for (int i = 0; i < Math.min(itemStacks.size(), old.size()); i++) {
            itemStacks.set(i, old.get(i));
        }
    }

    @Override
    public boolean isEmpty() {
        return Functions.isEmpty(itemStacks);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        ItemStack present=itemStacks.get(slot);
        if(Functions.areItemTypesEqual(present,stack))
        {
            int newCount = present.getCount() + stack.getCount();
            int remaining=present.getCount()+stack.getCount()-present.getMaxStackSize();
            if(!simulate)
                present.setCount(Math.min(present.getMaxStackSize(),newCount));
            return new ItemStack(stack.getItem(),remaining);
        }
        else {
            return stack;
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack out=itemStacks.get(slot);
        int toExtract = Math.min(amount, out.getCount());
        if(!simulate)
            out.setCount(out.getCount()-toExtract);
        return new ItemStack(out.getItem(), toExtract);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }
}
