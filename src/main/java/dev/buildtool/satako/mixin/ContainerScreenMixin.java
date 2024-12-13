package dev.buildtool.satako.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Screen.class)
public class ContainerScreenMixin {

    @Inject(method = "renderWithTooltip",at = @At(value = "TAIL"))
    public void drawTooltip(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci)
    {
//        RenderSystem.disableDepthTest();
//        TooltipHandler.handle(TooltipHandler.targetStack);
    }
}
