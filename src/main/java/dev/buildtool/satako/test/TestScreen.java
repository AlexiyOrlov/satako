package dev.buildtool.satako.test;

import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.gui.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;

public class TestScreen extends ContainerScreen2<TestContainer> {
    public TestScreen(TestContainer container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name, true);
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new BetterButton(centerX, 0, Component.literal("Button")));
        RadioButton radioButton = new RadioButton(centerX, 20, Component.literal("Radio button 1"));
        addRenderableWidget(radioButton);
        RadioButton radioButton2 = new RadioButton(radioButton.getX() + radioButton.getElementWidth(), 20, Component.literal("Radio button2"));
        addRenderableWidget(radioButton2);
        new ButtonGroup(radioButton, radioButton2);
        ArrayList<Object> elements = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Label label = new Label(0, 20 * i, Component.literal("#" + i));
            elements.add(label);
            addRenderableWidget(label);
        }
        Button button = new Button(0, 120, 100, 20, Component.literal("Vanilla button 1"), p_93751_ -> {
        });
        Button button1 = new Button(0, 140, 100, 20, Component.literal("Vanilla button 2"), p_93751_ -> {
        });

        addRenderableWidget(button);
        addRenderableWidget(button1);
        elements.add(button);
        elements.add(button1);
        SwitchButton switchButton = new SwitchButton(0, 200, Component.literal("true"), Component.literal("false"), true, p_93751_ -> {
        });
        addRenderableWidget(switchButton);
        elements.add(switchButton);
        addRenderableWidget(new Label(getGuiLeft() + imageWidth, getGuiTop(), Component.literal("Clickable 1"), this, p_93751_ -> minecraft.player.displayClientMessage(Component.literal("Clicked first label"), false)));
        addRenderableWidget(new Label(getGuiLeft() + imageWidth, getGuiTop() + 20, Component.literal("Clickable 2"), this, p_93751_ -> minecraft.player.displayClientMessage(Component.literal("Clicked second label"), false)));
        ScrollArea scrollArea = new ScrollArea(3, 3, getGuiLeft() - 10, height, Component.literal("List"), new IntegerColor(0x22F8A55E), elements);
        addRenderableWidget(scrollArea);
    }
}
