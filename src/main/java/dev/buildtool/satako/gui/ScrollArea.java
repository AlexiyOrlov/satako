package dev.buildtool.satako.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.buildtool.satako.IntegerColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ScrollArea extends AbstractWidget {
    public final IntegerColor color;
    int buttonLeft;
    int bottomButtonTop;
    protected List<?> guiEventListeners;
    private int scrollDirection;
    private int scrolled;
    private int highest;
    private int lowest;
    private final int maxScrollDistance;
    private Object bottomElement;

    public ScrollArea(int x, int y, int width, int height, Component p_93633_, IntegerColor color, List<?> guiEventListeners) {
        super(x, y, width, height, p_93633_);
        if (!p_93633_.getString().isEmpty()) {
            this.setY(getY() + 20);
        }
        buttonLeft = x + width - 20;
        bottomButtonTop = getY() + height / 2;
        this.color = color;
        this.guiEventListeners = guiEventListeners;
        for (Object guiEventListener : guiEventListeners) {
            if (guiEventListener instanceof Positionable positionable) {
                positionable.setY(this.getY() + positionable.getY());
                positionable.setX(this.getX() + positionable.getX());
                if (positionable instanceof Hideable hideable) {
                    hideable.setHidden(positionable.getY() < getY() || positionable.getY() + positionable.getHeight() > getY() + height);
                }
                if (positionable.getY() > highest)
                    highest = positionable.getY();
                if (positionable.getY() < lowest) {
                    lowest = positionable.getY();
                }
            } else if (guiEventListener instanceof AbstractWidget a) {
                a.setPosition(getX() + a.getX(), getY() + a.getY());
                a.visible = a.getY() >= getY() && a.getY() + a.getHeight() <= getY() + height;
                if (a.getY() > highest)
                    highest = a.getY();
                if (a.getY() < lowest)
                    lowest = a.getY();
            }
        }
        for (Object item : guiEventListeners) {
            Object topElement;
            if (item instanceof Positionable positionable) {
                if (positionable.getY() == highest)
                    bottomElement = item;
            } else if (item instanceof AbstractWidget abstractWidget) {
                if (abstractWidget.getY() == highest)
                    bottomElement = abstractWidget;
            }
        }
        maxScrollDistance = bottomElement instanceof Positionable positionable ? positionable.getY() + positionable.getHeight() : bottomElement instanceof AbstractWidget abstractWidget ? abstractWidget.getY() + abstractWidget.getHeight() : 0;
    }

    public ScrollArea(int x, int y, int width, int height, Component p_93633_, IntegerColor backgroundColor, Object... items) {
        this(x, y, width, height, p_93633_, backgroundColor, List.of(items));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

    }

    @Override
    public boolean mouseClicked(double mx, double my, int p_93643_) {
        if (mx > buttonLeft && mx < buttonLeft + 20 && my > getY() && my < getY() + getHeight() / 2f)
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
    public void render(PoseStack poseStack, int mx, int my, float p_93660_) {

        fill(poseStack, getX(), getY(), getX() + getWidth(), getY() + getHeight(), color.getIntColor());

        Font font = Minecraft.getInstance().font;
        if (!getMessage().getString().isEmpty()) {
            drawCenteredString(poseStack, font, getMessage(), (getX() + width / 2), getY() - 15, 0xffffff);
        }


        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(getX() + width, getY(), 0).color(color.getRed(), color.getGreen(), 128, 255).endVertex();
        bufferBuilder.vertex(buttonLeft, getY(), 0).color(color.getRed(), color.getGreen(), 128, 255).endVertex();
        bufferBuilder.vertex(buttonLeft, bottomButtonTop, 0).color(color.getRed(), color.getGreen(), 128, 255).endVertex();
        bufferBuilder.vertex(getX() + width, bottomButtonTop, 0).color(color.getRed(), color.getGreen(), 128, 255).endVertex();

        bufferBuilder.vertex(buttonLeft + 20, bottomButtonTop, 0).color(color.getRed(), 129, color.getBlue(), 255).endVertex();
        bufferBuilder.vertex(buttonLeft, bottomButtonTop, 0).color(color.getRed(), 128, color.getBlue(), 255).endVertex();
        bufferBuilder.vertex(buttonLeft, bottomButtonTop + height / 2f, 0).color(color.getRed(), 128, color.getBlue(), 255).endVertex();
        bufferBuilder.vertex(buttonLeft + 20, bottomButtonTop + height / 2f, 0).color(color.getRed(), 128, color.getBlue(), 255).endVertex();
        tesselator.end();
        drawCenteredString(poseStack, font, Component.literal("-"), buttonLeft + 10, getY() + height / 4, 0xffffff);
        drawCenteredString(poseStack, font, Component.literal("+"), buttonLeft + 10, (bottomButtonTop + height / 4) - 10, 0xffffff);
        if (scrollDirection != 0) {
            if (scrolled == 0 || (scrolled > -(maxScrollDistance - height) || (scrolled < -(maxScrollDistance - height) && scrollDirection == 1)) && ((scrolled < height / 2 || (scrolled > height / 2 || scrolled > 0 && scrollDirection == -1)))) {
                for (Object guiEventListener : guiEventListeners) {
                    if (guiEventListener instanceof Positionable positionable3) {
                        positionable3.setY(positionable3.getY() + scrollDirection * 20);
                        if (positionable3 instanceof Hideable hideable) {
                            hideable.setHidden(positionable3.getY() < getY() || positionable3.getY() + positionable3.getHeight() > getY() + height);
                        }
                    } else if (guiEventListener instanceof AbstractWidget a) {
                        a.setY(a.getY() + scrollDirection * 20);
                        a.visible = a.getY() > getY() && a.getY() + a.getHeight() < getY() + height;
                    }
                }
                scrolled += scrollDirection * 20;
            }
        }
    }

    @Override
    public boolean mouseScrolled(double p_94734_, double p_94735_, double direction) {
        for (Object guiEventListener : guiEventListeners) {
            if (guiEventListener instanceof Positionable positionable3) {
                positionable3.setY((int) (positionable3.getY() + direction * 20));
                if (positionable3 instanceof Hideable hideable) {
                    hideable.setHidden(positionable3.getY() < getY() || positionable3.getY() + positionable3.getHeight() > getY() + height);
                }
            } else if (guiEventListener instanceof AbstractWidget a) {
                a.setY((int) (a.getY() + direction * 20));
                a.visible = a.getY() > getY() && a.getY() + a.getHeight() < getY() + height;
            }
        }
        return true;
    }

    @Override
    public void playDownSound(SoundManager p_93665_) {

    }
}
