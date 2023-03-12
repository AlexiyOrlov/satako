package dev.buildtool.satako.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import dev.buildtool.satako.UniqueList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraftforge.client.gui.widget.ScrollPanel;

import java.util.Arrays;

public class ScrollPane extends ScrollPanel {
    protected UniqueList<GuiEventListener> items = new UniqueList<>();
    protected int biggestY;
    public ScrollPane(Minecraft client, int width, int height, int top, int left) {
        super(client, width, height, top, left);
    }

    public ScrollPane(Minecraft client, int width, int height, int top, int left, int border) {
        super(client, width, height, top, left, border);
    }

    public ScrollPane(Minecraft client, int width, int height, int top, int left, int border, int barWidth) {
        super(client, width, height, top, left, border, barWidth);
    }

    public ScrollPane(Minecraft client, int width, int height, int top, int left, int border, int barWidth, int bgColor) {
        super(client, width, height, top, left, border, barWidth, bgColor);
    }

    public ScrollPane(Minecraft client, int width, int height, int top, int left, int border, int barWidth, int bgColorFrom, int bgColorTo) {
        super(client, width, height, top, left, border, barWidth, bgColorFrom, bgColorTo);
    }

    public ScrollPane(Minecraft client, int width, int height, int top, int left, int border, int barWidth, int bgColorFrom, int bgColorTo, int barBgColor, int barColor, int barBorderColor) {
        super(client, width, height, top, left, border, barWidth, bgColorFrom, bgColorTo, barBgColor, barColor, barBorderColor);
    }

    @Override
    protected int getContentHeight() {
        int contentHeight = biggestY;

        if (contentHeight < bottom - top - 4)
            contentHeight = bottom - top - 4;
        return contentHeight;
    }

    @Override
    protected void drawPanel(PoseStack poseStack, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {

    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {

    }

    public ScrollPane addItems(GuiEventListener... items) {
        for (GuiEventListener guiEventListener : items) {
            if (guiEventListener instanceof Positionable positionable) {
                positionable.setY(this.top + positionable.getY());
                positionable.setX(this.left + positionable.getX());
                if (positionable instanceof Hideable hideable) {
                    hideable.setHidden(positionable.getY() < top || positionable.getY() + positionable.getHeight() > bottom);
                }
            } else if (guiEventListener instanceof AbstractWidget a) {
                a.setPosition(left + a.getX(), top + a.getY());
                a.visible = a.getY() >= top && a.getY() + a.getHeight() <= bottom;
            }
        }
        this.items.addAll(Arrays.asList(items));
        for (GuiEventListener item : this.items) {
            if (item instanceof Positionable positionable)
                if (positionable.getY() + positionable.getHeight() > biggestY)
                    biggestY = positionable.getY() + positionable.getHeight();
                else if (item instanceof AbstractWidget a) {
                    if (biggestY < a.getY() + a.getHeight())
                        biggestY = a.getY() + a.getHeight();
                }
        }
        return this;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (scrollDistance > 0 && scrollDistance < this.getContentHeight() - (this.height - this.border)) {
            for (GuiEventListener guiEventListener : items) {
                if (guiEventListener instanceof Positionable positionable) {
                    positionable.setY((int) (positionable.getY() - deltaY * getScrollAmount()));
                    if (positionable instanceof Hideable hideable) {
                        hideable.setHidden(positionable.getY() < top || positionable.getY() + positionable.getHeight() > bottom);
                    }
                } else if (guiEventListener instanceof AbstractWidget a) {
                    a.setY((int) (a.getY() - deltaY * getScrollAmount()));
                    a.visible = a.getY() >= top && a.getY() + a.getHeight() <= bottom;
                }
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
