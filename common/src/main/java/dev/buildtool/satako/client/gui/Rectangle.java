package dev.buildtool.satako.client.gui;

import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.client.ClientMethods;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * An element displaying color or sprite, can be partially filled
 */
public class Rectangle extends AbstractWidget {
    private Color color;
    protected final FillPercent fillPercent;
    protected TextureAtlasSprite sprite;
    protected ResourceLocation texture;
    protected boolean vertical;
    protected DynamicColor dynamicColor;

    public static Rectangle vertical(int x,int y,int width, int height,DynamicColor color,FillPercent fillPercent)
    {
        return new Rectangle(x,y,width,height,Component.empty(),fillPercent,color,true);
    }

    public static Rectangle horizontal(int x,int y,int width, int height,DynamicColor color,FillPercent fillPercent)
    {
        return new Rectangle(x,y,width,height,Component.empty(),fillPercent,color,false);
    }

    @Deprecated
    public Rectangle(int x, int y, int width, int height, Component message, FillPercent fillPercent, DynamicColor dynamicColor, boolean vertical) {
        this(x, y, width, height, fillPercent,null,dynamicColor);
        this.vertical = vertical;
        this.dynamicColor = dynamicColor;
    }

    public Rectangle(int x, int y, int width, int height, FillPercent fillPercent, TextureAtlasSprite sprite, DynamicColor dynamicColor) {
        this(x, y, width, height,dynamicColor,sprite,fillPercent,true);
    }

    public Rectangle(int x, int y, int width, int height, @Nullable DynamicColor color, @Nullable TextureAtlasSprite sprite, FillPercent fillPercent, boolean vertical) {
        super(x, y, width, height, Component.empty());
        this.dynamicColor=color;
        this.fillPercent = fillPercent;
        this.sprite = sprite;
        this.vertical=vertical;
    }

    @Deprecated
    public Rectangle(int x, int y, int width, int height , DynamicColor dynamicColor, FillPercent fillPercent) {
        super(x, y, width, height-2, Component.empty());
        this.fillPercent = fillPercent;
        this.dynamicColor = dynamicColor;
    }

    @Deprecated
    public Rectangle(int x, int y, int width, int height , DynamicColor dynamicColor, FillPercent fillPercent,boolean vertical) {
        super(x, y, width, height-2, Component.empty());
        this.fillPercent = fillPercent;
        this.dynamicColor = dynamicColor;

        this.vertical=vertical;
    }

    public Rectangle(int x, int y, int width, int height, FillPercent fillPercent, ResourceLocation texture, boolean vertical) {
        super(x, y, width, height-2, Component.empty());
        this.fillPercent = fillPercent;
        this.texture = texture;
        this.vertical = vertical;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().translate(0, 0, 399);
        if (sprite != null) {
            int min = Math.min(width, height);
            if(dynamicColor!=null)
            {
                IntegerColor integerColor=dynamicColor.getColor();
                guiGraphics.setColor(integerColor.getRed(), integerColor.getGreen(), integerColor.getBlue(), integerColor.getAlpha());
            }
            GuiSpriteScaling.NineSlice nineSlice = new GuiSpriteScaling.NineSlice(min, min, new GuiSpriteScaling.NineSlice.Border(0, 0, 0, 0));
            if (fillPercent != null) {
                if (vertical)
                    guiGraphics.blitNineSlicedSprite(sprite, nineSlice, getX(), (int) (getY() + height - height * fillPercent.getFillPercent()), -90, width, (int) (height * fillPercent.getFillPercent()));
                else
                    guiGraphics.blitNineSlicedSprite(sprite, nineSlice, getX(), getY(), -90, (int) (width * fillPercent.getFillPercent()), height);
            } else {
                guiGraphics.blitNineSlicedSprite(sprite, nineSlice, getX(), getY(), -90, width, height);
            }
            if(dynamicColor!=null)
                guiGraphics.setColor(1, 1, 1, 1);
        } else if (texture != null) {
            if (fillPercent != null) {
                if (vertical) {

//                   guiGraphics.blitSprite(texture,getX(), (int) (getY() + height - height * fillPercent.getFillPercent()),width, (int) (height*fillPercent.getFillPercent()));
                    ClientMethods.drawTiledSprite(texture, guiGraphics, getX(), (int) (getY() + height - height * fillPercent.getFillPercent()), width, (int) (height * fillPercent.getFillPercent()));
                } else {
//                    guiGraphics.blitSprite(texture,getX(),getY(), (int) (width*fillPercent.getFillPercent()),height);
                    ClientMethods.drawTiledSprite(texture, guiGraphics, getX(), getY(), (int) (width * fillPercent.getFillPercent()), height);
                }
            } else {
//                TextureAtlasSprite atlasSprite = Minecraft.getInstance().getGuiSprites().getSprite(texture);
//                SpriteContents contents= atlasSprite.contents();
//                for (int i = 0; i < height; i+=contents.height()) {
//                    for (int j = 0; j < width; j+=contents.width()) {
//                        guiGraphics.blitSprite(texture,j,i,contents.width(),contents.height());
//                    }
//                }
                ClientMethods.drawTiledSprite(texture, guiGraphics, getX(), getY(), width, height);
            }
        } else if (color != null) {
            color.getColor().ifPresent(color1 -> {
                if (fillPercent != null) {
                    if (vertical)
                        guiGraphics.fill(getX(), (int) (getY() + height - height * fillPercent.getFillPercent()), getX() + width, getY() + height, color1.getIntColor());
                    else
                        guiGraphics.fill(getX(), getY(), (int) (getX() + width * fillPercent.getFillPercent()), getY() + height, color.getColor().get().getIntColor());
                } else {
                    guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, color1.getIntColor());
                }
            });
        } else if (dynamicColor != null) {
            IntegerColor integerColor = dynamicColor.getColor();
            if(integerColor!=null) {
                if (fillPercent != null) {
                    if (vertical)
                        guiGraphics.fill(getX(), (int) (getY() + height - height * fillPercent.getFillPercent()), getX() + width, getY() + height, integerColor.getIntColor());
                    else
                        guiGraphics.fill(getX(), getY(), (int) (getX() + width * fillPercent.getFillPercent()), getY() + height, integerColor.getIntColor());
                } else {
                    guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, integerColor.getIntColor());
                }
            }
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @FunctionalInterface
    public interface FillPercent {
        float getFillPercent();
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }

    @FunctionalInterface
    public interface Color {
        Optional<IntegerColor> getColor();
    }
}
