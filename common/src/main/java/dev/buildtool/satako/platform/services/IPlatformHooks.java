package dev.buildtool.satako.platform.services;

import dev.buildtool.satako.BetterSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public interface IPlatformHooks {

    /**
     * Checks if a mod with the given id is loaded.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    void dropItemsIfAny(Level level, BlockPos pos);

    default void drawSlot(Slot slot, int mouseX, int mouseY, GuiGraphics guiGraphics,AbstractContainerScreen<?>  screen)
    {
        if (slot instanceof BetterSlot betterSlot && slot.getItem().isEmpty() && mouseX > slot.x + screen.leftPos && mouseX < slot.x + screen.leftPos + 18 && mouseY > slot.y + screen.topPos && mouseY < slot.y + screen.topPos + 18 && betterSlot.getTooltip() != null) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, betterSlot.getTooltip(), mouseX, mouseY);
        }
    }

    default void drawSlotBackground(Slot slot, GuiGraphics guiGraphics, AbstractContainerScreen<?> screen) {
        int sx = slot.x;
        int sy = slot.y;
        if (slot instanceof BetterSlot betterSlot) {
            if (betterSlot.getTexture() == null) {
                if (betterSlot.getColor() != null)
                    guiGraphics.fill(sx + screen.leftPos, sy + screen.topPos, sx + screen.leftPos + 16, sy + screen.topPos + 16, betterSlot.getColor().getIntColor());
            } else {
                //TODO check
                guiGraphics.blitSprite(betterSlot.getTexture(), sx + screen.leftPos, sy + screen.topPos, sx + screen.leftPos + 16, sy + screen.topPos + 16);
            }
        } else {
            guiGraphics.fill(sx + screen.leftPos, sy + screen.topPos, sx + screen.leftPos + 16, sy + screen.topPos + 16, 0xff666666);
        }
    }

    TextureAtlasSprite getFluidTexture(Fluid fluid, boolean still);

    boolean isClient();

    boolean isServer();

    MenuType<?> getTestMenu();

    boolean isFabric();

    boolean isNeoforge();
}