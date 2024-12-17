package dev.buildtool.satako.clientside.gui;

import dev.ftb.mods.ftblibrary.ui.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static dev.ftb.mods.ftblibrary.ui.Widget.isShiftKeyDown;

@Deprecated
public class ScrollPane extends AbstractWidget {
    private double scrollX = 0.0;
    private double scrollY = 0.0;
    private int contentWidth = -1;
    private int contentHeight = -1;
    private int contentWidthExtra;
    private int contentHeightExtra;
    private int offsetX = 0;
    private int offsetY = 0;
    private List<GuiEventListener> widgets=new ArrayList<>();
    private ScrollBar attachedScrollbar;
    private int scrollStep=20;
    public ScrollPane(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

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

    public int getContentWidth() {
        if (this.contentWidth == -1) {
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            Iterator<GuiEventListener> var3 = this.widgets.iterator();

            while(var3.hasNext()) {
                Widget widget = (Widget)var3.next();
                if (widget.posX < minX) {
                    minX = widget.posX;
                }

                if (widget.posX + widget.width > maxX) {
                    maxX = widget.posX + widget.width;
                }
            }

            this.contentWidth = maxX - minX + this.contentWidthExtra;
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

            this.contentHeight = maxY - minY + this.contentHeightExtra;
        }

        return this.contentHeight;
    }

    public void setOffset(boolean flag) {
        if (flag) {
            this.offsetX = (int)(-this.scrollX);
            this.offsetY = (int)(-this.scrollY);
        } else {
            this.offsetX = this.offsetY = 0;
        }

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.setOffset(true);

        for(int i = this.widgets.size() - 1; i >= 0; --i) {
            Widget widget = (Widget)this.widgets.get(i);
            if (widget.isEnabled() && widget.mouseScrolled(scrollY)) {
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
}
