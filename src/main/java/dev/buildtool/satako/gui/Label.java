package dev.buildtool.satako.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

/**
 * Label is a string for use in GUIs
 */
public class Label extends Button implements Scrollable {
    protected boolean enabled, verticalScroll, horizontalScroll;
    protected int scrollAmount;

    @SuppressWarnings("ConstantConditions")
    public Label(int x, int y, ITextComponent text) {
        super(x, y, Minecraft.getInstance().font.width(text.getString()) + 8, 18, text, null);
        scrollAmount = 20;
    }

    @Override
    public void onPress() {

    }

    @Override
    public void renderButton(MatrixStack matrixStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        drawString(matrixStack, Minecraft.getInstance().font, this.getMessage(), this.x, this.y + (this.height - 8) / 2, 16777215 | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    public void scroll(int amount, boolean vertical) {
        if (vertical && verticalScroll) {
            y += Math.signum(amount) * scrollAmount;
        } else if (!vertical && horizontalScroll) {
            x += amount;
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
    public boolean isEnabled() {
        return enabled;
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
}
