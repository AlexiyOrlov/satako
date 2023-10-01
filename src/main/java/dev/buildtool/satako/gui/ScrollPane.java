package dev.buildtool.satako.gui;

import dev.buildtool.satako.IntegerColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.List;

public class ScrollPane extends AbstractWidget {
    private final List<?> elements;
    private int scrolled;
    private final int maxScrollDistance;
    private static final IntegerColor backgroundColor = new IntegerColor(0x4466FC5e), barColor = new IntegerColor(0xE34063ff);

    public ScrollPane(int x, int y, int width, int height, Component label, List<?> elements) {
        super(x, y, width, height, label);
        this.elements = elements;
        int highest = 0;
        for (Object guiEventListener : elements) {
            if (guiEventListener instanceof Positionable positionable) {
                positionable.setY(this.getY() + positionable.getY());
                positionable.setX(this.getX() + positionable.getX());
                if (guiEventListener instanceof Hideable hideable) {
                    hideable.setHidden(positionable.getY() < this.getY() || positionable.getY() + positionable.getElementHeight() > this.getY() + height);
                }
                if (positionable.getY() > highest)
                    highest = positionable.getY();
            } else if (guiEventListener instanceof AbstractWidget a) {
                a.setX(x + a.getX());
                a.setY(y + a.getY());
                a.visible = a.getY() >= y && a.getY() + a.getHeight() <= this.getY() + height;
                if (a.getY() > highest)
                    highest = a.getY();
            }
        }
        Object bottomElement = null;
        for (Object item : elements) {
            if (item instanceof Positionable positionable) {
                if (positionable.getY() == highest)
                    bottomElement = item;
            } else if (item instanceof AbstractWidget abstractWidget) {
                if (abstractWidget.getY() == highest)
                    bottomElement = abstractWidget;
            }
        }
        maxScrollDistance = bottomElement instanceof Positionable positionable ? positionable.getY() + positionable.getElementHeight() : bottomElement instanceof AbstractWidget abstractWidget ? abstractWidget.getY() + abstractWidget.getHeight() : 0;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int p_268034_, int p_268009_, float p_268085_) {
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), backgroundColor.getIntColor());

        Font font = Minecraft.getInstance().font;
        if (!getMessage().getString().isEmpty()) {
            guiGraphics.drawCenteredString(font, getMessage(), (getX() + width / 2), getY() - 15, 0xffffff);
        }
        float div = (float) height * ((float) height / maxScrollDistance);
        float div2 = ((float) maxScrollDistance * ((float) scrolled / -maxScrollDistance));
        guiGraphics.fill(getX() + getWidth() - 10, (int) (getY() + div2), getX() + getWidth(), (int) (getY() + div), barColor.getIntColor());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {
        //
    }

    @Override
    public void playDownSound(SoundManager p_93665_) {
        //
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dx, double dy) {
        int d = dy > 0 ? Mth.ceil(dy) : Mth.floor(dy);
        if (mouseX > getX() + getWidth() - 10 && mouseX < getX() + getWidth()) {
            if ((scrolled <= 0 && d == 1) || (d == -1 && scrolled - height * 2 > -maxScrollDistance)) {
                for (Object guiEventListener : elements) {
                    if (guiEventListener instanceof Positionable positionable) {
                        positionable.setY(positionable.getY() + d);
                        if (positionable instanceof Hideable hideable) {
                            hideable.setHidden(positionable.getY() < getY() || positionable.getY() + positionable.getElementHeight() > getY() + height);
                        }
                    } else if (guiEventListener instanceof AbstractWidget a) {
                        a.setY(a.getY() + d);
                        a.visible = a.getY() > getY() && a.getY() + a.getHeight() < getY() + height;
                    }
                }
                scrolled += d;
            }
        }
    }
}
