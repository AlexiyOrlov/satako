package dev.buildtool.satako.clientside.gui;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.clientside.ClientMethods;
import dev.ftb.mods.ftblibrary.math.MathUtils;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.WidgetType;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.util.Mth;

import static dev.ftb.mods.ftblibrary.ui.Widget.isMouseButtonDown;
import static dev.ftb.mods.ftblibrary.ui.Widget.isShiftKeyDown;

public class ScrollBar extends AbstractWidget {
    private final int scrollBarSize;
    private double value = 0.0;
    private double scrollStep = 20.0;
    private double grab = -10000.0;
    private double minValue = 0.0;
    private double maxValue = 100.0;
    private boolean canAlwaysScroll = false;
    private boolean canAlwaysScrollPlane = true;
    protected final Plane plane;
    protected ScrollPane panel;
    public ScrollBar(int x, int y, int width, int height, Component message,int size,Plane plane,ScrollPane scrollPane) {
        super(x, y, width, height, message);
        scrollBarSize=size;
        this.plane=plane;
        this.panel=scrollPane;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int scrollBarSize = this.getScrollBarSize();
        if (scrollBarSize > 0) {
            double v = this.getValue();
            if (this.grab != -10000.0) {
                if (isMouseButtonDown(MouseButton.LEFT)) {
                    if (this.plane.isVertical) {
                        v = ((double)mouseY - ((double)getY() + this.grab)) * this.getMaxValue() / (double)(this.height - scrollBarSize);
                    } else {
                        v = ((double)mouseX - ((double)getX() + this.grab)) * this.getMaxValue() / (double)(this.width - scrollBarSize);
                    }
                } else {
                    this.grab = -10000.0;
                }
            }

            this.setValue(v);
        }

        this.drawBackground(guiGraphics,  getX(), getY(), this.width, this.height);
        if (scrollBarSize > 0) {
            if (this.plane.isVertical) {
                this.drawScrollBar(guiGraphics, getX(), getY() + this.getMappedValue(this.height - scrollBarSize), this.width, scrollBarSize);
            } else {
                this.drawScrollBar(guiGraphics, getX() + this.getMappedValue((this.width - scrollBarSize)), getY(), scrollBarSize, this.height);
            }
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public void setCanAlwaysScroll(boolean v) {
        this.canAlwaysScroll = v;
    }

    public void setCanAlwaysScrollPlane(boolean v) {
        this.canAlwaysScrollPlane = v;
    }

    public Plane getPlane() {
        return this.plane;
    }

    public double getMinValue() {
        return this.minValue;
    }

    public void setMinValue(double min) {
        this.minValue = min;
        this.setValue(this.getValue());
    }

    public double getMaxValue() {
        return this.maxValue;
    }

    public void setMaxValue(double max) {
        this.maxValue = max;
        this.setValue(this.getValue());
    }

    public int getScrollBarSize() {
        return this.scrollBarSize;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isMouseOver(mouseX,mouseY)) {
            this.grab = this.plane.isVertical ? (double)mouseY - ((double)this.getY() + this.getMappedValue((double)(this.height - this.getScrollBarSize()))) : (double)mouseX - ((double)this.getX() + this.getMappedValue((double)(this.width - this.getScrollBarSize())));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY != 0.0 && this.canMouseScrollPlane() && this.canMouseScroll(mouseX,mouseY)) {
            this.setValue(this.getValue() - this.getScrollStep() * scrollY);
            return true;
        } else {
            return false;
        }
    }

    public void drawBackground(GuiGraphics graphics, int x, int y, int w, int h) {
        ClientMethods.drawBackground(graphics,x,y,1,w,h, Constants.ORANGE);
    }

    public void drawScrollBar(GuiGraphics graphics, int x, int y, int w, int h) {
        //TODO
//        theme.drawScrollBar(graphics, x, y, w, h, WidgetType.mouseOver(this.grab != -10000.0), this.plane.isVertical);
    }

    public void onMoved() {
        double value = this.getMaxValue() <= 0.0 ? 0.0 : this.getValue();
        if (this.plane.isVertical) {
            this.panel.setScrollY(value);
        } else {
            this.panel.setScrollX(value);
        }

    }



    public boolean canMouseScrollPlane() {
        return this.canAlwaysScrollPlane || isShiftKeyDown() != this.plane.isVertical;
    }

    public boolean canMouseScroll(double mx,double my) {
        return this.canAlwaysScroll || this.isMouseOver(mx,my);
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double v) {
        v = Mth.clamp(v, this.getMinValue(), this.getMaxValue());
        if (this.value != v) {
            this.value = v;
            this.onMoved();
        }

    }

    public int getMappedValue(double max) {
        return (int) MathUtils.map(this.getMinValue(), this.getMaxValue(), 0.0, max, this.value);
    }

    public double getScrollStep() {
        return this.scrollStep;
    }

    public void setScrollStep(double s) {
        this.scrollStep = Math.max(0.0, s);
    }

    public static enum Plane {
        HORIZONTAL(false),
        VERTICAL(true);

        public final boolean isVertical;

        private Plane(boolean v) {
            this.isVertical = v;
        }
    }

    public  static class PanelScrollBar extends ScrollBar{

        public PanelScrollBar(int x, int y, int width, int height, Component message, int size, Plane plane,ScrollPane scrollPane) {
            super(x, y, width, height, message, size, plane,scrollPane);
        }
    }
}
