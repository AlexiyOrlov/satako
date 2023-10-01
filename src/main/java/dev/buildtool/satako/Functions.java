package dev.buildtool.satako;

import io.netty.buffer.Unpooled;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AirItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

/**
 * Functions return an object
 */
@SuppressWarnings("unused")
public final class Functions
{
    public static BlockPos findAirAbove(IServerWorld serverWorld,BlockPos start)
    {
        while(!serverWorld.isEmptyBlock(start))
        {
            start=start.above();
            if(start.getY()<2 || start.getY()>serverWorld.getHeight()-2)
                break;
        }
        return start;
    }

    public static BlockPos findAirBelow(IServerWorld serverWorld,BlockPos start)
    {
        while (!serverWorld.isEmptyBlock(start))
        {
            start=start.below();
            if(start.getY()<2 || start.getY()>serverWorld.getHeight()-2)
                break;
        }
        return start;
    }

    public static boolean isPlayerInSurvivalMode(PlayerEntity entityPlayer)
    {
        return !entityPlayer.isSpectator() && !entityPlayer.isCreative();
    }

    public static boolean isSurvivalPlayer(Entity entity)
    {
        return entity instanceof PlayerEntity && isPlayerInSurvival((PlayerEntity) entity);
    }

    public static float getDefaultXRightLimbRotation(float limbSwing, float swingAmount)
    {
        return MathHelper.cos((float) (limbSwing+Math.PI))*swingAmount;
    }

    public static float getDefaultXLeftLimbRotation(float limbSwing, float swingAmount) {
        return MathHelper.cos(limbSwing) * swingAmount;
    }

    /**Angle Y or Z*/
    public static float getDefaultHeadYaw(float netYaw)
    {
        return netYaw*0.017453292F;
    }

    /**Angle X */
    public static float getDefaultHeadPitch(float pitch)
    {
        return pitch * 0.017453292F;
    }
    /**
     * Tests whether a creature is in view of other creature
     * @param watched creature to be checked
     * @param watcher creature whose sight is checked
     * @param angleRange in degrees
     * @return true if the watched is in view of watcher
     */
    public static boolean isInSightOf(Entity watched, LivingEntity watcher, float angleRange)
    {
        assert angleRange <= 180;
        Vector3d vecOne = new Vector3d(watched.getX() - watcher.getX(), (watched.getY() + watched.getEyePosition(1).y - watcher.getY() - watcher.getEyePosition(1).y), watched.getZ() - watcher.getZ()).normalize();
        Vector3d vecTwo = watcher.getViewVector(1).normalize();
        double dotproduct = vecTwo.dot(vecOne);
        float threshold = (180 - angleRange) / 180f;
        if (dotproduct > threshold)
        {
            return watcher.canSee(watched);
        }
        return false;
    }

    /**
     * Actually performs ray cast from the entity's look direction
     *
     * @param from     entity
     * @param distance max. distance
     * @return reached position
     */
    public static BlockPos performBlockRayTrace(Entity from, double distance, World world)
    {
        Vector3d eyesPosition = from.getEyePosition(1);
        Vector3d look = from.getLookAngle();
        BlockPos blockPos = new BlockPos(eyesPosition);
        double dist;
        while (world.isEmptyBlock(blockPos))
        {
            eyesPosition = eyesPosition.add(look);
            blockPos = new BlockPos(eyesPosition);
            dist = from.distanceToSqr(eyesPosition.x, eyesPosition.y, eyesPosition.z);
            if (dist >= distance * distance)
            {
                break;
            }
        }
        return blockPos;
    }


    public static boolean isThereSpaceInAHandler(IItemHandler iItemHandler, boolean allowPartialStack)
    {
        for (int i = 0; i < iItemHandler.getSlots(); i++)
        {
            ItemStack itemStack = iItemHandler.getStackInSlot(i);
            if (itemStack.isEmpty() || (allowPartialStack && itemStack.getCount() < itemStack.getMaxStackSize()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Not sure whether to use this function in {@link Block#neighborChanged(BlockState, World, BlockPos, Block, BlockPos, boolean)} or {@link Block#tick(BlockState, ServerWorld, BlockPos, Random)}
     */
    public static boolean isDirectionalBlockPowered(Direction blockDirection, BlockPos blockPosition, BlockPos pulsePosition, World world)
    {
        Direction back = blockDirection.getOpposite();
        BlockPos backPosition = blockPosition.relative(back);
        if (pulsePosition.equals(backPosition))
        {
            BlockState backstate = world.getBlockState(backPosition);
            if (backstate.isSignalSource())
            {
                return world.hasSignal(backPosition, back);
            }
        }
        return false;
    }

    /**
     * Gets a direction of where a signal is coming from
     *
     * @param pulsePosition signal source position
     * @param target        signal target
     * @param notifier      signal source
     */
    public static Direction getPowerIncomingDirection(BlockPos pulsePosition, BlockPos target, Block notifier, World world)
    {
        BlockState source = world.getBlockState(pulsePosition);
        for (Direction value : Direction.values())
        {
            BlockPos sidepos = target.relative(value);
            if (sidepos.equals(pulsePosition))
            {
                BlockState sidestate = world.getBlockState(sidepos);
                if (sidestate == source && notifier == sidestate.getBlock())
                {
                    if (sidestate.getDirectSignal(world, sidepos, value) > 0)
                    {
                        return value;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param source notifier's position
     * @return direct ("weak") power
     */
    public static int getDirectPower(BlockPos source, BlockPos target, Block notifier, World world)
    {

        BlockState sourceState = world.getBlockState(source);
        for (Direction enumFacing : Direction.values())
        {
            BlockPos sidepos = target.relative(enumFacing);
            if (sidepos.equals(source))
            {
                BlockState sidestate = world.getBlockState(sidepos);
                if (sidestate == sourceState && notifier == sidestate.getBlock())
                {
                    return sidestate.getDirectSignal(world, sidepos, enumFacing);
                }
            }

        }
        return 0;
    }

    public static boolean isNotifierAdjacent(BlockPos source, BlockPos target, Block notifier, World world)
    {
        BlockState sourceState = world.getBlockState(source);
        for (Direction enumFacing : Direction.values())
        {
            BlockPos sidepos = target.relative(enumFacing);
            if (sidepos.equals(source))
            {
                BlockState sidestate = world.getBlockState(sidepos);
                if (sidestate == sourceState && notifier == sidestate.getBlock())
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return chunk's corner block position with Y=0
     */
    public static BlockPos getBlockPositionFrom(ChunkPos chunkPos)
    {
        return new BlockPos(chunkPos.x << 4, 0, chunkPos.z << 4);
    }

    public static float degreesToRadians(float degrees)
    {
        return (float) (degrees * Math.PI / 180);
    }

    public static float translateToXcoord(float degrees)
    {
        if (degrees == 90 || degrees == -90)
        {
            return 0;
        }
        if (degrees == 180 || degrees == -180)
        {
            return -1;
        }
        return MathHelper.cos(degreesToRadians(degrees));
    }

    public static float translateToZcoord(float degrees)
    {
        if (degrees == 90 || degrees == -90)
        {
            return 1;
        }
        if (degrees == 180 || degrees == -180)
        {
            return 0;
        }
        return MathHelper.sin(degreesToRadians(degrees));
    }

    /**
     * @return true if something can generate without cascading at that position
     */
    public static boolean canGenerateWithoutCascade(IServerWorld world, BlockPos blockPos)
    {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        int cx = chunkPos.x;
        int cz = chunkPos.z;
        ChunkStatus empty = ChunkStatus.EMPTY;
        return world.getChunk(cx, cz).getStatus() != empty && world.getChunk(cx + 1, cz).getStatus() != empty &&
                world.getChunk(cx - 1, cz).getStatus() != empty
                && world.getChunk(cx, cz + 1).getStatus() != empty && world.getChunk(cx, cz - 1).getStatus() != empty;
    }

    /**
     * Searches for a non-air block starting from world height-16
     *
     * @param pos any position
     * @return highest position which contains a block
     */
    public static BlockPos getTopBlockPosition(BlockPos pos, World world)
    {
        BlockPos blockpos;
        blockpos = new BlockPos(pos.getX(), world.getHeight() - 16, pos.getZ());
        BlockState nextstate = world.getBlockState(blockpos);
        while (nextstate == Blocks.AIR.defaultBlockState())
        {
            blockpos = blockpos.below();
            nextstate = world.getBlockState(blockpos);
        }
        return blockpos;
    }

    /**
     * Searches for any entity
     */
    @Nullable
    public static Entity findEntityOnPath(World world, Entity watcher)
    {
        Entity entity = null;
        Vector3d position = watcher.getEyePosition(1);
        Vector3d look = watcher.getLookAngle();
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(new BlockPos(watcher.getX(),watcher.getY(),watcher.getZ())).inflate(7);
        List<Entity> entities = world.getEntities(watcher, axisAlignedBB);
        int counter = 0;
        w:
        while (true)
        {

            position = position.add(look);
            for (Entity entity1 : entities)
            {
                if (entity1.getBoundingBox().contains(position))
                {
                    entity = entity1;
                    break w;
                }
            }
            if (counter > 10)
            {
                break;
            }
            counter++;
        }
        return entity;
    }

    /**
     * @return whether UUID was valid
     */
    public static boolean writeUUID(CompoundNBT nbtTagCompound, String key, UUID uuid)
    {
        if (uuid != null && !uuid.equals(Constants.NULL_UUID))
        {
            nbtTagCompound.putUUID(key, uuid);
            return true;
        }
        return false;
    }

    public static UUID readUUID(CompoundNBT nbtTagCompound, String key)
    {
        UUID uuid = nbtTagCompound.getUUID(key);
        return uuid.equals(Constants.NULL_UUID) ? null : uuid;
    }

    /**
     * Reads enum order
     *
     * @return byte cast to int
     */
    public static int readEnum(CompoundNBT compound, String key)
    {
        return compound.getByte(key);
    }

    public static boolean isLiquid(World world, BlockPos pos)
    {
        FluidState fluidState = world.getFluidState(pos);
        Fluid fluid = fluidState.getType();
        return fluid != Fluids.EMPTY;
    }

    public static boolean isFlowingLiquid(World world, BlockPos position)
    {
        FluidState fluidState = world.getFluidState(position);
        Fluid fluid = fluidState.getType();
        return fluid != Fluids.EMPTY && !fluid.isSource(fluidState);
    }

    public static boolean isLiquidSource(World world, BlockPos position)
    {
        FluidState fluidState = world.getFluidState(position);
        Fluid fluid = fluidState.getType();
        return fluid != Fluids.EMPTY && fluid.isSource(fluidState);
    }

    public static boolean isLookingAtHead(LivingEntity watcher, Entity target)
    {
        Vector3d lookvector = watcher.getViewVector(1.0F).normalize();
        Vector3d positionvector = new Vector3d(target.getX() - watcher.getX(), watcher.getBoundingBox().minY +  target.getEyeHeight() - (watcher.getY() +  watcher.getEyeHeight()), target.getZ() - watcher.getZ());
        double lengthVector = positionvector.length();
        positionvector = positionvector.normalize();
        double dotProduct = lookvector.dot(positionvector);
        if (dotProduct > 1.0D - 0.025D / lengthVector)
        {
            return watcher.canSee(target);
        }
        return false;
    }

    public static HashSet<BlockPos> getConnectedBlocks(Block of, Direction[] checkedSides, BlockPos pos, World world, HashSet<BlockPos> positions, int limit)
    {
        assert limit > 1 : "Limit must be >1";
        if (world.getBlockState(pos).getBlock() == of)
        {
            positions.add(pos);
        }
        if (positions.size() >= limit)
        {
            return positions;
        }
        for (Direction checkedSide : checkedSides)
        {
            BlockPos side = pos.relative(checkedSide);
            BlockState next = world.getBlockState(side);
            Block block = next.getBlock();
            if (block == of)
            {
                if (!positions.contains(side))
                {
                    getConnectedBlocks(of, ArrayUtils.removeElement(Direction.values(), checkedSide.getOpposite()), side, world, positions, limit);
                }
                else
                {
                    positions.add(side);
                    if (positions.size() >= limit)
                    {
                        return positions;
                    }
                }
            }
        }

        return positions;
    }

    public static boolean isEmpty(Collection<ItemStack> itemStackCollection)
    {
        for (ItemStack itemStack : itemStackCollection)
        {
            if (!itemStack.isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    public static boolean isPlayerInSurvival(PlayerEntity player)
    {
        return !player.isCreative() && !player.isSpectator();
    }

    /**
     * Tests whether an object is a subclass or an instance of specified Class
     *
     * @param subject class to check
     */
    public static boolean isSuperClass(Class<?> subject, Object of)
    {
        if (of.getClass() == Class.class)
        {
            return subject.isAssignableFrom((Class<?>) of);
        }
        return subject.isInstance(of);
    }

    /**
     * @return list of block positions inside the box
     */
    public static List<BlockPos> boundingBoxToPositions(AxisAlignedBB axisAlignedBB)
    {
        List<BlockPos> positions = new ArrayList<>();
        for (double X = axisAlignedBB.minX; X <= axisAlignedBB.maxX; X++)
        {
            for (double Y = axisAlignedBB.minY; Y <= axisAlignedBB.maxY; Y++)
            {
                for (double Z = axisAlignedBB.minZ; Z <= axisAlignedBB.maxZ; Z++)
                {
                    positions.add(new BlockPos(X, Y, Z));
                }
            }
        }
        return positions;
    }

    public static Direction randomHorizontalFacing()
    {
        return Constants.HORIZONTALS[Methods.RANDOMGENERATOR.nextInt(Constants.HORIZONTALS.length)];
    }

    /**
     * @return registered block item
     */
    public static Item registerBlockItem(Block block, String ID, String identifier, RegistryEvent.Register<Item> registryEvent, Item.Properties properties)
    {
        Item b1 = new BlockItem(block, properties).setRegistryName(ID, identifier);
        registryEvent.getRegistry().register(b1);
        return b1;
    }

    /**
     * @return true if there is no tile entity or unbreakable block
     */
    public static boolean canReplaceBlock(BlockPos blockPos, World world)
    {
        BlockState iBlockState = world.getBlockState(blockPos);
        if (world.getBlockEntity(blockPos) != null)
            return false;
        return iBlockState.getDestroySpeed(world, blockPos) != -1;
    }

    /**
     * Spawns an item in a way that it doesn't get any acceleration
     */
    public static ItemEntity spawnItemInWorld(ItemStack itemStack, World world, BlockPos pos)
    {
        if (world.isClientSide) throw new IllegalArgumentException("Don't spawn items in client world");
        ItemEntity entityItem = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, itemStack);
        world.addFreshEntity(entityItem);
        entityItem.setDeltaMovement(0, 0, 0);
        return entityItem;
    }

    /**
     * Searches for a position above solid block, starting from world height. Skips replaceable blocks and leaves
     */
    public static BlockPos getPosAboveSolidBlock(World world, BlockPos blockPos)
    {
        BlockState blockState = world.getBlockState(blockPos = blockPos.above(world.getHeight()));
        while (blockState.getMaterial().isReplaceable() || blockState.getMaterial() == Material.LEAVES)
        {
            blockState = world.getBlockState(blockPos = blockPos.below());
        }
        blockPos = blockPos.above();
        return blockPos;
    }

    public static Item registerItem(Item item, String mod, String ID, RegistryEvent.Register<Item> registryEvent)
    {
        item.setRegistryName(mod, ID);
        registryEvent.getRegistry().register(item);
        return item;
    }

    public static ArrayList<Direction> getSideDirections(Direction of)
    {
        ArrayList<Direction> sidedirections = new ArrayList<>(5);
        switch (of)
        {
            case DOWN:
                Collections.addAll(sidedirections, Direction.UP, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH);
                break;
            case UP:
                Collections.addAll(sidedirections, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH);
                break;
            case EAST:
                Collections.addAll(sidedirections, Direction.WEST, Direction.DOWN, Direction.UP, Direction.SOUTH, Direction.NORTH);
                break;
            case WEST:
                Collections.addAll(sidedirections, Direction.EAST, Direction.DOWN, Direction.UP, Direction.SOUTH, Direction.NORTH);
                break;
            case SOUTH:
                Collections.addAll(sidedirections, Direction.NORTH, Direction.DOWN, Direction.UP, Direction.WEST, Direction.EAST);
                break;
            case NORTH:
                Collections.addAll(sidedirections, Direction.SOUTH, Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST);
                break;
        }
        return sidedirections;
    }

    /**
     * Tests whether one Itemstack is equal to another except size
     *
     * @return false if not equal or any is empty
     */
    public static boolean areItemTypesEqual(ItemStack one, ItemStack two)
    {
        if (!one.isEmpty() && !two.isEmpty())
        {
            final Item oneItem = one.getItem();
            final Item secondItem = two.getItem();
            return oneItem == secondItem && one.getDamageValue() == two.getDamageValue() &&
                    oneItem.getRegistryName().equals(secondItem.getRegistryName())
                    && (ItemStack.tagMatches(one, two));
        }
        return false;
    }

    public static boolean areItemsEqualInDictionary(ItemStack one, ItemStack two)
    {
        if (!one.isEmpty() && !two.isEmpty())
        {
            Collection<ResourceLocation> identifiers = ItemTags.getAllTags().getMatchingTags(one.getItem());
            Collection<ResourceLocation> identifiers2 = ItemTags.getAllTags().getMatchingTags(two.getItem());
            for (ResourceLocation identifier : identifiers)
            {
                if (identifiers2.contains(identifier))
                {
                    return true;
                }

            }
        }
        return false;
    }

    public static int getFuelValue(@Nonnull ItemStack stack)
    {
        return ForgeHooks.getBurnTime(stack);
    }

    /**
     * Checks the weak power on all sides
     *
     * @param pos target position
     * @return direction from where the power is incoming
     */
    public static Direction isBlockDirectlyPowered(World worldIn, BlockPos pos)
    {
        for (Direction from : Direction.values())
        {
            BlockPos sidepos = pos.relative(from);
            BlockState sidestate = worldIn.getBlockState(sidepos);
            Block sideblock = sidestate.getBlock();
            if (sideblock.isSignalSource(sidestate))
            {
                int p = sidestate.getDirectSignal(worldIn, sidepos, from);
                if (p > 0)
                {
                    return from;
                }
            }
        }
        return null;
    }

    /**
     * Same as {@link #isBlockDirectlyPowered(World, BlockPos)} with excluded position
     *
     * @param side exception
     * @return side which recieves power
     */
    public static Direction isPoweredExceptSide(Direction side, World world, BlockPos position)
    {
        for (Direction facing : Direction.values())
        {
            if (side != facing)
            {
                BlockPos sidepos = position.relative(facing);
                BlockState sidestate = world.getBlockState(sidepos);
                Block sideblock = sidestate.getBlock();
                if (sideblock.isSignalSource(sidestate))
                {
                    int p = sidestate.getDirectSignal(world, sidepos, facing);
                    if (p > 0)
                    {
                        return facing;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the length of longest string
     */
    public static int calculateLongestStringWidth(Collection<ITextComponent> objects)
    {
        int width = 0;
        for (ITextComponent s : objects)
        {
            int nextwidth = ClientFunctions.calculateStringWidth(s);
            if (nextwidth > width) width = nextwidth;
        }
        return width;
    }

    /**
     * Reliably gets a translated block name
     */
    public static String getBlockName(BlockState blockState)
    {
        if (blockState.getBlock() instanceof AirBlock)
        {
            return "Air";
        }
        ItemStack itemStack = new ItemStack(blockState.getBlock(), 1);
        if (!itemStack.isEmpty())
        {
            return itemStack.getDisplayName().getString();
        }
        return blockState.getBlock().getDescriptionId();
    }

    /**
     * @return whether the stack was deleted
     */
    public static boolean removeHeldItem(PlayerEntity player, Item item)
    {

        if (!player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() == item)
        {
            player.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            return true;
        }
        else if (!player.getOffhandItem().isEmpty() && player.getOffhandItem().getItem() == item)
        {
            player.setItemInHand(Hand.OFF_HAND, ItemStack.EMPTY);
            return true;
        }

        return false;
    }

    /**
     *
     * @return a 1-sized stack. Can be empty
     */
    public static ItemStack getStackFromBlockState(BlockState blockState)
    {

        ItemStack ds;
        ds = new ItemStack(blockState.getBlock(), 1);
        if (ds.isEmpty())
        {
            //for blocks that don't have associated ItemBlock
            Item b = Item.byBlock(blockState.getBlock());
            if (b instanceof AirItem)
            {
//                ds = new ItemStack(blockState.getBlock(),blockState.getBlock().quantityDropped(blockState,0,randomgenerator));
            }
            else
            {
                ds = new ItemStack(b);
            }
        }
        return ds;
    }

    /**
     * Retrieves specified field from class. Searches superclasses if not found.
     */
    public static Field getSecureField(Class owner, int number)
    {
        Field f;
        Field[] fields = owner.getDeclaredFields();
        if (number < fields.length)
        {
            f = fields[number];
            if (f.getType() != owner.getEnclosingClass())
            {
                f.setAccessible(true);
                return f;
            }
            else
            {
                return getSecureField(owner.getSuperclass(), number);
            }
        }
//		System.err.println("No such field - index exceeds field array size");
        return null;
    }

    public static boolean isObfuscatedEnvironment()
    {
        return false;
//        return !((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"));
    }

    /**
     * Gets any field. Searches superclasses if not found
     */
    public static Field getSecureField(Class<?> owningClass, String field)
    {
        Field f = null;

        try
        {
            f = owningClass.getDeclaredField(field);
            f.setAccessible(true);
        }
        catch (NoSuchFieldException e)
        {
            if (owningClass.getSuperclass() != null)
            {
                return getSecureField(owningClass.getSuperclass(), field);
            }
            else
            {
                System.err.println("Searched all super classes - field " + field + " not found");
            }
        }
        return f;
    }

    public static Method getAnyMethod(Class<?> owner, String name, Class<?>... parameterTypes)
    {
        Method m = null;
        try
        {
            m = owner.getDeclaredMethod(name, parameterTypes);
            m.setAccessible(true);
        }
        catch (NoSuchMethodException | SecurityException e)
        {
            e.printStackTrace();
        }
        return m;
    }

    public static Field getPublicField(Class<?> owner, String fieldName)
    {
        Field f = null;
        try
        {
            f = owner.getField(fieldName);
        }
        catch (NoSuchFieldException | SecurityException e)
        {
            e.printStackTrace();
        }
        return f;
    }

    /**
     * Extracts an itemstack
     *
     * @return extracted ItemStack
     */
    public static ItemStack tryExtractItems(IItemHandler itemHandler, ItemStack itemStack, boolean simulate)
    {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++)
        {
            ItemStack presentstack = itemHandler.getStackInSlot(slot);
            if (areItemTypesEqual(itemStack, presentstack))
            {
                return itemHandler.extractItem(slot, itemStack.getCount(), simulate);
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Creates an ItemHandler with stacks copied from given IItemHandler
     */
    public static ItemHandler copyStacks(IItemHandler from)
    {
        ItemHandler itemHandler = new ItemHandler(from.getSlots());

        for (int i = 0; i < from.getSlots(); i++)
        {
            ItemStack stack = from.extractItem(i, 64, true);
            if (!stack.isEmpty())
            {

                if (!tryInsertItem(itemHandler, stack.copy()))
                {
                    System.out.println("Couldn't insert " + stack);
                    return itemHandler;
                }
            }
        }
        return itemHandler;
    }

    /**
     * Inserts an item into a handler. Merges present stacks first
     *
     * @param itemStack will be copied
     * @return true if fully inserted, false if not
     */
    public static boolean tryInsertItem(IItemHandler iItemHandler, ItemStack itemStack)
    {
        ItemStack out = ItemHandlerHelper.insertItemStacked(iItemHandler, itemStack.copy(), false);
        itemStack.setCount(out.getCount());
        return out.isEmpty();
    }

    /**
     * Tests whether a stack can be fully inserted
     */
    public static boolean canInsertItem(IItemHandler into, ItemStack stack)
    {
        if (stack.isEmpty()) return false;
        int slots = into.getSlots();
        for (int i = 0; i < slots; i++)
        {
            ItemStack pressent = into.getStackInSlot(i);
            if (into.isItemValid(i,stack) && areItemTypesEqual(stack, pressent))
            {
                ItemStack result = into.insertItem(i, stack, true);
                if (result.isEmpty())
                {
                    return true;
                }
            }
        }

        for (int i = 0; i < slots; i++)
        {
            ItemStack next = into.getStackInSlot(i);
            if (next.isEmpty() && into.isItemValid(i,stack))
            {
                ItemStack rem = into.insertItem(i, stack, true);
                if (rem.isEmpty()) {
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * Use {@link LivingEntity#isHolding(Predicate)}
     */
    @Deprecated
    public static boolean isHolding(Predicate<Item> itemPredicate, LivingEntity entity) {
        return itemPredicate.test(entity.getMainHandItem().getItem()) || itemPredicate.test(entity.getOffhandItem().getItem());
    }

    public static int ticksToSeconds(int ticks) {
        return ticks / 20;
    }

    public static int ticksToMinutes(int ticks)
    {
        return ticksToSeconds(ticks) / 60;
    }

    public static int ticksToHours(int ticks)
    {
        return ticksToMinutes(ticks) / 60;
    }

    public static int secondsToTicks(int seconds)
    {
        return seconds * 20;
    }

    public static int minutesToTicks(int minutes)
    {
        return secondsToTicks(minutes)*60;
    }

    public static int hoursToTicks(int hours)
    {
        return minutesToTicks(hours)*60;
    }

    public static ItemStack getHeldItem(PlayerEntity playerEntity, Item item)
    {
        if (playerEntity.getMainHandItem().getItem() == item)
        {
            return playerEntity.getMainHandItem();
        }
        else if (playerEntity.getOffhandItem().getItem() == item)
        {
            return playerEntity.getOffhandItem();
        }
        return null;
    }

    public static ItemStack getHeldItem(PlayerInventory playerInventory, Item item)
    {
        return getHeldItem(playerInventory.player, item);
    }

    public static Hand getHandHoldingItem(PlayerEntity playerEntity, Item item)
    {
        if (playerEntity.getMainHandItem().getItem() == item)
        {
            return Hand.MAIN_HAND;
        }
        else if (playerEntity.getOffhandItem().getItem() == item)
        {
            return Hand.OFF_HAND;
        }
        return null;
    }

    /**
     * @return slot number or -1 if not found
     */
    public static int findItemIn(IItemHandler itemHandler, ItemStack stack)
    {
        int size = itemHandler.getSlots();
        for (int slot = 0; slot < size; slot++)
        {
            ItemStack nextstack = itemHandler.getStackInSlot(slot);
            if (areItemTypesEqual(nextstack, stack))
            {
                return slot;
            }
        }
        return -1;
    }

    public static ItemStack searchItem(IItemHandler in, Item forItem)
    {
        for (int i = 0; i < in.getSlots(); i++)
        {
            ItemStack itemStack = in.getStackInSlot(i);
            if (itemStack.getItem() == forItem)
            {
                return itemStack;
            }
        }
        return null;
    }

    public static boolean removeBlock(BlockPos position, IWorld world) {
        return world.removeBlock(position, false);
    }

    public static Rotation directionToRotation(Direction direction) {
        assert direction.getAxis().isHorizontal();
        Rotation rotation;
        switch (direction) {
            case NORTH:
                rotation = Rotation.NONE;
                break;
            case SOUTH:
                rotation = Rotation.CLOCKWISE_180;
                break;
            case EAST:
                rotation = Rotation.CLOCKWISE_90;
                break;
            case WEST:
                rotation = Rotation.COUNTERCLOCKWISE_90;
                break;
            default:
                return null;
        }
        return rotation.getRotated(Rotation.COUNTERCLOCKWISE_90);
    }

    public static PacketBuffer emptyBuffer() {
        return new PacketBuffer(Unpooled.buffer());
    }
}
