package dev.buildtool.satako.gui;

import dev.buildtool.satako.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Radio button is used in a group, when a choice for a user is provided - {@link ButtonGroup}. Only one radio button
 * in a group must be active/selected at the same time
 */
public class RadioButton extends BetterButton
{
    public boolean selected;
    private final List<RadioButton> neighbours = new ArrayList<>();

    public RadioButton(int x, int y, int width, int height, Component text) {
        super(x, y, text);
        this.width = width;
        this.height = height;
    }

    public RadioButton(int x, int y, Component text) {
        this(x, y, Minecraft.getInstance().font.width(text.getString()) + 8, Constants.BUTTONHEIGHT, text);
    }

    public RadioButton(int x, int y, Component text, OnPress consumer) {
        super(x, y, text, consumer);
    }

    public void addNeighbour(RadioButton r) {
        neighbours.add(r);
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
    public void renderWidget(GuiGraphics matrixStack, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            this.isHovered = selected;
            int k = getTextureY();
            matrixStack.blitWithBorder(SPRITES.get(isHovered,isFocused()), this.getXPos(), this.getYPos(), 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2);
            super.renderWidget(matrixStack, mouseX, mouseY, partial);
        }
    }


    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_)
    {
        boolean pressed = super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
        if (pressed)
        {
            this.selected = true;
            neighbours.forEach(b -> b.selected = false);
        }
        return pressed;
    }
}
