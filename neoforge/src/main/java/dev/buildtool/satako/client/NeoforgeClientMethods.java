package dev.buildtool.satako.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
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

import javax.annotation.Nullable;
import java.util.List;

public class NeoforgeClientMethods {

    public static void renderBlock(BlockState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, net.neoforged.neoforge.client.model.data.ModelData modelData, net.minecraft.client.renderer.RenderType renderType, float alpha) {
        RenderShape rendershape = state.getRenderShape();
        BlockRenderDispatcher blockRenderDispatcher= Minecraft.getInstance().getBlockRenderer();
        if (rendershape != RenderShape.INVISIBLE) {
            switch (rendershape) {
                case MODEL:
                    BakedModel bakedmodel = blockRenderDispatcher.getBlockModel(state);
                    int i = blockRenderDispatcher.blockColors.getColor(state, null, null, 0);
                    float red = (float) (i >> 16 & 0xFF) / 255.0F;
                    float green = (float) (i >> 8 & 0xFF) / 255.0F;
                    float blue = (float) (i & 0xFF) / 255.0F;
                    for (net.minecraft.client.renderer.RenderType rt : bakedmodel.getRenderTypes(state, RandomSource.create(42), modelData))
                        renderModel(poseStack.last(),
                                bufferSource.getBuffer(renderType != null ? renderType : net.neoforged.neoforge.client.RenderTypeHelper.getEntityRenderType(rt, false)),
                                state, bakedmodel, red, green, blue, alpha, packedLight, packedOverlay,
                                modelData, rt
                        );
                    break;
                case ENTITYBLOCK_ANIMATED:
                    ItemStack stack = new ItemStack(state.getBlock());
                    net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.of(stack).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, poseStack, bufferSource, packedLight, packedOverlay);
            }
        }
    }

    private static void renderModel(PoseStack.Pose pose, VertexConsumer consumer, @Nullable BlockState state,
            BakedModel model, float red, float green, float blue, float alpha, int packedLight,
            int packedOverlay, net.neoforged.neoforge.client.model.data.ModelData modelData,
            net.minecraft.client.renderer.RenderType renderType
    ) {
        RandomSource randomsource = RandomSource.create();
        long i = 42L;

        for (Direction direction : Direction.values()) {
            randomsource.setSeed(i);
            renderQuadList(pose, consumer, red, green, blue, alpha, model.getQuads(state, direction, randomsource, modelData, renderType), packedLight, packedOverlay);
        }

        randomsource.setSeed(i);
        renderQuadList(pose, consumer, red, green, blue, alpha, model.getQuads(state, null, randomsource, modelData, renderType), packedLight, packedOverlay);
    }

    private static void renderQuadList(
            PoseStack.Pose pose, VertexConsumer consumer, float red, float green, float blue, float alpha,
            List<BakedQuad> quads, int packedLight, int packedOverlay
    ) {
        for (BakedQuad bakedquad : quads) {
            float f;
            float f1;
            float f2;
            if (bakedquad.isTinted()) {
                f = Mth.clamp(red, 0.0F, 1.0F);
                f1 = Mth.clamp(green, 0.0F, 1.0F);
                f2 = Mth.clamp(blue, 0.0F, 1.0F);
            } else {
                f = 1.0F;
                f1 = 1.0F;
                f2 = 1.0F;
            }

            consumer.putBulkData(pose, bakedquad, f, f1, f2, alpha, packedLight, packedOverlay);
        }
    }
}
