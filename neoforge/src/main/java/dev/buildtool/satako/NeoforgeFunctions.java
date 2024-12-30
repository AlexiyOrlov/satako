package dev.buildtool.satako;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class NeoforgeFunctions {
    public static FluidStack getCachedFluidStack(Fluid fluid) {
        return SatakoNeoforge.FLUID_STACK_CACHE.computeIfAbsent(fluid, fluid1 -> new FluidStack(fluid1, 1));
    }

    /**
     * Extracts an itemstack
     *
     * @return extracted ItemStack
     */
    public static ItemStack extractItems(IItemHandler itemHandler, ItemStack itemStack, boolean simulate) {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack presentstack = itemHandler.getStackInSlot(slot);
            if (Functions.areItemTypesEqual(itemStack, presentstack)) {
                return itemHandler.extractItem(slot, itemStack.getCount(), simulate);
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Inserts an item into a handler. Merges present stacks first
     *
     * @param itemStack will be copied
     * @return true if fully inserted, false if not
     */
    public static boolean insertItem(IItemHandler iItemHandler, ItemStack itemStack) {
        ItemStack out = ItemHandlerHelper.insertItemStacked(iItemHandler, itemStack.copy(), false);
        itemStack.setCount(out.getCount());
        return out.isEmpty();
    }

    /**
     * Tests whether a stack can be fully inserted
     */
    public static boolean canInsertItem(IItemHandler into, ItemStack stack) {
        if (stack.isEmpty())
            return false;
        int slots = into.getSlots();
        for (int i = 0; i < slots; i++) {
            ItemStack pressent = into.getStackInSlot(i);
            if (into.isItemValid(i, stack) && Functions.areItemTypesEqual(stack, pressent)) {
                ItemStack result = into.insertItem(i, stack, true);
                if (result.isEmpty()) {
                    return true;
                }
            }
        }

        for (int i = 0; i < slots; i++) {
            ItemStack next = into.getStackInSlot(i);
            if (next.isEmpty() && into.isItemValid(i, stack)) {
                ItemStack rem = into.insertItem(i, stack, true);
                if (rem.isEmpty()) {
                    return true;
                }

            }
        }
        return false;
    }

    public static boolean contains(Item item, IItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack next = handler.getStackInSlot(i);
            if (next.is(item)) {
                return true;
            }
        }

        return false;
    }

    public static ItemStack findItem(Item item, IItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack next = handler.getStackInSlot(i);
            if (next.is(item)) {
                return next;
            }
        }

        return ItemStack.EMPTY;
    }

    /**
     * @return slot number or -1 if not found
     */
    public static int findItemIn(IItemHandler itemHandler, ItemStack stack) {
        int size = itemHandler.getSlots();
        for (int slot = 0; slot < size; slot++)
        {
            ItemStack nextstack = itemHandler.getStackInSlot(slot);
            if (Functions.areItemTypesEqual(nextstack, stack))
            {
                return slot;
            }
        }
        return -1;
    }
}
