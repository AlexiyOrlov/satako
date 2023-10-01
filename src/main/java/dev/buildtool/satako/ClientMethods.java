package dev.buildtool.satako;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ClientMethods {
    public static void drawSingleBlockSelection(PlayerEntity player, float partialTicks, BlockState blockState, BlockPos pos) {
//        double d0 = player.lastTickPosX + (player.getX() - player.lastTickPosX) * partialTicks;
//        double d1 = player.lastTickPosY + (player.getY() - player.lastTickPosY) * partialTicks;
//        double d2 = player.lastTickPosZ + (player.getZ() - player.lastTickPosZ) * partialTicks;
        GlStateManager._enableBlend();
        GlStateManager._blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
        GlStateManager._lineWidth(2.0F);
        GlStateManager._disableTexture();
        GlStateManager._depthMask(false);
//     TODO   RenderGlobal.drawSelectionBoundingBox(blockState.getRaytraceShape(player.world, pos).grow(0.001, 0.001, 0.001).offset(-d0, -d1, -d2), 1f, 0.582156864f, 0.294118f, 1F);
        GlStateManager._depthMask(true);
        GlStateManager._enableTexture();
        GlStateManager._disableBlend();
    }

    public static void drawBlockSelection(PlayerEntity player, float partialTicks, BlockPos start, BlockPos end) {
//        double d0 = player.lastTickPosX + (player.getX() - player.lastTickPosX) * partialTicks;
//        double d1 = player.lastTickPosY + (player.getY() - player.lastTickPosY) * partialTicks;
//        double d2 = player.lastTickPosZ + (player.getZ() - player.lastTickPosZ) * partialTicks;
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(start, end);
        for (double X = axisAlignedBB.minX; X <= axisAlignedBB.maxX; X++) {
            for (double Y = axisAlignedBB.minY; Y <= axisAlignedBB.maxY; Y++) {
                for (double Z = axisAlignedBB.minZ; Z <= axisAlignedBB.maxZ; Z++) {
                    BlockPos nextpos = new BlockPos(X, Y, Z);
                    BlockState blockState = player.level.getBlockState(nextpos);

                    GlStateManager._enableBlend();
                    GlStateManager._blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
                    GlStateManager._lineWidth(2.0F);
                    GlStateManager._disableTexture();
                    GlStateManager._depthMask(false);
//                  TODO  RenderGlobal.drawSelectionBoundingBox(blockState.getSelectedBoundingBox(player.world, nextpos).grow(0.001, 0.001, 0.001).offset(-d0, -d1, -d2), 1f, 0.582156864f, 0.294118f, 1F);
                    GlStateManager._depthMask(true);
                    GlStateManager._enableTexture();
                    GlStateManager._disableBlend();

                }
            }
        }
    }

    private static void drawCircle(Tessellator tessellator) {
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        int num_segments = 16;
        float radius = 0.7f;
        for (int ii = 0; ii < num_segments; ii++) {
            float theta = 2.0f * 3.1415926f * ii / num_segments;//get the current angle

            float xx = radius * MathHelper.cos(theta);
            float yy = radius * MathHelper.sin(theta);
            bufferbuilder.vertex(xx, yy, 0).color(0, 0, 0, 255).endVertex();
        }
        tessellator.end();
    }

    private static void drawFilledCircle(Tessellator tessellator, float radius, Color color) {
        int circle_points = 50;
        float angle = 2.0f * 3.1416f / circle_points;
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_COLOR);
        double angle1 = 0.0;
        double xx = radius * Math.cos(0);
        double yy = radius * Math.sin(0);
        bufferBuilder.vertex(xx, yy, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
        int i;
        for (i = 0; i < circle_points; i++) {
            bufferBuilder.vertex(radius * Math.cos(angle1), radius * Math.sin(angle1), 0).color(0, 0, 0, 255).endVertex();
            angle1 += angle;
        }
        tessellator.end();
    }

    public static void drawStringWithBackground(MatrixStack matrixStack, Object obj, int x, int y, IntegerColor background) {
        String string = obj.toString();
        Minecraft.getInstance().font.draw(matrixStack, string, x + 2, y + 4, background.getIntColor());
    }

    @OnlyIn(Dist.CLIENT)
    public static void openClientGui(World world, Screen screen) {
        if (world.isClientSide)
            Minecraft.getInstance().setScreen(screen);
    }

    public static void drawVerticalLine(int x, int startY, int endY, IntegerColor color, int thickness) {
        float red = color.getRed();
        float green = color.getGreen();
        float blue = color.getBlue();
        float alpha = color.getAlpha();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        GL11.glLineWidth(thickness);
        bufferBuilder.vertex(x, startY, 0).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(x, endY, 0).color(red, green, blue, alpha).endVertex();
        tessellator.end();
    }

    public static void drawHorizontalLine(int startX, int endX, int y, IntegerColor color, int thickness) {
        float red = color.getRed();
        float green = color.getGreen();
        float blue = color.getBlue();
        float alpha = color.getAlpha();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        GL11.glLineWidth(thickness);
        bufferBuilder.vertex(startX, y, 0).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(endX, y, 0).color(red, green, blue, alpha).endVertex();
        tessellator.end();
    }

    /**
     * @param X position to be centered on
     */
    public static void drawCenteredString(MatrixStack matrixStack, ITextComponent o, int X, int Y, IntegerColor color) {
        drawString(matrixStack, o, X - ClientFunctions.calculateStringWidth(o) / 2, Y, color);
    }

    /**
     * @param X position to be centered on
     */
    public static void drawCenteredStringWithShadow(MatrixStack matrixStack, ITextComponent o, int X, int Y, IntegerColor color) {
        drawStringWithShadow(matrixStack, o, X - ClientFunctions.calculateStringWidth(o) / 2, Y, color);
    }

    /**
     * Draws string without shadow
     */
    public static void drawString(MatrixStack matrixStack, Object o, int X, int Y, IntegerColor color) {
        Minecraft.getInstance().font.draw(matrixStack, o.toString(), X, Y, color.getIntColor());
    }

    public static void drawStringWithShadow(MatrixStack matrixStack, Object o, int X, int Y, IntegerColor color) {
        Minecraft.getInstance().font.draw(matrixStack, o.toString(), X, Y, color.getIntColor());
    }

    /**
     * @param addBackFaces whether to add back faces for the sides
     * @param extruder     offsets faces
     */
    public static void addRectangle(IVertexBuilder vertexConsumer, Matrix4f matrix4f, int width, int height, int depth, float red, float green, float blue, float alpha, boolean addBackFaces, float extruder) {
        //Up
        vertexConsumer.vertex(matrix4f, 0, height + 1 + extruder, 0).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, 0, height + 1 + extruder, depth + 1).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, width + 1, height + 1 + extruder, depth + 1).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, width + 1, height + 1 + extruder, 0).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            vertexConsumer.vertex(matrix4f, 1 + width, height + 1 + extruder, 0).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 1 + width, height + 1 + extruder, 1 + depth).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 0, height + 1 + extruder, 1 + depth).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 0, height + 1 + extruder, 0).color(red, green, blue, alpha).endVertex();
        }

        //Down
        vertexConsumer.vertex(matrix4f, 1 + width, -extruder, 0).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, 1 + width, -extruder, 1 + depth).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, 0, -extruder, 1 + depth).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, 0, -extruder, 0).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            vertexConsumer.vertex(matrix4f, 0, -extruder, 0).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 0, -extruder, 1 + depth).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 1 + width, -extruder, 1 + depth).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 1 + width, -extruder, 0).color(red, green, blue, alpha).endVertex();
        }

        //North
        vertexConsumer.vertex(matrix4f, 0, 0, -extruder).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, 0, height + 1, -extruder).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, width + 1, height + 1, -extruder).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, width + 1, 0, -extruder).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            vertexConsumer.vertex(matrix4f, width + 1, 0, -extruder).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, width + 1, height + 1, -extruder).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 0, height + 1, -extruder).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 0, 0, -extruder).color(red, green, blue, alpha).endVertex();
        }

        //South
        vertexConsumer.vertex(matrix4f, width + 1, 0, depth + 1 + extruder).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, width + 1, height + 1, depth + 1 + extruder).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, 0, height + 1, depth + 1 + extruder).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, 0, 0, depth + 1 + extruder).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            vertexConsumer.vertex(matrix4f, 0, 0, depth + 1 + extruder).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 0, height + 1, depth + 1 + extruder).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, width + 1, height + 1, depth + 1 + extruder).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, width + 1, 0, depth + 1 + extruder).color(red, green, blue, alpha).endVertex();
        }

        //West
        vertexConsumer.vertex(matrix4f, -extruder, 0, 0).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, -extruder, 0, depth + 1).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, -extruder, height + 1, depth + 1).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, -extruder, height + 1, 0).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            vertexConsumer.vertex(matrix4f, -extruder, height + 1, 0).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, -extruder, height + 1, depth + 1).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, -extruder, 0, depth + 1).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, -extruder, 0, 0).color(red, green, blue, alpha).endVertex();
        }

        //East
        vertexConsumer.vertex(matrix4f, width + 1 + extruder, height + 1, 0).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, width + 1 + extruder, height + 1, depth + 1).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, width + 1 + extruder, 0, depth + 1).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, width + 1 + extruder, 0, 0).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            vertexConsumer.vertex(matrix4f, width + 1 + extruder, 0, 0).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, width + 1 + extruder, 0, depth + 1).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, width + 1 + extruder, height + 1, depth + 1).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, width + 1 + extruder, height + 1, 0).color(red, green, blue, alpha).endVertex();
        }
    }
}
