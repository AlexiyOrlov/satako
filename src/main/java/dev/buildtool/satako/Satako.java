package dev.buildtool.satako;

import dev.buildtool.satako.debugging.Analyzer;
import dev.buildtool.satako.debugging.EventListener;
import dev.buildtool.satako.packets.SendItemNBT;
import dev.buildtool.satako.packets.SendSound;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created on 11/30/19.
 */
@Mod(Satako.ID)
public class Satako
{
    public static final String ID = "satako";
    public static SimpleChannel CHANNEL;
    public static ForgeConfigSpec.BooleanValue DO_DEBUG;
    public Satako()
    {
        CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(ID, "first"), () -> "1.0", s -> true, s -> true);
        CHANNEL.messageBuilder(SendItemNBT.class, 0).encoder((sendItemNBT, packetBuffer) -> {
            packetBuffer.writeNbt(sendItemNBT.compoundNBT);
            packetBuffer.writeEnum(sendItemNBT.toHand);
        }).decoder(packetBuffer -> new SendItemNBT(packetBuffer.readNbt(), packetBuffer.readEnum(Hand.class)))
                .consumer((sendItemNBT, contextSupplier) -> {
                    NetworkEvent.Context context = contextSupplier.get();
                    if(context.getDirection()==NetworkDirection.PLAY_TO_SERVER)
                        context.enqueueWork(() -> context.getSender().getItemInHand(sendItemNBT.toHand).setTag(sendItemNBT.compoundNBT));
                    else{
                        context.enqueueWork(() -> {
                            Minecraft minecraft=Minecraft.getInstance();
                            final ItemStack heldItem = minecraft.player.getItemInHand(sendItemNBT.toHand);
                            heldItem.setTag(sendItemNBT.compoundNBT);
                        });
                    }
                    context.setPacketHandled(true);
                }).add();

        CHANNEL.messageBuilder(SendSound.class, 1, NetworkDirection.PLAY_TO_CLIENT).encoder((sendSound, buffer) -> {
            buffer.writeFloat(sendSound.pitch);
                    buffer.writeFloat(sendSound.volume);
                    buffer.writeUtf(sendSound.soundEvent.getRegistryName().toString());
                }).decoder(buffer -> new SendSound(buffer.readFloat(), buffer.readFloat(),
                        ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(buffer.readUtf()))))
                .consumer((sendSound, contextSupplier) -> {
                    contextSupplier.get().enqueueWork(() ->
                            Minecraft.getInstance().player.playSound(sendSound.soundEvent, sendSound.volume, sendSound.pitch));
                    contextSupplier.get().setPacketHandled(true);
                }).add();
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, new ForgeConfigSpec.Builder().configure(builder -> {
            DO_DEBUG = builder.define("Enable extra debugging info", true);
            return builder.build();
        }).getRight());
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        if (DO_DEBUG.get()) {
            MinecraftForge.EVENT_BUS.register(EventListener.class);
            eventBus.register(Analyzer.class);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    PrintWriter printWriter = new PrintWriter(new File("logs", "general.log"));
                    ModList.get().forEachModContainer((s, modContainer) -> {
                        if (!s.equals("minecraft") && !s.equals("forge")) {
                            printWriter.println("Next mod: " + s);
                            IModInfo iModInfo = modContainer.getModInfo();
                            iModInfo.getDependencies().forEach(modVersion -> {
                                printWriter.println("depends " + (modVersion.isMandatory() ? "obligatory" : "optionally") + " on " + modVersion.getModId());
                            });
                        }
                    });
                    printWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }));
        }
    }
}
