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
    private final IntegerColor color;
    private final FillPercent fillPercent;
    private TextureAtlasSprite sprite;
    public Rectangle(int x, int y, int width, int height, @org.jetbrains.annotations.Nullable IntegerColor integerColor, @Nullable FillPercent fillPercent) {
        super(x, y, width, height, Component.literal(""));
        color=integerColor;
        this.fillPercent=fillPercent;
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

    public Rectangle(int x, int y, int width, int height, @Nullable IntegerColor color, TextureAtlasSprite sprite, FillPercent fillPercent) {
        super(x, y, width, height, Component.literal(""));
        this.color = color;
        this.fillPercent = fillPercent;
        this.sprite = sprite;
    }

    public Rectangle(int x, int y, int width, int height, @Nullable IntegerColor color, TextureAtlasSprite atlasSprite)
    {
        this(x, y, width, height, color,atlasSprite,null);
        this.sprite=atlasSprite;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().translate(0,0,399);
        if(sprite!=null)
        {
            if(color!=null)
                guiGraphics.setColor(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
            if(fillPercent!=null)
            {
                guiGraphics.blit(getX(), (int) (getY()+ height-height*fillPercent.getFillPercent()),-90,width,(int) (height*fillPercent.getFillPercent()),sprite);
            }
            else
            {
                guiGraphics.blit(getX(),getY(),-90,width,height,sprite);
            }
        }
        else  {
            if (fillPercent != null) {
                guiGraphics.fill(getX(), (int) (getY() + height - height * fillPercent.getFillPercent()), getX() + width, getY() + height, color.getIntColor());
            } else {
                guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, color.getIntColor());
            }
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
