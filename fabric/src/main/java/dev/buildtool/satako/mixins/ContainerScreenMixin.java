package dev.buildtool.satako.mixins;

import dev.buildtool.satako.SatakoClient;
import dev.buildtool.satako.SatakoClientFabric;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class ContainerScreenMixin {
    
    @Inject(at = @At("TAIL"), method = "render")
    private void drawTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        AbstractContainerScreen<?> containerScreen=(AbstractContainerScreen<?>) (Object) this;
        //the warning is wrong
        Slot slot = containerScreen.hoveredSlot;
        if (slot != null) {
            SatakoClient.targetStack = slot.getItem();
            SatakoClient.handle(SatakoClient.targetStack,guiGraphics);
        }
        if(SatakoClient.jei)
        {
            SatakoClientFabric.handleJeiScreen(guiGraphics);
        }
    }

}