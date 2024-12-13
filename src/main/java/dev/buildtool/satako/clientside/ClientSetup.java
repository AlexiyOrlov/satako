package dev.buildtool.satako.clientside;

import dev.buildtool.satako.Satako;
import dev.buildtool.satako.test.TestScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void setup(RegisterMenuScreensEvent registerMenuScreensEvent)
    {
        registerMenuScreensEvent.register(Satako.TEST_CONTAINER.get(), TestScreen::new);
    }
}
