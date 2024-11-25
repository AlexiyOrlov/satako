package dev.buildtool.satako.gui;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.IntegerColor;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.ScreenWrapper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CombinedScreen extends ScreenWrapper {
    protected int showTime=200;
    protected int popupPositionX, popupPositionY;
    protected LinkedHashMap<Component,Integer> showTimes=new LinkedHashMap<>();

    /**
     * GUI's center coordinates
     */
    protected int centerX, centerY;
    public CombinedScreen(BaseScreen g) {
        super(g);
    }

    @Override
    public void init()
    {
        super.init();
        centerX = width / 2;
        centerY = height / 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderables.forEach(renderable -> renderable.render(graphics, mouseX, mouseY, partialTicks));

        int popupY = popupPositionY - (showTimes.keySet().size()-1) * 18;
        for (Map.Entry<Component, Integer> entry : showTimes.entrySet()) {
            Component component = entry.getKey();
            Integer integer = entry.getValue();
            if (integer > 0) {
                int textWidth = font.width(component);
                graphics.fill(popupPositionX - textWidth / 2-5, popupY-5, popupPositionX - textWidth / 2 + textWidth+5, popupY+13, new IntegerColor(0xff565656).getIntColor());
                graphics.drawCenteredString(font, component, popupPositionX, popupY, new IntegerColor(0xffffffff).getIntColor());
                popupY+=18;
                integer--;
                entry.setValue(integer);
            }
        }
        showTimes.entrySet().removeIf(componentIntegerEntry -> componentIntegerEntry.getValue()==0);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        for (GuiEventListener guiEventListener : children()) {
            if (guiEventListener.mouseClicked(x, y, button)) {
                if(guiEventListener.isMouseOver(x,y)) {
                    this.setFocused(guiEventListener);
                }
                if (button == 0) {
                    this.setDragging(true);
                }
                return true;
            }
        }

        try
        {
            return super.mouseClicked(x, y, button);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (GuiEventListener child : children()) {
            if(child.keyPressed(keyCode, scanCode, modifiers))
                return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
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
                if (button instanceof Scrollable scrollable) {
                    scrollable.scroll(mousewheeld, verticalscroll);
                }
            }
        }
        return false;
    }

    public void addPopup(Component message,int duration)
    {
        showTimes.put(message,duration);
    }

    public void addPopup(Component message)
    {
        addPopup(message,showTime);
    }
}
