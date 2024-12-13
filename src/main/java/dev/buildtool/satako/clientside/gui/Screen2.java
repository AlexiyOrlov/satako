package dev.buildtool.satako.clientside.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.buildtool.satako.Constants;
import dev.buildtool.satako.IntegerColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A UI without slots
 */
public class Screen2 extends Screen
{
    protected int showTime=200;
    protected int popupPositionX, popupPositionY;
    protected LinkedHashMap<Component,Integer> showTimes=new LinkedHashMap<>();
    protected HashMap<AbstractWidget,DynamicTooltip> tooltips=new HashMap<>();

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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tick) {
        renderBackground(guiGraphics,mouseX,mouseY,tick);
        super.render(guiGraphics, mouseX, mouseY, tick);
        tooltips.forEach((widget, tooltip) -> {
            if(widget.getX()<mouseX && widget.getX()+widget.getWidth()>mouseX && mouseY>widget.getY() && mouseY<widget.getY()+widget.getHeight())
            {
                guiGraphics.renderTooltip(font,tooltip.getTooltip(),mouseX,mouseY);
            }
        });
        int popupY = popupPositionY - (showTimes.keySet().size()-1) * 18;
        for (Map.Entry<Component, Integer> entry : showTimes.entrySet()) {
            Component component = entry.getKey();
            Integer integer = entry.getValue();
            if (integer > 0) {
                int textWidth = font.width(component);
                int finalPopupY = popupY;
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0,0,399);
                TooltipRenderUtil.renderTooltipBackground(guiGraphics,popupPositionX - textWidth / 2, finalPopupY -4, textWidth, 14, 0,Constants.GRAY.getIntColor(),Constants.GRAY.getIntColor(),Constants.WHITE.getIntColor(), Constants.WHITE.getIntColor());
                guiGraphics.drawCenteredString(font, component, popupPositionX, popupY, new IntegerColor(0xffffffff).getIntColor());
                guiGraphics.pose().popPose();
                popupY+=23;
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
            return super.mouseClicked(mouseX, mouseY, mouseButton);
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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX,double scrollY)
    {
        int mousewheeld = (int) Math.signum(scrollY) * Constants.BUTTONHEIGHT;
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

    @Override
    public boolean isPauseScreen()
    {
        return Minecraft.getInstance().player.getHealth() < 10;
    }

    public void addPopup(Component message,int duration)
    {
        showTimes.put(message,duration);
    }

    public void addPopup(Component message)
    {
        addPopup(message,showTime);
    }

    public void addTooltip(AbstractWidget target,DynamicTooltip tooltip)
    {
        tooltips.put(target,tooltip);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean consumed=super.keyPressed(keyCode, scanCode, modifiers);
        if(!consumed) {
            InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
            if (minecraft.options.keyInventory.isActiveAndMatches(key)) {
                onClose();
                consumed=true;
            }
        }
        return consumed;
    }
}
