package dev.buildtool.satako;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class Events {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event)
    {
        Satako.registerCommands(event.getDispatcher());
    }
}
