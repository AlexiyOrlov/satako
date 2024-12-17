package dev.buildtool.satako.test;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.clientside.gui.BetterButton;
import dev.buildtool.satako.clientside.gui.Label;
import dev.buildtool.satako.clientside.gui.Screen2;
import dev.buildtool.satako.clientside.gui.ScrollPane;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class TestClientScreen extends Screen2 {
    public TestClientScreen(Component title) {
        super(title);
    }

    @Override
    public void init() {
        super.init();
        ArrayList<GuiEventListener> labels=new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Label label1=new Label(3,3+20*i,Component.literal(""+i), Constants.DARK);
            labels.add(label1);
            addRenderableOnly(label1);
        }
        ScrollPane scrollPane=new ScrollPane(3,3,300,height/2,Component.literal("Scroll area"),labels);
        addRenderableWidget(scrollPane);
    }
}
