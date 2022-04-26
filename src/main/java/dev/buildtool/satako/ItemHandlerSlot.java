package dev.buildtool.satako;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

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
}
