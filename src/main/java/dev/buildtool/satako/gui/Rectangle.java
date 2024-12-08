package dev.buildtool.satako.gui;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.IntegerColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;

public class Rectangle extends AbstractWidget {
    private final IntegerColor color;
    private final FillPercent fillPercent;
    private final List<DynamicTooltip> tooltip;
    public Rectangle(int x, int y, int width, int height, IntegerColor integerColor, @Nullable FillPercent fillPercent,DynamicTooltip... tooltip) {
        super(x, y, width, height, Component.literal(""));
        color=integerColor;
        this.fillPercent=fillPercent;
        this.tooltip=List.of(tooltip);
    }


    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if(fillPercent!=null)
            guiGraphics.fill(getX(), (int) (getY()+height-height*fillPercent.getFillPercent()),getX()+width,getY()+height,color.getIntColor());
        else
            guiGraphics.fill(getX(),getY(),getX()+width,getY()+height,color.getIntColor());
        if(mouseX>getX() && mouseX<getX()+width && mouseY>getY() && mouseY<getY()+height)
        {
            drawToolTip(guiGraphics,mouseX,mouseY,tooltip);
        }
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

    public void drawToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY,List<DynamicTooltip> components)
    {
        Font font = Minecraft.getInstance().font;
        components.forEach(component -> {
            guiGraphics.fill(mouseX-2,mouseY-23,mouseX+font.width(component.getTooltip())+2,mouseY-10,Constants.GRAY.getIntColor());
            guiGraphics.drawString(font, component.getTooltip(),mouseX,mouseY-20, Constants.WHITE.getIntColor());
        });
    }

    public interface DynamicTooltip
    {
        Component getTooltip();
    }
}
