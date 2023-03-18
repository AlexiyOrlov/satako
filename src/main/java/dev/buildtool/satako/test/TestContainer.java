package dev.buildtool.satako.test;

import dev.buildtool.satako.*;
import dev.buildtool.satako.gui.ItemHandlerDisplaySlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TestContainer extends Container2 {
    public TestContainer(int i, Inventory playerInventory) {
        super(Satako.TEST_CONTAINER.get(), i);
        ItemHandler itemHandler = new ItemHandler(1);
        addSlot(new ItemHandlerDisplaySlot(itemHandler, 0, 0, 0).setColor(new IntegerColor(0xff4f6a7b)));
        ItemHandler testItemMove = new ItemHandler(2);
        addSlot(new ItemHandlerSlot(testItemMove, 0, 20, 0));
        addSlot(new ItemHandlerSlot(testItemMove, 1, 40, 0));
        addPlayerInventory(0, 20, playerInventory);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack stack = getSlot(index).getItem();
        if (index > 2) {
            if (!moveItemStackTo(stack, 1, 3, false))
                return ItemStack.EMPTY;
        }
        return super.quickMoveStack(playerIn, index);
    }
}
