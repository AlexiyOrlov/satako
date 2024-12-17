package dev.buildtool.satako.clientside.gui;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.clientside.ClientMethods;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static dev.ftb.mods.ftblibrary.ui.Widget.isShiftKeyDown;

public class ScrollPane extends AbstractWidget {
    private double scrollX = 0.0;
    private double scrollY = 0.0;
    private int contentWidth = -1;
    private int contentHeight = -1;
    private int offsetX = 0;
    private int offsetY = 0;
    private List<AbstractWidget> widgets=new ArrayList<>();
    private ScrollBar attachedScrollbar;
    private int scrollStep=20;
    private Screen parent;
    public ScrollPane(int x, int y, int width, int height, Component message,List<AbstractWidget> list,Screen parent) {
        super(x, y, width, height, message);
        widgets=list;
        this.parent=parent;
        attachedScrollbar=new ScrollBar(getX()+width,getY(),16,height,Component.literal("Bar"), 80,ScrollBar.Plane.VERTICAL,this);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ClientMethods.drawBackground(guiGraphics,getX(),getY(),0,width,height, Constants.GRAY);
        this.setOffset(true);
        this.widgets.forEach((widget) -> {
            this.drawWidget(guiGraphics, widget, widget.getX()+offsetX,widget.getY()+offsetY,partialTick);
        });
        this.widgets.stream().forEach((widget) -> {
            this.drawWidget(guiGraphics, widget, widget.getX() + this.offsetX, widget.getY() + this.offsetY,partialTick);
        });
        this.setOffset(false);
        attachedScrollbar.renderWidget(guiGraphics,mouseX,mouseY,partialTick);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public double getScrollX() {
        return scrollX;
    }

    public void setScrollX(double scroll) {
        this.scrollX = scroll;
    }

    public double getScrollY() {
        return this.scrollY;
    }

    public void setScrollY(double scrollY) {
        this.scrollY = scrollY;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.setOffset(true);

        for(int i = this.widgets.size() - 1; i >= 0; --i) {
            AbstractWidget widget = this.widgets.get(i);
            if (widget.isActive() && widget.mouseDragged(mouseX,mouseY,button, dragX, dragY)) {
                this.setOffset(false);
                return true;
            }
        }

        this.setOffset(false);
        return false;
    }

    public int getContentWidth() {
        if (this.contentWidth == -1) {
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;

            for (AbstractWidget widget : this.widgets) {
                if (widget.getX() < minX) {
                    minX = widget.getX();
                }

                if (widget.getX() + widget.getWidth() > maxX) {
                    maxX = widget.getX() + widget.getWidth();
                }
            }

            this.contentWidth = maxX - minX;
        }

        return this.contentWidth;
    }

    public int getContentHeight() {
        if (this.contentHeight == -1) {
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;

            for (GuiEventListener guiEventListener : this.widgets) {
                Widget widget = (Widget) guiEventListener;
                if (widget.posY < minY) {
                    minY = widget.posY;
                }

                if (widget.posY + widget.height > maxY) {
                    maxY = widget.posY + widget.height;
                }
            }

            this.contentHeight = maxY - minY ;
        }

        return this.contentHeight;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.setOffset(true);

        for(int i = this.widgets.size() - 1; i >= 0; --i) {
            AbstractWidget widget = this.widgets.get(i);
            if (widget.isActive() && widget.mouseScrolled(mouseX,mouseY,scrollX,scrollY)) {
                this.setOffset(false);
                return true;
            }
        }

        boolean scrollPanel = this.scrollPanel(scrollY,mouseX,mouseY);
        this.setOffset(false);
        return scrollPanel;
    }

    public double getScrollStep() {
        return this.scrollStep;
    }

    public boolean scrollPanel(double scroll, double mouseX, double mouseY) {
        if (this.attachedScrollbar == null && this.isMouseOver(mouseX,mouseY)) {
            return this.isDefaultScrollVertical() != isShiftKeyDown() ? this.movePanelScroll(0.0, -this.getScrollStep() * scroll) : this.movePanelScroll(-this.getScrollStep() * scroll, 0.0);
        } else {
            return false;
        }
    }

    boolean isDefaultScrollVertical()
    {
        return true;
    }

    public boolean movePanelScroll(double dx, double dy) {
        if (dx == 0.0 && dy == 0.0) {
            return false;
        } else {
            double sx = this.getScrollX();
            double sy = this.getScrollY();
            int h;
            if (dx != 0.0) {
                h = this.getContentWidth();
                if (h > this.width) {
                    this.setScrollX(Mth.clamp(sx + dx, 0.0, (double)(h - this.width)));
                }
            }

            if (dy != 0.0) {
                h = this.getContentHeight();
                if (h > this.height) {
                    this.setScrollY(Mth.clamp(sy + dy, 0.0, (double)(h - this.height)));
                }
            }

            return this.getScrollX() != sx || this.getScrollY() != sy;
        }
    }

    @Override
    public void playDownSound(SoundManager handler) {

    }

    public void setOffset(boolean flag) {
        if (flag) {
            this.offsetX = (int)(-this.scrollX);
            this.offsetY = (int)(-this.scrollY);
        } else {
            this.offsetX = this.offsetY = 0;
        }

    }

    public void drawWidget(GuiGraphics graphics, AbstractWidget widget, int x, int y, float tick) {
        int wx = widget.getX();
        int wy = widget.getY();
        int ww = widget.getWidth();
        int wh = widget.getHeight();
        widget.render(graphics, x,y,tick);
        if (Theme.renderDebugBoxes) {
            Color4I col = Color4I.rgb(Color4I.HSBtoRGB((float)(widget.hashCode() & 255) / 255.0F, 1.0F, 1.0F));
            GuiHelper.drawHollowRect(graphics, wx, wy, ww, wh, col.withAlpha(150), false);
            col.withAlpha(30).draw(graphics, wx + 1, wy + 1, ww - 2, wh - 2);
        }

    }
}
