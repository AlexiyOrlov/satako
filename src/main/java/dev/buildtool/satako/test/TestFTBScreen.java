package dev.buildtool.satako.test;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.clientside.gui.*;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.ScrollBar;
import net.minecraft.network.chat.Component;

public class TestFTBScreen extends FTBScreen {
    private LabelWidget label;
    private ToggleButtonWidget toggleButtonWidget;
    private final Panel panel;
    public ScrollBar scrollBar;

    public TestFTBScreen() {
        panel=new Panel(this) {
            @Override
            public void addWidgets() {
                label = new LabelWidget(this, Constants.BLACK, Component.literal("Label"));
                add(label);
                toggleButtonWidget = new ToggleButtonWidget(this, Component.literal("true"), Component.literal("false"), true);
                add(toggleButtonWidget);
            }

            @Override
            public void alignWidgets() {
                label.setPos(0,0);
                toggleButtonWidget.setPos(0,20);
            }
        };
        scrollBar=new PanelScrollBar(this, ScrollBar.Plane.VERTICAL,panel);
        scrollBar.setScrollStep(10);
    }

    @Override
    public void addWidgets() {

        add(panel);
        add(scrollBar);
    }

    @Override
    public void alignWidgets() {
        super.alignWidgets();
        panel.setPosAndSize(0,0,width,height);
        scrollBar.setPosAndSize(panel.getPosX() + this.panel.getWidth() - 12, panel.getPosY(), 12, panel.getHeight());
    }
}
