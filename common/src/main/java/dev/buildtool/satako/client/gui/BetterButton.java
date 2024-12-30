package dev.buildtool.satako.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class BetterButton extends ExtendedButton implements Scrollable {
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
    public BetterButton(int x, int y, int width, int height, Component text, OnPress pressable) {
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

    public void scroll(int amount, boolean vertical) {

        if (vertical && verticalScroll) {
            setY((int) (getY() + Math.signum(amount) * scrollingAmount));
        } else if (!vertical && horizontalScroll)
            setX(getX() + amount);
    }

    @Override
    public void setScrollable(boolean vertical, boolean b) {
        if (b) {
            if (vertical) {
                verticalScroll = true;
            } else {
                horizontalScroll = true;
            }
        } else {
            if (vertical) {
                verticalScroll = false;
            } else {
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

    public void updateWidth() {
        setX(getX() + width / 2 - fontRenderer.width(getMessage()) / 2);
        width = fontRenderer.width(getMessage()) + 8;
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void onPress() {
        if (onPress != null)
            onPress.onPress(this);
    }

    public void setPressHandler(OnPress pressHandler)
    {
        onPress=pressHandler;
    }
}
