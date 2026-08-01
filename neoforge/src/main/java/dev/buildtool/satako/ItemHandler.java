package dev.buildtool.satako;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;

/**
 * General handler, permits all items. Merges same itemstacks first.
 */
public class ItemHandler extends ItemStackHandler implements ItemContainer {
    private BlockEntity owner;

    public ItemHandler(int size) {
        super(size);
    }

    /**
     * @param tileEntity an owner of this ItemHandler
     */
    public ItemHandler(int size, @Nullable BlockEntity tileEntity) {
        this(size);
        owner = tileEntity;
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);

    }

    public ItemHandler(NonNullList<ItemStack> itemStacks) {
        super(itemStacks);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return super.serializeNBT(provider);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
    }

    @Override
    public int getSlotCount() {
        return getSlots();
    }

    public NonNullList<ItemStack> getItems() {
        return stacks;
    }

    public BlockEntity getOwner() {
        return owner;
    }

    /**
     * @return whether all stacks are empty
     */
    public boolean isEmpty() {

        for (ItemStack stack : stacks) {
            if (!stack.isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public void setItem(int i, ItemStack stack) {
        if(isItemValid(i,stack))
            stacks.set(i,stack);
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (owner != null) owner.setChanged();
    }

    @Override
    public void setSize(int size) {
        NonNullList<ItemStack> old = stacks;
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        for (int i = 0; i < Math.min(stacks.size(), old.size()); i++) {
            stacks.set(i, old.get(i));
        }
    }
}
