package dev.buildtool.satako.test;

import dev.buildtool.satako.Container2;
import dev.buildtool.satako.Satako;
import net.minecraft.world.entity.player.Inventory;

public class TestContainer extends Container2 {
    public TestContainer(int i, Inventory playerInventory) {
        super(Satako.TEST_CONTAINER.get(), i);
        addPlayerInventory(0, 0, playerInventory);
    }
}
