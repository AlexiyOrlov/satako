package dev.buildtool.satako.gui;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.buildtool.satako.Constants;
import dev.buildtool.satako.UniqueList;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;

/**
 * FIXME
 */
public class ScrollList extends GuiComponent implements Scrollable, Hideable, Renderable, GuiEventListener, NarratableEntry {
    /**
     * Objects contained in this scrollist
     */
    public UniqueList<GuiEventListener> items = new UniqueList<>(2);
    public int x, y, width, height;
    public boolean visible = true, scrolling;
    protected int scrollAmount, scrollAmount2;

    /**
     *
     */
    public ScrollList(int x, int y, int width_, int height_, GuiEventListener... entries) {
        this.x = x;
        this.y = y;
        width = width_;
        height = height_;
        if (entries != null) {
            for (GuiEventListener guiEventListener : entries)
                addItem(guiEventListener);
        }
    }

    public void addItem(GuiEventListener g) {
        if (g instanceof Scrollable scrollable) {
            scrollable.setScrollable(true, true);
        }
        if (g instanceof AbstractWidget abstractWidget) {
            abstractWidget.setPosition(x + abstractWidget.getX(), y + abstractWidget.getY());
        } else if (g instanceof Positionable positionable) {
            positionable.setX(positionable.getX() + this.x);
            positionable.setY(positionable.getY() + this.y);
        }
        if (g instanceof Positionable positionable && g instanceof Hideable hideable) {
            if ((positionable.getY() < y || positionable.getY() + positionable.getHeight() > y + height)) {
                hideable.setHidden();
            }
        }
        items.add(g);
    }

    @Override
    public void scroll(int amount, boolean vertical) {

    }

    @Override
    public boolean mouseScrolled(double p_94734_, double p_94735_, double amount) {
        if (!visible)
            return false;
        else {
            for (GuiEventListener item : items) {
                if (item instanceof Scrollable scrollable) {
                    scrollable.scroll((int) amount, true);
                }
                if (item instanceof Positionable p && item instanceof Hideable h) {
                    if (p.getY() + p.getHeight() > y + height || p.getY() < y) {
                        h.setHidden();
                    } else {
                        h.setVisible();
                    }
                }
            }
            return true;
        }
    }

    @Override
    public void setScrollable(boolean vertical, boolean b) {

    }

    @Override
    public void setEnabled() {

    }

    @Override
    public void setDisabled() {

    }

    @Override
    public void setScrollAmount(int pixels) {
        scrollAmount = pixels;
    }

    @Override
    public void setHidden() {
        visible = false;
        for (GuiEventListener item : items) {
            if (item instanceof Hideable)
                ((Hideable) item).setHidden();
        }
    }

    @Override
    public void setVisible()
    {
        visible = true;
        for (GuiEventListener item : items) {
            if (item instanceof Hideable) {
                if (isElementInside(item)) {
                    ((Hideable) item).setVisible();
                } else {
                    ((Hideable) item).setHidden();
                }
            }
        }
    }

    private boolean isElementInside(GuiEventListener g) {
        if (g instanceof Positionable positionable) {
            return !(positionable.getY() + positionable.getHeight() > y + height || positionable.getY() < y);
        }
        return false;
    }

    @Override
    public void render(PoseStack poseStack, int p_253973_, int p_254325_, float p_254004_) {
        if (visible) {
            hLine(poseStack, x, x + width, y, Constants.GREEN.getIntColor());
            hLine(poseStack, x, x + width, y + height, Constants.GREEN.getIntColor());
            vLine(poseStack, x, y, y + height, Constants.GREEN.getIntColor());
            vLine(poseStack, x + width, y, y + height, Constants.GREEN.getIntColor());
            renderScrollBar();
        }
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {
        p_169152_.add(NarratedElementType.USAGE, "Scroll list");
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean isMouseOver(double mx, double my) {
        return mx > x && mx < x + width && my > y && my < y + height;
    }

    protected int getInnerHeight() {
        int height = 0;
        for (GuiEventListener item : items) {
            if (item instanceof AbstractWidget abstractWidget) {
                height += abstractWidget.getHeight();
            } else if (item instanceof Positionable positionable) {
                height += positionable.getHeight();
            }
        }
        return height;
    }

    protected int getContentHeight() {
        return getInnerHeight();
    }

    protected int getScrollBarHeight() {
        return Mth.clamp((int) ((float) this.getContentHeight() / height), 32, this.height);
    }

    protected int getMaxScrollAmount() {
        return Math.max(0, this.getInnerHeight());
    }

    protected void renderScrollBar() {
        int scrollBarHeight = this.getScrollBarHeight();
        int endX = this.x + this.width - 8;
        int k = this.x + this.width;
        int l = Math.max(this.y, this.scrollAmount2 * (this.height - scrollBarHeight) / this.getMaxScrollAmount() + this.y);
        int i1 = l + scrollBarHeight;
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(endX, i1, 0.0D).color(128, 128, 128, 255).endVertex();
        bufferbuilder.vertex(k, i1, 0.0D).color(128, 128, 128, 255).endVertex();
        bufferbuilder.vertex(k, l, 0.0D).color(128, 128, 128, 255).endVertex();
        bufferbuilder.vertex(endX, l, 0.0D).color(128, 128, 128, 255).endVertex();
        bufferbuilder.vertex(endX, i1 - 1, 0.0D).color(192, 192, 192, 255).endVertex();
        bufferbuilder.vertex(k - 1, i1 - 1, 0.0D).color(192, 192, 192, 255).endVertex();
        bufferbuilder.vertex(k - 1, l, 0.0D).color(192, 192, 192, 255).endVertex();
        bufferbuilder.vertex(endX, l, 0.0D).color(192, 192, 192, 255).endVertex();
        tesselator.end();
    }

    @Override
    public boolean mouseClicked(double mx, double my, int p_94739_) {
        if (mx > x + width - 8 && mx < x + width && my > y && my < y + height) {
            scrolling = true;
            return true;
        }
        return false;
    }

    protected void setScrollAmount2(double p_240207_) {
        this.scrollAmount2 = (int) Mth.clamp(p_240207_, 0.0D, this.getMaxScrollAmount());
    }

    @Override
    public boolean mouseDragged(double mx, double my, int p_94742_, double p_94743_, double scroll) {
        if (scrolling) {
            if (my < y) {
                setScrollAmount2(0);
            } else if (my > y + height) {
                setScrollAmount2(getMaxScrollAmount());
            } else {
                int scrollBarHeight = getScrollBarHeight();
                int i = Math.max(1, this.getMaxScrollAmount() / (height - scrollBarHeight));
                setScrollAmount2(scrollAmount2 + scroll * i);
                for (GuiEventListener item : items) {
                    if (item instanceof Positionable p && item instanceof Hideable h) {
                        if (scrollAmount2 != 0 && scrollAmount2 != getInnerHeight())
                            p.setY((int) (p.getY() - scroll * i));
                        if (p.getY() + p.getHeight() > y + height || p.getY() < y) {
                            h.setHidden();
                        } else {
                            h.setVisible();
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double p_94753_, double p_94754_, int p_94755_) {
        scrolling = false;
        return GuiEventListener.super.mouseReleased(p_94753_, p_94754_, p_94755_);
    }
}
