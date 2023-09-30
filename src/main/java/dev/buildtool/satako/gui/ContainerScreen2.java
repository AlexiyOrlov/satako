package dev.buildtool.satako.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.buildtool.satako.Constants;
import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.ItemHandlerSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

/**
 * A preset GUI which automatically draws labels, slot textures,
 * GUI borders and sends input events to elements
 */
public class ContainerScreen2<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected int centerX, centerY;
    protected ArrayList<Page> pages = new ArrayList<>(0);
    protected boolean drawBorders;

    public ContainerScreen2(T container, Inventory playerInventory, Component name, boolean drawBorders_) {
        super(container, playerInventory, name);
        if (Minecraft.getInstance().screen != null) {
            Minecraft.getInstance().screen = null;
        }
        drawBorders = drawBorders_;
    }

    /**
     * Called when GUI is created or resized.
     * Centers window and fits it to slots
     */
    @Override
    public void init()
    {
        int maxX = 0, minX = getXSize(), maxY = 0;
        for (Slot slot : getSlots())
        {
            int x = slot.x;
            if (x > maxX)
                maxX = x;
            if (minX > x)
                minX = x;
            int y = slot.y;
            if (y > maxY)
                maxY = y;
        }
        imageWidth = maxX + Constants.SLOTWITHBORDERSIZE;
        imageHeight = maxY + Constants.SLOTWITHBORDERSIZE;
        super.init();
        centerX = width / 2;
        centerY = height / 2;

    }

    protected List<Slot> getSlots()
    {
        return menu.slots;
    }

    @Override
    public void render(GuiGraphics matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
        renderBackground(matrixStack);
        super.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);
        renderTooltip(matrixStack, p_render_1_, p_render_2_);
    }

    /**
     * Draws its elements and borders
     */
    @Override
    protected void renderBg(GuiGraphics matrixStack, float partialTicks, int mouseX, int mouseY) {
        List<Slot> slots = getSlots();
        GlStateManager._clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        for (Slot s : slots) {
            if (s.isActive()) {
                int sx = s.x;
                int sy = s.y;
                if (s instanceof ItemHandlerSlot itemHandlerSlot) {
                    if (itemHandlerSlot.getTexture() == null) {
                        //color
                        matrixStack.fill(sx + leftPos, sy + topPos, sx + leftPos + 16, sy + topPos + 16, itemHandlerSlot.getColor().getIntColor());
                    } else {
                        matrixStack.fill(sx + leftPos, sy + topPos, sx + leftPos + 16, sy + topPos + 16, 0xff666666);
                    }
                } else {
                    matrixStack.fill(sx + leftPos, sy + topPos, sx + leftPos + 16, sy + topPos + 16, 0xff666666);
                }
            }
        }

        IntegerColor color = Constants.BLUE;

        //lines have to be drawn after everything else
        if (drawBorders) {
            int intColor = color.getIntColor();
            matrixStack.hLine(this.leftPos - 1, this.getXSize() + leftPos - 2, this.topPos - 1, intColor);
            matrixStack.hLine(this.leftPos, this.getXSize() + leftPos - 2, this.getYSize() + topPos - 2, intColor);
            matrixStack.vLine(this.leftPos - 1, this.topPos - 1, getYSize() + this.topPos - 1, intColor);
            matrixStack.vLine(getXSize() + this.leftPos - 2, this.topPos - 1, getYSize() + this.topPos - 2, intColor);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics poseStack, int p1, int p2) {
        poseStack.drawString(font, title, imageWidth / 2 - font.width(title.getString()) / 2, -14, 0xE35F3B);
    }

    @Override
    public boolean mouseDragged(double p_97752_, double p_97753_, int p_97754_, double p_97755_, double p_97756_) {
        for (GuiEventListener child : this.children()) {
            child.mouseDragged(p_97752_, p_97753_, p_97754_, p_97755_, p_97756_);
        }
        return super.mouseDragged(p_97752_, p_97753_, p_97754_, p_97755_, p_97756_);
    }
}
