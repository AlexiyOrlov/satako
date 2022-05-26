package dev.buildtool.satako.test;

import dev.buildtool.satako.Container2;
import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.ItemHandler;
import dev.buildtool.satako.Satako;
import dev.buildtool.satako.gui.ItemHandlerDisplaySlot;
import net.minecraft.world.entity.player.Inventory;

public class TestContainer extends Container2 {
    public TestContainer(int i, Inventory playerInventory) {
        super(Satako.TEST_CONTAINER.get(), i);
        ItemHandler itemHandler = new ItemHandler(1);
        addSlot(new ItemHandlerDisplaySlot(itemHandler, 0, 0, 0).setColor(new IntegerColor(0xff4f6a7b)));
        addPlayerInventory(0, 20, playerInventory);
    }
}
