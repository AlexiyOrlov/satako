package dev.buildtool.satako.platform;

import dev.buildtool.satako.ItemHandlerSlot;
import dev.buildtool.satako.NeoforgeFunctions;
import dev.buildtool.satako.SatakoNeoforge;
import dev.buildtool.satako.platform.services.IPlatformHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.items.IItemHandler;

public class NeoForgePlatformHooks implements IPlatformHooks {

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public void dropItemsIfAny(Level level, BlockPos pos) {
        IItemHandler itemHandler= level.getCapability(Capabilities.ItemHandler.BLOCK,pos,null);
        if(itemHandler!=null) {
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (!stack.isEmpty())
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);
            }
        }
    }

    @Override
    public TextureAtlasSprite getFluidTexture(Fluid fluid, boolean still) {
        IClientFluidTypeExtensions properties = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation spriteLocation;
        if (still) {
            spriteLocation = properties.getStillTexture(NeoforgeFunctions.getCachedFluidStack(fluid));
        } else {
            spriteLocation = properties.getFlowingTexture(NeoforgeFunctions.getCachedFluidStack(fluid));
        }
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(spriteLocation);
    }

    @Override
    public boolean isClient() {
        return FMLLoader.getDist()== Dist.CLIENT;
    }

    @Override
    public boolean isServer() {
        return FMLLoader.getDist()==Dist.DEDICATED_SERVER;
    }
    @Override
    public void drawSlot(Slot slot, int mouseX, int mouseY, GuiGraphics guiGraphics, AbstractContainerScreen<?> screen) {
        if (slot instanceof ItemHandlerSlot handlerSlot && slot.getItem().isEmpty() && mouseX > slot.x + screen.getGuiLeft() && mouseX < slot.x + screen.getGuiLeft() + 18 && mouseY > slot.y + screen.getGuiTop() && mouseY < slot.y + screen.getGuiTop() + 18 && handlerSlot.getTooltip() != null) {
            guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, handlerSlot.getTooltip(), mouseX, mouseY);
        }
    }

    @Override
    public void drawSlotBackground(Slot slot, GuiGraphics guiGraphics, AbstractContainerScreen<?> screen) {
        int sx = slot.x;
        int sy = slot.y;
        if (slot instanceof ItemHandlerSlot itemHandlerSlot) {
            if (itemHandlerSlot.getTexture() == null) {
                if (itemHandlerSlot.getColor() != null)
                    guiGraphics.fill(sx + screen.getGuiLeft(), sy + screen.getGuiTop(), sx + screen.getGuiLeft() + 16, sy + screen.getGuiTop() + 16, itemHandlerSlot.getColor().getIntColor());
            } else {
                guiGraphics.blitSprite(itemHandlerSlot.getTexture(), sx + screen.getGuiLeft(), sy + screen.getGuiTop(), sx + screen.getGuiLeft() + 16, sy + screen.getGuiTop() + 16);
            }
        } else {
            guiGraphics.fill(sx + screen.getGuiLeft(), sy + screen.getGuiTop(), sx + screen.getGuiLeft() + 16, sy + screen.getGuiTop() + 16, 0xff666666);
        }
    }

    @Override
    public MenuType<?> getTestMenu() {
        return SatakoNeoforge.TEST_MENU.get();
    }
}