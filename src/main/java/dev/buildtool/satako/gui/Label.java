package dev.buildtool.satako.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

/**
 * Label is a string for use in GUIs
 */
@SuppressWarnings("ConstantConditions")
public class Label extends BetterButton implements Scrollable {
    protected boolean enabled, verticalScroll, horizontalScroll, hidden;
    protected int scrollAmount;
    protected Screen parent;

    public Label(int x, int y, Component text) {
        super(x, y, Minecraft.getInstance().font.width(text.getString()) + 8, 18, text, null);
        scrollAmount = 20;
    }

    public Label(int x, int y, Component text, @Nullable Screen parent, @Nullable Button.OnPress pressHandler) {
        super(x, y, Minecraft.getInstance().font.width(text.getString()) + 8, 18, text, pressHandler);
        scrollAmount = 20;
        this.parent = parent;
    }

    @Override
    public void onPress() {
        if (onPress != null)
            onPress.onPress(this);
    }

    @Override
    public void renderButton(PoseStack matrixStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        if (!hidden) {
            if (onPress != null) {
                if (parent != null)
                    parent.renderTooltip(matrixStack, getMessage(), getX() - 8, getY() + 18);
                else
                    drawString(matrixStack, Minecraft.getInstance().font, this.getMessage(), this.getX(), this.getY() + (this.height - 8) / 2, 16777215 | Mth.ceil(this.alpha * 255.0F) << 24);

            } else
                drawString(matrixStack, Minecraft.getInstance().font, this.getMessage(), this.getX(), this.getY() + (this.height - 8) / 2, 16777215 | Mth.ceil(this.alpha * 255.0F) << 24);
        }
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
}
