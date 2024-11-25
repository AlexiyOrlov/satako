package dev.buildtool.satako.mixin;

import dev.buildtool.satako.TooltipHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractContainerScreen.class)
public class ContainerScreenMixin {

    @Inject(method = "renderTooltip",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;Lnet/minecraft/world/item/ItemStack;II)V"))
    public void drawTooltip(GuiGraphics pGuiGraphics, int pX, int pY, CallbackInfo ci)
    {
//        TooltipHandler.renderHoveringTooltip();
    }
}
