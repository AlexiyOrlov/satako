package dev.buildtool.satako;

import java.util.Arrays;
import java.util.Collection;

/**
 * List with unique elements
 */
public class UniqueList<E> extends RandomizedList<E>
{
    public UniqueList(int size)
    {
        super(size);
    }

    public UniqueList(E... elements)
    {
        this.addAll(Arrays.asList(elements));
    }

    public UniqueList(Collection<E> collection)
    {
        addAll(collection);
    }

    @Override
    public boolean add(E e)
    {
        if (!contains(e))
        {
            return super.add(e);
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c)
    {
        boolean added=false;
        for (E e : c)
        {
            if(add(e))
                added=true;
        }
        return added;
    }
}
