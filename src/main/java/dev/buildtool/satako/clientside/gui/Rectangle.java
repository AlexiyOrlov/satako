package dev.buildtool.satako.clientside.gui;

import dev.buildtool.satako.IntegerColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Optional;

public class Rectangle extends AbstractWidget {
    private Color color;
    private final FillPercent fillPercent;
    private TextureAtlasSprite sprite;
    private boolean vertical=true;
    public Rectangle(int x,int y,int width,int height,@Nullable IntegerColor color,@Nullable TextureAtlasSprite atlasSprite,@Nullable FillPercent fillPercent,boolean vertical)
    {
        this(x, y, width, height, atlasSprite, fillPercent);
        this.color=() -> Optional.ofNullable(color);
        this.vertical=vertical;
    }
    public Rectangle(int x, int y, int width, int height, @Nullable IntegerColor integerColor, @Nullable FillPercent fillPercent) {
        super(x, y, width, height, Component.literal(""));
        color=() -> Optional.ofNullable(integerColor);
        this.fillPercent=fillPercent;
    }

    private Rectangle(int x, int y, int width, int height, TextureAtlasSprite atlasSprite, @Nullable FillPercent fillPercent)
    {
        this(x,y,width,height, (IntegerColor) null,fillPercent);
        sprite=atlasSprite;
    }

    public Rectangle(int x,int y,int width,int height,TextureAtlasSprite atlasSprite)
    {
        this(x,y,width,height,atlasSprite,null);
    }

    private Rectangle(int x, int y, int width, int height, @Nullable IntegerColor color, TextureAtlasSprite sprite, FillPercent fillPercent) {
        super(x, y, width, height, Component.literal(""));
        this.color = () -> Optional.ofNullable(color);
        this.fillPercent = fillPercent;
        this.sprite = sprite;
    }

    public Rectangle(int x, int y, int width, int height, @Nullable IntegerColor color, TextureAtlasSprite atlasSprite)
    {
        this(x, y, width, height, color,atlasSprite,null);
        this.sprite=atlasSprite;
    }

    public static Rectangle horizontal(int x,int y,int width,int height,@Nullable IntegerColor color,@Nullable TextureAtlasSprite atlasSprite,FillPercent fillPercent)
    {
        Rectangle rectangle = new Rectangle(x, y, width, height, color, atlasSprite, fillPercent);
        rectangle.vertical=false;
        return rectangle;
    }

    public static Rectangle withSprite(int x,int y,int width,int height,TextureAtlasSprite atlasSprite,FillPercent fillPercent)
    {
        return new Rectangle(x,y,width,height,atlasSprite,fillPercent);
    }

    public static Rectangle colored(int x,int y,int width,int height,IntegerColor color,FillPercent fillPercent)
    {
        return new Rectangle(x,y,width,height,color,fillPercent);
    }

    public static Rectangle withColoredSprite(int x,int y,int width,int height,IntegerColor color,TextureAtlasSprite atlasSprite,FillPercent fillPercent)
    {
        return new Rectangle(x,y,width,height,color,atlasSprite,fillPercent);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().translate(0,0,399);
        if(sprite!=null)
        {
            int min=Math.min(width,height);
            color.getColor().ifPresent(color1 -> guiGraphics.setColor(color1.getRed(),color1.getGreen(),color1.getBlue(),color1.getAlpha()));
            GuiSpriteScaling.NineSlice nineSlice = new GuiSpriteScaling.NineSlice(min, min, new GuiSpriteScaling.NineSlice.Border(0, 0, 0, 0));
            if(fillPercent!=null)
            {
                if(vertical)
                    guiGraphics.blitNineSlicedSprite(sprite, nineSlice,getX(), (int) (getY()+ height-height*fillPercent.getFillPercent()),-90,width,(int) (height*fillPercent.getFillPercent()));
                else
                    guiGraphics.blitNineSlicedSprite(sprite,nineSlice,getX(),getY(),-90,(int)(width*fillPercent.getFillPercent()),height);
            }
            else
            {
                guiGraphics.blitNineSlicedSprite(sprite, nineSlice,getX(),getY(),-90,width,height);
            }
            color.getColor().ifPresent(color1 -> guiGraphics.setColor(1,1,1,1));
        }
        else  {
            color.getColor().ifPresent(color1 -> {
                if(fillPercent!=null)
                {
                    if(vertical)
                        guiGraphics.fill(getX(), (int) (getY() + height - height * fillPercent.getFillPercent()), getX() + width, getY() + height, color1.getIntColor());
                    else
                        guiGraphics.fill(getX(),getY(),(int)(getX()+width*fillPercent.getFillPercent()),getY()+height,color.getColor().get().getIntColor());
                }
                else
                {
                    guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, color1.getIntColor());
                }
            });
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @FunctionalInterface
    public interface FillPercent
    {
        float getFillPercent();
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }

    @FunctionalInterface
    public interface Color
    {
        Optional<IntegerColor> getColor();
    }
}
