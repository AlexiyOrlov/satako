package dev.buildtool.satako;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class SatakoFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Satako.run();
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> Satako.registerCommands(commandDispatcher));
    }
}
