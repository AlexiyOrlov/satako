package dev.buildtool.satako;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class FabricMethods {

    public static void sendToPlayers(double aroundX, double aroundY, double aroundZ, ServerLevel serverLevel, int distance, CustomPacketPayload packetPayload)
    {
        List<ServerPlayer> serverPlayers= serverLevel.players().stream().filter(serverPlayer -> serverPlayer.distanceToSqr(aroundX,aroundY,aroundZ)<=distance*distance).toList();
        serverPlayers.forEach(serverPlayer -> ServerPlayNetworking.send(serverPlayer,packetPayload));
    }
}
