package dev.buildtool.satako;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class ContainerSlot extends BetterSlot{
    protected ItemContainer itemContainer;
    protected static final Container dummy=new SimpleContainer(0);
    public ContainerSlot(ItemContainer container, int i, int j, int k, Component tooltip) {
        super(dummy, i, j, k, tooltip);
        itemContainer=container;
    }

    public ContainerSlot(ItemContainer container, int index, int x, int y) {
        super(dummy, index, x, y);
        itemContainer=container;
    }

    @Override
    public void set(ItemStack stack) {
        itemContainer.setItem(getContainerSlot(),stack);
    }

    @Override
    public void setChanged() {

    }

    @Override
    public int getMaxStackSize() {
        return itemContainer.getSizeLimit(getContainerSlot());
    }

    @Override
    public ItemStack remove(int amount) {
        return itemContainer.extractItem(getContainerSlot(),amount,false);
    }

    @Override
    public ItemStack getItem() {
        return itemContainer.getStackInSlot(getContainerSlot());
    }
}
