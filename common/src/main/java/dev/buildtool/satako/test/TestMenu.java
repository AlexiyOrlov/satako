package dev.buildtool.satako.test;

import dev.buildtool.satako.Menu;
import dev.buildtool.satako.Satako;
import dev.buildtool.satako.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class TestMenu extends Menu {
    public TestMenu(int i, Inventory playerInventory, FriendlyByteBuf byteBuf) {
        super(Services.PLATFORM.getTestMenu(), i, playerInventory, byteBuf);
        Container container = new SimpleContainer(1);
        addSlot(new Slot(container, 0, 0, 0));
    }
}
