package dev.buildtool.satako;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class RandomizedList<I> extends ArrayList<I> {

    private final Random r=new Random();

    public RandomizedList(int initialCapacity) {
        super(initialCapacity);
    }

    public RandomizedList() {
    }

    public RandomizedList(Collection<? extends I> c) {
        super(c);
    }

    public I getRandom()
    {
        if(size()>0)
            return this.get(r.nextInt(size()));
        return null;
    }

    /**
     * Gets a random element and removes it
     */
    public I removeRandom()
    {
        if(size()>0)
            return remove(r.nextInt(size()));
        return null;
    }
}
