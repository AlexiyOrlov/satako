package dev.buildtool.satako.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.buildtool.satako.Satako;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ClientConstants {
    private static final RenderStateShard.TransparencyStateShard TRANSLUCENT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    static final RenderType.CompositeState translucentCompositeState = RenderType.CompositeState.builder().setTransparencyState(TRANSLUCENT_TRANSPARENCY).setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, true)).setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeLightningShader)).createCompositeState(false);

    public static final ResourceLocation ORANGE_SQUARE = ResourceLocation.fromNamespaceAndPath(Satako.ID, "orange");
    public static final ResourceLocation DARK_SQUARE = ResourceLocation.fromNamespaceAndPath(Satako.ID, "dark");
    public static final ResourceLocation BLUE_SQUARE = ResourceLocation.fromNamespaceAndPath(Satako.ID, "blue");
    public static final ResourceLocation GRAY_SQUARE = ResourceLocation.fromNamespaceAndPath(Satako.ID, "gray");
    public static final ResourceLocation GREEN_SQUARE = ResourceLocation.fromNamespaceAndPath(Satako.ID, "green");
    public static final ResourceLocation YELLOW_SQUARE=ResourceLocation.fromNamespaceAndPath(Satako.ID,"yellow");
    public static final ResourceLocation WHITE_SQUARE=ResourceLocation.fromNamespaceAndPath(Satako.ID,"white");
    public static final ResourceLocation BLACK_SQUARE=ResourceLocation.fromNamespaceAndPath(Satako.ID,"black");
}
