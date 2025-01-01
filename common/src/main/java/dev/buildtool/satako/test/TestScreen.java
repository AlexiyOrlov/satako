package dev.buildtool.satako.test;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.Satako;
import dev.buildtool.satako.client.gui.*;
import dev.buildtool.satako.client.gui.Label;
import dev.buildtool.satako.client.gui.Rectangle;
import dev.buildtool.satako.client.gui.TextField;
import dev.buildtool.satako.platform.Services;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;

public class TestScreen extends MenuScreen<TestMenu> {
    private float hue;
    public TestScreen(TestMenu container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name, true);
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new BetterButton(centerX, 0, Component.literal("Button")));
        RadioButton radioButton = new RadioButton(centerX, 20, Component.literal("Radio button 1"));
        addRenderableWidget(radioButton);
        RadioButton radioButton2 = new RadioButton(radioButton.getX() + radioButton.getWidth(), 20, Component.literal("Radio button2"));
        addRenderableWidget(radioButton2);
        new ButtonGroup(radioButton, radioButton2);

        Button button = new Button.Builder(Component.literal("Open client screen"), p_93751_ -> {
//            Minecraft.getInstance().setScreen(new ConfigScreen2(Component.literal("Client screen"),this));
        }).pos(0, 120).size(100, 20).build();
        button.setTooltip(Tooltip.create(Component.literal("Veeeeeeeeeeeeeryyyyyyyyyyyyyyyy looooooooooooooooooooooooooong tooooooooltiiiiiiiip")));
        addRenderableWidget(button);
        TextField textField = new TextField(button.getX() + button.getWidth(), button.getY(), 150);
        addRenderableWidget(textField);
        SwitchButton switchButton = new SwitchButton(0, 200, Component.literal("true"), Component.literal("false"), true, p_93751_ -> {
        });
        addRenderableWidget(switchButton);
        addRenderableWidget(new Label(leftPos + imageWidth, topPos, Component.literal("Clickable"), this, p_93751_ -> addPopup(Component.literal("Clicked first label")), null));
        addRenderableWidget(new Label(leftPos + imageWidth, topPos + 20, Component.literal("Clickable with background"), 60, p_93751_ -> addPopup(Component.literal("Clicked second label")), Constants.ORANGE));
        LinkedHashMap<Component, Button.OnPress> linkedHashMap = new LinkedHashMap<>();
        DropDownButton dropDownButton = new DropDownButton(leftPos + imageWidth, topPos + 40, this);
        linkedHashMap.put(Component.literal("First choice"), p_93751_ -> {
            addPopup(Component.literal("Clicked 1st choice"));
            addPopup(Component.literal("Notification"));
        });
        linkedHashMap.put(Component.literal("Second choice"), p_93751_ -> addPopup(Component.literal("Clicked 2nd choice")));
        linkedHashMap.put(Component.literal("Third choice"), p_93751_ -> addPopup(Component.literal("Clicked 3d choice")));
        dropDownButton.setChoices(linkedHashMap, 1);
        addRenderableWidget(dropDownButton);

        Label label = new Label(leftPos + imageWidth, topPos + 20 * 3, Component.literal("Overlapping label"), this, p_93751_ -> addPopup(Component.literal("Clicked the label")), Constants.ORANGE);
        addRenderableWidget(label);
        addRenderableWidget(new Button.Builder(Component.literal("Button"), p_93751_ -> addPopup(Component.literal("Clicked the button"))).pos(label.getX() + label.getWidth(), label.getY()).size(40, 20).build());
        addRenderableOnly(new Button.Builder(Component.literal("Below label"), button1 -> {
        }).pos(label.getX(), label.getY() + label.getHeight()).build());
//        addRenderableWidget(new SelectionButton(10, height - 20, Component.literal("Selection button")));

        Rectangle rectangle = new Rectangle(20, topPos, 50, imageHeight,() -> new IntegerColor(Color.getHSBColor(hue,1,1).getRGB()), () -> 0.5f,true);
        addRenderableOnly(rectangle);
        addTooltip(rectangle, () -> Component.literal("Tooltip"));
        Rectangle water = Rectangle.withColoredSprite(leftPos - 22, topPos, 20, imageHeight, Constants.BLUE, Services.PLATFORM.getFluidTexture(Fluids.WATER, true), () -> 0.66f);
        addRenderableOnly(water);

        Rectangle horizontal = new Rectangle(leftPos, topPos - 20, imageWidth, 18, new IntegerColor(0, 128, 255), Services.PLATFORM.getFluidTexture(Fluids.WATER, false), () -> 0.33f, false);
        addRenderableOnly(horizontal);
        addRenderableWidget(new ExtendedSlider(leftPos, topPos + imageHeight, imageWidth, 20, Component.literal(""), Component.literal(""), 1, 20, 1, true));
        Rectangle horizontalColored = Rectangle.horizontal(leftPos, topPos - 40, imageWidth, 18, Constants.GRAY, null, () -> 0.6f);
        addRenderableOnly(horizontalColored);
        Rectangle textured = new Rectangle(20, 20, 30, 30, () -> 1f, ResourceLocation.withDefaultNamespace("container/anvil/error"), true);
        addRenderableOnly(textured);

        ImageButton2 imageButton = new ImageButton2(3, 3, 16, 16, List.of(ResourceLocation.fromNamespaceAndPath(Satako.ID, "grey_slot")), 0, null);
        addRenderableWidget(imageButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float p_render_3_) {
        super.render(guiGraphics, mouseX, mouseY, p_render_3_);
        hue+=0.01f;
        if(hue>=1)
            hue=0;
    }
}
