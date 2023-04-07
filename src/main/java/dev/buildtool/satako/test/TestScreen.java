package dev.buildtool.satako.test;

import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.gui.*;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;

public class TestScreen extends ContainerScreen2<TestContainer> {
    public TestScreen(TestContainer container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name, true);
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new BetterButton(centerX, 0, new TextComponent("Button")));
        RadioButton radioButton = new RadioButton(centerX, 20, new TextComponent("Radio button 1"));
        addRenderableWidget(radioButton);
        RadioButton radioButton2 = new RadioButton(radioButton.getX() + radioButton.getElementWidth(), 20, new TextComponent("Radio button2"));
        addRenderableWidget(radioButton2);
        new ButtonGroup(radioButton, radioButton2);
        ArrayList<Object> elements = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Label label = new Label(0, 20 * i, new TextComponent("#" + i));
            elements.add(label);
            addRenderableWidget(label);
        }
        Button button = new Button(0, 120, 100, 20, new TextComponent("Vanilla button 1"), p_93751_ -> {
        });
        Button button1 = new Button(0, 140, 100, 20, new TextComponent("Vanilla button 2"), p_93751_ -> {
        });

        addRenderableWidget(button);
        addRenderableWidget(button1);
        elements.add(button);
        elements.add(button1);
        SwitchButton switchButton = new SwitchButton(0, 200, new TextComponent("true"), new TextComponent("false"), true, p_93751_ -> {
        });
        addRenderableWidget(switchButton);
        elements.add(switchButton);
        addRenderableWidget(new Label(getGuiLeft() + imageWidth, getGuiTop(), new TextComponent("Clickable 1"), this, p_93751_ -> minecraft.player.sendMessage(new TextComponent("Clicked first label"), Util.NIL_UUID)));
        addRenderableWidget(new Label(getGuiLeft() + imageWidth, getGuiTop() + 20, new TextComponent("Clickable 2"), this, p_93751_ -> minecraft.player.sendMessage(new TextComponent("Clicked second label"), Util.NIL_UUID)));
        ScrollArea scrollArea = new ScrollArea(3, 3, getGuiLeft() - 10, height, new TextComponent("List"), new IntegerColor(0x22F8A55E), elements);
        addRenderableWidget(scrollArea);
    }
}
