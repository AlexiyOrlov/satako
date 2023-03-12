package dev.buildtool.satako.test;

import dev.buildtool.satako.gui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

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
        RadioButton radioButton2 = new RadioButton(radioButton.getX() + radioButton.getWidth(), 20, Component.literal("Radio button2"));
        addRenderableWidget(radioButton2);
        new ButtonGroup(radioButton, radioButton2);
        Label one = new Label(0, 0, Component.literal("One"));
        Label two = new Label(0, 20, Component.literal("Two"));
        Label three = new Label(0, 40, Component.literal("Three"));
        Label four = new Label(0, 60, Component.literal("Four"));
        Label five = new Label(0, 80, Component.literal("Five"));
        Label six = new Label(0, 100, Component.literal("Six"));
        addRenderableWidget(one);
        addRenderableWidget(two);
        addRenderableWidget(three);
        addRenderableWidget(four);
        addRenderableWidget(five);
        addRenderableWidget(six);
        Button button = Button.builder(Component.literal("Vanilla button 1"), p_93751_ -> {
        }).pos(0, 120).size(100, 20).build();
        Button button1 = Button.builder(Component.literal("Vanilla button 2"), p_93751_ -> {
        }).pos(0, 140).size(100, 20).build();

        ScrollPane scrollPane = new ScrollPane(Minecraft.getInstance(), getGuiLeft() - 2, getGuiTop() - 2, 20, 0, 3, 10);
        addRenderableWidget(scrollPane.addItems(one, two, three, four, five, six, button, button1));

        addRenderableWidget(button);
        addRenderableWidget(button1);
        SwitchButton switchButton = new SwitchButton(0, 200, Component.literal("true"), Component.literal("false"), true, p_93751_ -> {
        });
        addRenderableWidget(switchButton);
        scrollPane.addItems(switchButton);
        addRenderableWidget(new Label(getGuiLeft() + imageWidth, getGuiTop(), Component.literal("Clickable 1"), this, p_93751_ -> minecraft.player.displayClientMessage(Component.literal("Clicked first label"), false)));
        addRenderableWidget(new Label(getGuiLeft() + imageWidth, getGuiTop() + 20, Component.literal("Clickable 2"), this, p_93751_ -> minecraft.player.displayClientMessage(Component.literal("Clicked second label"), false)));

    }
}
