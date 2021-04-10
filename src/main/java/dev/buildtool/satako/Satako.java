package dev.buildtool.satako;

import dev.buildtool.satako.packets.SendItemNBT;
import dev.buildtool.satako.packets.SendSound;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Created on 11/30/19.
 */
@Mod(Satako.ID)
public class Satako
{
    public static final String ID = "satako";
    public static SimpleChannel CHANNEL;

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
//                    contextSupplier.get().setPacketHandled(true);
                }).add();
        MinecraftForge.EVENT_BUS.register(this);
    }
}
