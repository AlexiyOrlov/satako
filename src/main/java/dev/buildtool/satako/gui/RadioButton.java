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

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            this.isHovered = selected;
            guiGraphics.blitSprite(SPRITES.get(selected, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
            Minecraft mc = Minecraft.getInstance();
            guiGraphics.drawCenteredString(mc.font, getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, 16777215);
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
