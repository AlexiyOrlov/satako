package dev.buildtool.satako.client.gui;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
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
import java.util.Map;

public class ScrollArea extends AbstractWidget{
    public static final int SCROLLBAR_HEIGHT = 15;
    public static final int SCROLLBAR_WIDTH = 12;
    protected List<AbstractWidget> widgets;
    protected static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller");
    protected static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller_disabled");
    protected int totalContentHeight;
    private boolean dragging;
    protected float scrollForScrollBar;
    protected HashMap<AbstractWidget,Integer> widgetYOffsets=new HashMap<>();
    protected Screen parentScreen;
    protected TreeBasedTable<Integer,Integer,AbstractWidget> widgetTable=TreeBasedTable.create();
    protected List<AbstractWidget> widgetsThatSpanColumns=new ArrayList<>();
    private float relativeScroll;

    public ScrollArea(int x, int y, int width, int height, Component message, Screen parentScreen) {
        super(x, y, width, height, message);
        this.widgets=new ArrayList<>();
        this.parentScreen=parentScreen;
        scrollForScrollBar =getY();
        if(!getMessage().getString().isEmpty())
            scrollForScrollBar+=10;
    }

    public void addWidget(AbstractWidget widget,int row,int column)
    {
        widgetTable.put(row,column,widget);
        widgets.add(widget);
        parentScreen.addRenderableWidget(widget);
    }

    public void addSpanningWidget(AbstractWidget widget,int row,int column)
    {
        addWidget(widget,row,column);
        widgetsThatSpanColumns.add(widget);
    }

    public void alignWidgets()
    {
        int startY = getY();
        if(!getMessage().getString().isEmpty())
            startY+=10;
        //align y
        int elementY=0;
        for (Integer row : widgetTable.rowKeySet()) {
           var map= widgetTable.row(row);
           int highest=0;
            for (Map.Entry<Integer, AbstractWidget> columnEntry : map.entrySet()) {
                AbstractWidget abstractWidget = columnEntry.getValue();
                abstractWidget.setY(startY +elementY);
                if(abstractWidget.getHeight()>highest)
                    highest=abstractWidget.getHeight();
                widgetYOffsets.put(abstractWidget,abstractWidget.getY());
                if(abstractWidget.getY()+abstractWidget.getHeight()> startY +height || abstractWidget.getY()< startY)
                    abstractWidget.visible=false;
            }
            elementY+=highest;
            totalContentHeight+=highest;
        }

        //calculate widest elements
        HashMap<Integer,Integer> columnToWidest=new HashMap<>();

        for (Integer column : widgetTable.columnKeySet()) {
            Map<Integer,AbstractWidget> map= widgetTable.column(column);
            for (Map.Entry<Integer, AbstractWidget> rowEntry : map.entrySet()) {
                AbstractWidget abstractWidget = rowEntry.getValue();
                if(!widgetsThatSpanColumns.contains(abstractWidget)) {
                    int widgetWidth = abstractWidget.getWidth();
                    if (columnToWidest.containsKey(column)) {
                        int widest = columnToWidest.get(column);
                        if (widest < widgetWidth)
                            columnToWidest.put(column, widgetWidth);
                    } else {
                        columnToWidest.put(column, widgetWidth);
                    }
                }
            }
        }

        //align x
        for (Table.Cell<Integer, Integer, AbstractWidget> cell : widgetTable.cellSet()) {
            Integer column = cell.getColumnKey();
            AbstractWidget next = cell.getValue();
            if(column>0) {
                int widest=0;
                for (int col = 0; col < column; col++) {
                    widest+=columnToWidest.get(col);
                }
                next.setX(getX() + widest);
            }
            else
                next.setX(getX());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean withinScrollBar=mouseX<getX()+width+ SCROLLBAR_WIDTH && mouseX>getX()+width-4 && mouseY> scrollForScrollBar && mouseY< scrollForScrollBar + SCROLLBAR_HEIGHT;
        if(button==0  && totalContentHeight>height && withinScrollBar)
        {
            dragging = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(dragging) {
            int startY = getY();
            if(!getMessage().getString().isEmpty())
            {
                startY+=10;
            }
            scrollForScrollBar = (int) Math.clamp(mouseY - (double) startY / (startY +height), startY, startY + height);
            relativeScroll = (float) Math.clamp(mouseY- (double) startY /(startY +height)- startY,0,height);
            float div = (float) (totalContentHeight) / widgets.size();
            float relative = relativeScroll * (totalContentHeight - height) / (height);
            int finalStartY = startY;
            widgetTable.rowKeySet().forEach(integer -> {
                var row=widgetTable.row(integer);
                row.forEach((integer1, widget) -> {
                    int offsetY=widgetYOffsets.get(widget);
                    widget.setY((int) (offsetY-relative));
                    widget.visible = widget.getY() >= finalStartY && widget.getY() + widget.getHeight() <= finalStartY + height + div / 2;
                });
            });
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(dragging)
        {
            dragging =false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int startY=getY();
        if(!getMessage().getString().isEmpty())
        {
            startY+=10;
        }
        scrollForScrollBar = (int) Math.clamp(scrollForScrollBar- scrollY*5, startY,startY+ height);
        relativeScroll = (float) Math.clamp(relativeScroll-scrollY*5,0,height);
        float div = (float) (totalContentHeight) / widgets.size();
        float relative = relativeScroll * (totalContentHeight - height) / height ;
        int finalStartY = startY;
        widgetTable.rowKeySet().forEach(integer -> {
            var row=widgetTable.row(integer);
            row.forEach((integer1, widget) -> {
                int offsetY=widgetYOffsets.get(widget);
                widget.setY((int) (offsetY-relative));
                widget.visible = widget.getY() >= finalStartY && widget.getY() + widget.getHeight() <=finalStartY+ height + div / 2;
            });
        });
        return true;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Component message = getMessage();
        int startY=getY();
        if(!message.getString().isEmpty())
        {
            startY+=10;
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, message,width/2+getX(),getY(),Constants.WHITE.getIntColor());
        }
        guiGraphics.fill(getX(),startY,getX()+width,startY+height, Constants.DARK.getIntColor());
        for (AbstractWidget widget : widgets) {
            widget.render(guiGraphics,mouseX,mouseY,partialTick);
        }
        if(totalContentHeight>height)
            guiGraphics.blitSprite(SCROLLER_SPRITE,width-2+getX(), (int) scrollForScrollBar, SCROLLBAR_WIDTH, 3);
        else
            guiGraphics.blitSprite(SCROLLER_DISABLED_SPRITE,width-2+getX(), (int) scrollForScrollBar, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }
}
