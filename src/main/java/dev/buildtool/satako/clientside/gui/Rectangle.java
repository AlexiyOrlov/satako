package dev.buildtool.satako.clientside.gui;

import dev.buildtool.satako.IntegerColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class Rectangle extends AbstractWidget {
    private final IntegerColor color;
    private final FillPercent fillPercent;
    public Rectangle(int x, int y, int width, int height, IntegerColor integerColor, @Nullable FillPercent fillPercent) {
        super(x, y, width, height, Component.literal(""));
        color=integerColor;
        this.fillPercent=fillPercent;
    }


    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if(fillPercent!=null)
            guiGraphics.fill(getX(), (int) (getY()+height-height*fillPercent.getFillPercent()),getX()+width,getY()+height,color.getIntColor());
        else
            guiGraphics.fill(getX(),getY(),getX()+width,getY()+height,color.getIntColor());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public interface FillPercent
    {
        float getFillPercent();
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }

}
