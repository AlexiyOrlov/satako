package dev.buildtool.satako.clientside.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.buildtool.satako.IntegerColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ScrollArea extends AbstractWidget {
    public final IntegerColor color;
    static final IntegerColor MINUS_BUTTON_COLOR = new IntegerColor(0x538AA6ff), PLUS_BUTTON_COLOR = new IntegerColor(0xABAB38ff);
    int buttonLeft;
    int bottomButtonTop;
    protected List<?> guiEventListeners;
    private int scrollDirection;
    private int scrolled;
    private int highest;
    private final int maxScrollDistance;
    private Object bottomElement;

    public ScrollArea(int x, int y, int width, int height, Component p_93633_, IntegerColor color, List<?> guiEventListeners) {
        super(x, y, width, height, p_93633_);
        if (!p_93633_.getString().isEmpty()) {
            this.setY(y + 20);
        }
        buttonLeft = x + width - 20;
        if (p_93633_.getString().isEmpty())
            bottomButtonTop = this.getY() + height / 2;
        else bottomButtonTop = this.getY() + height / 2 - 20;
        this.color = color;
        this.guiEventListeners = guiEventListeners;
        for (Object guiEventListener : guiEventListeners) {
            if (guiEventListener instanceof Positionable positionable) {
                positionable.setYPos(this.getY() + positionable.getYPos());
                positionable.setXPos(this.getX() + positionable.getXPos());
                if (guiEventListener instanceof Hideable hideable) {
                    hideable.setHidden(positionable.getYPos() < this.getY() || positionable.getYPos() + positionable.getElementHeight() > this.getY() + height);
                }
                if (positionable.getYPos() > highest)
                    highest = positionable.getYPos();
            } else if (guiEventListener instanceof AbstractWidget a) {
                a.setX(x + a.getX());
                a.setY(y + a.getY());
                a.visible = a.getY() >= y && a.getY() + a.getHeight() <= this.getY() + height;
                if (a.getY() > highest)
                    highest = a.getY();
            }
        }
        for (Object item : guiEventListeners) {
            if (item instanceof Positionable positionable) {
                if (positionable.getYPos() == highest)
                    bottomElement = item;
            } else if (item instanceof AbstractWidget abstractWidget) {
                if (abstractWidget.getY() == highest)
                    bottomElement = abstractWidget;
            }
        }
        maxScrollDistance = bottomElement instanceof Positionable positionable ? positionable.getYPos() + positionable.getElementHeight() : bottomElement instanceof AbstractWidget abstractWidget ? abstractWidget.getY() + abstractWidget.getHeight() : 0;
    }

    public ScrollArea(int x, int y, int width, int height, Component p_93633_, IntegerColor backgroundColor, Object... items) {
        this(x, y, width, height, p_93633_, backgroundColor, List.of(items));
    }

    @Override
    public boolean mouseClicked(double mx, double my, int p_93643_) {
        if (mx > buttonLeft && mx < buttonLeft + 20 && my > getY() && my < (getY() + getHeight()) / 2f)
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
    public void renderWidget(GuiGraphics poseStack, int mx, int my, float p_93660_) {

        poseStack.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color.getIntColor());

        Font font = Minecraft.getInstance().font;
        if (!getMessage().getString().isEmpty()) {
            poseStack.drawCenteredString(font, getMessage(), (getX() + width / 2), getY() - 15, 0xffffff);
        }

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        //RenderSystem.disableTexture();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        int offsetY = getMessage().getString().isEmpty() ? 0 : 10;
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(getX() + width, getY(), 0).color(MINUS_BUTTON_COLOR.getRed(), MINUS_BUTTON_COLOR.getGreen(), MINUS_BUTTON_COLOR.getBlue(), 255).endVertex();
        bufferBuilder.vertex(buttonLeft, getY(), 0).color(MINUS_BUTTON_COLOR.getRed(), MINUS_BUTTON_COLOR.getGreen(), MINUS_BUTTON_COLOR.getBlue(), 255).endVertex();
        bufferBuilder.vertex(buttonLeft, bottomButtonTop + offsetY, 0).color(MINUS_BUTTON_COLOR.getRed(), MINUS_BUTTON_COLOR.getGreen(), MINUS_BUTTON_COLOR.getBlue(), 255).endVertex();
        bufferBuilder.vertex(getX() + width, bottomButtonTop + offsetY, 0).color(MINUS_BUTTON_COLOR.getRed(), MINUS_BUTTON_COLOR.getGreen(), MINUS_BUTTON_COLOR.getBlue(), 255).endVertex();

        bufferBuilder.vertex(buttonLeft + 20, bottomButtonTop + offsetY, 0).color(PLUS_BUTTON_COLOR.getRed(), PLUS_BUTTON_COLOR.getGreen(), PLUS_BUTTON_COLOR.getBlue(), 255).endVertex();
        bufferBuilder.vertex(buttonLeft, bottomButtonTop + offsetY, 0).color(PLUS_BUTTON_COLOR.getRed(), PLUS_BUTTON_COLOR.getGreen(), PLUS_BUTTON_COLOR.getBlue(), 255).endVertex();
        bufferBuilder.vertex(buttonLeft, bottomButtonTop + height / 2f, 0).color(PLUS_BUTTON_COLOR.getRed(), PLUS_BUTTON_COLOR.getGreen(), PLUS_BUTTON_COLOR.getBlue(), 255).endVertex();
        bufferBuilder.vertex(buttonLeft + 20, bottomButtonTop + height / 2f, 0).color(PLUS_BUTTON_COLOR.getRed(), PLUS_BUTTON_COLOR.getGreen(), PLUS_BUTTON_COLOR.getBlue(), 255).endVertex();
        tesselator.end();
        poseStack.drawCenteredString(font, Component.literal("-"), buttonLeft + 10, getY() + height / 4, 0xffffff);
        poseStack.drawCenteredString(font, Component.literal("+"), buttonLeft + 10, (bottomButtonTop + height / 4) - 10, 0xffffff);
        if (scrollDirection != 0) {
            if (scrolled == 0 || (scrolled > -(maxScrollDistance - height) || scrolled < -(maxScrollDistance - height) && scrollDirection == 1) && (scrolled <= 0 || scrollDirection == -1)) {
                for (Object guiEventListener : guiEventListeners) {
                    if (guiEventListener instanceof Positionable positionable3) {
                        positionable3.setYPos(positionable3.getYPos() + scrollDirection * 20);
                        if (positionable3 instanceof Hideable hideable) {
                            hideable.setHidden(positionable3.getYPos() < getY() || positionable3.getYPos() + positionable3.getElementHeight() > getY() + height);
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
                positionable3.setYPos((int) (positionable3.getYPos() + direction * 20));
                if (positionable3 instanceof Hideable hideable) {
                    hideable.setHidden(positionable3.getYPos() < getY() || positionable3.getYPos() + positionable3.getElementHeight() > getY() + height);
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

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

    }
}
