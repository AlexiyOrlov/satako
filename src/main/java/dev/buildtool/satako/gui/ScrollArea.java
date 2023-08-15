package dev.buildtool.satako.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.buildtool.satako.IntegerColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;

import java.util.List;

public class ScrollArea extends AbstractButton {
    public final IntegerColor color;
    int buttonLeft;
    int bottomButtonTop;
    protected List<?> guiEventListeners;
    private int scrollDirection;
    private int scrolled;
    private int highest;
    private final int maxScrollDistance;
    private Object bottomElement;

    public ScrollArea(int x, int y, int width, int height, TextComponent p_93633_, IntegerColor color, List<?> guiEventListeners) {
        super(x, y, width, height, p_93633_);
        if (!p_93633_.getString().isEmpty()) {
            this.y = (y + 20);
        }
        buttonLeft = x + width - 20;
        if (p_93633_.getString().isEmpty())
            bottomButtonTop = this.y + height / 2;
        else bottomButtonTop = this.y + height / 2 - 20;
        this.color = color;
        this.guiEventListeners = guiEventListeners;
        for (Object guiEventListener : guiEventListeners) {
            if (guiEventListener instanceof Positionable) {
                Positionable positionable = (Positionable) guiEventListener;
                positionable.setY(this.y + positionable.getY());
                positionable.setX(this.x + positionable.getX());
                if (guiEventListener instanceof Hideable) {
                    Hideable hideable = (Hideable) guiEventListener;
                    if (positionable.getY() < this.y || positionable.getY() + positionable.getHeight() > this.y + height)
                        hideable.setHidden();
                }
                if (positionable.getY() > highest)
                    highest = positionable.getY();
            } else if (guiEventListener instanceof AbstractButton) {
                AbstractButton a = (AbstractButton) guiEventListener;
                a.x = x + a.x;
                a.y = y + a.y;
                a.visible = a.y >= y && a.y + a.getHeight() <= this.y + height;
                if (a.y > highest)
                    highest = a.y;
            }
        }
        for (Object item : guiEventListeners) {
            if (item instanceof Positionable) {
                Positionable positionable = (Positionable) item;
                if (positionable.getY() == highest)
                    bottomElement = item;
            } else if (item instanceof AbstractButton) {
                AbstractButton abstractWidget = (AbstractButton) item;
                if (abstractWidget.y == highest)
                    bottomElement = abstractWidget;
            }
        }
        if (bottomElement instanceof Positionable) {
            Positionable positionable = (Positionable) bottomElement;
            maxScrollDistance = positionable.getY() + positionable.getHeight();
        } else if (bottomElement instanceof AbstractButton) {
            AbstractButton abstractButton = (AbstractButton) bottomElement;
            maxScrollDistance = abstractButton.y + abstractButton.getHeight();
        } else maxScrollDistance = 0;
    }

//    public ScrollArea(int x, int y, int width, int height, Component p_93633_, IntegerColor backgroundColor, Object... items) {
//        this(x, y, width, height, p_93633_, backgroundColor, Arrays.asList(items));
//    }

    @Override
    public boolean mouseClicked(double mx, double my, int p_93643_) {
        if (mx > buttonLeft && mx < buttonLeft + 20 && my > y && my < (y + getHeight()) / 2f)
            scrollDirection = -1;
        else if (mx > buttonLeft && mx < buttonLeft + 20 && my > bottomButtonTop && my < bottomButtonTop + getHeight() / 2f) {
            scrollDirection = 1;
        }
        return super.mouseClicked(mx, my, p_93643_);
    }

    @Override
    public boolean mouseReleased(double p_93684_, double p_93685_, int p_93686_) {
        scrollDirection = 0;
        return super.mouseReleased(p_93684_, p_93685_, p_93686_);
    }

    @Override
    public void render(MatrixStack poseStack, int mx, int my, float p_93660_) {

        fill(poseStack, x, y, x + getWidth(), y + getHeight(), color.getIntColor());

        FontRenderer font = Minecraft.getInstance().font;
        if (!getMessage().getString().isEmpty()) {
            drawCenteredString(poseStack, font, getMessage(), (x + width / 2), y - 15, 0xffffff);
        }


        //RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();
        Tessellator tesselator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        int offsetY = getMessage().getString().isEmpty() ? 0 : 10;
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(x + width, y, 0).color(color.getRed(), color.getGreen(), 128, 255).endVertex();
        bufferBuilder.vertex(buttonLeft, y, 0).color(color.getRed(), color.getGreen(), 128, 255).endVertex();
        bufferBuilder.vertex(buttonLeft, bottomButtonTop + offsetY, 0).color(color.getRed(), color.getGreen(), 128, 255).endVertex();
        bufferBuilder.vertex(x + width, bottomButtonTop + offsetY, 0).color(color.getRed(), color.getGreen(), 128, 255).endVertex();

        bufferBuilder.vertex(buttonLeft + 20, bottomButtonTop + offsetY, 0).color(color.getRed(), 129, color.getBlue(), 255).endVertex();
        bufferBuilder.vertex(buttonLeft, bottomButtonTop + offsetY, 0).color(color.getRed(), 128, color.getBlue(), 255).endVertex();
        bufferBuilder.vertex(buttonLeft, bottomButtonTop + height / 2f, 0).color(color.getRed(), 128, color.getBlue(), 255).endVertex();
        bufferBuilder.vertex(buttonLeft + 20, bottomButtonTop + height / 2f, 0).color(color.getRed(), 128, color.getBlue(), 255).endVertex();
        tesselator.end();
        drawCenteredString(poseStack, font, new StringTextComponent("+"), buttonLeft + 10, y + height / 4, 0xffffff);
        drawCenteredString(poseStack, font, new StringTextComponent("-"), buttonLeft + 10, (bottomButtonTop + height / 4) - 10, 0xffffff);
        if (scrollDirection != 0) {
            if (scrolled == 0 || (scrolled > -(maxScrollDistance - height) || scrolled < -(maxScrollDistance - height) && scrollDirection == 1) && (scrolled <= 0 || scrollDirection == -1)) {
                for (Object guiEventListener : guiEventListeners) {
                    if (guiEventListener instanceof Positionable) {
                        Positionable positionable3 = (Positionable) guiEventListener;
                        positionable3.setY(positionable3.getY() + scrollDirection * 20);
                        if (positionable3 instanceof Hideable) {
                            Hideable hideable = (Hideable) positionable3;
                            if (positionable3.getY() < y || positionable3.getY() + positionable3.getHeight() > y + height)
                                hideable.setHidden();
                        }
                    } else if (guiEventListener instanceof AbstractButton) {
                        AbstractButton a = (AbstractButton) guiEventListener;
                        a.y = a.y + scrollDirection * 20;
                        a.visible = a.y > y && a.y + a.getHeight() < y + height;
                    }
                }
                scrolled += scrollDirection * 20;
            }
        }
    }

    @Override
    public void playDownSound(SoundHandler p_230988_1_) {

    }

    @Override
    protected void narrate() {

    }

    @Override
    public void onPress() {

    }
}
