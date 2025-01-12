//package dev.buildtool.satako.client;
//
//import com.mojang.blaze3d.systems.RenderSystem;
//import dev.ftb.mods.ftblibrary.ui.IFocusableWidget;
//import dev.ftb.mods.ftblibrary.ui.Panel;
//import dev.ftb.mods.ftblibrary.ui.Theme;
//import dev.ftb.mods.ftblibrary.ui.Widget;
//import dev.ftb.mods.ftblibrary.ui.input.Key;
//import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.Font;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.components.AbstractWidget;
//import net.minecraft.client.gui.navigation.CommonInputs;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.Mth;
//
//import java.text.DecimalFormat;
//
////FIXME is not focusable in scrolling panel
//public abstract class SliderWidget extends Widget implements IFocusableWidget {
//    protected static final ResourceLocation SLIDER_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider");
//    protected static final ResourceLocation HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider_highlighted");
//    protected static final ResourceLocation SLIDER_HANDLE_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider_handle");
//    protected static final ResourceLocation SLIDER_HANDLE_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider_handle_highlighted");
//    private final double minValue,maxValue;
//    private final double stepSize;
//    protected double value;
//    private boolean canChangeValue;
//    protected Component message;
//    protected boolean isHovered,isFocused;
//    public boolean active=true;
//    protected Component prefix;
//    protected Component suffix;
//    private final DecimalFormat format;
//
//    public SliderWidget(Panel p, int x, int y, int width, int height, Component message, Component prefix,Component suffix,double minValue,double maxValue, double currentValue,int precision,double stepSize) {
//        super(p);
//        setPosAndSize(x,y,width,height);
//        this.message=message;
//        this.prefix = prefix;
//        this.suffix = suffix;
//        this.minValue = minValue;
//        this.maxValue = maxValue;
//        this.stepSize = Math.abs(stepSize);
//        this.value = this.snapToNearest((currentValue - minValue) / (maxValue - minValue));
//
//        if (stepSize == 0D) {
//            precision = Math.min(precision, 4);
//
//            StringBuilder builder = new StringBuilder("0");
//
//            if (precision > 0)
//                builder.append('.');
//
//            while (precision-- > 0)
//                builder.append('0');
//
//            this.format = new DecimalFormat(builder.toString());
//        } else if (Mth.equal(this.stepSize, Math.floor(this.stepSize))) {
//            this.format = new DecimalFormat("0");
//        } else {
//            this.format = new DecimalFormat(Double.toString(this.stepSize).replaceAll("\\d", "0"));
//        }
//
//        this.updateMessage();
//    }
//
//    public SliderWidget(Panel panel,int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue) {
//        this(panel,x, y, width, height, Component.empty(),prefix, suffix, minValue, maxValue, currentValue, 0, 1);
//    }
//
//    private double snapToNearest(double value) {
//        if (stepSize <= 0D)
//            return Mth.clamp(value, 0D, 1D);
//
//        value = Mth.lerp(Mth.clamp(value, 0D, 1D), this.minValue, this.maxValue);
//
//        value = (stepSize * Math.round(value / stepSize));
//
//        if (this.minValue > this.maxValue) {
//            value = Mth.clamp(value, this.maxValue, this.minValue);
//        } else {
//            value = Mth.clamp(value, this.minValue, this.maxValue);
//        }
//
//        return Mth.map(value, this.minValue, this.maxValue, 0D, 1D);
//    }
//
//    protected ResourceLocation getSprite() {
//        return this.isFocused && !this.canChangeValue ? HIGHLIGHTED_SPRITE : SLIDER_SPRITE;
//    }
//
//    protected ResourceLocation getHandleSprite() {
//        return !this.isHovered && !this.canChangeValue ? SLIDER_HANDLE_SPRITE : SLIDER_HANDLE_HIGHLIGHTED_SPRITE;
//    }
//
//    private void setValueFromMouse(double mouseX) {
//        this.setValue((mouseX - (double)(this.getX() + 4)) / (double)(this.width - 8));
//    }
//
//    public void setValue(double value) {
//        double d0 = this.value;
//        this.value = snapToNearest(value);
//        if (d0 != this.value) {
//            this.applyValue();
//        }
//        this.updateMessage();
//    }
//
//    public long getValueLong() {
//        return Math.round(this.getValue());
//    }
//
//    public abstract void applyValue();
//
//    public int getValueInt()
//    {
//        return (int) getValueLong();
//    }
//
//    @Override
//    public boolean keyPressed(Key key) {
//        if (CommonInputs.selected(key.keyCode)) {
//            this.canChangeValue = !this.canChangeValue;
//            return true;
//        } else {
//            if (this.canChangeValue) {
//                boolean flag = key.keyCode == 263;
//                if (flag || key.keyCode == 262) {
//                    float f = flag ? -1.0F : 1.0F;
//                    this.setValue(this.value + (double)(f / (float)(this.width - 8)));
//                    return true;
//                }
//            }
//
//            return false;
//        }
//    }
//
//    @Override
//    public boolean mousePressed(MouseButton button) {
//        if(isFocused)
//            setValueFromMouse(getMouseX());
//        return super.mousePressed(button);
//    }
//
//    @Override
//    public boolean mouseDragged(int button, double dragX, double dragY) {
//        if(isFocused)
//            setValueFromMouse(getMouseX());
//        return super.mouseDragged(button, dragX, dragY);
//    }
//
//    public double getValue() {
//        return this.value * (maxValue - minValue) + minValue;
//    }
//
//    public String getValueString() {
//        return this.format.format(this.getValue());
//    }
//
//    protected  void updateMessage(){
//        this.message=Component.literal("").append(prefix).append(this.getValueString()).append(suffix);
//    }
//
//
//    @Override
//    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
//        Minecraft minecraft = Minecraft.getInstance();
//        graphics.setColor(1.0F, 1.0F, 1.0F, 1);
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.enableDepthTest();
//        graphics.blitSprite(this.getSprite(), this.getX(), this.getY(), this.getWidth(), this.getHeight());
//        graphics.blitSprite(this.getHandleSprite(), this.getX() + (int) (this.value * (double) (this.width - 8)), this.getY(), 8, this.getHeight());
//        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
//        int i = this.active ? 16777215 : 10526880;
//        this.renderScrollingString(graphics, minecraft.font, 2, i | Mth.ceil(1 * 255.0F) << 24);
//    }
//
//    public static void renderScrollingString(GuiGraphics guiGraphics, Font font, Component text, int minX, int minY, int maxX, int maxY, int color) {
//        AbstractWidget.renderScrollingString(guiGraphics, font, text, (minX + maxX) / 2, minX, minY, maxX, maxY, color);
//    }
//
//    protected void renderScrollingString(GuiGraphics guiGraphics, Font font, int width, int color) {
//        int i = this.getX() + width;
//        int j = this.getX() + this.getWidth() - width;
//        renderScrollingString(guiGraphics, font, this.message, i, this.getY(), j, this.getY() + this.getHeight(), color);
//    }
//
//    @Override
//    public boolean isFocused() {
//        return isFocused;
//    }
//
//    @Override
//    public void setFocused(boolean b) {
//        isFocused=b;
//    }
//}
