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

import java.util.HashMap;

/**
 * AbstractWidget check goes after Positionable
 */
public class ScrollList extends GuiComponent implements Scrollable, Hideable, Renderable, GuiEventListener, NarratableEntry {
    /**
     * Objects contained in this scrollist
     */
    public UniqueList<GuiEventListener> items;
    public int x, y, width, height;
    public boolean visible = true, scrolling;
    protected int scrollAmount, scrollPosition;
    private HashMap<GuiEventListener, Integer> startingPositions;

    /**
     */
    public ScrollList(int x, int y, int width_, int height_, int scrollingAmount, GuiEventListener... entries) {
        this.x = x;
        this.y = y;
        width = width_;
        height = height_;
        scrollAmount = scrollingAmount;
        if (entries != null) {
            items = new UniqueList<>(entries.length);
            startingPositions = new HashMap<>(entries.length);
            for (GuiEventListener guiEventListener : entries)
                addItem(guiEventListener);
        }
    }

    public void addItem(GuiEventListener g) {
        if (g instanceof Scrollable scrollable) {
            scrollable.setScrollable(true, true);
        }
        if (g instanceof Positionable positionable) {
            positionable.setX(positionable.getX() + this.x + 1);
            positionable.setY(positionable.getY() + this.y + 1);
            startingPositions.put(g, positionable.getY());
        } else if (g instanceof AbstractWidget abstractWidget) {
            abstractWidget.setPosition(x + abstractWidget.getX() + 1, y + abstractWidget.getY() + 1);
            startingPositions.put(g, abstractWidget.getY());
        }
        if (g instanceof Positionable positionable) {
            if (g instanceof Hideable hideable) {
                hideable.setHidden(!isElementInside(g));
            }
        } else if (g instanceof AbstractWidget abstractWidget) {
            abstractWidget.visible = isElementInside(abstractWidget);
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
                    scrollable.scroll((int) (scrollAmount * Math.signum(amount)), true);
                }
                if (item instanceof Hideable h) {
                    h.setHidden(!isElementInside(item));
                } else if (item instanceof AbstractWidget abstractWidget) {
                    abstractWidget.setY((int) (abstractWidget.getY() + scrollAmount * Math.signum(amount)));
                    abstractWidget.visible = isElementInside(item);
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
    public void setHidden(boolean hidden) {
        visible = !hidden;
        for (GuiEventListener item : items) {
            if (item instanceof Hideable hideable)
                hideable.setHidden(hidden);
            else if (item instanceof AbstractWidget abstractWidget) {
                abstractWidget.visible = !hidden;
            }
        }
    }

    /**
     *
     */
    private boolean isElementInside(GuiEventListener g) {
        if (g instanceof Positionable positionable) {
            return positionable.getY() > y && positionable.getY() + positionable.getHeight() < y + height;
        } else if (g instanceof AbstractWidget abstractWidget) {
            return abstractWidget.getY() > y && abstractWidget.getY() + abstractWidget.getHeight() < y + height;
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
            if (item instanceof Positionable positionable) {
                height += positionable.getHeight();
            } else if (item instanceof AbstractWidget abstractWidget) {
                height += abstractWidget.getHeight();
            }
        }
        return height;
    }

    protected int getScrollBarHeight() {
        return Mth.clamp((int) ((float) this.getInnerHeight() / height), 3, this.height);
    }

    protected int getMaxScrollAmount() {
        return Math.max(0, this.getInnerHeight());
    }

    protected void renderScrollBar() {
        int scrollBarHeight = this.getScrollBarHeight();
        int endX = this.x + this.width - 8;
        int k = this.x + this.width;
        int l = Math.max(this.y, this.scrollPosition * (this.height - scrollBarHeight) / this.getMaxScrollAmount() + this.y);
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
        if (visible && mx > x + width - 8 && mx < x + width && my > y && my < y + height) {
            scrolling = true;
            return true;
        }
        return false;
    }

    protected void setScrollPosition(double p_240207_) {
        this.scrollPosition = (int) Mth.clamp(p_240207_, 0.0D, this.getMaxScrollAmount());
    }

    @Override
    public boolean mouseDragged(double mx, double my, int p_94742_, double p_94743_, double drag) {
        if (scrolling) {
            if (my < y) {
                setScrollPosition(0);
                for (GuiEventListener item : items) {
                    if (item instanceof Positionable positionable) {
                        positionable.setY(startingPositions.get(item));
                        if (item instanceof Hideable hideable) {
                            hideable.setHidden(!isElementInside(item));
                        }
                    } else if (item instanceof AbstractWidget abstractWidget) {
                        abstractWidget.setY(startingPositions.get(item));
                        abstractWidget.visible = isElementInside(item);
                    }

                }
            } else if (my > y + height) {
                //delete?
                setScrollPosition(getMaxScrollAmount());
                for (GuiEventListener item : items) {
                    if (item instanceof Positionable positionable) {
//                        positionable.setY(startingPositions.get(item) - getInnerHeight()+y+height);
                        if (item instanceof Hideable hideable) {
//                            hideable.setHidden(isElementInside(item));
                        }
                    } else if (item instanceof AbstractWidget a) {
//                        a.setY(startingPositions.get(item)-getInnerHeight()+y+height);
                    }
                }
            } else {
                int scrollBarHeight = getScrollBarHeight();
                int i = Math.max(1, this.getMaxScrollAmount() / (height - scrollBarHeight));
                setScrollPosition(Mth.clamp(scrollPosition + drag * Mth.clamp(i, 1, scrollAmount), 0, getInnerHeight()));
                for (GuiEventListener item : items) {
                    if (item instanceof Positionable p) {
                        p.setY(p.getY() - (int) drag);
                        if (item instanceof Hideable hideable)
                            hideable.setHidden(!isElementInside(item));
                    } else if (item instanceof AbstractWidget abstractWidget) {
                        abstractWidget.setY(abstractWidget.getY() - (int) Mth.clamp(drag, -scrollAmount, scrollAmount));
                        abstractWidget.visible = isElementInside(abstractWidget);
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
        return false;
    }
}
