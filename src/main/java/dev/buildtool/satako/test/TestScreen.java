package dev.buildtool.satako.test;

import dev.buildtool.satako.gui.BetterButton;
import dev.buildtool.satako.gui.ContainerScreen2;
import dev.buildtool.satako.gui.RadioButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;

public class TestScreen extends ContainerScreen2<TestContainer> {
    public TestScreen(TestContainer container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name, true);
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new BetterButton(centerX, 0, new TextComponent("Button")));
        addRenderableWidget(new RadioButton(centerX, 20, new TextComponent("Radio button")));
    }
}
