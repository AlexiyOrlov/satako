package dev.buildtool.satako.test;

import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.gui.*;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
        RadioButton radioButton2 = new RadioButton(radioButton.getX() + radioButton.getElementWidth(), 20, Component.literal("Radio button2"));
        addRenderableWidget(radioButton2);
        new ButtonGroup(radioButton, radioButton2);
        ArrayList<Object> elements = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Label label = new Label(0, 20 * i, Component.literal("#" + i), false);
            elements.add(label);
            addRenderableWidget(label);
        }
        Button button = new Button.Builder(Component.literal("Vanilla button 1"), p_93751_ -> {
        }).pos(0, 120).size(100, 20).build();
        Button button1 = new Button.Builder(Component.literal("Vanilla button 2"), p_93751_ -> {
        }).pos(0, 140).size(100, 20).build();

        addRenderableWidget(button);
        addRenderableWidget(button1);
        elements.add(button);
        elements.add(button1);
        SwitchButton switchButton = new SwitchButton(0, 200, Component.literal("true"), Component.literal("false"), true, p_93751_ -> {
        });
        addRenderableWidget(switchButton);
        elements.add(switchButton);
        addRenderableWidget(new Label(getGuiLeft() + imageWidth, getGuiTop(), Component.literal("Clickable"), this, p_93751_ -> minecraft.player.displayClientMessage(Component.literal("Clicked first label"), false), false));
        addRenderableWidget(new Label(getGuiLeft() + imageWidth, getGuiTop() + 20, Component.literal("Clickable with background"), this, p_93751_ -> minecraft.player.displayClientMessage(Component.literal("Clicked second label"), false), true));
        LinkedHashMap<Component, Button.OnPress> linkedHashMap = new LinkedHashMap<>();
        DropDownButton dropDownButton = new DropDownButton(getGuiLeft() + imageWidth, getGuiTop() + 40, this, Component.literal("First choice"));
        linkedHashMap.put(Component.literal("First choice"), p_93751_ -> {
            minecraft.player.displayClientMessage(Component.literal("Clicked 1st choice"), false);
            dropDownButton.setMessage(p_93751_.getMessage());
            dropDownButton.onPress();
        });
        linkedHashMap.put(Component.literal("Second choice"), p_93751_ -> {
            minecraft.player.displayClientMessage(Component.literal("CLicked 2nd choice"), false);
            dropDownButton.setMessage(p_93751_.getMessage());
            dropDownButton.onPress();
        });
        linkedHashMap.put(Component.literal("Third choice"), p_93751_ -> {
            minecraft.player.displayClientMessage(Component.literal("Clicked 3d choice"), false);
            dropDownButton.setMessage(p_93751_.getMessage());
            dropDownButton.onPress();
        });
        dropDownButton.setChoices(linkedHashMap, 1);
        addRenderableWidget(dropDownButton);
        addRenderableWidget(new Label(getGuiLeft() + imageWidth, getGuiTop() + 20 * 3, Component.literal("Overlapping label"), this, p_93751_ -> minecraft.player.displayClientMessage(Component.literal("Clicked the label"), false), false));
        addRenderableWidget(new Button.Builder(Component.literal("Button"), p_93751_ -> minecraft.player.displayClientMessage(Component.literal("Clicked the button"), false)).pos(getGuiLeft() + imageWidth, getGuiTop() + 20 * 4).size(40, 20).build());

        List<BetterButton> betterButtons=new ArrayList<>(40);
        for (int i = 0; i < 40; i++) {
            betterButtons.add(new BetterButton(0,i*20,Component.literal("Button "+i)));
        }
        ScrollPane scrollPane=new ScrollPane(3,height/2,getGuiLeft()-10,height/2,Component.literal("List"),this,betterButtons.toArray(AbstractWidget[]::new));
        addRenderableWidget(scrollPane);

        addRenderableWidget(new SelectionButton(scrollPane.getWidth()+50, height - 20, Component.literal("Selection button")));

        ScrollArea scrollArea = new ScrollArea(3, 3, getGuiLeft() - 10, height / 4, Component.literal("List"), new IntegerColor(0x22F8A55E), elements);
        addRenderableWidget(scrollArea);
    }
}
