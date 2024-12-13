package dev.buildtool.satako.clientside.gui;

import dev.buildtool.satako.IntegerColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

/**
 * Label is a string for use in GUIs
 */
public class Label extends BetterButton implements Scrollable, Hideable {
    protected boolean enabled, verticalScroll, horizontalScroll, hidden;
    protected int scrollAmount;
    protected Screen parent;
    protected IntegerColor backgroundColor;

    @SuppressWarnings("ConstantConditions")
    public Label(int x, int y, Component text, IntegerColor backgroundColor) {
        super(x, y, Minecraft.getInstance().font.width(text.getString()), 10, text, null);
        scrollAmount = 20;
        this.backgroundColor=backgroundColor;
    }

    @SuppressWarnings("ConstantConditions")
    public Label(int x, int y, Component text, @Nullable Screen parent, @Nullable OnPress pressHandler, IntegerColor backgroundColor) {
        super(x, y, Minecraft.getInstance().font.width(text.getString()), 10, text, pressHandler);
        scrollAmount = 20;
        this.parent = parent;
        this.backgroundColor=backgroundColor;
    }

    @Override
    public void onPress() {
        if (onPress != null)
            onPress.onPress(this);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        if (!hidden) {
            if(backgroundColor!=null)
            {
                TooltipRenderUtil.renderTooltipBackground(guiGraphics,getX(),getY(),width,height,399,backgroundColor.getIntColor(),backgroundColor.getIntColor(),backgroundColor.getIntColor(),backgroundColor.getIntColor());
            }
            guiGraphics.pose().translate(0,0,400);
            guiGraphics.drawString(Minecraft.getInstance().font, getMessage(), getXPos(), getYPos() + (height - 8) / 2, 16777215 | Mth.ceil(this.alpha * 255.0F) << 24);
        }
    }

    @Override
    public void scroll(int amount, boolean vertical) {
        if (vertical && verticalScroll) {
            setYPos((int) (getYPos() + Math.signum(amount) * scrollAmount));
        } else if (!vertical && horizontalScroll) {
            setXPos(getXPos() + amount);
        }
    }

    @Override
    public void setScrollable(boolean vertical, boolean b) {
        if (vertical)
            verticalScroll = b;
        else
            horizontalScroll = b;
    }

    @Override
    public void setEnabled() {
        enabled = true;
    }

    @Override
    public void setDisabled() {
        enabled = false;
    }

    @Override
    public void setScrollAmount(int pixels) {
        this.scrollAmount = pixels;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
