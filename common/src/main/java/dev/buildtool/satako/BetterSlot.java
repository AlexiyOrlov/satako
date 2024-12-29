package dev.buildtool.satako;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

@SuppressWarnings("unused")
public class BetterSlot extends Slot {
    protected IntegerColor color = Constants.BLUE;
    protected ResourceLocation texture;
    protected boolean enabled = true;
    protected Component tooltip;

    public BetterSlot(Container container, int i, int j, int k, Component tooltip) {
        super(container, i, j, k);
        this.tooltip = tooltip;
    }

    public BetterSlot(Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    public BetterSlot setColor(IntegerColor color) {
        this.color = color;
        return this;
    }

    public BetterSlot setTexture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public IntegerColor getColor() {
        return color;
    }

    /**
     * Is visible?
     */
    @Override
    public boolean isActive() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean hasTexture() {
        return texture != null;
    }

    public Component getTooltip() {
        return tooltip;
    }
}
