package dev.buildtool.satako.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import dev.buildtool.satako.Constants;
import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.ItemHandlerSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
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
                if (s instanceof ItemHandlerSlot itemHandlerSlot) {
                    if (itemHandlerSlot.getTexture() == null) {
                        //color
                        fill(matrixStack, sx + leftPos, sy + topPos, sx + leftPos + 16, sy + topPos + 16, itemHandlerSlot.getColor().getIntColor());
                    } else {
                        textureManager.bindForSetup(itemHandlerSlot.getTexture());
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
                        RenderSystem.setShader(GameRenderer::getPositionTexShader);
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                        RenderSystem.setShaderTexture(0, Constants.GREY_SLOT_TEXTURE);

                    }
                    blit(matrixStack,sx + leftPos, sy + topPos, 0, 0, 16, 16);
                }
            }
        }

        IntegerColor color = Constants.BLUE;

        //lines have to be drawn after everything else
        if (drawBorders) {
            int intColor = color.getIntColor();
            hLine(matrixStack, this.leftPos - 1, this.getXSize() + leftPos - 2, this.topPos - 1, intColor);
            hLine(matrixStack, this.leftPos, this.getXSize() + leftPos - 2, this.getYSize() + topPos - 2, intColor);
            vLine(matrixStack, this.leftPos - 1, this.topPos - 1, getYSize() + this.topPos - 1, intColor);
            vLine(matrixStack, getXSize() + this.leftPos - 2, this.topPos - 1, getYSize() + this.topPos - 2, intColor);
        }
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int p1, int p2) {
        font.draw(poseStack, title, imageWidth / 2f - font.width(title.getString()) / 2f, -14, Constants.ORANGE_COLOR);
    }
}
