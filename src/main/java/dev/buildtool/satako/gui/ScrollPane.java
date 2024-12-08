package dev.buildtool.satako.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.buildtool.satako.Constants;
import dev.buildtool.satako.IntegerColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.List;

public class ScrollPane extends AbstractWidget {
    public static final int SCROLL_BAR_HEIGHT = 27;
    public static final int SCROLL_BAR_WIDTH = 6;
    protected AbstractContainerMenu menu;
//    private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation("textures/gui/container/villager2.png");
    private static final IntegerColor backgroundColor = new IntegerColor(0x4466FC5e), barColor = new IntegerColor(0xE34063ff);
    int scrollOff;
    protected int topPos;
    protected boolean isDragging;
    protected List<AbstractWidget> guiListeners;
    protected AbstractContainerScreen<?> owner;

    public ScrollPane(int x, int y, int width, int height, Component label, AbstractContainerScreen<?> owner,AbstractWidget... widgets) {
        super(x, y, width, height, label);
        menu=owner.getMenu();
        topPos=owner.getGuiTop();
        guiListeners=List.of(widgets);
        this.owner=owner;
        guiListeners.forEach(pWidget -> {
            pWidget.setY(pWidget.getY()+getY());
//            owner.addRenderableWidget(pWidget);
        });
    }

    public ScrollPane(int x,int y,int width,int height,Component label,AbstractContainerScreen<?> owner,List<AbstractWidget> abstractWidgets)
    {
        this(x,y,width,height,label,owner,abstractWidgets.toArray(AbstractWidget[]::new));
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

    }

    @Override
    public void playDownSound(SoundManager p_93665_) {

    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.isDragging) {
            scrollOff= (int) Mth.clamp(pMouseY-height,0,height-SCROLL_BAR_HEIGHT);
            for (AbstractWidget guiListener : guiListeners) {
                guiListener.setY((int) (guiListener.getY()-20*Math.signum(pDragY)));
            }
            return true;
        } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        this.isDragging = !guiListeners.isEmpty() && pMouseX > getX() + width && pMouseX < getX() + width + SCROLL_BAR_WIDTH && pMouseY > getY() && pMouseY < getY() + height;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private void renderScroller(GuiGraphics pGuiGraphics, int pPosX, int pPosY) {
        if (!guiListeners.isEmpty()) {
//            pGuiGraphics.blit(VILLAGER_LOCATION, getX()+width, getY() + scrollOff, 0, 0.0F, 199.0F, SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT, 512, 256);
        }
    }

//    @Override
//    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
//        pGuiGraphics.drawString(Minecraft.getInstance().font, getMessage(),getX()+width/2-Minecraft.getInstance().font.width(getMessage())/2,getY()-12,0xE35F3B);
//        if (owner.getMinecraft().level != null) {
//            pGuiGraphics.fillGradient(getX(), getY(),getX()+ this.width,getY()+ this.height, Constants.GREEN.getIntColor(), Constants.GREEN.getIntColor());
//        } else {
//            owner.renderDirtBackground(pGuiGraphics);
//        }
//        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
//        if (!guiListeners.isEmpty()) {
//            int i = this.width;
//            int j = this.height;
//            this.renderScroller(pGuiGraphics, i, j);
//            RenderSystem.enableDepthTest();
//        }
//    }
}
