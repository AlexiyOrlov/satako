package dev.buildtool.satako.test;

import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.gui.ContainerScreen2;
import dev.buildtool.satako.gui.Label;
import dev.buildtool.satako.gui.ScrollArea;
import dev.buildtool.satako.gui.SwitchButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;

public class TestScreen extends ContainerScreen2<TestContainer> {
    public TestScreen(TestContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name, true);
    }

    @Override
    public void init() {
        super.init();
        ArrayList<Object> elements = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Label label = new Label(0, 20 * i + 20, new StringTextComponent("#" + i));
            elements.add(label);
            addButton(label);
        }
        Button button = new Button(0, 120, 100, 20, new StringTextComponent("Vanilla button 1"), p_93751_ -> {
        });
        Button button1 = new Button(0, 140, 100, 20, new StringTextComponent("Vanilla button 2"), p_93751_ -> {
        });

        addButton(button);
        addButton(button1);
        elements.add(button);
        elements.add(button1);
        SwitchButton switchButton = new SwitchButton(0, 200, new StringTextComponent("true"), new StringTextComponent("false"), true, p_93751_ -> {
        });
        addButton(switchButton);
        elements.add(switchButton);
        ScrollArea scrollArea = new ScrollArea(3, 3, getGuiLeft() - 10, height, new StringTextComponent("List"), new IntegerColor(0x22F8A55E), elements);
        addButton(scrollArea);
    }
}
