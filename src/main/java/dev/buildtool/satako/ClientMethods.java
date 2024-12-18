package dev.buildtool.satako;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
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
}
