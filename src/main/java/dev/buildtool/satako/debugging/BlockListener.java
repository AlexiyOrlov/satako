package dev.buildtool.satako.debugging;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class BlockListener {
    private static ResourceLocation lastBlockPlacedByEntity;
    private static ResourceLocation placer;

    @SubscribeEvent
    public static void trackPlacementByEntity(BlockEvent.EntityPlaceEvent event) {
        lastBlockPlacedByEntity = event.getPlacedBlock().getBlock().getRegistryName();
        if (event.getEntity() != null)
            placer = event.getEntity().getType().getRegistryName();
    }

    private static ResourceLocation lastBlockPlacedByFluid;

    @SubscribeEvent
    public static void trackPlacementByFluid(BlockEvent.FluidPlaceBlockEvent event) {
        lastBlockPlacedByFluid = event.getOriginalState().getBlock().getRegistryName();
    }

    private static ResourceLocation lastBlockToBeBroken;

    @SubscribeEvent
    public static void trackBreak(BlockEvent.BreakEvent breakEvent) {
        lastBlockToBeBroken = breakEvent.getState().getBlock().getRegistryName();
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(new File("logs", "block-log.txt")));
                writer.println("Last block to be broken: " + lastBlockToBeBroken);
                writer.println("Last block to be placed by " + placer + ": " + lastBlockPlacedByEntity);
                writer.println("Last block to be placed by fluid: " + lastBlockPlacedByFluid);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}
