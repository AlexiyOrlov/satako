package dev.buildtool.satako.gui;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Used to group radio buttons - {@link RadioButton}.
 */
public class ButtonGroup
{

    private final ArrayList<RadioButton> buttons = new ArrayList<>();

    /**
     * Adds and connects buttons
     */
    public ButtonGroup(RadioButton... radioButtons)
    {
        buttons.addAll(Arrays.asList(radioButtons));
        connect();
    }

    public void add(RadioButton rb)
    {
        buttons.add(rb);
    }

    /**
     * Performs interconnection of buttons
     */
    public void connect()
    {
        for (RadioButton rb : buttons)
        {
            ArrayList<RadioButton> others = (ArrayList<RadioButton>) buttons.clone();
            others.remove(rb);
            for (RadioButton rb2 : others)
            {
                rb.addNeighbour(rb2);
            }
        }
    }

    @Nullable
    public RadioButton getSelected()
    {
        for (RadioButton r : buttons)
        {
            if (r.selected) return r;
        }
        return null;
    }

    public void setSelected(int button)
    {
        if (button >= 0 && button < buttons.size())
        {
            setSelected(buttons.get(button));
        }
    }

    public void setSelected(RadioButton button)
    {
        buttons.forEach(radioButton -> radioButton.selected= radioButton == button);
    }

    public ArrayList<RadioButton> getButtons()
    {
        return buttons;
    }
}
