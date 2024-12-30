package dev.buildtool.satako;

import com.google.common.collect.Lists;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemList implements ItemContainer{
    protected ArrayList<ItemStack> itemStacks;

    public ItemList(int size) {
        itemStacks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            itemStacks.add(ItemStack.EMPTY);
        }
    }

    public ItemList(List<ItemStack> itemStacks) {
        this.itemStacks =new ArrayList<>(itemStacks.size());
        this.itemStacks.addAll(itemStacks);
    }

    public ItemList(int capacity,List<ItemStack> itemStacks)
    {
        this.itemStacks=new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            if(i<itemStacks.size())
                this.itemStacks.add(itemStacks.get(i));
            else
                this.itemStacks.add(ItemStack.EMPTY);
        }
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbtCompound = new CompoundTag();
        for (int i = 0; i < itemStacks.size(); i++) {
            ItemStack itemStack = itemStacks.get(i);
            if(!itemStack.isEmpty())
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
                String key = "Stack#" + i;
                if(nbt.contains(key))
                    ItemStack.parse(provider,nbt.getCompound(key)).ifPresent(itemStack ->itemStacks.set(finalI,itemStack));
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
    public List<ItemStack> getItems() {
        return itemStacks;
    }

    @Override
    public void setSize(int size) {
        List<ItemStack> old = itemStacks;
         itemStacks = new ArrayList<>(size);
        for (int i = 0; i < Math.min(size, old.size()); i++) {
            itemStacks.add(i, old.get(i));
        }
    }

    @Override
    public boolean isEmpty() {
        return Functions.isEmpty(itemStacks);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        ItemStack present=itemStacks.get(slot);
        if(isItemValid(slot,stack))
        {
            if(present.isEmpty())
            {
                if(!simulate)
                    setItem(slot,stack);
                return ItemStack.EMPTY;
            } else if (Functions.areItemTypesEqual(present, stack)) {
                int newCount = present.getCount() + stack.getCount();
                int remaining = present.getCount() + stack.getCount() - present.getMaxStackSize();
                if (!simulate)
                    present.setCount(Math.min(present.getMaxStackSize(), newCount));
                return new ItemStack(stack.getItem(), remaining);
            }
            return stack;
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

    @Override
    public void setItem(int i, ItemStack stack) {
        if(isItemValid(i,stack))
            itemStacks.set(i,stack);
    }
}
