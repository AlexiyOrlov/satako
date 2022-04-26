package dev.buildtool.satako;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

/**
 * General handler, permits all items. Merges same itemstacks first.
 */
public class ItemHandler extends ItemStackHandler
{
    private BlockEntity owner;

    public ItemHandler(int size)
    {
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

    public ItemHandler(NonNullList<ItemStack> itemStacks)
    {
        super(itemStacks);
    }

    public NonNullList<ItemStack> getItems()
    {
        return stacks;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (stack.isEmpty())
        {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = getStackInSlot(slot);

        int m;
        if (!stackInSlot.isEmpty())
        {
            if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot)))
            {
                return stack;
            }

            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
            {
                return stack;
            }

            if (!isItemValid(slot, stack))
            {
                return stack;
            }

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();

            if (stack.getCount() <= m)
            {
                if (!simulate)
                {
                    ItemStack copy = stack.copy();
                    copy.grow(stackInSlot.getCount());
                    setStackInSlot(slot, copy);
                    onContentsChanged(slot);
                }

                return ItemStack.EMPTY;
            }
            else
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate)
                {
                    ItemStack copy = stack.split(m);
                    copy.grow(stackInSlot.getCount());
                    setStackInSlot(slot, copy);
                    onContentsChanged(slot);
                }
                else
                {
                    stack.shrink(m);
                }
                return stack;
            }
        }
        else
        {
            if (!isItemValid(slot, stack))
            {
                return stack;
            }

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.getCount())
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate)
                {
                    setStackInSlot(slot, stack.split(m));
                    onContentsChanged(slot);
                }
                else
                {
                    stack.shrink(m);
                }
                return stack;
            }
            else
            {
                if (!simulate)
                {
                    setStackInSlot(slot, stack);
                    onContentsChanged(slot);
                }
                return ItemStack.EMPTY;
            }
        }
    }

    public BlockEntity getOwner() {
        return owner;
    }

    /**
     * @return whether all stacks are empty
     */
    public boolean isEmpty()
    {

        for (ItemStack stack : stacks)
        {
            if (!stack.isEmpty())
                return false;
        }
        return true;
    }

    @Override
    protected void onContentsChanged(int slot)
    {
        if (owner != null) owner.setChanged();
    }
}
