package dev.buildtool.satako.client;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.client.PositionedIngredient;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public class ToggleButtonWidget extends SimpleTextButton {
    private final Component t;
    private final Component f;
    private boolean state;
    private final OnClick onClick;

    public ToggleButtonWidget(Panel panel, Component whenTrue,Component whenFalse,boolean state,OnClick onClick) {
        super(panel, state ? whenTrue :whenFalse, null);
        t=whenTrue;
        f=whenFalse;
        this.state=state;
        this.onClick=onClick;
    }

    @Override
    public void onClicked(MouseButton mouseButton) {
        state=!state;
        if(state)
            setTitle(t);
        else
            setTitle(f);
        playClickSound();
        if(onClick!=null)
            onClick.onClicked(this);
    }

    public boolean getState()
    {
        return state;
    }

    @Override
    public boolean hasIcon() {
        return false;
    }

    @Override
    public Optional<PositionedIngredient> getIngredientUnderMouse() {
        return Optional.empty();
    }

    public interface OnClick{
        void onClicked(ToggleButtonWidget thisWidget);
    }
}
