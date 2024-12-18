package dev.buildtool.satako.test;

import dev.buildtool.satako.Container2;
import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.ItemHandler;
import dev.buildtool.satako.Satako;
import dev.buildtool.satako.ItemHandlerDisplaySlot;
import dev.buildtool.satako.ItemHandlerSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class TestContainer extends Container2 {
    public TestContainer(int i, Inventory playerInventory) {
        super(Satako.TEST_CONTAINER.get(), i);
        ItemHandler itemHandler = new ItemHandler(2);
        addSlot(new ItemHandlerDisplaySlot(itemHandler, 0, 0, 0).setColor(new IntegerColor(0xff4f6a7b)).setTooltip(List.of(Component.literal("Tooltip line 1"),Component.literal("Tooltip line 2"))));
        addSlot(new ItemHandlerSlot(itemHandler, 1, 18, 0).setColor(new IntegerColor(0xff4f6a7b)).setTooltip(List.of(Component.literal("Tooltip line 1"),Component.literal("Tooltip line 2"))));
        addPlayerInventory(0, 20, playerInventory);
    }
}
