package dev.buildtool.satako;

/**
 * For some reason color in minecraft is encoded in A-R-G-B order, not R-G-B-A.
 */
public class IntegerColor
{
    private final int color;
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;
    /**
     * @param color ARGB
     */
    public IntegerColor(int color)
    {
        this.color = color;
        red = (color >> 24 & 255) / 255f;
        green = (color >> 16 & 255) / 255f;
        blue = (color >> 8 & 255) / 255f;
        alpha = (color & 255) / 255f;
    }

    public int getIntColor()
    {
        return color;
    }

    public float getRed()
    {
        return red;
    }

    public float getBlue()
    {
        return blue;
    }

    public float getGreen()
    {
        return green;
    }

    public float getAlpha()
    {
        return alpha;
    }

}
