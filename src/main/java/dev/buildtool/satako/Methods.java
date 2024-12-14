package dev.buildtool.satako;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
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


    /**
     * @param inputHandler  from
     * @param outputHandler to
     * @param byAmount      how many per operation
     */
    public static void transferItems(IItemHandler inputHandler, IItemHandler outputHandler, int byAmount) {
        both:
        for (int i = 0; i < inputHandler.getSlots(); i++) {
            ItemStack itemStack = inputHandler.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                for (int i1 = 0; i1 < outputHandler.getSlots(); i1++) {
                    ItemStack present=outputHandler.getStackInSlot(i1);
                    if(!present.isEmpty()) {
                        ItemStack tryExtract = inputHandler.extractItem(i, byAmount, true);
                        ItemStack tryInsert = outputHandler.insertItem(i1, tryExtract, true);
                        if (tryInsert.isEmpty()) {
                            tryExtract = inputHandler.extractItem(i, tryExtract.getCount(), false);
                            outputHandler.insertItem(i1, tryExtract, false);
                            break both;
                        }
                    }
                }
                for (int i1 = 0; i1 < outputHandler.getSlots(); i1++) {
                    ItemStack tryExtract = inputHandler.extractItem(i, byAmount, true);
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

    public static void loadConfig(Pair<ConfigSpec, ConfigSpec> pair, String path) {
        String s = FMLPaths.CONFIGDIR.get().resolve(path).toString();
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(s)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
//        pair.getRight().setConfig(file);
    }

    public static void removeFluid(BlockPos target,Level level,boolean sourceOnly)
    {
        if(sourceOnly && Functions.isLiquidSource(level,target))
            level.setBlock(target, Blocks.AIR.defaultBlockState(),2);
        else if(Functions.isLiquid(level,target))
            level.setBlock(target,Blocks.AIR.defaultBlockState(),2);
    }
}
