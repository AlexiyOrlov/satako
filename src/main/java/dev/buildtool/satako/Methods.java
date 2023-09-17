package dev.buildtool.satako;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import org.joml.Matrix4f;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;


/**
 * Methods don't return a value
 */
public final class Methods {

    public static final Random RANDOMGENERATOR = new Random();

    public static void setBlocks(BlockPos from, BlockPos to, BlockState state, Level world) {
        Stream<BlockPos> poss = BlockPos.betweenClosedStream(from, to);
        poss.forEach(blockPos -> world.setBlockAndUpdate(blockPos, state));
    }

//    public static void drawSingleBlockSelection(Player player, float partialTicks, BlockState blockState, BlockPos pos)
//    {
////        double d0 = player.lastTickPosX + (player.getX() - player.lastTickPosX) * partialTicks;
////        double d1 = player.lastTickPosY + (player.getY() - player.lastTickPosY) * partialTicks;
////        double d2 = player.lastTickPosZ + (player.getZ() - player.lastTickPosZ) * partialTicks;
//        GlStateManager._enableBlend();
//        GlStateManager._blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
//        GlStateManager._lineWidth(2.0F);
//        GlStateManager._disableTexture();
//        GlStateManager._depthMask(false);
////     TODO   RenderGlobal.drawSelectionBoundingBox(blockState.getRaytraceShape(player.world, pos).grow(0.001, 0.001, 0.001).offset(-d0, -d1, -d2), 1f, 0.582156864f, 0.294118f, 1F);
//        GlStateManager._depthMask(true);
//        GlStateManager._enableTexture();
//        GlStateManager._disableBlend();
//    }

//    public static void drawBlockSelection(Player player, float partialTicks, BlockPos start, BlockPos end)
//    {
////        double d0 = player.lastTickPosX + (player.getX() - player.lastTickPosX) * partialTicks;
////        double d1 = player.lastTickPosY + (player.getY() - player.lastTickPosY) * partialTicks;
////        double d2 = player.lastTickPosZ + (player.getZ() - player.lastTickPosZ) * partialTicks;
//        AABB axisAlignedBB = new AABB(start, end);
//        for (double X = axisAlignedBB.minX; X <= axisAlignedBB.maxX; X++)
//        {
//            for (double Y = axisAlignedBB.minY; Y <= axisAlignedBB.maxY; Y++)
//            {
//                for (double Z = axisAlignedBB.minZ; Z <= axisAlignedBB.maxZ; Z++)
//                {
//                    BlockPos nextpos = new BlockPos(X, Y, Z);
//                    BlockState blockState = player.level.getBlockState(nextpos);
//
//                    GlStateManager._enableBlend();
//                    GlStateManager._blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
//                    GlStateManager._lineWidth(2.0F);
//                    GlStateManager._disableTexture();
//                    GlStateManager._depthMask(false);
////                  TODO  RenderGlobal.drawSelectionBoundingBox(blockState.getSelectedBoundingBox(player.world, nextpos).grow(0.001, 0.001, 0.001).offset(-d0, -d1, -d2), 1f, 0.582156864f, 0.294118f, 1F);
//                    GlStateManager._depthMask(true);
//                    GlStateManager._enableTexture();
//                    GlStateManager._disableBlend();
//
//                }
//            }
//        }
//    }


    public static void removeTileEntitySilently(BlockPos pos, Level world) {
        BlockEntity tileentity = world.getBlockEntity(pos);
        try {
            Field processingLoadedTiles;
            if (Functions.isObfuscatedEnvironment()) {
                processingLoadedTiles = Functions.getSecureField(Level.class, Options.processingLoadedTiles);
            } else {
                processingLoadedTiles = Functions.getSecureField(world.getClass(), "updatingBlockEntities");
            }
            Field addedTileEntityList;
            if (Functions.isObfuscatedEnvironment()) {
                addedTileEntityList = Functions.getSecureField(Level.class, Options.addedTileEntityList);
            } else {
                addedTileEntityList = Functions.getSecureField(world.getClass(), "pendingBlockEntities");
            }
            List<BlockEntity> atl = (List<BlockEntity>) addedTileEntityList.get(world);
            if (tileentity != null && processingLoadedTiles.getBoolean(world)) {
                tileentity.setRemoved();
                atl.remove(tileentity);
                world.removeBlockEntity(pos);
            } else {
                if (tileentity != null) {
                    atl.remove(tileentity);
                    world.removeBlockEntity(pos);
//                    world.tickableBlockEntities.remove(tileentity);
                }

                ChunkAccess chunk = world.getChunk(pos);
                chunk.removeBlockEntity(pos);
            }
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
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

    /**
     * Notifies clients of a block update
     */
    public static void sendBlockUpdate(ServerLevel worldServer, BlockPos blockPos) {
        BlockState blockState = worldServer.getBlockState(blockPos);
        worldServer.sendBlockUpdated(blockPos, blockState, blockState, 2);
    }

    @OnlyIn(Dist.CLIENT)
    public static void openClientGui(Level world, Screen screen) {
        if (world.isClientSide)
            Minecraft.getInstance().setScreen(screen);
    }

    public static void playSound(Level world, BlockPos blockPos, SoundEvent sound, float volume, float pitch) {
        world.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), sound, null, volume, pitch, false);
    }
    //FIXME
//    public static void drawVerticalLine(int x, int startY, int endY, IntegerColor color, int thickness)
//    {
//        int red = color.getRed();
//        int green = color.getGreen();
//        int blue = color.getBlue();
//        int alpha = color.getAlpha();
//        Tesselator tessellator = Tesselator.getInstance();
//        BufferBuilder bufferBuilder = tessellator.getBuilder();
//        bufferBuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
//        GL11.glLineWidth(thickness);
//        bufferBuilder.vertex(x, startY, 0).color(red, green, blue, alpha).endVertex();
//        bufferBuilder.vertex(x, endY, 0).color(red, green, blue, alpha).endVertex();
//        tessellator.end();
//    }
//    FIXME
//    public static void drawHorizontalLine(int startX, int endX, int y, IntegerColor color, int thickness)
//    {
//        int red = color.getRed();
//        int green = color.getGreen();
//        int blue = color.getBlue();
//        int alpha = color.getAlpha();
//        Tesselator tessellator = Tesselator.getInstance();
//        BufferBuilder bufferBuilder = tessellator.getBuilder();
//        bufferBuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
//        GL11.glLineWidth(thickness);
//        bufferBuilder.vertex(startX, y, 0).color(red, green, blue, alpha).endVertex();
//        bufferBuilder.vertex(endX, y, 0).color(red, green, blue, alpha).endVertex();
//        tessellator.end();
//    }

    public static void sendMessageToPlayer(Player player, String message) {
        player.displayClientMessage(Component.literal(message), false);
    }

    /**
     * @param X position to be centered on
     */
    public static void drawCenteredString(GuiGraphics matrixStack, net.minecraft.network.chat.Component o, int X, int Y, IntegerColor color) {
        drawString(matrixStack, o, X - Functions.calculateStringWidth(o) / 2, Y, color);
    }

    /**
     * @param X position to be centered on
     */
    public static void drawCenteredStringWithShadow(GuiGraphics matrixStack, Component o, int X, int Y, IntegerColor color) {
        drawStringWithShadow(matrixStack, o, X - Functions.calculateStringWidth(o) / 2, Y, color);
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

    /**
     * Shows array contents
     */
    public static void show(Object[] objects) {
        System.out.println(Arrays.toString(objects));
    }

    public static void addPotionEffectNoParticles(LivingEntity entityLivingBase, MobEffect potion, int duration, int strength) {
        entityLivingBase.addEffect(new MobEffectInstance(potion, duration, strength, false, false));
    }


    /**
     * @param inputHandler  from
     * @param outputHandler to
     * @param byAmount      how many per operation
     */
    @Promote
    private static void transferItems(IItemHandler inputHandler, IItemHandler outputHandler, int byAmount) {
        both:
        for (int i = 0; i < inputHandler.getSlots(); i++) {
            ItemStack itemStack = inputHandler.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                for (int i1 = 0; i1 < outputHandler.getSlots(); i1++) {
                    if (outputHandler.isItemValid(i1, itemStack)) {
                        ItemStack stack2 = outputHandler.getStackInSlot(i1);
                        int min = Math.min(byAmount, itemStack.getCount());
                        if (Functions.areItemTypesEqual(itemStack, stack2) && stack2.getCount() + min <= itemStack.getMaxStackSize()) {
                            itemStack.shrink(min);
                            stack2.grow(min);
                            break both;
                        }
                    }
                }
                for (int i1 = 0; i1 < outputHandler.getSlots(); i1++) {
                    if (outputHandler.isItemValid(i1, itemStack) && outputHandler.getStackInSlot(i1).isEmpty()) {
                        int min = Math.min(byAmount, itemStack.getCount());
                        outputHandler.insertItem(i1, new ItemStack(itemStack.getItem(), min), false);
                        itemStack.shrink(min);
                        break both;
                    }
                }
            }
        }
    }

    /**
     * Adds rectangular faces of specified dimensions and color to the vertex consumer
     */
    public static void addRectangularFaces(VertexConsumer target, Matrix4f matrix4f, float width, float height, float depth, float red, float green, float blue, float alpha, boolean addBackFaces) {
        //Up
        target.vertex(matrix4f, 0, 1 + height, 0).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 0, 1 + height, 1 + depth).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 1 + width, 1 + height, 1 + depth).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 1 + width, 1 + height, 0).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            target.vertex(matrix4f, 1 + width, 1 + height, 0).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 1 + width, 1 + height, 1 + depth).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 0, 1 + height, 1 + depth).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 0, 1 + height, 0).color(red, green, blue, alpha).endVertex();
        }

        //Down
        target.vertex(matrix4f, 1 + width, 0, 0).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 1 + width, 0, 1 + depth).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 0, 0, 1 + depth).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 0, 0, 0).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            target.vertex(matrix4f, 0, 0, 0).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 0, 0, 1 + depth).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 1 + width, 0, 1 + depth).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 1 + width, 0, 0).color(red, green, blue, alpha).endVertex();
        }

        //North
        target.vertex(matrix4f, 0, 0, 0).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 0, 1 + height, 0).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 1 + width, 1 + height, 0).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 1 + width, 0, 0).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            target.vertex(matrix4f, 1 + width, 0, 0).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 1 + width, 1 + height, 0).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 0, 1 + height, 0).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 0, 0, 0).color(red, green, blue, alpha).endVertex();
        }

        //South
        target.vertex(matrix4f, 1 + width, 0, 1 + depth).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 1 + width, 1 + height, 1 + depth).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 0, 1 + height, 1 + depth).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 0, 0, 1 + depth).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            target.vertex(matrix4f, 0, 0, 1 + depth).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 0, 1 + height, 1 + depth).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 1 + width, 1 + height, 1 + depth).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 1 + width, 0, 1 + depth).color(red, green, blue, alpha).endVertex();
        }

        //West
        target.vertex(matrix4f, 0, 0, 0).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 0, 0, 1 + depth).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 0, 1 + height, 1 + depth).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 0, 1 + height, 0).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            target.vertex(matrix4f, 0, 1 + height, 0).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 0, 1 + height, 1 + depth).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 0, 0, 1 + depth).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 0, 0, 0).color(red, green, blue, alpha).endVertex();
        }

        //East
        target.vertex(matrix4f, 1 + width, 1 + height, 0).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 1 + width, 1 + height, 1 + depth).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 1 + width, 0, 1 + depth).color(red, green, blue, alpha).endVertex();
        target.vertex(matrix4f, 1 + width, 0, 0).color(red, green, blue, alpha).endVertex();
        if (addBackFaces) {
            target.vertex(matrix4f, 1 + width, 0, 0).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 1 + width, 0, 1 + depth).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 1 + width, 1 + height, 1 + depth).color(red, green, blue, alpha).endVertex();
            target.vertex(matrix4f, 1 + width, 1 + height, 0).color(red, green, blue, alpha).endVertex();
        }
    }

    public static VertexConsumer createTransclucentStateBuffer(MultiBufferSource bufferSource) {
        return bufferSource.getBuffer(RenderType.create("opaque", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false, translucentCompositeState));
    }
}
