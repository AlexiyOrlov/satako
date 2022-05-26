package dev.buildtool.satako.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.buildtool.satako.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.GuiUtils;

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

    public void addNeighbour(RadioButton r)
    {
        neighbours.add(r);
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            Minecraft mc = Minecraft.getInstance();
            this.isHovered = selected;
            int k = this.getYImage(this.isHovered);
            GuiUtils.drawContinuousTexturedBox(matrixStack, WIDGETS_LOCATION, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.getBlitOffset());
            this.renderBg(matrixStack, mc, mouseX, mouseY);
            int color = getFGColor();

            if (this.isHovered && this.packedFGColor == AbstractWidget.UNSET_FG_COLOR)
                color = 0xFFFFA0; // Slightly Yellow

            Component buttonText = this.getMessage();

            drawCenteredString(matrixStack, mc.font, buttonText, this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
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
