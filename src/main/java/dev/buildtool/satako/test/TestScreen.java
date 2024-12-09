package dev.buildtool.satako.test;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.gui.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;

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
        RadioButton radioButton2 = new RadioButton(radioButton.getXPos() + radioButton.getElementWidth(), 20, Component.literal("Radio button2"));
        addRenderableWidget(radioButton2);
        new ButtonGroup(radioButton, radioButton2);
        ArrayList<Object> elements = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Label label = new Label(0, 20 * i, Component.literal("#" + i), null);
            elements.add(label);
            addRenderableWidget(label);
        }
        Button button = new Button.Builder(Component.literal("Vanilla button 1"), p_93751_ -> {
        }).pos(0, 120).size(100, 20).build();
        button.setTooltip(Tooltip.create(Component.literal("Veeeeeeeeeeeeeryyyyyyyyyyyyyyyy looooooooooooooooooooooooooong tooooooooltiiiiiiiip")));
        addRenderableWidget(button);
        elements.add(button);
        TextField textField=new TextField(button.getX(),button.getY()+button.getHeight(),150);
        addRenderableWidget(textField);
        SwitchButton switchButton = new SwitchButton(0, 200, Component.literal("true"), Component.literal("false"), true, p_93751_ -> {
        });
        addRenderableWidget(switchButton);
        elements.add(switchButton);
        addRenderableWidget(new Label(getGuiLeft() + imageWidth, getGuiTop(), Component.literal("Clickable"), this, p_93751_ -> addPopup(Component.literal("Clicked first label")), null));
        addRenderableWidget(new Label(getGuiLeft() + imageWidth, getGuiTop() + 20, Component.literal("Clickable with background"), this, p_93751_ -> addPopup(Component.literal("Clicked second label")), Constants.ORANGE));
        LinkedHashMap<Component, Button.OnPress> linkedHashMap = new LinkedHashMap<>();
        DropDownButton dropDownButton = new DropDownButton(getGuiLeft() + imageWidth, getGuiTop() + 40, this, Component.literal("First choice"));
        linkedHashMap.put(Component.literal("First choice"), p_93751_ -> {
            addPopup(Component.literal("Clicked 1st choice"));
            addPopup(Component.literal("Notification"));
        });
        linkedHashMap.put(Component.literal("Second choice"), p_93751_ -> addPopup(Component.literal("Clicked 2nd choice")));
        linkedHashMap.put(Component.literal("Third choice"), p_93751_ -> addPopup(Component.literal("Clicked 3d choice")));
        dropDownButton.setChoices(linkedHashMap, 1);
        addRenderableWidget(dropDownButton);
        addRenderableWidget(new Label(getGuiLeft() + imageWidth, getGuiTop() + 20 * 3, Component.literal("Overlapping label"), this, p_93751_ -> minecraft.player.displayClientMessage(Component.literal("Clicked the label"), false), null));
        addRenderableWidget(new Button.Builder(Component.literal("Button"), p_93751_ -> minecraft.player.displayClientMessage(Component.literal("Clicked the button"), false)).pos(getGuiLeft() + imageWidth, getGuiTop() + 20 * 4).size(40, 20).build());

        addRenderableWidget(new SelectionButton(10, height - 20, Component.literal("Selection button")));

        Rectangle rectangle = new Rectangle(20, getGuiTop(), 50, getYSize(), Constants.ORANGE, () -> 0.33f);
        addRenderableWidget(rectangle);
        addTooltip(rectangle,() -> Component.literal("Tooltip"));
    }
}
