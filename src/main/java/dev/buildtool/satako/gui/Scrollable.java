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

    /**
     * Only enabled element should be able to scroll. Ability doesn't mean visibility and disability doesn't mean invisibility
     */
    boolean isEnabled();

    void setEnabled();

    void setDisabled();

    void setScrollAmount(int pixels);
}
