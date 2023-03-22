package dev.buildtool.satako.test;

import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.gui.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

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
        List<Label> labels = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            Label label = new Label(0, 20 * i, Component.literal("#" + i));
            labels.add(label);
        }
        labels.forEach(this::addRenderableWidget);
        Button button = Button.builder(Component.literal("Vanilla button 1"), p_93751_ -> {
        }).pos(0, 260).size(100, 20).build();
        Button button1 = Button.builder(Component.literal("Vanilla button 2"), p_93751_ -> {
        }).pos(0, 280).size(100, 20).build();

        addRenderableWidget(button);
        addRenderableWidget(button1);
        addRenderableWidget(new Label(getGuiLeft() + imageWidth, getGuiTop(), Component.literal("Clickable 1"), this, p_93751_ -> minecraft.player.displayClientMessage(Component.literal("Clicked first label"), false)));
        addRenderableWidget(new Label(getGuiLeft() + imageWidth, getGuiTop() + 20, Component.literal("Clickable 2"), this, p_93751_ -> minecraft.player.displayClientMessage(Component.literal("Clicked second label"), false)));

        ScrollArea scrollArea = new ScrollArea(3, 3, getGuiLeft() - 20, 200, Component.literal("List"), new IntegerColor(0x22F88672), labels);
        addRenderableWidget(scrollArea);
    }
}
