package dev.buildtool.satako;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.items.IItemHandler;

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

    /**
     * Notifies clients of a block update
     */
    public static void sendBlockUpdate(ServerLevel worldServer, BlockPos blockPos) {
        BlockState blockState = worldServer.getBlockState(blockPos);
        worldServer.sendBlockUpdated(blockPos, blockState, blockState, 2);
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
}
