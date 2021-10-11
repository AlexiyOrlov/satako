package dev.buildtool.satako.debugging;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ItemListener {
    private static ResourceLocation lastItemUsedByEntity;
    private static ResourceLocation lastUser;

    @SubscribeEvent
    public static void trackUseByEntity(LivingEntityUseItemEvent event) {
        lastItemUsedByEntity = event.getItem().getItem().getRegistryName();
        lastUser = event.getEntityLiving().getType().getRegistryName();
    }

    static {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(new File("logs", "item-log.txt")));
            writer.println("Last item used by " + lastUser + ": " + lastItemUsedByEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
