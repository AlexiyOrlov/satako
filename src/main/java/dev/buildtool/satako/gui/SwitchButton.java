package dev.buildtool.satako.gui;

import net.minecraft.util.text.ITextComponent;

/**
 * This button holds "true" or "false" state.
 */
public class SwitchButton extends BetterButton
{
    public boolean state;
    ITextComponent whenTrue, whenFalse;

    /**
     * @param whenTrue_  String shown when "true" is active
     * @param whenFalse_ String shown when "false" is active
     */
    public SwitchButton(int x, int y, ITextComponent whenTrue_, ITextComponent whenFalse_, boolean startState, IPressable pressable)
    {
        super(x, y, startState ? whenTrue_ : whenFalse_, pressable);
        int l1 = whenTrue_.getString().length();
        int l2 = whenFalse_.getString().length();
        if (l1 > l2)
        {
            this.width = fontRenderer.width(whenTrue_.getString()) + 8;
        }
        else
        {
            width = fontRenderer.width(whenFalse_.getString()) + 8;
        }
        state = startState;
        if (state)
        {
            setMessage(whenTrue_);
        }
        else
        {
            setMessage(whenFalse_);
        }
        whenTrue = whenTrue_;
        this.whenFalse = whenFalse_;
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_)
    {
        boolean clicked = super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
        if (clicked)
        {
            if (state)
            {
                setMessage(whenTrue);
            }
            else
            {
                setMessage(whenFalse);
            }
        }
        return clicked;
    }
}
