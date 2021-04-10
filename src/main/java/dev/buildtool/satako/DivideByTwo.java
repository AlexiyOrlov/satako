package dev.buildtool.satako;

public class DivideByTwo {

    public static int oneTime(int number)
    {
        return number>>1;
    }

    public static int twoTimes(int number)
    {
        return number>>2;
    }

    public static int threeTimes(int number)
    {
        return number>>3;
    }

    public static int fourTimes(int number)
    {
        return number>>4;
    }

    public static int nTimes(int number, int times)
    {
        return number>>times;
    }
}
