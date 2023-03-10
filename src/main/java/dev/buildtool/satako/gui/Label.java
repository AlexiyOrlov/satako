package dev.buildtool.satako.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

/**
 * Label is a string for use in GUIs
 */
public class Label extends Button implements Scrollable, Positionable, Hideable {
    protected boolean enabled, verticalScroll, horizontalScroll, hidden;
    protected int scrollAmount;

    @SuppressWarnings("ConstantConditions")
    public Label(int x, int y, Component text) {
        super(x, y, Minecraft.getInstance().font.width(text.getString()) + 8, 18, text, null, null);
        scrollAmount = 20;
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        return getMessage().copy();
    }

    @Override
    public void onPress() {

    }

    @Override
    public void renderButton(PoseStack matrixStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        if (!hidden)
            drawString(matrixStack, Minecraft.getInstance().font, this.getMessage(), this.getX(), this.getY() + (this.height - 8) / 2, 16777215 | Mth.ceil(this.alpha * 255.0F) << 24);
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
    public void setHidden() {
        hidden = true;
    }

    @Override
    public void setVisible() {
        hidden = false;
    }
}
