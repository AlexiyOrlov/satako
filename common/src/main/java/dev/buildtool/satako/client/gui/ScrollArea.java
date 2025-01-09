package dev.buildtool.satako.client.gui;

import dev.buildtool.satako.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;

public class ScrollArea extends AbstractWidget{
    protected List<AbstractWidget> widgets;
    protected static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller");
    protected static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller_disabled");
    protected int totalContentHeight;
    private boolean scrolling;
    protected int scroll;
    protected HashMap<AbstractWidget,Integer> widgetYOffsets=new HashMap<>();

    public ScrollArea(int x, int y, int width, int height, Component message, List<AbstractWidget> widgets, Screen parentScreen) {
        super(x, y, width, height, message);
        this.widgets=widgets;
        int elementY=0;
        for (AbstractWidget widget : widgets) {
            widget.setX(x);
            widget.setY(y+elementY);
            totalContentHeight+=widget.getHeight();
            elementY+=widget.getHeight();
            parentScreen.addRenderableWidget(widget);
            if(widget.getY()+widget.getHeight()>height || widget.getY()<y)
                widget.visible=false;
            widgetYOffsets.put(widget,widget.getY());
        }
        scroll=y;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button==0)
        {
            if(mouseX<width && mouseX>width-15) {
                scrolling = true;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        scroll= (int) Math.clamp(mouseY- (double) getY() /height,getY(),height);
        float div= (float) (totalContentHeight) /widgets.size();
        float relative= (float) (Math.clamp((mouseY- (double) getY() /height),getY(),height)*(totalContentHeight-height+div+getY())/height-div-getY());
        for (AbstractWidget widget : widgets) {
            Integer integer = widgetYOffsets.get(widget);
            widget.setY((int) (integer -relative));
            widget.visible=widget.getY()>=getY() && widget.getY()+widget.getHeight()<=height+div/2;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(scrolling)
        {
            scrolling=false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(getX(),getY(),getX()+width,getY()+height, Constants.DARK.getIntColor());
        for (AbstractWidget widget : widgets) {
            widget.render(guiGraphics,mouseX,mouseY,partialTick);
        }
        guiGraphics.blitSprite(SCROLLER_SPRITE,width-2,scroll,12,15);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, getMessage(),width/2,getY()-10,Constants.WHITE.getIntColor());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
