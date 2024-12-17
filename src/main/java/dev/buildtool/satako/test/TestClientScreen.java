package dev.buildtool.satako.test;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.clientside.gui.*;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

import java.util.AbstractList;
import java.util.ArrayList;

public class TestClientScreen extends FTBScreen {
    private LabelWidget label;
    private ToggleButtonWidget toggleButtonWidget;

    @Override
    public void addWidgets() {
        label = new LabelWidget(this, Constants.BLACK, Component.literal("Label"));
        add(label);
        toggleButtonWidget = new ToggleButtonWidget(this, Component.literal("true"), Component.literal("false"), true);
        add(toggleButtonWidget);
    }

    @Override
    public void alignWidgets() {
        super.alignWidgets();
        label.setPos(centerX/2,centerY);
        toggleButtonWidget.setPos(centerX/2,centerY+20);
    }
}
