package dev.buildtool.satako.test;

import dev.buildtool.satako.gui.ContainerScreen2;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class TestScreen extends ContainerScreen2<TestContainer> {
    public TestScreen(TestContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name, true);
    }
}
