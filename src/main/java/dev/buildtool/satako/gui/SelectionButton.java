
package dev.buildtool.satako.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

/**
 * This button is used in multiple-selection lists
 */
public class SelectionButton extends BetterButton {
    public boolean selected;

    public SelectionButton(int x, int y, ITextComponent text) {
        super(x, y, text);
    }

    public SelectionButton(int x, int y, ITextComponent text, IPressable onPress) {
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
    public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            Minecraft mc = Minecraft.getInstance();
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int k = this.getYImage(this.isHovered());
            GuiUtils.drawContinuousTexturedBox(mStack, WIDGETS_LOCATION, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.getBlitOffset());
            this.renderBg(mStack, mc, mouseX, mouseY);

            ITextComponent buttonText = this.getMessage();
            int strWidth = mc.font.width(buttonText);
            int ellipsisWidth = mc.font.width("...");

            if (strWidth > width - 6 && strWidth > ellipsisWidth) {
                buttonText = new StringTextComponent(mc.font.substrByWidth(buttonText, width - 6 - ellipsisWidth).getString() + "...");
            }
            int color;
            if (selected)
                color = 16777120;
            else
                color = 16777215;
            drawCenteredString(mStack, mc.font, buttonText, this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
        }
    }
}

