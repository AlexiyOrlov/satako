package dev.buildtool.satako.gui;


import dev.buildtool.satako.Constants;
import dev.buildtool.satako.Methods;
import dev.buildtool.satako.UniqueList;

public class ScrollList implements Clickable, Scrollable, Hideable
{
    /**
     * Objects contained in this scrollist
     */
    public UniqueList<Object> items = new UniqueList<>(2);
    public int x, y, width, height;
    public boolean visible = true;
    protected int scrollAmount;
    /**
     *
     */
    public ScrollList(int x, int y, int width_, int height_, Object... entries)
    {
        this.x = x;
        this.y = y;
        width = width_;
        height = height_;
        if (entries != null)
        {
            for (Object object : entries)
                addItem(object);
        }
    }

    public void addItem(Object o)
    {
        if (o instanceof Scrollable)
        {
            Scrollable scrollable = (Scrollable) o;
            scrollable.setScrollable(true, true);
        }
        if (o instanceof Positionable && o instanceof Hideable)
        {
            Positionable positionable = (Positionable) o;
            if ((positionable.getY() < y || positionable.getY() + positionable.getHeight() > y + height))
            {
                ((Hideable) o).setHidden();
            }
        }
        items.add(o);
    }

    /**
     * Draws {@link Label} instances and borders
     */
    public void draw()
    {
        if (visible)
        {
            Methods.drawHorizontalLine(x, x + width, y, Constants.GREEN, 2);
            Methods.drawHorizontalLine(x, x + width, y + height, Constants.GREEN, 2);
            Methods.drawVerticalLine(x, y, y + height, Constants.GREEN, 2);
            Methods.drawVerticalLine(x + width, y, y + height, Constants.GREEN, 2);
        }


    }


    @Override
    public void scroll(int amount, boolean vertical)
    {
        if (visible)
        {
            for (Object item : items)
            {
                if (item instanceof Scrollable) {
                    ((Scrollable) item).scroll(amount, vertical);
                }
                if (item instanceof Positionable && item instanceof Hideable)
                {
                    Positionable p = (Positionable) item;
                    Hideable h = (Hideable) item;
                    if (p.getY() + p.getHeight() > y + height || p.getY() < y)
                    {
                        h.setHidden();
                    }
                    else
                    {
                        h.setVisible();
                    }
                }
            }
        }
    }

    @Override
    public void setScrollable(boolean vertical, boolean b)
    {

    }

    @Override
    public boolean isEnabled()
    {
        return true;
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
        for (Object item : items) {
            if (item instanceof Hideable)
                ((Hideable) item).setHidden();
        }
    }

    @Override
    public void setVisible()
    {
        visible = true;
        for (Object item : items)
        {
            if (item instanceof Hideable)
            {
                if (isElementInside(item))
                {
                    ((Hideable) item).setVisible();
                }
                else
                {
                    ((Hideable) item).setHidden();
                }
            }
        }
    }

    private boolean isElementInside(Object o)
    {
        if (o instanceof Positionable)
        {
            Positionable positionable = (Positionable) o;
            return !(positionable.getY() + positionable.getHeight() > y + height || positionable.getY() < y);
        }
        return false;
    }
}
