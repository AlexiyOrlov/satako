//package dev.buildtool.satako.client;
//
//import dev.buildtool.satako.Constants;
//import dev.buildtool.satako.IntegerColor;
//import dev.ftb.mods.ftblibrary.ui.Panel;
//import dev.ftb.mods.ftblibrary.ui.Theme;
//import dev.ftb.mods.ftblibrary.ui.Widget;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.components.AbstractWidget;
//import net.minecraft.network.chat.Component;
//
//
//public class LabelWidget extends Widget {
//    private final IntegerColor background;
//    private final Component text;
//    public LabelWidget(Panel p, IntegerColor background, Component text) {
//        super(p);
//        this.background=background;
//        this.text=text;
//        setWidth(ClientFunctions.calculateStringWidth(text)+5);
//        setHeight(10);
//    }
//
//    @Override
//    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
//        ClientMethods.drawBackground(graphics,x,y,0,width,height, background);
//        AbstractWidget.renderScrollingString(graphics,theme.getFont(),text,getX()+5,getY()+2,getX()+getWidth()-4,getY()+getHeight(),Constants.WHITE.getIntColor());
//    }
//}
