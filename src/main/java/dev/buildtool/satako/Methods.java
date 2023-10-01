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
