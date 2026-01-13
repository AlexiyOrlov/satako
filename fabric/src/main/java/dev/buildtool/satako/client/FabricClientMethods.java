package dev.buildtool.satako.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FabricClientMethods {

    public void renderBlock(BlockState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay,float alpha) {
        BlockRenderDispatcher blockRenderDispatcher= Minecraft.getInstance().getBlockRenderer();
        RenderShape renderShape = state.getRenderShape();
        if (renderShape != RenderShape.INVISIBLE) {
            switch (renderShape) {
                case MODEL:
                    BakedModel bakedModel = blockRenderDispatcher.getBlockModel(state);
                    int i = blockRenderDispatcher.blockColors.getColor(state, null, null, 0);
                    float red = (float)(i >> 16 & 0xFF) / 255.0F;
                    float green = (float)(i >> 8 & 0xFF) / 255.0F;
                    float blue = (float)(i & 0xFF) / 255.0F;
                    renderModel(poseStack.last(), bufferSource.getBuffer(ItemBlockRenderTypes.getRenderType(state, false)), state, bakedModel, red, green, blue,alpha, packedLight, packedOverlay);
                    break;
                case ENTITYBLOCK_ANIMATED:
                    blockRenderDispatcher.blockEntityRenderer.renderByItem(new ItemStack(state.getBlock()), ItemDisplayContext.NONE, poseStack, bufferSource, packedLight, packedOverlay);
            }
        }
    }

    private static void renderModel(PoseStack.Pose pose, VertexConsumer consumer, @Nullable BlockState state, BakedModel model, float red, float green, float blue, float alpha, int packedLight, int packedOverlay) {
        RandomSource randomSource = RandomSource.create();
        long l = 42L;

        for (Direction direction : Direction.values()) {
            randomSource.setSeed(l);
            renderQuadList(pose, consumer, red, green, blue,alpha, model.getQuads(state, direction, randomSource), packedLight, packedOverlay);
        }

        randomSource.setSeed(l);
        renderQuadList(pose, consumer, red, green, blue, alpha,model.getQuads(state, null, randomSource), packedLight, packedOverlay);
    }

    private static void renderQuadList(PoseStack.Pose pose, VertexConsumer consumer, float red, float green, float blue,float alpha, List<BakedQuad> quads, int packedLight, int packedOverlay) {
        for (BakedQuad bakedQuad : quads) {
            float r;
            float g;
            float b;
            if (bakedQuad.isTinted()) {
                r = Mth.clamp(red, 0.0F, 1.0F);
                g = Mth.clamp(green, 0.0F, 1.0F);
                b = Mth.clamp(blue, 0.0F, 1.0F);
            } else {
                r = 1.0F;
                g = 1.0F;
                b = 1.0F;
            }

            consumer.putBulkData(pose, bakedQuad, r, g, b, alpha, packedLight, packedOverlay);
        }
    }
}
