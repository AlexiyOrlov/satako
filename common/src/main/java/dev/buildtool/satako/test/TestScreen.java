package dev.buildtool.satako.test;

import dev.buildtool.satako.Constants;
import dev.buildtool.satako.IntegerColor;
import dev.buildtool.satako.Satako;
import dev.buildtool.satako.client.gui.*;
import dev.buildtool.satako.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class TestScreen extends MenuScreen<TestMenu> {
    private float hue;
    public TestScreen(TestMenu container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name, true);
    }

    @Override
    public void init() {
        super.init();
//        addRenderableWidget(new BetterButton(centerX, 0, Component.literal("Button")));
//        RadioButton radioButton = new RadioButton(centerX, 20, Component.literal("Radio button 1"));
//        addRenderableWidget(radioButton);
//        RadioButton radioButton2 = new RadioButton(radioButton.getX() + radioButton.getWidth(), 20, Component.literal("Radio button2"));
//        addRenderableWidget(radioButton2);
//        new ButtonGroup(radioButton, radioButton2);
//
        Button button = new Button.Builder(Component.literal("Open client screen"), p_93751_ -> {
            Minecraft.getInstance().setScreen(new TestSlotlessScreen(Component.literal("Client screen"),this));
        }).pos(0, 120).size(100, 20).build();
//        button.setTooltip(Tooltip.create(Component.literal("Veeeeeeeeeeeeeryyyyyyyyyyyyyyyy looooooooooooooooooooooooooong tooooooooltiiiiiiiip")));
        addRenderableWidget(button);

        ScrollArea scrollArea=new ScrollArea(button.getX()+button.getWidth(),10,width/2,height-40,Component.literal("Test scroll area"),this);
        for (int i = 0; i < 30; i++) {
//            scrollArea.addWidget(new Label(0,0,Component.literal("Label "+i),Constants.ORANGE),i,0);
        }
        scrollArea.addWidget(new EditBox(font,60,20,Component.empty()),30,0);
        scrollArea.alignWidgets();
//        addRenderableWidget(scrollArea);

        Rectangle water = new Rectangle(leftPos - 22, topPos, 20, imageHeight,() -> 0.5f, Services.PLATFORM.getFluidTexture(Fluids.WATER, true), null, false);
        addRenderableOnly(water);


//        addRenderableWidget(new ExtendedSlider(leftPos, topPos + imageHeight, imageWidth, 20, Component.literal(""), Component.literal(""), 1, 20, 1, true));

        Rectangle textured = new Rectangle(20, 20, 30, 30, () -> 0.5f, ResourceLocation.withDefaultNamespace("container/anvil/error"), true);
        addRenderableOnly(textured);

        ImageButton2 imageButton = new ImageButton2(23, 23, 16, 16, List.of(ResourceLocation.fromNamespaceAndPath(Satako.ID, "blue"),ResourceLocation.fromNamespaceAndPath(Satako.ID,"green")), 0, null);
        addRenderableWidget(imageButton);

        Label testLabel=new Label(this.width/2,topPos-20,Component.literal("Test"), Constants.WHITE);
        addRenderableOnly(testLabel);

        Rectangle vertical=new Rectangle(10,10,10,200,() -> 0f,null,()-> Constants.BLUE,true);
        addRenderableOnly(vertical);

        Rectangle horizontal = new Rectangle(vertical.getX()+vertical.getWidth()+10, topPos - 20, imageWidth, 18,() ->  new IntegerColor(0, 128, 255),null, () -> 0.5f, false);
        addRenderableOnly(horizontal);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float p_render_3_) {
        super.render(guiGraphics, mouseX, mouseY, p_render_3_);
        hue+=0.01f;
        if(hue>=1)
            hue=0;
    }
}
