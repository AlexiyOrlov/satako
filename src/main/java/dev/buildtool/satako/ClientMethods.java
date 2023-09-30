package dev.buildtool.satako;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class ClientMethods {
    /**
     * @param x
     * @param y
     * @param z
     * @param addBackFaces whether to add back faces for the sides
     * @param extruder     offsets faces
     */
    public static void addRectangle(VertexConsumer vertexConsumer, Matrix4f matrix4f, int x, int y, int z, float red, float green, float blue, float alpha, boolean addBackFaces, float extruder) {
        //Up
        vertexConsumer.vertex(matrix4f, 0, y + 1 + extruder, 0).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, 0, y + 1 + extruder, z + 1).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, x + 1, y + 1 + extruder, z + 1).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, x + 1, y + 1 + extruder, 0).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            vertexConsumer.vertex(matrix4f, 1 + x, y + 1 + extruder, 0).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 1 + x, y + 1 + extruder, 1 + z).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 0, y + 1 + extruder, 1 + z).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 0, y + 1 + extruder, 0).color(red, green, blue, alpha).endVertex();
        }

        //Down
        vertexConsumer.vertex(matrix4f, 1 + x, -extruder, 0).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, 1 + x, -extruder, 1 + z).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, 0, -extruder, 1 + z).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, 0, -extruder, 0).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            vertexConsumer.vertex(matrix4f, 0, -extruder, 0).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 0, -extruder, 1 + z).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 1 + x, -extruder, 1 + z).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 1 + x, -extruder, 0).color(red, green, blue, alpha).endVertex();
        }

        //North
        vertexConsumer.vertex(matrix4f, 0, 0, -extruder).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, 0, y + 1, -extruder).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, x + 1, y + 1, -extruder).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, x + 1, 0, -extruder).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            vertexConsumer.vertex(matrix4f, x + 1, 0, -extruder).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, x + 1, y + 1, -extruder).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 0, y + 1, -extruder).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 0, 0, -extruder).color(red, green, blue, alpha).endVertex();
        }

        //South
        vertexConsumer.vertex(matrix4f, x + 1, 0, z + 1 + extruder).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, x + 1, y + 1, z + 1 + extruder).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, 0, y + 1, z + 1 + extruder).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, 0, 0, z + 1 + extruder).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            vertexConsumer.vertex(matrix4f, 0, 0, z + 1 + extruder).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, 0, y + 1, z + 1 + extruder).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, x + 1, y + 1, z + 1 + extruder).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, x + 1, 0, z + 1 + extruder).color(red, green, blue, alpha).endVertex();
        }

        //West
        vertexConsumer.vertex(matrix4f, -extruder, 0, 0).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, -extruder, 0, z + 1).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, -extruder, y + 1, z + 1).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, -extruder, y + 1, 0).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            vertexConsumer.vertex(matrix4f, -extruder, y + 1, 0).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, -extruder, y + 1, z + 1).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, -extruder, 0, z + 1).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, -extruder, 0, 0).color(red, green, blue, alpha).endVertex();
        }

        //East
        vertexConsumer.vertex(matrix4f, x + 1 + extruder, y + 1, 0).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, x + 1 + extruder, y + 1, z + 1).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, x + 1 + extruder, 0, z + 1).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix4f, x + 1 + extruder, 0, 0).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            vertexConsumer.vertex(matrix4f, x + 1 + extruder, 0, 0).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, x + 1 + extruder, 0, z + 1).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, x + 1 + extruder, y + 1, z + 1).color(red, green, blue, alpha).endVertex();
            vertexConsumer.vertex(matrix4f, x + 1 + extruder, y + 1, 0).color(red, green, blue, alpha).endVertex();
        }
    }

    private static void drawCircle(Tesselator tessellator) {
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
        int num_segments = 16;
        float radius = 0.7f;
        for (int ii = 0; ii < num_segments; ii++) {
            float theta = 2.0f * 3.1415926f * ii / num_segments;//get the current angle

            float xx = radius * Mth.cos(theta);
            float yy = radius * Mth.sin(theta);
            bufferbuilder.vertex(xx, yy, 0).color(0, 0, 0, 255).endVertex();
        }
        tessellator.end();
    }

    private static void drawFilledCircle(Tesselator tessellator, float radius, IntegerColor color) {
        int circle_points = 50;
        float angle = 2.0f * 3.1416f / circle_points;
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        //TODO checl format
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
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

    public static void drawStringWithBackground(GuiGraphics matrixStack, Object obj, int x, int y, IntegerColor background) {
        String string = obj.toString();
        matrixStack.drawString(Minecraft.getInstance().font, string, x + 2, y + 4, background.getIntColor());
    }

    @OnlyIn(Dist.CLIENT)
    public static void openClientGui(Level world, Screen screen) {
        if (world.isClientSide)
            Minecraft.getInstance().setScreen(screen);
    }

    /**
     * @param X position to be centered on
     */
    public static void drawCenteredString(GuiGraphics matrixStack, net.minecraft.network.chat.Component o, int X, int Y, IntegerColor color) {
        drawString(matrixStack, o, X - ClientFunctions.calculateStringWidth(o) / 2, Y, color);
    }

    /**
     * @param X position to be centered on
     */
    public static void drawCenteredStringWithShadow(GuiGraphics matrixStack, Component o, int X, int Y, IntegerColor color) {
        drawStringWithShadow(matrixStack, o, X - ClientFunctions.calculateStringWidth(o) / 2, Y, color);
    }

    /**
     * Draws string without shadow
     */
    public static void drawString(GuiGraphics matrixStack, Object o, int X, int Y, IntegerColor color) {
        matrixStack.drawString(Minecraft.getInstance().font, o.toString(), X, Y, color.getIntColor());
    }

    public static void drawStringWithShadow(GuiGraphics matrixStack, Object o, int X, int Y, IntegerColor color) {
        matrixStack.drawString(Minecraft.getInstance().font, o.toString(), X, Y, color.getIntColor());
    }
}
