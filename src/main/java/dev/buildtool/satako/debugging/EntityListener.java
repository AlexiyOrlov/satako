package dev.buildtool.satako.debugging;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class EntityListener {
    private static ResourceLocation lastConstructedEntity;

    @SubscribeEvent
    public static void trackEntityConstruction(EntityEvent.EntityConstructing event) {
        Entity entity = event.getEntity();
        lastConstructedEntity = entity.getType().getRegistryName();
    }

    private static ResourceLocation lastEntityAddedToChunk;

    @SubscribeEvent
    public static void trackEntityEnteringChunk(EntityEvent.EnteringChunk event) {
        Entity entity = event.getEntity();
        lastEntityAddedToChunk = entity.getType().getRegistryName();
    }

    private static ResourceLocation lastEntityAddedToWorld;

    @SubscribeEvent
    public static void trackEntityJoin(EntityJoinWorldEvent event) {
        lastEntityAddedToWorld = event.getEntity().getType().getRegistryName();
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(new File("logs", "entity-log.txt")));
                writer.println("Last entity being constructed: " + lastConstructedEntity);
                writer.println("Last entity added to world: " + lastEntityAddedToWorld);
                writer.println("Last entity added to chunk: " + lastEntityAddedToChunk);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}
