package dev.buildtool.satako;

import dev.buildtool.satako.test.TestScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(dist = Dist.CLIENT,value = Satako.ID)
public class SatakoClientNeoforge {
    public SatakoClientNeoforge(IEventBus eventBus) {
        SatakoClient.run();
        eventBus.addListener(this::registerScreen);
    }

    private void registerScreen(RegisterMenuScreensEvent event)
    {
        event.register(SatakoNeoforge.TEST_MENU.get(), TestScreen::new);
    }
}
