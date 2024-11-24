package dev.buildtool.satako.gui;

import dev.buildtool.satako.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

/**
 * Label is a string for use in GUIs
 */
public class Label extends BetterButton implements Scrollable, Hideable {
    protected boolean enabled, verticalScroll, horizontalScroll, hidden,hasBackground;
    protected int scrollAmount;
    protected Screen parent;

    @SuppressWarnings("ConstantConditions")
    public Label(int x, int y, Component text, boolean makeBackground) {
        super(x, y, Minecraft.getInstance().font.width(text.getString()) + 8, 18, text, null);
        scrollAmount = 20;
        hasBackground=makeBackground;
    }

    @SuppressWarnings("ConstantConditions")
    public Label(int x, int y, Component text, @Nullable Screen parent, @Nullable OnPress pressHandler, boolean makeBackground) {
        super(x, y, Minecraft.getInstance().font.width(text.getString()) + 8, 18, text, pressHandler);
        scrollAmount = 20;
        this.parent = parent;
        hasBackground=makeBackground;
    }

    @Override
    public void onPress() {
        if (onPress != null)
            onPress.onPress(this);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        if (!hidden) {
            if(hasBackground)
            {
                guiGraphics.fill(getX(),getY(),getX()+width,getY()+height, Constants.ORANGE.getIntColor());
            }
            if (onPress != null) {
                guiGraphics.drawString(Minecraft.getInstance().font, getMessage(), getX()+4, getY() + (height - 8) / 2, 16777215 | Mth.ceil(this.alpha * 255.0F) << 24);
            }
        }
    }

    @Override
    public void scroll(int amount, boolean vertical) {
        if (vertical && verticalScroll) {
            setY((int) (getY() + Math.signum(amount) * scrollAmount));
        } else if (!vertical && horizontalScroll) {
            setX(getX() + amount);
        }
    }

    @Override
    public void setScrollable(boolean vertical, boolean b) {
        if (vertical)
            verticalScroll = b;
        else
            horizontalScroll = b;
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
        this.scrollAmount = pixels;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
