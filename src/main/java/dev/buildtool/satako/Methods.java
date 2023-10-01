package dev.buildtool.satako;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;

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

                            if (flag && tileEntityIn instanceof ITickableTileEntity)
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

    /**
     * Notifies clients of a block update
     */
    public static void sendBlockUpdate(ServerWorld worldServer, BlockPos blockPos)
    {
        BlockState blockState = worldServer.getBlockState(blockPos);
        worldServer.sendBlockUpdated(blockPos, blockState, blockState, 2);
    }

    public static void playSound(World world, BlockPos blockPos, SoundEvent sound, float volume, float pitch)
    {
        world.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), sound, null, volume, pitch, false);
    }

    public static void sendMessageToPlayer(PlayerEntity player, String message)
    {
        player.sendMessage(new StringTextComponent(message),player.getUUID());
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
