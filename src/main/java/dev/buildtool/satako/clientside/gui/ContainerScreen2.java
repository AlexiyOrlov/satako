package dev.buildtool.satako.clientside.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.buildtool.satako.Constants;
import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.clientside.ClientMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;

import java.util.*;

/**
 * A preset GUI which automatically draws labels, slot textures,
 * GUI borders and sends input events to elements
 */
public class ContainerScreen2<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    public static final int POPUP_SPACING=22;
    protected int centerX, centerY;
    protected ArrayList<Page> pages = new ArrayList<>(0);
    protected boolean drawBorders;
    public static final int defaultShowTime =200;
    protected int popupPositionX, popupPositionY;
    protected LinkedHashMap<Component,Integer> showTimes=new LinkedHashMap<>();
    protected HashMap<AbstractWidget,DynamicTooltip> tooltips=new HashMap<>();

    public ContainerScreen2(T container, Inventory playerInventory, Component name, boolean drawBorders_) {
        super(container, playerInventory, name);
        if (Minecraft.getInstance().screen != null) {
            Minecraft.getInstance().screen = null;
        }
        drawBorders = drawBorders_;
    }

    public ContainerScreen2(T menu,Inventory playerInventory,Component name)
    {
        this(menu,playerInventory,name,true);
    }

    /**
     * Called when GUI is created or resized.
     * Centers window and fits it to slots
     */
    @Override
    public void init()
    {
        int maxX = 0, minX = getXSize(), maxY = 0;
        for (Slot slot : getSlots())
        {
            int x = slot.x;
            if (x > maxX)
                maxX = x;
            if (minX > x)
                minX = x;
            int y = slot.y;
            if (y > maxY)
                maxY = y;
        }
        imageWidth = maxX + Constants.SLOTWITHBORDERSIZE;
        imageHeight = maxY + Constants.SLOTWITHBORDERSIZE;
        super.init();
        centerX = width / 2;
        centerY = height / 2;
        popupPositionX=centerX;
        popupPositionY =height-18;
    }

    protected List<Slot> getSlots()
    {
        return menu.slots;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float p_render_3_) {
        renderBackground(guiGraphics,mouseX,mouseY,p_render_3_);
        super.render(guiGraphics, mouseX, mouseY, p_render_3_);
        renderTooltip(guiGraphics, mouseX, mouseY);

        List<Slot> slots=getSlots();
        slots.stream().filter(Slot::isActive).forEach(slot -> {
            if(slot instanceof ItemHandlerSlot handlerSlot && slot.getItem().isEmpty() && mouseX>slot.x+leftPos&& mouseX<slot.x+leftPos+18 && mouseY>slot.y+topPos && mouseY<slot.y+topPos+18 && handlerSlot.getTooltip()!=null)
            {
                guiGraphics.renderComponentTooltip(font,handlerSlot.getTooltip(),mouseX,mouseY);
            }
        });

        tooltips.forEach((widget, tooltip) -> {
            if(widget.getX()<mouseX && widget.getX()+widget.getWidth()>mouseX && mouseY>widget.getY() && mouseY<widget.getY()+widget.getHeight())
            {
                guiGraphics.renderTooltip(font,tooltip.getTooltip(),mouseX,mouseY);
            }
        });

        int popupY = popupPositionY - (showTimes.keySet().size()-1) * POPUP_SPACING;
        for (Map.Entry<Component, Integer> entry : showTimes.entrySet()) {
            Component component = entry.getKey();
            Integer integer = entry.getValue();
            if (integer > 0) {
                ClientMethods.drawTooltipLine(guiGraphics,component,popupPositionX,popupY);
                popupY+=POPUP_SPACING;
                integer--;
                entry.setValue(integer);
            }
        }
        showTimes.entrySet().removeIf(componentIntegerEntry -> componentIntegerEntry.getValue()==0);
    }

    /**
     * Draws its elements and borders
     */
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        List<Slot> slots = getSlots();
        GlStateManager._clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        slots.stream().filter(Slot::isActive).forEach(s -> {
            int sx = s.x;
            int sy = s.y;
            if (s instanceof ItemHandlerSlot itemHandlerSlot) {
                if (itemHandlerSlot.getTexture() == null) {
                    //color
                    guiGraphics.fill(sx + leftPos, sy + topPos, sx + leftPos + 16, sy + topPos + 16, itemHandlerSlot.getColor().getIntColor());
                } else {
                    guiGraphics.fill(sx + leftPos, sy + topPos, sx + leftPos + 16, sy + topPos + 16, 0xff666666);
                }
            } else {
                guiGraphics.fill(sx + leftPos, sy + topPos, sx + leftPos + 16, sy + topPos + 16, 0xff666666);
            }
        });

        IntegerColor color = Constants.BLUE;

        //lines have to be drawn after everything else
        if (drawBorders) {
            int intColor = color.getIntColor();
            guiGraphics.hLine(this.leftPos - 1, this.getXSize() + leftPos - 2, this.topPos - 1, intColor);
            guiGraphics.hLine(this.leftPos, this.getXSize() + leftPos - 2, this.getYSize() + topPos - 2, intColor);
            guiGraphics.vLine(this.leftPos - 1, this.topPos - 1, getYSize() + this.topPos - 1, intColor);
            guiGraphics.vLine(getXSize() + this.leftPos - 2, this.topPos - 1, getYSize() + this.topPos - 2, intColor);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int p1, int p2) {
        guiGraphics.drawString(font, title, imageWidth / 2 - font.width(title.getString()) / 2, -14, Constants.ORANGE.getIntColor());
    }

    @Override
    public boolean mouseDragged(double p_97752_, double p_97753_, int button, double p_97755_, double p_97756_) {
        if(getFocused()!=null && button==0 && isDragging())
        {
            return getFocused().mouseDragged(p_97752_, p_97753_, button, p_97755_, p_97756_);
        }
        return super.mouseDragged(p_97752_, p_97753_, button, p_97755_, p_97756_);
    }

    public void addPopup(Component message,int duration)
    {
        showTimes.put(message,duration);
    }

    public void addPopup(Component message)
    {
        addPopup(message, defaultShowTime);
    }

    public void addTooltip(AbstractWidget target,DynamicTooltip tooltip)
    {
        tooltips.put(target,tooltip);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode== GLFW.GLFW_KEY_ESCAPE)
        {
            onClose();
            return true;
        }
        if(getFocused()!=null)
            return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (GuiEventListener child : children()) {
            if(child.mouseClicked(mouseX, mouseY, button))
            {
                if(button==0)
                    setDragging(true);
                setFocused(child);
                return true;
            }
        }
        setFocused(null);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.isDragging()) {
            this.setDragging(false);
            if (this.getFocused() != null) {
                return this.getFocused().mouseReleased(mouseX, mouseY, button);
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
