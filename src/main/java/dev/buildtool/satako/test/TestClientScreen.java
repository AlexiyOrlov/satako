package dev.buildtool.satako.test;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.clientside.gui.Label;
import dev.buildtool.satako.clientside.gui.Screen2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestClientScreen extends Screen2 {
    private static class WidgetList extends AbstractSelectionList<Entry>{
        int rowWidth;
        public WidgetList(Minecraft minecraft, int width, int height,int x, int y, int itemHeight, List<? extends AbstractWidget> entries) {
            super(minecraft, width, height, y, itemHeight);
            setX(x);
            for (AbstractWidget entry : entries) {
                addEntry(new TestClientScreen.Entry(entry));
            }
            rowWidth=entries.stream().reduce((abstractWidget, abstractWidget2) -> abstractWidget.getWidth()>abstractWidget2.getWidth()?abstractWidget:abstractWidget2).get().getWidth();
            setWidth(Math.max(width,rowWidth));
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }

        @Override
        public int getRowWidth() {
            return width;
        }

        @Override
        protected int getDefaultScrollbarPosition() {
            return width-4;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            double d0 = Math.max(1, this.getMaxScroll());
            int i = this.height;
            int j = Mth.clamp((int)((float)(i * i) / (float)this.getMaxPosition()), 32, i - 8);
            double d1 = Math.max(1.0, d0 / (double)(i - j))*dragY;
            System.out.println(d1);
            for (TestClientScreen.Entry child : children()) {
                child.widget.setY((int) ((int) child.widget.getY()-dragY));
            }
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    public TestClientScreen(Component title) {
        super(title);
    }

    @Override
    public void init() {
        super.init();
        List<Label> list=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new Label(0,20*i,Component.literal("Label "+i), Constants.DARK));
        }
        addRenderableWidget(new WidgetList(minecraft, width / 2, height / 2, 3, 20, 20, list));
    }

    static class Entry extends ObjectSelectionList.Entry<Entry>
    {
        AbstractWidget widget;
        Entry(AbstractWidget abstractWidget)
        {
            this.widget=abstractWidget;
        }


        @Override
        public Component getNarration() {
            return Component.empty();
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            widget.render(guiGraphics,mouseX,mouseY,partialTick);
        }
    }
}
