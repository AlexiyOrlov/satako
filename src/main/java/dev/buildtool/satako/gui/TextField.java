package dev.buildtool.satako.gui;

import dev.buildtool.satako.ClientFunctions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
        super(Minecraft.getInstance().font, X, Y, width, 15, new TextComponent(""));
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
            if (vertical)
            {
                x += amount;
            }
            else
            {
                y += amount;
            }
        }
    }

    @Override
    public void setScrollable(boolean vertical, boolean b)
    {
        scrollable = b;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setDisabled() {
        enabled = false;
    }

    @Override
    public int getElementHeight() {
        return height;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int X)
    {
        x = X;
    }

    @Override
    public int getY()
    {
        return y;
    }

    @Override
    public void setY(int Y)
    {
        y = Y;
    }

    public int getElementWidth() {
        return width;
    }

    @Override
    public void setHidden(boolean hidden) {
        setVisible(false);
    }

    private void setVisible() {
        setVisible(true);
    }
}
