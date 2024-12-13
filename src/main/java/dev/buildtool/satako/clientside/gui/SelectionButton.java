
package dev.buildtool.satako.clientside.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

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

    private int getTextureY() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }
        return 46 + i * 20;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            Minecraft mc = Minecraft.getInstance();
            this.isHovered = mouseX >= this.getXPos() && mouseY >= this.getYPos() && mouseX < this.getXPos() + this.width && mouseY < this.getYPos() + this.height;
            guiGraphics.blitSprite(SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());

            Component buttonText = this.getMessage();
            int strWidth = mc.font.width(buttonText);
            int ellipsisWidth = mc.font.width("...");

            if (strWidth > width - 6 && strWidth > ellipsisWidth) {
                buttonText = Component.literal(mc.font.substrByWidth(buttonText, width - 6 - ellipsisWidth).getString() + "...");
            }
            int color;
            if (selected)
                color = 16777120;
            else
                color = 16777215;
            guiGraphics.drawCenteredString(mc.font, buttonText, this.getXPos() + this.width / 2, this.getYPos() + (this.height - 8) / 2, color);
        }
    }
}

