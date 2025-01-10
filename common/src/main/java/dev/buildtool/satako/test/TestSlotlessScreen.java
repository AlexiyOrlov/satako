package dev.buildtool.satako.test;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.buildtool.satako.Constants;
import dev.buildtool.satako.client.gui.*;
import dev.buildtool.satako.platform.Services;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

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
        HashBasedTable<Integer,Integer,AbstractWidget> hashBasedTable=HashBasedTable.create();
        int index=0;
        for (int row = 0; row < 30; row++) {
            for (int column = 0; column < 1; column++) {
                AbstractWidget l=new Label(0,0,Component.literal("Label #"+index),Constants.DARK);
                AbstractWidget betterButton=new TextField(0,0,"Text field #"+index);//new Label(0,0,Component.literal("Label"+index),Constants.ORANGE);
                hashBasedTable.put(row,column,l);
                hashBasedTable.put(row,column+1,betterButton);
                index++;
            }
        }
        ScrollArea scrollArea=new ScrollArea(10,10,lava2.getX(),height/2,Component.literal("Scroll area"), this);
        addRenderableWidget(scrollArea);
        for (Table.Cell<Integer, Integer, AbstractWidget> cell : hashBasedTable.cellSet()) {
            scrollArea.addWidget(cell.getValue(),cell.getRowKey(),cell.getColumnKey());
        }
        scrollArea.alignWidgets();
    }

    @Override
    public void onClose() {
        super.onClose();
        minecraft.setScreen(previous);
    }
}
