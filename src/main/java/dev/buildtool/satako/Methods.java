package dev.buildtool.satako;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;


/**
 * Methods don't return a value
 */
public final class Methods
{

    public static final Random RANDOMGENERATOR = new Random();

    public static void setBlocks(BlockPos from, BlockPos to, BlockState state, World world)
    {
        Stream<BlockPos> poss = BlockPos.betweenClosedStream(from, to);
        poss.forEach(blockPos -> world.setBlockAndUpdate(blockPos, state));
    }

    public static void drawSingleBlockSelection(PlayerEntity player, float partialTicks, BlockState blockState, BlockPos pos)
    {
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

    public static void drawBlockSelection(PlayerEntity player, float partialTicks, BlockPos start, BlockPos end)
    {
//        double d0 = player.lastTickPosX + (player.getX() - player.lastTickPosX) * partialTicks;
//        double d1 = player.lastTickPosY + (player.getY() - player.lastTickPosY) * partialTicks;
//        double d2 = player.lastTickPosZ + (player.getZ() - player.lastTickPosZ) * partialTicks;
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(start, end);
        for (double X = axisAlignedBB.minX; X <= axisAlignedBB.maxX; X++)
        {
            for (double Y = axisAlignedBB.minY; Y <= axisAlignedBB.maxY; Y++)
            {
                for (double Z = axisAlignedBB.minZ; Z <= axisAlignedBB.maxZ; Z++)
                {
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


    public static void removeTileEntitySilently(BlockPos pos, World world)
    {
        TileEntity tileentity = world.getBlockEntity(pos);
        try
        {
            Field processingLoadedTiles;
            if (Functions.isObfuscatedEnvironment())
            {
                processingLoadedTiles = Functions.getSecureField(World.class, Options.processingLoadedTiles);
            }
            else
            {
                processingLoadedTiles = Functions.getSecureField(world.getClass(), "updatingBlockEntities");
            }
            Field addedTileEntityList;
            if (Functions.isObfuscatedEnvironment())
            {
                addedTileEntityList = Functions.getSecureField(World.class, Options.addedTileEntityList);
            }
            else
            {
                addedTileEntityList = Functions.getSecureField(world.getClass(), "pendingBlockEntities");
            }
            List<TileEntity> atl = (List<TileEntity>) addedTileEntityList.get(world);
            if (tileentity != null && processingLoadedTiles.getBoolean(world))
            {
                tileentity.setRemoved();
                atl.remove(tileentity);
                world.blockEntityList.remove(tileentity);
            }
            else
            {
                if (tileentity != null)
                {
                    atl.remove(tileentity);
                    world.blockEntityList.remove(tileentity);
                    world.tickableBlockEntities.remove(tileentity);
                }

                Chunk chunk = (Chunk) world.getChunk(pos);
                chunk.getBlockEntities().remove(pos);
            }
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    private static void drawCircle(Tessellator tessellator)
    {
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        int num_segments = 16;
        float radius = 0.7f;
        for (int ii = 0; ii < num_segments; ii++)
        {
            float theta = 2.0f * 3.1415926f * ii / num_segments;//get the current angle

            float xx = radius * MathHelper.cos(theta);
            float yy = radius * MathHelper.sin(theta);
            bufferbuilder.vertex(xx, yy, 0).color(0, 0, 0, 255).endVertex();
        }
        tessellator.end();
    }

    private static void drawFilledCircle(Tessellator tessellator, float radius, Color color)
    {
        int circle_points = 50;
        float angle = 2.0f * 3.1416f / circle_points;
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_COLOR);
        double angle1 = 0.0;
        double xx = radius * Math.cos(0);
        double yy = radius * Math.sin(0);
        bufferBuilder.vertex(xx, yy, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
        int i;
        for (i = 0; i < circle_points; i++)
        {
            bufferBuilder.vertex(radius * Math.cos(angle1), radius * Math.sin(angle1), 0).color(0, 0, 0, 255).endVertex();
            angle1 += angle;
        }
        tessellator.end();
    }

    public static void setTileEntitySilently(World world, BlockData blockData)
    {
        BlockPos pos = blockData.position.immutable();
        {
            TileEntity tileEntityIn = blockData.tile;
            if (tileEntityIn != null)
            {
                try
                {
                    Field processingLoadedTiles;
                    if (Functions.isObfuscatedEnvironment())
                    {
                        processingLoadedTiles = Functions.getSecureField(World.class, Options.processingLoadedTiles);
                    }
                    else
                    {
                        processingLoadedTiles = Functions.getSecureField(World.class, "updatingBlockEntities");
                    }
                    boolean iplt = processingLoadedTiles.getBoolean(world);

                    List<TileEntity> addedTileEntityList;
                    if (Functions.isObfuscatedEnvironment())
                    {
                        addedTileEntityList = (List<TileEntity>) Functions.getSecureField(World.class, Options.addedTileEntityList).get(world);
                    }
                    else
                    {
                        addedTileEntityList = (List<TileEntity>) Functions.getSecureField(World.class, "pendingBlockEntities").get(world);
                    }
                    if (iplt)
                    {
                        if (tileEntityIn.getLevel() != world)
                        {
                            tileEntityIn.setLevelAndPosition(world, pos);
                        }

                        addedTileEntityList.removeIf(tileentity -> tileentity.getBlockPos().equals(pos));

                        addedTileEntityList.add(tileEntityIn);
                    }
                    else
                    {
                        {
                            List<TileEntity> dest = (iplt ? addedTileEntityList : world.blockEntityList);
                            boolean flag = dest.add(tileEntityIn);

                            if (flag && tileEntityIn instanceof ITickable)
                            {
                                world.tickableBlockEntities.add(tileEntityIn);
                            }

                        }
                        Chunk chunk = (Chunk) world.getChunk(pos);
                        tileEntityIn.setLevelAndPosition(world, pos);
                        tileEntityIn.setPosition(pos);
                        chunk.getBlockEntities().put(pos, tileEntityIn);


                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    //TODO
    /**
     * @return true if a block was set/deleted
     */
    public static boolean setBlockStateSilently(World world, BlockPos targetPos, BlockState newState)
    {

//        if (targetPos.getY()<0 || targetPos.getY()>255)
//        {
//            return false;
//        }
//        else
//        {
//            Chunk chunk = (Chunk) world.getChunk(targetPos);
//
//            net.minecraftforge.common.util.BlockSnapshot blockSnapshot = null;
//            if (world.captureBlockSnapshots)
//            {
//                blockSnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, targetPos, 2);
//                world.capturedBlockSnapshots.add(blockSnapshot);
//            }
//            BlockState oldState = world.getBlockState(targetPos);
//            int oldLight = oldState.getLightValue(world, targetPos);
//            int oldOpacity = oldState.getLightValue(world, targetPos);
//
//            BlockState iblockstate;
//            {
//                int i = targetPos.getX() & 15;
//                int j = targetPos.getY();
//                int k = targetPos.getZ() & 15;
//                int l = k << 4 | i;
//
//
//                int i1 = chunk.getHeightMap()[l];
//                iblockstate = chunk.getBlockState(targetPos);
//
//                {
//                    Block block = newState.getBlock();
//                    ChunkSection extendedblockstorage = chunk.getSections()[j >> 4];
//                    boolean flag = false;
//
//                    if (extendedblockstorage == Chunk.EMPTY_SECTION)
//                    {
//                        extendedblockstorage = new ChunkSection(j >> 4 << 4);
//                        chunk.getSections()[j >> 4] = extendedblockstorage;
//                        flag = j >= i1;
//                    }
//
//                    extendedblockstorage.setBlockState(i, j & 15, k, newState);
//
//
//                    if (extendedblockstorage.getBlockState(i, j & 15, k).getBlock() == block) {
//                        if (flag)
//                        {
////                            chunk.generateSkylightMap();
//                        }
//
//                        chunk.setModified(true);
//                    }
//                }
//
//
//                if (blockSnapshot != null) world.capturedBlockSnapshots.remove(blockSnapshot);
//                {
//                    if (newState.getLightOpacity(world, targetPos) != oldOpacity || newState.getLightValue(world, targetPos) != oldLight)
//                    {
//                        world.profiler.startSection("checkLight");
//                        world.checkLight(targetPos);
//                        world.profiler.endSection();
//                    }
//
//                    if (blockSnapshot == null)
//                    {
//                        if (chunk.isPopulated())
//                        {
//
//                            try
//                            {
//
//                                Field eventListeners;
//                                if(Tools.isObfuscatedEnvironment())
//                                    eventListeners = Tools.getSecureField(World.class, Options.eventlisteners);
//                                else eventListeners=Tools.getSecureField(World.class,"eventListeners");
//                                List<WorldEventListener> listeners= (List<IWorldEventListener>) eventListeners.get(world);
//                                for(WorldEventListener listener:listeners)
//                                {
//                                    if(listener instanceof ServerWorldEventHandler)
//                                    {
//
//                                        WorldServer server= (WorldServer) world;
//                                        PlayerChunkMap chunkMap= server.getPlayerChunkMap();
//                                        chunkMap.markBlockForUpdate(targetPos);
//
//                                    }
//                                    if(listener instanceof PathWorldListener)
//                                    {
//                                        listener.notifyBlockUpdate(world,targetPos,iblockstate,newState,2);
//                                    }
//                                }
//                            } catch (IllegalAccessException e)
//                            {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }
//                    return true;
//                }
//            }
//        }
        return false;
    }

    public static void drawStringWithBackground(MatrixStack matrixStack,Object obj, int x, int y, IntegerColor background)
    {
        String string = obj.toString();
        Minecraft.getInstance().font.draw(matrixStack,string, x + 2, y + 4, background.getIntColor());
    }

    /**
     * Notifies clients of a block update
     */
    public static void sendBlockUpdate(ServerWorld worldServer, BlockPos blockPos)
    {
        BlockState blockState = worldServer.getBlockState(blockPos);
        worldServer.sendBlockUpdated(blockPos, blockState, blockState, 2);
    }

    @OnlyIn(Dist.CLIENT)
    public static void openClientGui(World world, Screen screen)
    {
        if(world.isClientSide)
            Minecraft.getInstance().setScreen(screen);
    }

    public static void playSound(World world, BlockPos blockPos, SoundEvent sound, float volume, float pitch)
    {
        world.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), sound, null, volume, pitch, false);
    }

    public static void drawVerticalLine(int x, int startY, int endY, IntegerColor color, int thickness)
    {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        GL11.glLineWidth(thickness);
        bufferBuilder.vertex(x, startY, 0).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(x, endY, 0).color(red, green, blue, alpha).endVertex();
        tessellator.end();
    }

    public static void drawHorizontalLine(int startX, int endX, int y, IntegerColor color, int thickness)
    {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        GL11.glLineWidth(thickness);
        bufferBuilder.vertex(startX, y, 0).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(endX, y, 0).color(red, green, blue, alpha).endVertex();
        tessellator.end();
    }

    public static void sendMessageToPlayer(PlayerEntity player, String message)
    {
        player.sendMessage(new StringTextComponent(message),player.getUUID());
    }

    /**
     * @param X position to be centered on
     */
    public static void drawCenteredString(MatrixStack matrixStack, ITextComponent o, int X, int Y, IntegerColor color)
    {
        drawString(matrixStack,o, X - Functions.calculateStringWidth(o) / 2, Y, color);
    }

    /**
     * @param X position to be centered on
     */
    public static void drawCenteredStringWithShadow(MatrixStack matrixStack,ITextComponent o, int X, int Y, IntegerColor color)
    {
        drawStringWithShadow(matrixStack,o, X - Functions.calculateStringWidth(o) / 2, Y, color);
    }

    /**
     * Draws string without shadow
     */
    public static void drawString(MatrixStack matrixStack,Object o, int X, int Y, IntegerColor color)
    {
        Minecraft.getInstance().font.draw(matrixStack,o.toString(), X, Y, color.getIntColor());
    }

    public static void drawStringWithShadow(MatrixStack matrixStack,Object o, int X, int Y, IntegerColor color)
    {
        Minecraft.getInstance().font.draw(matrixStack,o.toString(), X, Y, color.getIntColor());
    }

    /**
     * Shows array contents
     */
    public static void show(Object[] objects)
    {
        System.out.println(Arrays.toString(objects));
    }

    public static void addPotionEffectNoParticles(LivingEntity entityLivingBase, Effect potion, int duration, int strength)
    {
        entityLivingBase.addEffect(new EffectInstance(potion, duration, strength, false, false));
    }
}
