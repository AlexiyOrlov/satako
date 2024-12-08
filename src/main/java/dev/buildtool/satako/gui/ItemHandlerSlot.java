package dev.buildtool.satako.gui;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.IntegerColor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Default item handler slot
 */
public class ItemHandlerSlot extends SlotItemHandler {
    /**
     * Visible
     */
    protected boolean active = true;
    private IntegerColor color = Constants.BLUE;
    private ResourceLocation texture;
    protected List<Component> tooltip;

    public ItemHandlerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return !stack.isEmpty();
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return true;
    }

    public ItemHandlerSlot setTexture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    public ItemHandlerSlot setColor(IntegerColor color) {
        this.color = color;
        return this;
    }

    public IntegerColor getColor() {
        return color;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ItemHandlerSlot setTooltip(List<Component> tooltip)
    {
        this.tooltip=tooltip;
        return this;
    }

    public List<Component> getTooltip()
    {
        return  tooltip;
    }
}
