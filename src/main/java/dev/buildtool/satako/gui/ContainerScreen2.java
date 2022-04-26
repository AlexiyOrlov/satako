package dev.buildtool.satako.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import dev.buildtool.satako.Constants;
import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.ItemHandlerSlot;
import dev.buildtool.satako.Methods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
    protected ArrayList<ScrollList> lists = new ArrayList<>(0);
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
        lists.clear();
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
    public void render(PoseStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
        renderBackground(matrixStack);
        super.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);
        renderTooltip(matrixStack, p_render_1_, p_render_2_);
    }

    /**
     * Draws its elements and borders
     */
    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        List<Slot> slots = getSlots();
        GlStateManager._clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        for (Slot s : slots) {
            if (s.isActive()) {
                int sx = s.x;
                int sy = s.y;
                ;
                TextureManager textureManager = minecraft.getTextureManager();
                if (s instanceof ItemHandlerSlot) {
                    ItemHandlerSlot itemHandlerSlot = (ItemHandlerSlot) s;
                    if (itemHandlerSlot.getTexture() == null) {
                        //color
                        fill(matrixStack, sx + leftPos, sy + topPos, sx + leftPos + 16, sy + topPos + 16, itemHandlerSlot.getColor().getIntColor());
                    } else {
                        textureManager.getTexture(itemHandlerSlot.getTexture());
                        blit(matrixStack, sx + leftPos, sy + topPos, 0, 0, 16, 16);
                    }
                }
                else {
                    Pair<ResourceLocation, ResourceLocation> atlasAndSprite = s.getNoItemIcon();
                    if (atlasAndSprite != null) {
                        ResourceLocation background = atlasAndSprite.getSecond();
                        if (background.getNamespace().equals("minecraft")) {
                            textureManager.bindForSetup(Constants.GREY_SLOT_TEXTURE);
                        } else {
                            textureManager.bindForSetup(background);
                        }
                    } else {
                        textureManager.bindForSetup(Constants.GREY_SLOT_TEXTURE);
                    }
                    blit(matrixStack,sx + leftPos, sy + topPos, 0, 0, 16, 16);
                }
            }
        }

        IntegerColor color = Constants.BLUE;
        lists.forEach(ScrollList::draw);

        //lines have to be drawn after everything else
        if (drawBorders)
        {
            Methods.drawHorizontalLine(this.leftPos, this.getXSize()+leftPos, this.topPos, color, 2);
            Methods.drawHorizontalLine(this.leftPos,  this.getXSize()+leftPos, this.getYSize()+topPos, color, 2);
            Methods.drawVerticalLine(this.leftPos, this.topPos, getYSize()+this.topPos, color, 2);
            Methods.drawVerticalLine(getXSize() + this.leftPos, this.topPos, getYSize() + this.topPos, color, 2);

        }
    }

    public void addScrollList(ScrollList list) {
        lists.add(list);
    }

    @Override
    protected void renderLabels(PoseStack p_230451_1_, int p_230451_2_, int p_230451_3_) {
        //don't render labels
    }
}
