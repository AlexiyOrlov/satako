package dev.buildtool.satako.debugging;

import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

;

public class EventListener {
    private static ResourceLocation lastConstructedEntity;

//    @SubscribeEvent
//    public static void trackEntityConstruction(EntityEvent.EntityConstructing event) {
//        Entity entity = event.getEntity();
//        lastConstructedEntity = entity.getType().getRegistryName();
//    }

    private static ResourceLocation lastEntityAddedToChunk;

//    @SubscribeEvent
//    public static void trackEntityEnteringChunk(EntityEvent.EnteringSection event) {
//        Entity entity = event.getEntity();
//        lastEntityAddedToChunk = entity.getType().getRegistryName();
//    }

    private static ResourceLocation lastEntityAddedToWorld;

//    @SubscribeEvent
//    public static void trackEntityJoin(EntityJoinWorldEvent event) {
//        lastEntityAddedToWorld = event.getEntity().getType().getRegistryName();
//    }

    private static ResourceLocation lastBlockPlacedByEntity;
    private static ResourceLocation placer;

//    @SubscribeEvent
//    public static void trackPlacementByEntity(BlockEvent.EntityPlaceEvent event) {
//        lastBlockPlacedByEntity = event.getPlacedBlock().getBlock().getRegistryName();
//        if (event.getEntity() != null)
//            placer = event.getEntity().getType().getRegistryName();
//    }

    private static ResourceLocation lastBlockPlacedByFluid;

//    @SubscribeEvent
//    public static void trackPlacementByFluid(BlockEvent.FluidPlaceBlockEvent event) {
//        lastBlockPlacedByFluid = event.getOriginalState().getBlock().getRegistryName();
//    }

    private static ResourceLocation lastBlockToBeBroken;

//    @SubscribeEvent
//    public static void trackBreak(BlockEvent.BreakEvent breakEvent) {
//        lastBlockToBeBroken = breakEvent.getState().getBlock().getRegistryName();
//    }

    private static ResourceLocation lastItemUsedByEntity;
    private static ResourceLocation lastUser;

//    @SubscribeEvent
//    public static void trackUseByEntity(LivingEntityUseItemEvent event) {
//        lastItemUsedByEntity = event.getItem().getItem().getRegistryName();
//        lastUser = event.getEntityLiving().getType().getRegistryName();
//    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(new File("logs", "event-log.txt")));
                writer.println("Last entity being constructed: " + lastConstructedEntity);
                writer.println("Last entity added to world: " + lastEntityAddedToWorld);
                writer.println("Last entity added to chunk: " + lastEntityAddedToChunk);
                writer.println("Last block to be broken: " + lastBlockToBeBroken);
                writer.println("Last block to be placed by " + placer + ": " + lastBlockPlacedByEntity);
                writer.println("Last block to be placed by fluid: " + lastBlockPlacedByFluid);
                writer.println("Last item used by " + lastUser + ": " + lastItemUsedByEntity);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}
