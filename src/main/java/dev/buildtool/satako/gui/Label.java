package dev.buildtool.satako.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

/**
 * Label is a string for use in GUIs
 */
public class Label extends BetterButton implements Scrollable {
    protected boolean enabled, verticalScroll, horizontalScroll, hidden;
    protected int scrollAmount;
    protected Screen parent;

    @SuppressWarnings("ConstantConditions")
    public Label(int x, int y, Component text) {
        super(x, y, Minecraft.getInstance().font.width(text.getString()) + 8, 18, text, null);
        scrollAmount = 20;
    }

    @SuppressWarnings("ConstantConditions")
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

    private void updateTooltip() {
        if (this.getTooltip() != null) {
            boolean flag = this.isHovered || this.isFocused() && Minecraft.getInstance().getLastInputType().isKeyboard();
//            if (flag != this.wasHoveredOrFocused) {
//                if (flag) {
//                    this.hoverOrFocusedStartTime = Util.getMillis();
//                }
//
//                this.wasHoveredOrFocused = flag;
//            }

            if (flag /*&& Util.getMillis() - this.hoverOrFocusedStartTime > (long)this.tooltipMsDelay*/) {
                Screen screen = Minecraft.getInstance().screen;
                if (screen != null) {
                    screen.setTooltipForNextRenderPass(this.getTooltip(), this.createTooltipPositioner(), this.isFocused());
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics matrixStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        if (!hidden) {
            if (onPress != null) {
                if (parent != null)
                    //parent.renderTooltip(matrixStack, getMessage(), getX() - 8, getY() + 18);
                    updateTooltip();
                else
                    matrixStack.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX(), this.getY() + (this.height - 8) / 2, 16777215 | Mth.ceil(this.alpha * 255.0F) << 24);

            } else
                matrixStack.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX(), this.getY() + (this.height - 8) / 2, 16777215 | Mth.ceil(this.alpha * 255.0F) << 24);

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
}
