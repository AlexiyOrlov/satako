package dev.buildtool.satako.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

import java.util.List;

public class BetterButton extends ExtendedButton implements Scrollable, Positionable, Hideable
{
    public String string;
    public boolean verticalScroll, horizontalScroll;
    /**
     * By how much the button will be able to scroll
     */
    public int scrollingAmount;
    protected Font fontRenderer;

    {
        fontRenderer = Minecraft.getInstance().font;
        scrollingAmount = height;
    }

    /**
     * @param height optimal height is 20
     */
    public BetterButton(int x, int y, int width, int height, Component text, Button.OnPress pressable) {
        super(x, y, width, height, text, pressable);
        string = text.getString();
    }

    /**
     * Construct a button with optimal height and width fitted to label
     */
    public BetterButton(int x, int y, Component text) {
        this(x, y, Minecraft.getInstance().font.width(text.getString()) + 8, 20, text,
                p_onPress_1_ -> {
                });
        string = text.getString();
    }

    public BetterButton(int x, int y, Component text, OnPress onPress) {
        this(x, y, Minecraft.getInstance().font.width(text.getString()) + 8, 20, text, onPress);
    }

    public BetterButton(int x, int y, Component text, boolean verticalScroll_, boolean horizontalScroll_) {
        this(x, y, text);
        verticalScroll = verticalScroll_;
        horizontalScroll = horizontalScroll_;
    }

    public void scroll(int amount, boolean vertical)
    {

        if (vertical && verticalScroll) {
            setYPos((int) (getYPos() + Math.signum(amount) * scrollingAmount));
        } else if (!vertical && horizontalScroll)
            setXPos(getXPos() + amount);
    }

    @Override
    public void setScrollable(boolean vertical, boolean b)
    {
        if (b)
        {
            if (vertical)
            {
                verticalScroll = true;
            }
            else
            {
                horizontalScroll = true;
            }
        }
        else
        {
            if (vertical)
            {
                verticalScroll = false;
            }
            else
            {
                horizontalScroll = false;
            }
        }
    }

    @Override
    public void setEnabled() {
        active = true;
    }

    @Override
    public void setDisabled() {
        active = false;
    }

    @Override
    public void setScrollAmount(int pixels) {
        scrollingAmount = pixels;
    }

    @Override
    public int getElementWidth() {
        return width;
    }

    @Override
    public int getElementHeight() {
        return height;
    }

    @Override
    public int getXPos()
    {
        return super.getX();
    }

    @Override
    public void setXPos(int X)
    {
        super.setX(X);
    }

    @Override
    public int getYPos()
    {
        return super.getY();
    }

    @Override
    public void setYPos(int Y)
    {
        super.setY(Y);
    }

    @Override
    public void setHidden(boolean hidden) {
        visible = !hidden;
    }
}
