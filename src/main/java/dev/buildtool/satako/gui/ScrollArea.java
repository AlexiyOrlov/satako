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
import net.minecraft.network.chat.TextComponent;

import java.util.Arrays;
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
            this.y = (y + 20);
        }
        buttonLeft = x + width - 20;
        if (p_93633_.getString().isEmpty())
            bottomButtonTop = this.y + height / 2;
        else bottomButtonTop = this.y + height / 2 - 20;
        this.color = color;
        this.guiEventListeners = guiEventListeners;
        for (Object guiEventListener : guiEventListeners) {
            if (guiEventListener instanceof Positionable positionable) {
                positionable.setY(this.y + positionable.getY());
                positionable.setX(this.x + positionable.getX());
                if (guiEventListener instanceof Hideable hideable) {
                    hideable.setHidden(positionable.getY() < this.y || positionable.getY() + positionable.getElementHeight() > this.y + height);
                }
                if (positionable.getY() > highest)
                    highest = positionable.getY();
            } else if (guiEventListener instanceof AbstractWidget a) {
                a.x = x + a.x;
                a.y = y + a.y;
                a.visible = a.y >= y && a.y + a.getHeight() <= this.y + height;
                if (a.y > highest)
                    highest = a.y;
            }
        }
        for (Object item : guiEventListeners) {
            if (item instanceof Positionable positionable) {
                if (positionable.getY() == highest)
                    bottomElement = item;
            } else if (item instanceof AbstractWidget abstractWidget) {
                if (abstractWidget.y == highest)
                    bottomElement = abstractWidget;
            }
        }
        maxScrollDistance = bottomElement instanceof Positionable positionable ? positionable.getY() + positionable.getElementHeight() : bottomElement instanceof AbstractWidget abstractWidget ? abstractWidget.y + abstractWidget.getHeight() : 0;
    }

    public ScrollArea(int x, int y, int width, int height, Component p_93633_, IntegerColor backgroundColor, Object... items) {
        this(x, y, width, height, p_93633_, backgroundColor, Arrays.asList(items));
    }

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
    public void render(PoseStack poseStack, int mx, int my, float p_93660_) {

        fill(poseStack, x, y, x + getWidth(), y + getHeight(), color.getIntColor());

        Font font = Minecraft.getInstance().font;
        if (!getMessage().getString().isEmpty()) {
            drawCenteredString(poseStack, font, getMessage(), (x + width / 2), y - 15, 0xffffff);
        }


        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        int offsetY = getMessage().getString().isEmpty() ? 0 : 10;
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(x + width, y, 0).color(MINUS_BUTTON_COLOR.getRed(), MINUS_BUTTON_COLOR.getGreen(), MINUS_BUTTON_COLOR.getBlue(), 255).endVertex();
        bufferBuilder.vertex(buttonLeft, y, 0).color(MINUS_BUTTON_COLOR.getRed(), MINUS_BUTTON_COLOR.getGreen(), MINUS_BUTTON_COLOR.getBlue(), 255).endVertex();
        bufferBuilder.vertex(buttonLeft, bottomButtonTop + offsetY, 0).color(MINUS_BUTTON_COLOR.getRed(), MINUS_BUTTON_COLOR.getGreen(), MINUS_BUTTON_COLOR.getBlue(), 255).endVertex();
        bufferBuilder.vertex(x + width, bottomButtonTop + offsetY, 0).color(MINUS_BUTTON_COLOR.getRed(), MINUS_BUTTON_COLOR.getGreen(), MINUS_BUTTON_COLOR.getBlue(), 255).endVertex();

        bufferBuilder.vertex(buttonLeft + 20, bottomButtonTop + offsetY, 0).color(PLUS_BUTTON_COLOR.getRed(), PLUS_BUTTON_COLOR.getGreen(), PLUS_BUTTON_COLOR.getBlue(), 255).endVertex();
        bufferBuilder.vertex(buttonLeft, bottomButtonTop + offsetY, 0).color(PLUS_BUTTON_COLOR.getRed(), PLUS_BUTTON_COLOR.getGreen(), PLUS_BUTTON_COLOR.getBlue(), 255).endVertex();
        bufferBuilder.vertex(buttonLeft, bottomButtonTop + height / 2f, 0).color(PLUS_BUTTON_COLOR.getRed(), PLUS_BUTTON_COLOR.getGreen(), PLUS_BUTTON_COLOR.getBlue(), 255).endVertex();
        bufferBuilder.vertex(buttonLeft + 20, bottomButtonTop + height / 2f, 0).color(PLUS_BUTTON_COLOR.getRed(), PLUS_BUTTON_COLOR.getGreen(), PLUS_BUTTON_COLOR.getBlue(), 255).endVertex();
        tesselator.end();
        drawCenteredString(poseStack, font, new TextComponent("-"), buttonLeft + 10, y + height / 4, 0xffffff);
        drawCenteredString(poseStack, font, new TextComponent("+"), buttonLeft + 10, (bottomButtonTop + height / 4) - 10, 0xffffff);
        if (scrollDirection != 0) {
            if (scrolled == 0 || (scrolled > -(maxScrollDistance - height) || scrolled < -(maxScrollDistance - height) && scrollDirection == 1) && (scrolled <= 0 || scrollDirection == -1)) {
                for (Object guiEventListener : guiEventListeners) {
                    if (guiEventListener instanceof Positionable positionable3) {
                        positionable3.setY(positionable3.getY() + scrollDirection * 20);
                        if (positionable3 instanceof Hideable hideable) {
                            hideable.setHidden(positionable3.getY() < y || positionable3.getY() + positionable3.getElementHeight() > y + height);
                        }
                    } else if (guiEventListener instanceof AbstractWidget a) {
                        a.y = a.y + scrollDirection * 20;
                        a.visible = a.y > y && a.y + a.getHeight() < y + height;
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
                    hideable.setHidden(positionable3.getY() < y || positionable3.getY() + positionable3.getElementHeight() > y + height);
                }
            } else if (guiEventListener instanceof AbstractWidget a) {
                a.y = (int) (a.y + direction * 20);
                a.visible = a.y > y && a.y + a.getHeight() < y + height;
            }
        }
        return true;
    }

    @Override
    public void playDownSound(SoundManager p_93665_) {

    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {

    }
}
