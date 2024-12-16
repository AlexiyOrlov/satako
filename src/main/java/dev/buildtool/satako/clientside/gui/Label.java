package dev.buildtool.satako.clientside.gui;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.clientside.ClientMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

/**
 * Label is a string with optional background for use in GUIs
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
        if(backgroundColor!=null)
        {
            setX(x+5);
            setY(y+5);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public Label(int x, int y, Component text, @Nullable Screen parent, @Nullable OnPress pressHandler, IntegerColor backgroundColor) {
        super(x, y, Minecraft.getInstance().font.width(text.getString()), 10, text, pressHandler);
        scrollAmount = 20;
        this.parent = parent;
        this.backgroundColor=backgroundColor;
        if(backgroundColor!=null)
        {
            setX(x+5);
            setY(y+5);
        }
    }

    public Label(int x,int y,Component text,int labelWidth,@Nullable OnPress onPress, IntegerColor color)
    {
        super(x,y,labelWidth,10,text,onPress);
        backgroundColor=color;
        if(backgroundColor!=null)
        {
            setX(x+5);
            setY(y+5);
        }
    }

    @SuppressWarnings("ConstantConditions")
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
                ClientMethods.drawBackground(guiGraphics,getX(),getYPos(),399,width,height,backgroundColor);
            }
            guiGraphics.pose().translate(0,0,400);
            renderScrollingString(guiGraphics,fontRenderer,getMessage(),getXPos(),getYPos(),getX()+getWidth()-4,getYPos()+getHeight(), Constants.WHITE.getIntColor());
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

    @Override
    public int getWidth() {
        if(backgroundColor!=null)
            return getElementWidth()+5;
        return super.getWidth();
    }

    @Override
    public int getY() {
        if(backgroundColor!=null)
            return super.getY()-5;
        return super.getY();
    }
}
