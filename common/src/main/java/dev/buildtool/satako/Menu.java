package dev.buildtool.satako;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Menu extends AbstractContainerMenu {
    public Menu(@Nullable MenuType<?> menuType, int i, Inventory playerInventory, FriendlyByteBuf byteBuf) {
        super(menuType, i);
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

            if (clickedStack.getCount() == stack.getCount()) {
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

    protected void addPlayerInventory(int y, Inventory inventory) {
        addPlayerInventory(0, y, inventory);
    }

    protected void addPlayerInventoryWithLockedItem(int verticalMargin, Inventory inventory, Item locked) {
        int index = 0;

        for (int i = 4; i > 0; --i) {
            for (int j = 0; j < 9; ++j) {
                ItemStack stack = inventory.getItem(index);
                if (stack.getItem() == locked) {
                    this.addSlot(new DisplaySlot(inventory, index, 18 * j, 18 * i + verticalMargin));
                } else {
                    this.addSlot(new Slot(inventory, index, 18 * j, 18 * i + verticalMargin));
                }

                ++index;
            }
        }

    }
}
