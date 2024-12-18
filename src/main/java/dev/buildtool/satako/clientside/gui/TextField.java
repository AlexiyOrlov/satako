package dev.buildtool.satako.clientside.gui;

import dev.buildtool.satako.clientside.ClientFunctions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;

public class TextField extends EditBox implements Scrollable, Positionable, Hideable {
    public static final Predicate<String> POSITIVE_NUMBER = s -> s.isEmpty() || StringUtils.isNumeric(s);
    public boolean scrollable;
    private boolean enabled;
    protected int scrollAmount;

    /**
     * Creates a string field fitted to text
     */
    public TextField(int X, int Y, Component text) {
        super(Minecraft.getInstance().font, X, Y, ClientFunctions.calculateStringWidth(text) + 10, 15, text);
        insertText(text.getString());
    }

    public TextField(int x, int y, Component string, int width) {
        this(x, y, string);
        setWidth(width);
    }

    /**
     * Main constructor
     */
    public TextField(int X, int Y, int width) {
        super(Minecraft.getInstance().font, X, Y, width, 15, Component.literal(""));
    }

    {
        scrollAmount = height;
    }

    public static TextField createWithMaxStringLength(int x_, int y_, int width, int maxStringLength, String string) {
        TextField textField = new TextField(x_, y_, width);
        textField.setMaxLength(maxStringLength);
        textField.insertText(string);
        return textField;
    }

    @Override
    public void scroll(int amount, boolean vertical)
    {
        if (scrollable)
        {
            if (vertical) {
                setXPos(getXPos() + amount);
            } else {
                setYPos(getYPos() + amount);
            }
        }
    }

    @Override
    public void setScrollable(boolean vertical, boolean b)
    {
        scrollable = b;
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
        scrollAmount = pixels;
    }

    @Override
    public int getElementHeight() {
        return height;
    }

    @Override
    public int getXPos() {
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
        setYPos(Y);
    }

    public int getElementWidth() {
        return width;
    }

    @Override
    public void setHidden(boolean hidden) {
        setVisible(!hidden);
    }
}
