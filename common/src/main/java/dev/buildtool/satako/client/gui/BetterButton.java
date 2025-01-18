package dev.buildtool.satako.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

/**
 * Base control for other controls
 */
public class BetterButton extends ExtendedButton implements Switchable {
    public boolean verticalScroll, horizontalScroll;

    protected Font fontRenderer;

    {
        fontRenderer = Minecraft.getInstance().font;
    }

    /**
     * @param height optimal height is 20
     */
    public BetterButton(int x, int y, int width, int height, Component text, OnPress pressable) {
        super(x, y, width, height, text, pressable);
    }

    /**
     * Construct a button with optimal height and width fitted to label
     */
    public BetterButton(int x, int y, Component text) {
        this(x, y, Minecraft.getInstance().font.width(text.getString()) + 8, 20, text,null);
    }

    public BetterButton(int x, int y, Component text, OnPress onPress) {
        this(x, y, Minecraft.getInstance().font.width(text.getString()) + 8, 20, text, onPress);
    }

    public BetterButton(int x,int y,Component text,OnPress press,boolean centered) {
        this(x-Minecraft.getInstance().font.width(text)/2,y,text,press);
    }

    @Override
    public void setEnabled() {
        active = true;
    }

    @Override
    public void setDisabled() {
        active = false;
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
