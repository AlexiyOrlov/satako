package dev.buildtool.satako;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
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
        //TODO check format
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

    public static void drawStringWithBackground(PoseStack matrixStack, Object obj, int x, int y, IntegerColor background) {
        String string = obj.toString();
        Minecraft.getInstance().font.draw(matrixStack, string, x + 2, y + 4, background.getIntColor());
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

    public static void sendMessageToPlayer(Player player, String message) {
        player.displayClientMessage(Component.literal(message), false);
    }

    /**
     * @param X position to be centered on
     */
    public static void drawCenteredString(PoseStack matrixStack, net.minecraft.network.chat.Component o, int X, int Y, IntegerColor color) {
        drawString(matrixStack, o, X - Functions.calculateStringWidth(o) / 2, Y, color);
    }

    /**
     * @param X position to be centered on
     */
    public static void drawCenteredStringWithShadow(PoseStack matrixStack, Component o, int X, int Y, IntegerColor color) {
        drawStringWithShadow(matrixStack, o, X - Functions.calculateStringWidth(o) / 2, Y, color);
    }

    /**
     * Draws string without shadow
     */
    public static void drawString(PoseStack matrixStack, Object o, int X, int Y, IntegerColor color) {
        Minecraft.getInstance().font.draw(matrixStack, o.toString(), X, Y, color.getIntColor());
    }

    public static void drawStringWithShadow(PoseStack matrixStack, Object o, int X, int Y, IntegerColor color) {
        Minecraft.getInstance().font.draw(matrixStack, o.toString(), X, Y, color.getIntColor());
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
     * @param addBackFaces whether to add back faces for the sides
     * @param extruder     offsets faces
     */
    public static void addRectangle(VertexConsumer vertexConsumer, Matrix4f matrix4f, int width, int height, int depth, float red, float green, float blue, float alpha, boolean addBackFaces, float extruder) {
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
