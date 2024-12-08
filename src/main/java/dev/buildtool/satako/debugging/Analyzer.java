package dev.buildtool.satako.debugging;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Analyzer {
    private static String lastMod;

    @SubscribeEvent
    public static void trackMods(FMLConstructModEvent constructModEvent) {
        lastMod = ModLoadingContext.get().getActiveNamespace();
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                PrintWriter writer = new PrintWriter(new File("logs", "mod-log.log"));
                writer.println("Last constructed mod: " + lastMod);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}
