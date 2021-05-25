package dev.buildtool.satako.gui;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.ItemHandlerSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Slot for displaying the item only
 */
public class ItemHandlerDisplaySlot extends ItemHandlerSlot {
    public int scrollAmount;
    private boolean active;

    public ItemHandlerDisplaySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        this(itemHandler, index, xPosition, yPosition, Constants.SLOTWITHBORDERSIZE);
    }

    public ItemHandlerDisplaySlot(IItemHandler itemHandler, int index, int xPos, int yPos, int scrollAmount)
    {
        super(itemHandler, index, xPos, yPos);
        this.scrollAmount = scrollAmount;
    }

    @Override
    public boolean mayPickup(PlayerEntity playerIn)
    {
        return false;
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return false;
    }

    @Override
    public void set(ItemStack stack)
    {
        IItemHandler itemHandler = getItemHandler();
        if (itemHandler instanceof IItemHandlerModifiable) {
            super.set(stack);
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
