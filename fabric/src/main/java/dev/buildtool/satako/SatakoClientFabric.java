package dev.buildtool.satako;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public class SatakoClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SatakoClient.run();
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipFlag, list) -> SatakoClient.originalTooltip=list);
    }
}
