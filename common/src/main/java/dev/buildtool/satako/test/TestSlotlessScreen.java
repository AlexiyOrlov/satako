package dev.buildtool.satako.test;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.client.gui.BetterButton;
import dev.buildtool.satako.client.gui.Label;
import dev.buildtool.satako.client.gui.Rectangle;
import dev.buildtool.satako.client.gui.Screen2;
import dev.buildtool.satako.platform.Services;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluids;

public class TestSlotlessScreen extends Screen2 {
    private Screen previous;
    public TestSlotlessScreen(Component title,Screen screen) {
        super(title);
        previous=screen;
    }

    @Override
    public void init() {
        super.init();
        Rectangle lava=new Rectangle(centerX,centerY,60,70, Services.PLATFORM.getFluidTexture(Fluids.LAVA,true));
        addRenderableOnly(lava);
        Rectangle lava2=new Rectangle(lava.getX()-lava.getWidth(),centerY,60,60,() -> 0.5f,Services.PLATFORM.getFluidTexture(Fluids.LAVA,true),null);
        addRenderableOnly(lava2);
        Label label=new Label(lava2.getX()+lava2.getWidth(),lava2.getY()-20,Component.literal("Label"), Constants.BLACK);
        addRenderableOnly(label);
        BetterButton press=new BetterButton(label.getX()+label.getWidth(),label.getY(),Component.literal("Press for popup"),button -> addPopup(Component.literal("Popup")));
        addRenderableWidget(press);
    }

    @Override
    public void onClose() {
        super.onClose();
        minecraft.setScreen(previous);
    }
}
