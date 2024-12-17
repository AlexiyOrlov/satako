package dev.buildtool.satako.clientside.gui;

import dev.ftb.mods.ftblibrary.icon.Icon;
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
    public ToggleButtonWidget(Panel panel, Component whenTrue,Component whenFalse,boolean state) {
        super(panel, state ? whenTrue :whenFalse, null);
        t=whenTrue;
        f=whenFalse;
        this.state=state;
    }

    @Override
    public void onClicked(MouseButton mouseButton) {
        state=!state;
        if(state)
            setTitle(t);
        else
            setTitle(f);
        playClickSound();
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
}
