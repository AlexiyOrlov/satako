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
    private final FillPercent fillPercent;
    private TextureAtlasSprite sprite;
    private ResourceLocation texture;
    private boolean vertical = true;

    public Rectangle(int x, int y, int width, int height, @Nullable IntegerColor color, @Nullable TextureAtlasSprite atlasSprite, @Nullable FillPercent fillPercent, boolean vertical) {
        this(x, y, width, height, atlasSprite, fillPercent);
        this.color = () -> Optional.ofNullable(color);
        this.vertical = vertical;
    }

    public Rectangle(int x, int y, int width, int height, @Nullable IntegerColor integerColor, @Nullable FillPercent fillPercent) {
        super(x, y, width, height, Component.empty());
        color = () -> Optional.ofNullable(integerColor);
        this.fillPercent = fillPercent;
    }

    private Rectangle(int x, int y, int width, int height, TextureAtlasSprite atlasSprite, @Nullable FillPercent fillPercent) {
        this(x, y, width, height, (IntegerColor) null, fillPercent);
        sprite = atlasSprite;
    }

    public Rectangle(int x, int y, int width, int height, TextureAtlasSprite atlasSprite) {
        this(x, y, width, height, atlasSprite, null);
    }

    private Rectangle(int x, int y, int width, int height, @Nullable IntegerColor color, TextureAtlasSprite sprite, FillPercent fillPercent) {
        super(x, y, width, height, Component.empty());
        this.color = () -> Optional.ofNullable(color);
        this.fillPercent = fillPercent;
        this.sprite = sprite;
    }

    public Rectangle(int x, int y, int width, int height, @Nullable IntegerColor color, TextureAtlasSprite atlasSprite) {
        this(x, y, width, height, color, atlasSprite, null);
        this.sprite = atlasSprite;
    }

    public static Rectangle horizontal(int x, int y, int width, int height, @Nullable IntegerColor color, @Nullable TextureAtlasSprite atlasSprite, FillPercent fillPercent) {
        Rectangle rectangle = new Rectangle(x, y, width, height, color, atlasSprite, fillPercent);
        rectangle.vertical = false;
        return rectangle;
    }

    public Rectangle(int x, int y, int width, int height, FillPercent fillPercent, ResourceLocation texture, boolean vertical) {
        super(x, y, width, height, Component.empty());
        this.fillPercent = fillPercent;
        this.texture = texture;
        this.vertical = vertical;
    }

    public static Rectangle withSprite(int x, int y, int width, int height, TextureAtlasSprite atlasSprite, FillPercent fillPercent) {
        return new Rectangle(x, y, width, height, atlasSprite, fillPercent);
    }

    public static Rectangle colored(int x, int y, int width, int height, IntegerColor color, FillPercent fillPercent) {
        return new Rectangle(x, y, width, height, color, fillPercent);
    }

    public static Rectangle withColoredSprite(int x, int y, int width, int height, IntegerColor color, TextureAtlasSprite atlasSprite, FillPercent fillPercent) {
        return new Rectangle(x, y, width, height, color, atlasSprite, fillPercent);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().translate(0, 0, 399);
        if (sprite != null) {
            int min = Math.min(width, height);
            color.getColor().ifPresent(color1 -> guiGraphics.setColor(color1.getRed(), color1.getGreen(), color1.getBlue(), color1.getAlpha()));
            GuiSpriteScaling.NineSlice nineSlice = new GuiSpriteScaling.NineSlice(min, min, new GuiSpriteScaling.NineSlice.Border(0, 0, 0, 0));
            if (fillPercent != null) {
                if (vertical)
                    guiGraphics.blitNineSlicedSprite(sprite, nineSlice, getX(), (int) (getY() + height - height * fillPercent.getFillPercent()), -90, width, (int) (height * fillPercent.getFillPercent()));
                else
                    guiGraphics.blitNineSlicedSprite(sprite, nineSlice, getX(), getY(), -90, (int) (width * fillPercent.getFillPercent()), height);
            } else {
                guiGraphics.blitNineSlicedSprite(sprite, nineSlice, getX(), getY(), -90, width, height);
            }
            color.getColor().ifPresent(color1 -> guiGraphics.setColor(1, 1, 1, 1));
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
        } else {
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
