package dev.buildtool.satako;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientFunctions {
    public static VertexConsumer createTransclucentStateBuffer(MultiBufferSource bufferSource) {
        return bufferSource.getBuffer(RenderType.create("opaque", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false, Constants.translucentCompositeState));
    }

    public static int calculateStringWidth(Component string) {
        if (string != null) {
            return Minecraft.getInstance().font.width(string.getString());
        }
        return 0;
    }
}
