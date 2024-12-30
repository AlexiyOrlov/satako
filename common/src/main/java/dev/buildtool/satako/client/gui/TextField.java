package dev.buildtool.satako.client.gui;

import dev.buildtool.satako.client.ClientFunctions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;

public class TextField extends EditBox implements Scrollable {
    public static final Predicate<String> POSITIVE_NUMBER = s -> s.isEmpty() || StringUtils.isNumeric(s);
    public boolean scrollable;
    private boolean enabled;
    protected int scrollAmount;

    {
        scrollAmount = height;
    }

    /**
     * Creates a string field fitted to text
     */
    public TextField(int X, int Y, String text) {
        super(Minecraft.getInstance().font, X, Y, ClientFunctions.calculateStringWidth(text) + 10, 20, Component.empty());
        insertText(text);
    }

    public TextField(int x, int y, String string, int width) {
        this(x, y, string);
        setWidth(width);
    }

    public TextField(int X, int Y, int width) {
        super(Minecraft.getInstance().font, X, Y, width, 20, Component.empty());
    }

    @Override
    public void scroll(int amount, boolean vertical) {
        if (scrollable) {
            if (vertical) {
                setX(getX() + amount);
            } else {
                setY(getY() + amount);
            }
        }
    }

    @Override
    public void setScrollable(boolean vertical, boolean b) {
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
}
