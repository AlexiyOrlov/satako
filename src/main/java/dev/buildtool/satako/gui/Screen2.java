package dev.buildtool.satako.gui;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.IntegerColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A UI without slots
 */
public class Screen2 extends Screen
{
    protected ArrayList<Component> popups=new ArrayList<>();
    protected int showTime=200;
    protected int popupPositionX, popupPositionY;
    protected LinkedHashMap<Component,Integer> showTimes=new LinkedHashMap<>();

    /**
     * GUI's center coordinates
     */
    protected int centerX, centerY;

    public Screen2(Component title) {
        super(title);
    }

    /**
     * Call this first. Provides gui center position
     */
    @Override
    public void init()
    {
        centerX = width / 2;
        centerY = height / 2;
    }

    /**
     * This should be called first
     */
    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float tick) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, tick);
        int popupY = popupPositionY - (showTimes.keySet().size()-1) * 18;
        for (Map.Entry<Component, Integer> entry : showTimes.entrySet()) {
            Component component = entry.getKey();
            Integer integer = entry.getValue();
            if (integer > 0) {
                int textWidth = font.width(component);
                matrixStack.fill(popupPositionX - textWidth / 2-5, popupY-5, popupPositionX - textWidth / 2 + textWidth+5, popupY+13, new IntegerColor(0xff565656).getIntColor());
                matrixStack.drawCenteredString(font, component, popupPositionX, popupY, new IntegerColor(0xffffffff).getIntColor());
                popupY+=18;
                integer--;
                entry.setValue(integer);
            }
        }
        showTimes.entrySet().removeIf(componentIntegerEntry -> componentIntegerEntry.getValue()==0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        try
        {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode)
    {
        try
        {
            return super.charTyped(typedChar, keyCode);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double amount)
    {
        int mousewheeld = (int) Math.signum(amount) * Constants.BUTTONHEIGHT;
        boolean verticalscroll = Screen.hasAltDown();
        if (mousewheeld != 0)
        {
            for (Renderable button : renderables) {
                if (button instanceof Scrollable) {
                    ((Scrollable) button).scroll(mousewheeld, verticalscroll);
                }
            }
        }
        return false;
    }

    @Override
    public boolean isPauseScreen()
    {
        return Minecraft.getInstance().player.getHealth() < 10;
    }
}
