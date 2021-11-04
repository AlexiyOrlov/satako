package dev.buildtool.satako;

import dev.buildtool.satako.gui.ItemHandlerDisplaySlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

/**
 * Created on 24/10/16
 */
public class Container2 extends Container
{
    public Container2(@Nullable ContainerType<?> type, int i)
    {
        super(type, i);
    }

    protected void addPlayerInventory(PlayerEntity player, int horizontalOffset, int verticalOffset)
    {

        for (int row = 0; row < 3; ++row)
        {
            for (int column = 0; column < 9; ++column)
            {
                Slot slot = new Slot(player.inventory, column + row * 9 + 9, column * 18 + horizontalOffset, row * 18 + verticalOffset);
                this.addSlot(slot);
            }
        }

        for (int i1 = 0; i1 < 9; ++i1)
        {
            Slot slotIn = new Slot(player.inventory, i1, i1 * 18 + horizontalOffset, 3 * 18 + verticalOffset);
            this.addSlot(slotIn);
        }
    }

    /**
     * Uses IItemHandler
     */
    protected void addPlayerInventory(int horizontalMargin, int verticalMargin, PlayerEntity player)
    {
        IItemHandler inventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElse(null);
        int index = 0;
        for (int i = 4; i > 0; i--)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlot(new ItemHandlerSlot(inventory, index++, 3 + 18 * j + horizontalMargin, 3 + 18 * i + verticalMargin));
            }
        }
    }

    protected void addPlayerInventory(int horOffset,int verOffset, PlayerInventory inventory)
    {
        for (int row = 0; row < 3; ++row)
        {
            for (int column = 0; column < 9; ++column)
            {
                Slot slot = new Slot(inventory, column + row * 9 + 9, column * 18 + horOffset, row * 18 + verOffset);
                this.addSlot(slot);
            }
        }

        for (int i1 = 0; i1 < 9; ++i1)
        {
            Slot slotIn = new Slot(inventory, i1, i1 * 18 + horOffset, 3 * 18 + verOffset);
            this.addSlot(slotIn);
        }
    }

    protected void addPlayerInventoryWithLockedItem(int horizontalMargin, int verticalMargin, PlayerEntity player, Item locked)
    {
        IItemHandler inventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElse(null);
        int index = 0;
        for (int i = 4; i > 0; i--)
        {
            for (int j = 0; j < 9; j++)
            {
                ItemStack stack = inventory.getStackInSlot(index);
                if (stack.getItem() == locked)
                {
                    addSlot(new ItemHandlerDisplaySlot(inventory, index, 3 + 18 * j + horizontalMargin, 3 + 18 * i + verticalMargin));
                }
                else
                {
                    addSlot(new ItemHandlerSlot(inventory, index, 3 + 18 * j + horizontalMargin, 3 + 18 * i + verticalMargin));
                }
                index++;
            }
        }
    }

    /**
     * To change item count by 1 - click with empty cursor; to change count by 10 - Shift + click with empty cursor;
     * to change by arbitrary number - click with same item with appropriate size; remove item - middle click
     *
     * @param buttonType 0 - primary, 1 - secondary, 2 - middle
     */
    protected ItemStack doPhantomItemBehavior(int slotId, int buttonType, ClickType clickTypeIn, PlayerEntity player, IItemHandlerModifiable itemHandler)
    {
        ItemStack current = player.inventory.getCarried();
        if (slotId >= 0 && slotId <= itemHandler.getSlots() - 1)
        {
            ItemStack stackInslot = itemHandler.getStackInSlot(slotId);
            if (current.isEmpty())
            {
                if (!stackInslot.isEmpty())
                {
                    if (buttonType == 2)
                    {
                        itemHandler.setStackInSlot(slotId, ItemStack.EMPTY);
                    }
                    else if (buttonType == 1)
                    {
                        if (clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.PICKUP_ALL)
                        {
                            stackInslot.shrink(1);
                        }
                        else if (clickTypeIn == ClickType.QUICK_MOVE)
                        {
                            stackInslot.shrink(10);
                        }
                    }
                    //button=0
                    else
                    {
                        if (clickTypeIn == ClickType.PICKUP)
                        {
                            stackInslot.grow(1);
                        }
                        else if (clickTypeIn == ClickType.QUICK_MOVE)
                        {
                            stackInslot.grow(10);
                        }
                    }
                }
            }
            else
            {
                if (stackInslot.isEmpty())
                {
                    itemHandler.setStackInSlot(slotId, current.copy());
                }
                else
                {
                    if (Functions.areItemTypesEqual(stackInslot, current))
                    {
                        if (buttonType == 0)
                        {
                            if (clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.PICKUP_ALL)
                            {
                                stackInslot.grow(current.getCount());
                            }
                        }
                        else
                        {
                            if (clickTypeIn == ClickType.PICKUP)
                            {
                                stackInslot.shrink(current.getCount());
                            }
                        }
                    }
                }
            }
            broadcastChanges();
        }
        return current;
    }


    /**
     * Does nothing when Shift+clicked and doesn't cause NPE
     */
    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index)
    {
        Slot clickedSlot = this.slots.get(index);
        ItemStack stack;
        if (clickedSlot.hasItem())
        {
            ItemStack clickedStack = clickedSlot.getItem();
            stack = clickedStack.copy();
            if (clickedStack.getCount() == 0)
            {
                clickedSlot.set(ItemStack.EMPTY);
            }
            else
            {
                clickedSlot.setChanged();
            }

            if (clickedStack.getCount() == stack.getCount())
            {
                return ItemStack.EMPTY;
            }

            clickedSlot.onTake(playerIn, clickedStack);
        }
        return super.quickMoveStack(playerIn, index);
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn)
    {
        return true;
    }
}
