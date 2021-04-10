package dev.buildtool.satako.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.util.Pair;
import dev.buildtool.satako.Constants;
import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.ItemHandlerSlot;
import dev.buildtool.satako.Methods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * A preset GUI which automatically draws labels, slot textures,
 * GUI borders and sends input events to elements
 */
public class ContainerScreen2<T extends net.minecraft.inventory.container.Container> extends ContainerScreen<T>
{
    protected ArrayList<ScrollList> lists = new ArrayList<>(0);
    protected int centerX, centerY;
    protected ArrayList<Page> pages = new ArrayList<>(0);
    protected boolean drawBorders;

    public ContainerScreen2(T container, PlayerInventory playerInventory, ITextComponent name, boolean drawBorders_)
    {
        super(container, playerInventory, name);
        if (Minecraft.getInstance().currentScreen != null)
        {
            Minecraft.getInstance().currentScreen = null;
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
        int maxX = 0, minX = xSize, maxY = 0;
        for (Slot slot : getSlots())
        {
            int x = slot.xPos;
            if (x > maxX)
                maxX = x;
            if (minX > x)
                minX = x;
            int y = slot.yPos;
            if (y > maxY)
                maxY = y;
        }
        xSize = maxX + Constants.SLOTWITHBORDERSIZE;
        ySize = maxY + Constants.SLOTWITHBORDERSIZE;
        super.init();
        centerX = width / 2;
        centerY = height / 2;

    }

    protected List<Slot> getSlots()
    {
        return container.inventorySlots;
    }

    @Override
    public void render(MatrixStack matrixStack,int p_render_1_, int p_render_2_, float p_render_3_)
    {
        renderBackground(matrixStack);
        super.render(matrixStack,p_render_1_, p_render_2_, p_render_3_);
        renderHoveredTooltip(matrixStack,p_render_1_, p_render_2_);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {

    }

    /**
     * Draws its elements and borders
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack,float partialTicks, int mouseX, int mouseY)
    {
        List<Slot> slots = getSlots();
        GlStateManager.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        for (Slot s : slots)
        {
            if (s.isEnabled())
            {
                int sx = s.xPos;
                int sy = s.yPos;
                if(s instanceof ItemHandlerSlot)
                {
                    ItemHandlerSlot itemHandlerSlot= (ItemHandlerSlot) s;
                    if(itemHandlerSlot.getTexture()==null)
                    {
                        //color
                        fill(matrixStack,sx+guiLeft,sy+guiTop,sx+guiLeft+16,sy+guiTop+16,itemHandlerSlot.getColor().getIntColor());
                    }
                    else
                    {
                        minecraft.getTextureManager().bindTexture(itemHandlerSlot.getTexture());
                        blit(matrixStack,sx+guiLeft,sy+guiTop,0,0,16,16);
                    }
                }
                else {
                    Pair<ResourceLocation, ResourceLocation> atlasAndSprite = s.getBackground();
                    if (atlasAndSprite != null) {
                        ResourceLocation background = atlasAndSprite.getSecond();
                        if (background.getNamespace().equals("minecraft")) {
                            minecraft.getTextureManager().bindTexture(Constants.GREY_SLOT_TEXTURE);
                        } else {
                            minecraft.getTextureManager().bindTexture(background);
                        }
                    } else {
                        minecraft.getTextureManager().bindTexture(Constants.GREY_SLOT_TEXTURE);
                    }
                    blit(matrixStack,sx + guiLeft, sy + guiTop, 0, 0, 16, 16);
                }
            }
        }

        IntegerColor color = Constants.BLUE;
        lists.forEach(ScrollList::draw);

        //lines have to be drawn after everything else
        if (drawBorders)
        {
            Methods.drawHorizontalLine(this.guiLeft, this.xSize+guiLeft, this.guiTop, color, 2);
            Methods.drawHorizontalLine(this.guiLeft,  this.xSize+guiLeft, this.ySize+guiTop, color, 2);
            Methods.drawVerticalLine(this.guiLeft, this.guiTop, ySize+this.guiTop, color, 2);
            Methods.drawVerticalLine(xSize+this.guiLeft , this.guiTop, ySize+this.guiTop, color, 2);

        }
    }

    public void addScrollList(ScrollList list)
    {
        lists.add(list);
    }

}
