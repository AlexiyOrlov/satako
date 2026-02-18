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
public class Label extends BetterButton implements Switchable {
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

    public static Label centered(int x,int y,Component text,IntegerColor backgroundColor)
    {
        return new Label(x-Minecraft.getInstance().font.width(text)/2-10,y,text,backgroundColor);
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
            ClientMethods.drawBackground(guiGraphics, getX()+4, getY() + 4, 399, width+4, height + 2, backgroundColor);
            guiGraphics.pose().pushPose();
            //this is a minimum needed translation
            guiGraphics.pose().translate(0, 0, 399);
            renderScrollingString(guiGraphics, fontRenderer, getMessage(), getX()+4, getY() + 3, getX() + getWidth()-4, getY() + getHeight()/2 + 6, Constants.WHITE.getIntColor());
            guiGraphics.pose().popPose();
        }
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
    public int getHeight() {
        return super.getHeight()+10;
    }

    @Override
    public int getWidth() {
        return super.getWidth()+12;
    }
}
