package dev.buildtool.satako;

import dev.buildtool.satako.integrations.integration.JEI;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.sound.SoundEngineLoadEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event)
    {
        SatakoClient.originalTooltip=event.getToolTip();
    }

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event)
    {
        Screen containerScreen=event.getScreen();
        if(containerScreen instanceof AbstractContainerScreen<?> abstractContainerScreen) {
            Slot slot = abstractContainerScreen.getSlotUnderMouse();
            if (slot != null) {
                SatakoClient.targetStack = slot.getItem();
                SatakoClient.handle(SatakoClient.targetStack,event.getGuiGraphics());
            }
            if(SatakoClient.jei && JEI.ingredientListOverlay!=null)
            {
                JEI.ingredientListOverlay.getIngredientUnderMouse().flatMap(ITypedIngredient::getItemStack).ifPresent(itemStack -> SatakoClient.handle(itemStack, event.getGuiGraphics()));
            }
        }
    }

    @SubscribeEvent
    private static void getSoundManager(SoundEngineLoadEvent event) {
        SoundController.soundEngine = event.getEngine();
    }
}
