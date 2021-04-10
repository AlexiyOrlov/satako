package dev.buildtool.satako.gui;

import dev.buildtool.satako.Constants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

/**
 * Slot for displaying the item only
 */
public class ItemHandlerDisplaySlot extends SlotItemHandler implements Scrollable, Hideable
{
    public boolean verticalScroll, horizontalScroll;
    public boolean isVisible = true, enabled = true;
    public int scrollAmount;

    public ItemHandlerDisplaySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition)
    {
        this(itemHandler, index, xPosition, yPosition, Constants.SLOTWITHBORDERSIZE);
    }

    public ItemHandlerDisplaySlot(IItemHandler itemHandler, int index, int xPos, int yPos, int scrollAmount)
    {
        super(itemHandler, index, xPos, yPos);
        this.scrollAmount = scrollAmount;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public void setEnabled()
    {
        enabled = true;
    }

    @Override
    public void setDisabled()
    {
        enabled = false;
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
        if (itemHandler instanceof IItemHandlerModifiable)
        {
            super.set(stack);
        }
    }

    /**
     * Scrolls object for specified amount
     *
     * @param amount
     * @param vertical whether scroll is vertical
     */
    @Override
    public void scroll(int amount, boolean vertical)
    {
        if (enabled)
        {

            if (vertical && verticalScroll)
            {
//                yPos += scrollAmount * Math.signum(amount);
            }
            else if (horizontalScroll)
            {
//                xPos += scrollAmount * Math.signum(amount);
            }
        }
    }

    @Override
    public void setScrollable(boolean vertical, boolean b)
    {
        if (b)
        {
            if (vertical)
            {
                verticalScroll = true;
            }
            else
            {
                horizontalScroll = true;
            }
        }
        else
        {
            if (vertical)
            {
                verticalScroll = false;
            }
            else
            {
                horizontalScroll = false;
            }
        }
    }

    @Override
    public void setHidden()
    {
        isVisible = false;
    }

    @Override
    public void setVisible()
    {
        isVisible = true;
    }
}
