package dev.buildtool.satako.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Button that cycles through sprites when pressed
 */
public class ImageButton2 extends BetterButton {
    private final List<ResourceLocation> sprites;
    public int activeSprite;

    public ImageButton2(int x, int y, int width, int height, List<ResourceLocation> sprites, int initialSprite, OnPress pressable) {
        super(x, y, width, height, Component.empty(), pressable);
        this.sprites = sprites;
        activeSprite = initialSprite;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(sprites.get(activeSprite), getX(), getY(), width, height);
    }

    @Override
    public void onPress() {
        activeSprite++;
        if (activeSprite == sprites.size())
            activeSprite = 0;
        super.onPress();
    }
}
