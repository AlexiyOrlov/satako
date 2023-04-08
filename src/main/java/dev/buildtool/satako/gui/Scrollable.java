package dev.buildtool.satako.gui;

public interface Scrollable
{
    /**
     * Scrolls the object for the specified amount if it is enabled
     *
     * @param vertical whether scroll is vertical
     */
    void scroll(int amount, boolean vertical);

    void setScrollable(boolean vertical, boolean b);

    void setEnabled(boolean enabled);
}
