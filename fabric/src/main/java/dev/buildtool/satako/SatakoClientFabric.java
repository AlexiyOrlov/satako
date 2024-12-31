package dev.buildtool.satako;

import dev.buildtool.satako.test.TestScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screens.MenuScreens;

public class SatakoClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SatakoClient.run();
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipFlag, list) -> SatakoClient.originalTooltip=list);
        MenuScreens.register(SatakoFabric.testMenu, TestScreen::new);
    }
}
