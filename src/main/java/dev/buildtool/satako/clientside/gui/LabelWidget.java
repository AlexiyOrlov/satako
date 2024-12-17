package dev.buildtool.satako.clientside.gui;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.Functions;
import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.clientside.ClientFunctions;
import dev.buildtool.satako.clientside.ClientMethods;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;


public class LabelWidget extends Widget {
    private final IntegerColor background;
    private final Component text;
    public LabelWidget(Panel p, IntegerColor background, Component text) {
        super(p);
        this.background=background;
        this.text=text;
        setWidth(ClientFunctions.calculateStringWidth(text)+3);
        setHeight(10);
    }

    @Override
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        ClientMethods.drawBackground(graphics,x,y,0,width,height, background);
        AbstractWidget.renderScrollingString(graphics,theme.getFont(),text,getX(),getY(),getX()+getWidth()-4,getY()+getHeight(),Constants.WHITE.getIntColor());
    }
}
