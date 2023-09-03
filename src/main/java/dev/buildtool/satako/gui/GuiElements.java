package dev.buildtool.satako.gui;

import com.google.common.collect.TreeBasedTable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;

import java.util.*;

/**
 * A class for managing UI controls
 */
public class GuiElements
{
    private Screen owner;
    private TreeBasedTable<Integer, Integer, Object> elementmap = TreeBasedTable.create();
    private HashMap<Integer, Integer> rowHeights = new HashMap<>();
    private HashMap<Integer, Integer> columnWidths = new HashMap<>();
    private int spacing;

    public GuiElements(Screen screen, int elementSpacing)
    {
        owner = screen;
        spacing = elementSpacing;
    }

    public GuiElements(Screen screen)
    {
        owner = screen;
        spacing = 3;
    }


    public void addElement(int row, int column, Object object)
    {
        addElement(row, column, object, 0, 0);
    }

    /**
     * Adds an element to the table. Subsequent elements will be automatically aligned based on previous elements positions
     *
     * @param offsetX left margin for the object (affects all elements in current row)
     * @param offsetY top margin for the object (affects subsequent element height)
     */
    public void addElement(int row, int column, Object object, int offsetX, int offsetY)
    {
        if (owner instanceof ControlsContainer)
        {
            if (object instanceof Positionable)
            {
                Positionable positionable = (Positionable) object;
                Object ob = elementmap.get(row, column - 1);
                if (ob instanceof Positionable)
                {
                    Positionable previouselement = (Positionable) ob;
                    positionable.setX(spacing + previouselement.getX() + previouselement.getWidth() + offsetX);
                }
                else
                {
                    positionable.setX(spacing + offsetX);
                }

                SortedMap previousrow = elementmap.row(row - 1);
                if (previousrow != null)
                {
                    int y = 0;
                    for (int i = 0; i < previousrow.size(); i++)
                    {
                        Object objj = previousrow.get(i);
                        if (objj instanceof Positionable)
                        {
                            Positionable positionable1 = (Positionable) objj;
                            if (y < positionable1.getY() + positionable1.getElementHeight())
                            {
                                y = positionable1.getY() + positionable1.getElementHeight();
                            }
                        }
                    }
                    positionable.setY(spacing + y + offsetY);
                }
                else
                {
                    positionable.setY(spacing + offsetY);
                }
                if (positionable instanceof Label)
                {
                    ((ControlsContainer) owner).addLabel((Label) positionable);
                }
                else if (positionable instanceof TextField)
                {
                    ((ControlsContainer) owner).addTextField((TextField) positionable);
                }
                else if (positionable instanceof Button) ((ControlsContainer) owner).addButton((Button) positionable);
            }
        }
        elementmap.put(row, column, object);
    }

    public void alignColumns()
    {
        Map<Integer, Map<Integer, Object>> columns = elementmap.columnMap();
        for (Integer column : columns.keySet())
        {
            int broadest = 0;
            Map<Integer, Object> row = columns.get(column);
            for (Integer key : row.keySet())
            {
                Object value = row.get(key);
                if (value instanceof Positionable)
                {
                    Positionable positionable = (Positionable) value;
                    int width = positionable.getWidth();
                    if (width > broadest) broadest = width;

                }
            }
            columnWidths.put(column, broadest);
        }
        columnWidths.forEach((nextcolumn, integer2) -> {
            Map nextColumn = (columns.get(nextcolumn + 1));
            if (nextColumn != null)
            {
                for (Object o : nextColumn.values())
                {
                    if (o instanceof Positionable)
                    {
                        ((Positionable) o).setX(columnWidths.get(nextcolumn) + spacing);
                    }
                }
            }
        });
    }

    public Object getElement(int row, int column)
    {
        return elementmap.get(row, column);
    }

    public void removeElement(int row, int column)
    {
        Object o = elementmap.remove(row, column);
        if (owner instanceof ControlsContainer)
        {
            ControlsContainer controlsContainer = (ControlsContainer) owner;
            if (o instanceof TextField)
            {
                controlsContainer.removeTextField((TextField) o);
            }
            else if (o instanceof Label)
            {
                controlsContainer.removeLabel((Label) o);
            }
            else if (o instanceof Button) controlsContainer.removeButton((Button) o);
        }
    }

    /**
     * Removes the object from the map. This method is slower than {@link GuiElements#removeElement(int, int)}
     *
     * @param o
     */
    public void removeElement(Object o)
    {
        SortedSet<Integer> rowKeys = elementmap.rowKeySet();
        Set<Integer> columnkeys = elementmap.columnKeySet();
        dble:
        for (Integer rowKey : rowKeys)
        {
            for (Integer columnkey : columnkeys)
            {
                Object value = elementmap.get(rowKey, columnkey);
                if (value != null && value == o)
                {
                    elementmap.remove(rowKey, columnkey);
                    break dble;
                }
            }
        }
    }

    public void clearElements()
    {
        elementmap.clear();
        rowHeights.clear();
        columnWidths.clear();
    }

    @Override
    public String toString()
    {
        return elementmap.toString();
    }
}
