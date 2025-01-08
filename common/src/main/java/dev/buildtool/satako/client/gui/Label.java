package dev.buildtool.satako.client.gui;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.client.ClientMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * Label is a string with background
 */
public class Label extends BetterButton implements Scrollable {
    protected boolean enabled, verticalScroll, horizontalScroll, hidden;
    protected int scrollAmount;
    protected Screen parent;
    protected IntegerColor backgroundColor;

    @SuppressWarnings("ConstantConditions")
    public Label(int x, int y, Component text, IntegerColor backgroundColor) {
        this(x, y,text,backgroundColor,null);
        this.backgroundColor = backgroundColor;
    }

    public Label(int x,int y,Component text,IntegerColor backgroundColor,OnPress pressHandler)
    {
        super(x,y,Minecraft.getInstance().font.width(text.getString())+8, 10, text,pressHandler);
        this.backgroundColor=backgroundColor;
    }

    @Deprecated
    @SuppressWarnings("ConstantConditions")
    public Label(int x, int y, Component text, @Nullable Screen parent, @Nullable OnPress pressHandler, IntegerColor backgroundColor) {
        super(x, y, Minecraft.getInstance().font.width(text.getString())+8, 10, text, pressHandler);
        scrollAmount = 20;
        this.parent = parent;
        this.backgroundColor = backgroundColor;
    }

    public Label(int x, int y, Component text, int labelWidth, @Nullable OnPress onPress, IntegerColor color) {
        super(x, y, labelWidth, 10, text, onPress);
        backgroundColor = color;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        if (!hidden) {
            ClientMethods.drawBackground(guiGraphics, getX(), getY() + 4, 399, width+8, height + 2, backgroundColor);
            guiGraphics.pose().translate(0, 0, 400);
            renderScrollingString(guiGraphics, fontRenderer, getMessage(), getX(), getY() + 3, getX() + getWidth()-4, getY() + getHeight()/2 + 6, Constants.WHITE.getIntColor());
        }
    }

    @Override
    public void scroll(int amount, boolean vertical) {
        if (vertical && verticalScroll) {
            setY((int) (getY() + Math.signum(amount) * scrollAmount));
        } else if (!vertical && horizontalScroll) {
            setX(getX() + amount);
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
    public void updateWidth() {
        setX(getX() + width / 2 - fontRenderer.width(getMessage()) / 2);
        width = fontRenderer.width(getMessage());
    }

    @Override
    public int getHeight() {
        return super.getHeight()+10;
    }

    @Override
    public int getWidth() {
        return super.getWidth()+12;
    }
}
