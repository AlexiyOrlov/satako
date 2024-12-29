package dev.buildtool.satako.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

public class ClientFunctions {
    public static VertexConsumer createTransclucentStateBuffer(MultiBufferSource bufferSource) {
        return bufferSource.getBuffer(RenderType.create("opaque", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false, ClientConstants.translucentCompositeState));
    }

    public static int calculateStringWidth(Component string) {
        if (string != null) {
            return Minecraft.getInstance().font.width(string);
        }
        return 0;
    }

    public static int calculateStringWidth(String string) {
        return Minecraft.getInstance().font.width(string);
    }
}
