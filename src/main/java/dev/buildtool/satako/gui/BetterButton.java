package dev.buildtool.satako.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;

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

    public static BetterButton createPositionlessButton(Component caption) {
        return new BetterButton(0, 0, caption);
    }

    public void scroll(int amount, boolean vertical)
    {

        if (vertical && verticalScroll) {
            setY((int) (getY() + Math.signum(amount) * scrollingAmount));
        } else if (!vertical && horizontalScroll) {
            setX(getX() + amount);
        }
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
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getX()
    {
        return super.getX();
    }

    @Override
    public void setX(int X)
    {
        super.setX(X);
    }

    @Override
    public int getY()
    {
        return super.getY();
    }

    @Override
    public void setY(int Y)
    {
        super.setY(Y);
    }

    @Override
    public void setHidden()
    {
        visible = false;
    }

    @Override
    public void setVisible()
    {
        visible = true;
    }

}
