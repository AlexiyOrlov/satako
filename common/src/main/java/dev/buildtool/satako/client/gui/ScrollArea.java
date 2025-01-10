package dev.buildtool.satako.client.gui;

import dev.buildtool.satako.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScrollArea extends AbstractWidget{
    protected List<AbstractWidget> widgets;
    protected static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller");
    protected static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller_disabled");
    protected int totalContentHeight;
    private boolean scrolling;
    protected int scrollForScrollBar;
    protected HashMap<AbstractWidget,Integer> widgetYOffsets=new HashMap<>();
    protected Screen parentScreen;
    protected List<List<AbstractWidget>> widgetTable=new ArrayList<>();

    public ScrollArea(int x, int y, int width, int height, Component message, Screen parentScreen) {
        super(x, y, width, height, message);
        this.widgets=new ArrayList<>();
        this.parentScreen=parentScreen;
    }

    public void addWidget(AbstractWidget widget,boolean sameRow)
    {
        if(!widgetTable.isEmpty()) {
            List<AbstractWidget> columns = widgetTable.getLast();
            if (sameRow) {
                columns.add(widget);
            } else {
                List<AbstractWidget> newRow = new ArrayList<>();
                newRow.add(widget);
                widgetTable.add(newRow);
            }
        }
        else {
            List<AbstractWidget> newRow = new ArrayList<>();
            newRow.add(widget);
            widgetTable.add(newRow);
        }
        widgets.add(widget);
    }

    public void alignWidgets()
    {
        int elementY=0;
        for (AbstractWidget widget : widgets) {
            widget.setX(getX());
            widget.setY(getY()+elementY);
            totalContentHeight+=widget.getHeight();
            elementY+=widget.getHeight();
            parentScreen.addRenderableWidget(widget);
            if(widget.getY()+widget.getHeight()>getY()+height || widget.getY()<getY())
                widget.visible=false;
            widgetYOffsets.put(widget,widget.getY());
        }
        scrollForScrollBar =getY();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button==0  && totalContentHeight>height)
        {
            if(mouseX<getX()+width && mouseX>getX()+width-12 && mouseY> scrollForScrollBar && mouseY< scrollForScrollBar +15) {
                scrolling = true;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(scrolling) {
            scrollForScrollBar = (int) Math.clamp(mouseY - (double) getY() / (getY()+height), getY(),getY()+ height-15);
            float relativeScroll= (float) Math.clamp(mouseY- (double) getY() /(getY()+height)-getY(),0,height);
            float div = (float) (totalContentHeight) / widgets.size();
            float relative = relativeScroll * (totalContentHeight - height) / height ;
            for (AbstractWidget widget : widgets) {
                Integer integer = widgetYOffsets.get(widget);
                widget.setY((int) (integer - relative));
                widget.visible = widget.getY() >= getY() && widget.getY() + widget.getHeight() <=getY()+ height + div / 2;
            }
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
        if(totalContentHeight>height)
            guiGraphics.blitSprite(SCROLLER_SPRITE,width-2, scrollForScrollBar,12,15);
        else
            guiGraphics.blitSprite(SCROLLER_DISABLED_SPRITE,width-2, scrollForScrollBar,12,15);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, getMessage(),width/2,getY()-10,Constants.WHITE.getIntColor());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
