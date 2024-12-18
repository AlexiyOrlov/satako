
package dev.buildtool.satako.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.ScreenUtils;

/**
 * This button is used in multiple-selection lists
 */
public class SelectionButton extends BetterButton {
    public boolean selected;

    public SelectionButton(int x, int y, Component text) {
        super(x, y, text);
    }

    public SelectionButton(int x, int y, Component text, OnPress onPress) {
        super(x, y, text, onPress);
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        boolean pressed = super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
        if (pressed) {
            selected = !selected;
        }
        return pressed;
    }


    @Override
    public int getFGColor() {
        return selected ? 0xFFFFA0 : 10526880;
    }

    @Override
    public void renderWidget(PoseStack mStack, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            Minecraft mc = Minecraft.getInstance();
            int k = !this.active ? 0 : (this.isHoveredOrFocused() ? 2 : 1);
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            ScreenUtils.blitWithBorder(mStack, WIDGETS_LOCATION, this.getX(), this.getY(), 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, 0);

            Component buttonText = this.getMessage();
            int strWidth = mc.font.width(buttonText);
            int ellipsisWidth = mc.font.width("...");

            if (strWidth > width - 6 && strWidth > ellipsisWidth) {
                buttonText = Component.literal(mc.font.substrByWidth(buttonText, width - 6 - ellipsisWidth).getString() + "...");
            }
            int color=getFGColor();
            drawCenteredString(mStack, mc.font, buttonText, this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, color);
        }
    }
}

