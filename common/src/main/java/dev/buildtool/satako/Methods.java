package dev.buildtool.satako;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

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

    public static void addPotionEffectNoParticles(LivingEntity entityLivingBase, Holder<MobEffect> potion, int duration, int strength) {
        entityLivingBase.addEffect(new MobEffectInstance(potion, duration, strength, false, false));
    }

    public static void removeFluid(BlockPos target, Level level, boolean sourceOnly) {
        if (sourceOnly && Functions.isLiquidSource(level, target))
            level.setBlock(target, Blocks.AIR.defaultBlockState(), 2);
        else if (Functions.isLiquid(level, target))
            level.setBlock(target, Blocks.AIR.defaultBlockState(), 2);
    }

    public static void transferItems(ItemContainer inputHandler, ItemContainer outputHandler, int byAmount) {
        both:
        for (int i = 0; i < inputHandler.getSlotCount(); i++) {
            ItemStack itemStack = inputHandler.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                int clamped = Mth.clamp(byAmount, 1, 64);
                for (int i1 = 0; i1 < outputHandler.getSlotCount(); i1++) {
                    ItemStack present = outputHandler.getStackInSlot(i1);
                    if (!present.isEmpty()) {
                        ItemStack tryExtract = inputHandler.extractItem(i, clamped, true);
                        ItemStack tryInsert = outputHandler.insertItem(i1, tryExtract, true);
                        if (tryInsert.isEmpty()) {
                            tryExtract = inputHandler.extractItem(i, tryExtract.getCount(), false);
                            outputHandler.insertItem(i1, tryExtract, false);
                            break both;
                        }
                    }
                }
                for (int i1 = 0; i1 < outputHandler.getSlotCount(); i1++) {
                    ItemStack tryExtract = inputHandler.extractItem(i, clamped, true);
                    ItemStack tryInsert = outputHandler.insertItem(i1, tryExtract, true);
                    if (tryInsert.isEmpty()) {
                        tryExtract = inputHandler.extractItem(i, tryExtract.getCount(), false);
                        outputHandler.insertItem(i1, tryExtract, false);
                        break both;
                    }
                }
            }
        }
    }
}
