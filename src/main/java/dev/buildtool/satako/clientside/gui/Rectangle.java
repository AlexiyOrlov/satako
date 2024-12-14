package dev.buildtool.satako.clientside.gui;

import dev.buildtool.satako.IntegerColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class Rectangle extends AbstractWidget {
    @Nullable
    private  IntegerColor color;
    private  FillPercent fillPercent;
    private TextureAtlasSprite sprite;
    public Rectangle(int x, int y, int width, int height, @org.jetbrains.annotations.Nullable IntegerColor integerColor, @Nullable FillPercent fillPercent) {
        super(x, y, width, height, Component.literal(""));
        color=integerColor;
        this.fillPercent=fillPercent;
    }

    public Rectangle(int x, int y, int width, int height, Component message, @Nullable IntegerColor color) {
        this(x, y, width, height,color,null);
    }

    public Rectangle(int x, int y, int width, int height, TextureAtlasSprite atlasSprite, @Nullable FillPercent fillPercent)
    {
        this(x,y,width,height, (IntegerColor) null,fillPercent);
        sprite=atlasSprite;
    }

    public Rectangle(int x,int y,int width,int height,TextureAtlasSprite atlasSprite)
    {
        this(x,y,width,height,atlasSprite,null);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().translate(0,0,399);
        if(color!=null) {
            if (fillPercent != null) {
                guiGraphics.fill(getX(), (int) (getY() + height - height * fillPercent.getFillPercent()), getX() + width, getY() + height, color.getIntColor());
            } else {
                guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, color.getIntColor());
            }
        } else if (sprite != null) {
            if(fillPercent!=null)
                guiGraphics.blit(getX(), (int) (getY()+height-height*fillPercent.getFillPercent()),0,width,height,sprite);
            else
                guiGraphics.blit(getX(),getY(),-90,width,height,sprite);
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

}
