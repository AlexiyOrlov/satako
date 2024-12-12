package dev.buildtool.satako;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Slot for displaying the item only
 */
public class ItemHandlerDisplaySlot extends ItemHandlerSlot {
    public int scrollAmount;

    public ItemHandlerDisplaySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        this(itemHandler, index, xPosition, yPosition, Constants.SLOTWITHBORDERSIZE);
    }

    public ItemHandlerDisplaySlot(IItemHandler itemHandler, int index, int xPos, int yPos, int scrollAmount)
    {
        super(itemHandler, index, xPos, yPos);
        this.scrollAmount = scrollAmount;
    }

    @Override
    public boolean mayPickup(Player playerIn) {
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
}
