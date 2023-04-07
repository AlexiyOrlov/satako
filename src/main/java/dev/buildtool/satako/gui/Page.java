package dev.buildtool.satako.gui;


import net.minecraft.client.gui.components.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 10/19/18 by alexiy.
 */
public class Page
{
    protected List<Object> uiElements = new ArrayList<>();
    protected List<Page> otherPages = new ArrayList<>(1);
    protected boolean hidden;

    public Page(Object... uiElements)
    {
        this.uiElements = new ArrayList<>(Arrays.asList(uiElements));
    }

    public Page(Page... otherPages)
    {
        this.otherPages = new ArrayList<>(Arrays.asList(otherPages));
    }

    public void addOtherPage(Page page)
    {
        otherPages.add(page);
    }

    public void add(Object uiElement)
    {
        uiElements.add(uiElement);
    }

    public void hide()
    {
        uiElements.forEach(o -> {
            if (o instanceof Hideable)
            {
                ((Hideable) o).setHidden(false);
            }
            if (o instanceof TextField)
            {
                ((TextField) o).setVisible(false);
                ((TextField) o).setEnabled();
            }
            else if (o instanceof Button)
            {
                ((Button) o).visible = false;
            }

        });
        hidden = true;
    }

    public void show()
    {
        uiElements.forEach(o -> {

            if (o instanceof Hideable) {
                ((Hideable) o).setHidden(false);
            }
            if (o instanceof TextField) {
                ((TextField) o).setVisible(true);
                ((TextField) o).setDisabled();
            } else if (o instanceof Button) {
                ((Button) o).visible = true;
            }

        });
        otherPages.forEach(Page::hide);
        hidden = false;
    }

    public boolean isHidden()
    {
        return hidden;
    }
}
