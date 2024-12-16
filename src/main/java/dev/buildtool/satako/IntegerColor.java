package dev.buildtool.satako;

import net.minecraft.util.FastColor;

/**
 * For some reason color in minecraft is encoded in A-R-G-B order, not R-G-B-A.
 */
public class IntegerColor
{
    private int color;
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
        red = (float) FastColor.ARGB32.red(color) /255;
        green = (float) FastColor.ARGB32.green(color) /255;
        blue = (float) FastColor.ARGB32.blue(color) /255;
        alpha = (float) FastColor.ARGB32.alpha(color) /255;
    }

    public IntegerColor(int redByte,int greenByte,int blueByte)
    {
        this.red=redByte/255f;
        this.green=greenByte/255f;
        this.blue=blueByte/255f;
        alpha=1;
        color=FastColor.ARGB32.color(255,redByte,greenByte,blueByte);
    }

    /**
     *
     * @param color without alpha
     */
    public IntegerColor(String color)
    {
        this(0xff000000+Integer.parseInt(color,16));
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

    @Override
    public String toString() {
        return "IntegerColor [red="+red+", green="+green+", blue="+blue+", alpha="+alpha+"]";
    }
}
