package dev.buildtool.satako.test;

import dev.buildtool.satako.Menu;
import dev.buildtool.satako.Satako;
import dev.buildtool.satako.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TestMenu extends Menu {
    public TestMenu(int i, Inventory playerInventory, FriendlyByteBuf byteBuf) {
        super(Services.PLATFORM.getTestMenu(), i, playerInventory, byteBuf);
        Container container = new SimpleContainer(1);
        Slot slot = new Slot(container, 0, 0, 0);
        addSlot(slot);
        slot.set(new ItemStack(Items.COPPER_INGOT));
    }
}
