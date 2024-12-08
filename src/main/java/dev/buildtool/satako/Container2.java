package dev.buildtool.satako;

import dev.buildtool.satako.gui.ItemHandlerDisplaySlot;
import dev.buildtool.satako.gui.ItemHandlerSlot;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

/**
 * Created on 24/10/16
 */
public class Container2 extends AbstractContainerMenu {
    public Container2(MenuType<?> type, int i) {
        super(type, i);
    }

    protected void addPlayerInventory(Player player, int horizontalOffset, int verticalOffset) {

        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                Slot slot = new Slot(player.getInventory(), column + row * 9 + 9, column * 18 + horizontalOffset, row * 18 + verticalOffset);
                this.addSlot(slot);
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            Slot slotIn = new Slot(player.getInventory(), i1, i1 * 18 + horizontalOffset, 3 * 18 + verticalOffset);
            this.addSlot(slotIn);
        }
    }

    /**
     * Uses IItemHandler
     */
    protected void addPlayerInventory(int horizontalMargin, int verticalMargin, Player player) {
        IItemHandler inventory = player.getCapability(Capabilities.ItemHandler.ENTITY);
        int index = 0;
        for (int i = 4; i > 0; i--) {
            for (int j = 0; j < 9; j++) {
                addSlot(new ItemHandlerSlot(inventory, index++, 18 * j + horizontalMargin, 18 * i + verticalMargin));
            }
        }
    }

    protected void addPlayerInventory(int horOffset, int verOffset, Inventory inventory) {
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                Slot slot = new Slot(inventory, column + row * 9 + 9, column * 18 + horOffset, row * 18 + verOffset);
                this.addSlot(slot);
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            Slot slotIn = new Slot(inventory, i1, i1 * 18 + horOffset, 3 * 18 + verOffset);
            this.addSlot(slotIn);
        }
    }

    protected void addPlayerInventoryWithLockedItem(int horizontalMargin, int verticalMargin, Player player, Item locked) {
        IItemHandler inventory = player.getCapability(Capabilities.ItemHandler.ENTITY);
        int index = 0;
        for (int i = 4; i > 0; i--) {
            for (int j = 0; j < 9; j++) {
                ItemStack stack = inventory.getStackInSlot(index);
                if (stack.getItem() == locked) {
                    addSlot(new ItemHandlerDisplaySlot(inventory, index, 18 * j + horizontalMargin, 18 * i + verticalMargin));
                } else {
                    addSlot(new ItemHandlerSlot(inventory, index, 18 * j + horizontalMargin, 18 * i + verticalMargin));
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
    protected ItemStack doPhantomItemBehavior(int slotId, int buttonType, ClickType clickTypeIn, Player player, IItemHandlerModifiable itemHandler) {
        ItemStack current = getCarried();
        if (slotId >= 0 && slotId <= itemHandler.getSlots() - 1) {
            ItemStack stackInslot = itemHandler.getStackInSlot(slotId);
            if (current.isEmpty()) {
                if (!stackInslot.isEmpty()) {
                    if (buttonType == 2) {
                        itemHandler.setStackInSlot(slotId, ItemStack.EMPTY);
                    } else if (buttonType == 1) {
                        if (clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.PICKUP_ALL) {
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
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot clickedSlot = this.slots.get(index);
        ItemStack stack = ItemStack.EMPTY;
        if (clickedSlot.hasItem()) {
            ItemStack clickedStack = clickedSlot.getItem();
            stack = clickedStack.copy();
            if (clickedStack.getCount() == 0) {
                clickedSlot.set(ItemStack.EMPTY);
            } else {
                clickedSlot.setChanged();
            }

            if (clickedStack.getCount() == stack.getCount())
            {
                return ItemStack.EMPTY;
            }

            clickedSlot.onTake(playerIn, clickedStack);
        }
        return stack;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }
}
