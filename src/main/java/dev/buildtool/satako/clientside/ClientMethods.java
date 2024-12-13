package dev.buildtool.satako.clientside;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.buildtool.satako.Constants;
import dev.buildtool.satako.IntegerColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.List;
import java.util.stream.Collectors;

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
        vertexConsumer.addVertex(matrix4f, 0, y + 1 + extruder, 0).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, 0, y + 1 + extruder, z + 1).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, x + 1, y + 1 + extruder, z + 1).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, x + 1, y + 1 + extruder, 0).setColor(red, green, blue, alpha);
        if (addBackFaces) {
            vertexConsumer.addVertex(matrix4f, 1 + x, y + 1 + extruder, 0).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, 1 + x, y + 1 + extruder, 1 + z).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, 0, y + 1 + extruder, 1 + z).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, 0, y + 1 + extruder, 0).setColor(red, green, blue, alpha);
        }

        //Down
        vertexConsumer.addVertex(matrix4f, 1 + x, -extruder, 0).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, 1 + x, -extruder, 1 + z).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, 0, -extruder, 1 + z).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, 0, -extruder, 0).setColor(red, green, blue, alpha);
        if (addBackFaces) {
            vertexConsumer.addVertex(matrix4f, 0, -extruder, 0).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, 0, -extruder, 1 + z).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, 1 + x, -extruder, 1 + z).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, 1 + x, -extruder, 0).setColor(red, green, blue, alpha);
        }

        //North
        vertexConsumer.addVertex(matrix4f, 0, 0, -extruder).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, 0, y + 1, -extruder).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, x + 1, y + 1, -extruder).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, x + 1, 0, -extruder).setColor(red, green, blue, alpha);
        if (addBackFaces) {
            vertexConsumer.addVertex(matrix4f, x + 1, 0, -extruder).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, x + 1, y + 1, -extruder).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, 0, y + 1, -extruder).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, 0, 0, -extruder).setColor(red, green, blue, alpha);
        }

        //South
        vertexConsumer.addVertex(matrix4f, x + 1, 0, z + 1 + extruder).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, x + 1, y + 1, z + 1 + extruder).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, 0, y + 1, z + 1 + extruder).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, 0, 0, z + 1 + extruder).setColor(red, green, blue, alpha);
        if (addBackFaces) {
            vertexConsumer.addVertex(matrix4f, 0, 0, z + 1 + extruder).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, 0, y + 1, z + 1 + extruder).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, x + 1, y + 1, z + 1 + extruder).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, x + 1, 0, z + 1 + extruder).setColor(red, green, blue, alpha);
        }

        //West
        vertexConsumer.addVertex(matrix4f, -extruder, 0, 0).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, -extruder, 0, z + 1).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, -extruder, y + 1, z + 1).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, -extruder, y + 1, 0).setColor(red, green, blue, alpha);
        if (addBackFaces) {
            vertexConsumer.addVertex(matrix4f, -extruder, y + 1, 0).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, -extruder, y + 1, z + 1).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, -extruder, 0, z + 1).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, -extruder, 0, 0).setColor(red, green, blue, alpha);
        }

        //East
        vertexConsumer.addVertex(matrix4f, x + 1 + extruder, y + 1, 0).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, x + 1 + extruder, y + 1, z + 1).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, x + 1 + extruder, 0, z + 1).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(matrix4f, x + 1 + extruder, 0, 0).setColor(red, green, blue, alpha);
        if (addBackFaces) {
            vertexConsumer.addVertex(matrix4f, x + 1 + extruder, 0, 0).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, x + 1 + extruder, 0, z + 1).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, x + 1 + extruder, y + 1, z + 1).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix4f, x + 1 + extruder, y + 1, 0).setColor(red, green, blue, alpha);
        }
    }

    public static void drawCircle(GuiGraphics graphics) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(3);
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.LINES,DefaultVertexFormat.POSITION_COLOR);
        int num_segments = 16;
        float radius = 200f;
        for (int ii = 0; ii < num_segments; ii++) {
            float theta = 2.0f * 3.1415926f * ii / num_segments;//get the current angle

            float xx = radius * Mth.cos(theta);
            float yy = radius * Mth.sin(theta);
            IntegerColor white=Constants.WHITE;
            bufferbuilder.addVertex(graphics.pose().last().pose(), xx, yy, 0).setColor(white.getRed(), white.getGreen(), white.getBlue(), white.getAlpha());
        }
        BufferUploader.drawWithShader(bufferbuilder.build());
    }

    public static void drawFilledCircle(Tesselator tessellator, float radius, IntegerColor color) {
        int circle_points = 50;
        float angle = 2.0f * 3.1416f / circle_points;
        //TODO check format
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_COLOR);

        float angle1 = 0;
        float xx = radius * Mth.cos(0);
        float yy = radius * Mth.sin(0);
        bufferBuilder.addVertex(xx, yy, 0).setColor(color.getRed(), color.getGreen(), color.getBlue(), 255);
        int i;
        for (i = 0; i < circle_points; i++) {
            bufferBuilder.addVertex(radius * Mth.cos(angle1), radius * Mth.sin(angle1), 0).setColor(0, 0, 0, 255);
            angle1 += angle;
        }
        BufferUploader.draw(bufferBuilder.build());
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

    public static void drawTooltipLine(GuiGraphics guiGraphics, Component component, int popupX, int popupY, IntegerColor stringColor, IntegerColor backgroundColor, IntegerColor borderColor) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0,0,399);
        Font font = Minecraft.getInstance().font;
        int stringWidth = font.width(component);
        TooltipRenderUtil.renderTooltipBackground(guiGraphics,popupX - stringWidth / 2, popupY-4, stringWidth, 14, 0,backgroundColor.getIntColor(),backgroundColor.getIntColor(),borderColor.getIntColor(), borderColor.getIntColor());
        guiGraphics.drawCenteredString(font, component, popupX, popupY-1, stringColor.getIntColor());
        guiGraphics.pose().popPose();
    }
    public static void drawTooltipLine(GuiGraphics guiGraphics,Component component,int x,int y)
    {
        drawTooltipLine(guiGraphics,component,x,y, Constants.WHITE,Constants.GRAY, Constants.YELLOW);
    }

    public static void drawTooltipLines(GuiGraphics guiGraphics, List<MutableComponent> components, int x, int y)
    {
        List<ClientTooltipComponent> clientTooltipComponents= components.stream().map(component -> new ClientTextTooltip(component.getVisualOrderText())).collect(Collectors.toList());
        int tooltipWidth=0;
        int tooltipHeight=0;
        Font font=Minecraft.getInstance().font;
        for (ClientTooltipComponent clientTooltipComponent : clientTooltipComponents) {
            int componentWidth = clientTooltipComponent.getWidth(font);
            if (componentWidth > tooltipWidth)
                tooltipWidth = componentWidth;
            tooltipHeight+=clientTooltipComponent.getHeight();
        }
        guiGraphics.pose().translate(0,0,401);
        TooltipRenderUtil.renderTooltipBackground(guiGraphics,x,y,tooltipWidth,tooltipHeight,0);
        int stringY=y;
        for (Component component : components) {
            guiGraphics.drawString(font, component, x, stringY,Constants.WHITE.getIntColor());
            stringY+=10;
        }
    }
}
