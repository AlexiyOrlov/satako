package dev.buildtool.satako;

import dev.buildtool.satako.integrations.integration.JEI;
import dev.buildtool.satako.test.TestScreen;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;

public class SatakoClientFabric implements ClientModInitializer {

    public static void handleJeiScreen(GuiGraphics guiGraphics) {
        if(JEI.ingredientListOverlay!=null)
            JEI.ingredientListOverlay.getIngredientUnderMouse().flatMap(ITypedIngredient::getItemStack).ifPresent(itemStack -> SatakoClient.handle(itemStack, guiGraphics));
    }

    @Override
    public void onInitializeClient() {
        SatakoClient.run();
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipFlag, list) -> SatakoClient.originalTooltip=list);
        MenuScreens.register(SatakoFabric.testMenu, TestScreen::new);
    }
}
