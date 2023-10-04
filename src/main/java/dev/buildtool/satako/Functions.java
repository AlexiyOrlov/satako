package dev.buildtool.satako;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
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
public final class Functions {
    public static BlockPos findAirAbove(WorldGenLevel serverWorld, BlockPos start) {
        while (!serverWorld.isEmptyBlock(start)) {
            start = start.above();
            if (start.getY() < 2 || start.getY() > serverWorld.getHeight() - 2)
                break;
        }
        return start;
    }

    public static BlockPos findAirBelow(WorldGenLevel serverWorld, BlockPos start) {
        while (!serverWorld.isEmptyBlock(start)) {
            start = start.below();
            if (start.getY() < 2 || start.getY() > serverWorld.getHeight() - 2)
                break;
        }
        return start;
    }

    public static boolean isPlayerInSurvivalMode(Player entityPlayer) {
        return !entityPlayer.isSpectator() && !entityPlayer.isCreative();
    }

    public static boolean isSurvivalPlayer(Entity entity)
    {
        return entity instanceof Player && isPlayerInSurvival((Player) entity);
    }

    public static float getDefaultXRightLimbRotation(float limbSwing, float swingAmount)
    {
        return Mth.cos((float) (limbSwing + Math.PI)) * swingAmount;
    }

    public static float getDefaultXLeftLimbRotation(float limbSwing, float swingAmount) {
        return Mth.cos(limbSwing) * swingAmount;
    }

    /**Angle Y or Z*/
    public static float getDefaultHeadYaw(float netYaw)
    {
        return netYaw*0.017453292F;
    }

    /**Angle X */
    public static float getDefaultHeadPitch(float pitch) {
        return pitch * 0.017453292F;
    }

    /**
     * Tests whether a creature is in view of other creature
     *
     * @param watched    creature to be checked
     * @param watcher    creature whose sight is checked
     * @param angleRange in degrees
     * @return true if the watched is in view of watcher
     */
    public static boolean isInSightOf(Entity watched, LivingEntity watcher, float angleRange) {
        assert angleRange <= 180;
        Vec3 vecOne = new Vec3(watched.getX() - watcher.getX(), (watched.getY() + watched.getEyePosition(1).y - watcher.getY() - watcher.getEyePosition(1).y), watched.getZ() - watcher.getZ()).normalize();
        Vec3 vecTwo = watcher.getViewVector(1).normalize();
        double dotproduct = vecTwo.dot(vecOne);
        float threshold = (180 - angleRange) / 180f;
        if (dotproduct > threshold) {
            return watcher.hasLineOfSight(watched);
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
    public static BlockPos performBlockRayTrace(Entity from, double distance, Level world) {
        Vec3 eyesPosition = from.getEyePosition(1);
        Vec3 look = from.getLookAngle();
        BlockPos blockPos = new BlockPos((int) eyesPosition.x, (int) eyesPosition.y, (int) eyesPosition.z);
        double dist;
        while (world.isEmptyBlock(blockPos)) {
            eyesPosition = eyesPosition.add(look);
            blockPos = new BlockPos((int) eyesPosition.x, (int) eyesPosition.y, (int) eyesPosition.z);
            dist = from.distanceToSqr(eyesPosition.x, eyesPosition.y, eyesPosition.z);
            if (dist >= distance * distance) {
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
     * Not sure whether to use this function in {@link Block#neighborChanged(BlockState, Level, BlockPos, Block, BlockPos, boolean)} or {@link Block#tick(BlockState, ServerLevel, BlockPos, RandomSource)}
     */
    public static boolean isDirectionalBlockPowered(Direction blockDirection, BlockPos blockPosition, BlockPos pulsePosition, Level world) {
        Direction back = blockDirection.getOpposite();
        BlockPos backPosition = blockPosition.relative(back);
        if (pulsePosition.equals(backPosition)) {
            BlockState backstate = world.getBlockState(backPosition);
            if (backstate.isSignalSource()) {
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
    public static Direction getPowerIncomingDirection(BlockPos pulsePosition, BlockPos target, Block notifier, Level world) {
        BlockState source = world.getBlockState(pulsePosition);
        for (Direction value : Direction.values()) {
            BlockPos sidepos = target.relative(value);
            if (sidepos.equals(pulsePosition)) {
                BlockState sidestate = world.getBlockState(sidepos);
                if (sidestate == source && notifier == sidestate.getBlock()) {
                    if (sidestate.getDirectSignal(world, sidepos, value) > 0) {
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
    public static int getDirectPower(BlockPos source, BlockPos target, Block notifier, Level world) {

        BlockState sourceState = world.getBlockState(source);
        for (Direction enumFacing : Direction.values()) {
            BlockPos sidepos = target.relative(enumFacing);
            if (sidepos.equals(source)) {
                BlockState sidestate = world.getBlockState(sidepos);
                if (sidestate == sourceState && notifier == sidestate.getBlock()) {
                    return sidestate.getDirectSignal(world, sidepos, enumFacing);
                }
            }

        }
        return 0;
    }

    public static boolean isNotifierAdjacent(BlockPos source, BlockPos target, Block notifier, Level world) {
        BlockState sourceState = world.getBlockState(source);
        for (Direction enumFacing : Direction.values()) {
            BlockPos sidepos = target.relative(enumFacing);
            if (sidepos.equals(source)) {
                BlockState sidestate = world.getBlockState(sidepos);
                if (sidestate == sourceState && notifier == sidestate.getBlock()) {
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
        return Mth.cos(degreesToRadians(degrees));
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
        return Mth.sin(degreesToRadians(degrees));
    }

    /**
     * @return true if something can generate without cascading at that position
     */
    public static boolean canGenerateWithoutCascade(WorldGenLevel world, BlockPos blockPos) {
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
    public static BlockPos getTopBlockPosition(BlockPos pos, Level world) {
        BlockPos blockpos;
        blockpos = new BlockPos(pos.getX(), world.getHeight() - 16, pos.getZ());
        BlockState nextstate = world.getBlockState(blockpos);
        while (nextstate == Blocks.AIR.defaultBlockState()) {
            blockpos = blockpos.below();
            nextstate = world.getBlockState(blockpos);
        }
        return blockpos;
    }

    /**
     * Searches for any entity
     */
    @Nullable
    public static Entity findEntityOnPath(Level world, Entity watcher) {
        Entity entity = null;
        Vec3 position = watcher.getEyePosition(1);
        Vec3 look = watcher.getLookAngle();
        AABB axisAlignedBB = new AABB(new BlockPos((int) watcher.getX(), (int) watcher.getY(), (int) watcher.getZ())).inflate(7);
        List<Entity> entities = world.getEntities(watcher, axisAlignedBB);
        int counter = 0;
        w:
        while (true) {

            position = position.add(look);
            for (Entity entity1 : entities) {
                if (entity1.getBoundingBox().contains(position)) {
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
    public static boolean writeUUID(CompoundTag nbtTagCompound, String key, UUID uuid) {
        if (uuid != null && !uuid.equals(Constants.NULL_UUID)) {
            nbtTagCompound.putUUID(key, uuid);
            return true;
        }
        return false;
    }

    public static UUID readUUID(CompoundTag nbtTagCompound, String key) {
        UUID uuid = nbtTagCompound.getUUID(key);
        return uuid.equals(Constants.NULL_UUID) ? null : uuid;
    }

    /**
     * Reads enum order
     *
     * @return byte casted to int
     */
    public static int readEnum(CompoundTag compound, String key) {
        return compound.getByte(key);
    }

    public static boolean isLiquid(Level world, BlockPos pos) {
        FluidState fluidState = world.getFluidState(pos);
        Fluid fluid = fluidState.getType();
        return fluid != Fluids.EMPTY;
    }

    public static boolean isFlowingLiquid(Level world, BlockPos position) {
        FluidState fluidState = world.getFluidState(position);
        Fluid fluid = fluidState.getType();
        return fluid != Fluids.EMPTY && !fluid.isSource(fluidState);
    }

    public static boolean isLiquidSource(Level world, BlockPos position) {
        FluidState fluidState = world.getFluidState(position);
        Fluid fluid = fluidState.getType();
        return fluid != Fluids.EMPTY && fluid.isSource(fluidState);
    }

    public static boolean isLookingAtHead(LivingEntity watcher, Entity target) {
        Vec3 lookvector = watcher.getViewVector(1.0F).normalize();
        Vec3 positionvector = new Vec3(target.getX() - watcher.getX(), watcher.getBoundingBox().minY + target.getEyeHeight() - (watcher.getY() + watcher.getEyeHeight()), target.getZ() - watcher.getZ());
        double lengthVector = positionvector.length();
        positionvector = positionvector.normalize();
        double dotProduct = lookvector.dot(positionvector);
        if (dotProduct > 1.0D - 0.025D / lengthVector) {
            return watcher.hasLineOfSight(target);
        }
        return false;
    }

    public static HashSet<BlockPos> getConnectedBlocks(Block of, Direction[] checkedSides, BlockPos pos, Level world, HashSet<BlockPos> positions, int limit) {
        assert limit > 1 : "Limit must be >1";
        if (world.getBlockState(pos).getBlock() == of) {
            positions.add(pos);
        }
        if (positions.size() >= limit) {
            return positions;
        }
        for (Direction checkedSide : checkedSides) {
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

    public static boolean isPlayerInSurvival(Player player) {
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
    public static List<BlockPos> boundingBoxToPositions(AABB axisAlignedBB) {
        List<BlockPos> positions = new ArrayList<>();
        for (double X = axisAlignedBB.minX; X <= axisAlignedBB.maxX; X++) {
            for (double Y = axisAlignedBB.minY; Y <= axisAlignedBB.maxY; Y++) {
                for (double Z = axisAlignedBB.minZ; Z <= axisAlignedBB.maxZ; Z++) {
                    positions.add(new BlockPos((int) X, (int) Y, (int) Z));
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
     * @return true if there is no tile entity or unbreakable block
     */
    public static boolean canReplaceBlock(BlockPos blockPos, Level world) {
        BlockState iBlockState = world.getBlockState(blockPos);
        if (world.getBlockEntity(blockPos) != null)
            return false;
        return iBlockState.getDestroySpeed(world, blockPos) != -1;
    }

    /**
     * Spawns an item in a way that it doesn't get any acceleration
     */
    public static ItemEntity spawnItemInWorld(ItemStack itemStack, Level world, BlockPos pos) {
        if (world.isClientSide) throw new IllegalArgumentException("Don't spawn items in client world");
        ItemEntity entityItem = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, itemStack);
        world.addFreshEntity(entityItem);
        entityItem.setDeltaMovement(0, 0, 0);
        return entityItem;
    }

    /**
     * Searches for a position above solid block, starting from world height. Skips replaceable blocks and leaves
     */
    public static BlockPos getPosAboveSolidBlock(Level world, BlockPos blockPos) {
        BlockState blockState = world.getBlockState(blockPos = blockPos.above(world.getHeight()));
        while (blockState.canBeReplaced() || blockState.getBlock() instanceof LeavesBlock) {
            blockState = world.getBlockState(blockPos = blockPos.below());
        }
        blockPos = blockPos.above();
        return blockPos;
    }

    public static ArrayList<Direction> getSideDirections(Direction of) {
        ArrayList<Direction> sidedirections = new ArrayList<>(5);
        switch (of) {
            case DOWN ->
                    Collections.addAll(sidedirections, Direction.UP, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH);
            case UP ->
                    Collections.addAll(sidedirections, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH);
            case EAST ->
                    Collections.addAll(sidedirections, Direction.WEST, Direction.DOWN, Direction.UP, Direction.SOUTH, Direction.NORTH);
            case WEST ->
                    Collections.addAll(sidedirections, Direction.EAST, Direction.DOWN, Direction.UP, Direction.SOUTH, Direction.NORTH);
            case SOUTH ->
                    Collections.addAll(sidedirections, Direction.NORTH, Direction.DOWN, Direction.UP, Direction.WEST, Direction.EAST);
            case NORTH ->
                    Collections.addAll(sidedirections, Direction.SOUTH, Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST);
        }
        return sidedirections;
    }

    /**
     * Tests whether one Itemstack is equal to another except size
     *
     * @return false if not equal or any is empty
     */
    public static boolean areItemTypesEqual(ItemStack one, ItemStack two) {
        if (!one.isEmpty() && !two.isEmpty()) {
            return ItemStack.isSameItemSameTags(one, two);
        }
        return false;
    }

    public static boolean areItemsEqualIngoreNbt(ItemStack one, ItemStack two) {
        if (!one.isEmpty() && !two.isEmpty()) {
            return ItemStack.isSameItem(one, two) && one.getDamageValue() == two.getDamageValue();
        }
        return false;
    }

    public static int getFuelValue(@Nonnull ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, null);
    }

    /**
     * Checks the weak power on all sides
     *
     * @param pos target position
     * @return direction from where the power is incoming
     */
    public static Direction isBlockDirectlyPowered(Level worldIn, BlockPos pos) {
        for (Direction from : Direction.values()) {
            BlockPos sidepos = pos.relative(from);
            BlockState sidestate = worldIn.getBlockState(sidepos);
            Block sideblock = sidestate.getBlock();
            if (sideblock.isSignalSource(sidestate)) {
                int p = sidestate.getDirectSignal(worldIn, sidepos, from);
                if (p > 0) {
                    return from;
                }
            }
        }
        return null;
    }

    /**
     * Same as {@link #isBlockDirectlyPowered(Level, BlockPos)} with excluded position
     *
     * @param side exception
     * @return side which recieves power
     */
    public static Direction isPoweredExceptSide(Direction side, Level world, BlockPos position) {
        for (Direction facing : Direction.values()) {
            if (side != facing) {
                BlockPos sidepos = position.relative(facing);
                BlockState sidestate = world.getBlockState(sidepos);
                Block sideblock = sidestate.getBlock();
                if (sideblock.isSignalSource(sidestate)) {
                    int p = sidestate.getDirectSignal(world, sidepos, facing);
                    if (p > 0) {
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
    public static int calculateLongestStringWidth(Collection<Component> objects) {
        int width = 0;
        for (Component s : objects) {
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
    public static boolean removeHeldItem(Player player, Item item) {

        if (!player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() == item) {
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            return true;
        } else if (!player.getOffhandItem().isEmpty() && player.getOffhandItem().getItem() == item) {
            player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
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
    public static Field getSecureField(Class<?> owner, int number) {
        Field f;
        Field[] fields = owner.getDeclaredFields();
        if (number < fields.length) {
            f = fields[number];
            if (f.getType() != owner.getEnclosingClass()) {
                f.setAccessible(true);
                return f;
            } else {
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

    public static int minutesToTicks(int minutes) {
        return secondsToTicks(minutes) * 60;
    }

    public static int hoursToTicks(int hours) {
        return minutesToTicks(hours) * 60;
    }

    public static ItemStack getHeldItem(Player playerEntity, Item item) {
        if (playerEntity.getMainHandItem().getItem() == item) {
            return playerEntity.getMainHandItem();
        } else if (playerEntity.getOffhandItem().getItem() == item) {
            return playerEntity.getOffhandItem();
        }
        return null;
    }

    public static ItemStack getHeldItem(Inventory playerInventory, Item item) {
        return getHeldItem(playerInventory.player, item);
    }

    public static InteractionHand getHandHoldingItem(Player playerEntity, Item item) {
        if (playerEntity.getMainHandItem().getItem() == item) {
            return InteractionHand.MAIN_HAND;
        } else if (playerEntity.getOffhandItem().getItem() == item) {
            return InteractionHand.OFF_HAND;
        }
        return null;
    }

    /**
     * @return slot number or -1 if not found
     */
    public static int findItemIn(IItemHandler itemHandler, ItemStack stack) {
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

    public static boolean removeBlock(BlockPos position, LevelAccessor world) {
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

    public static FriendlyByteBuf emptyBuffer() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    public static boolean isBlockIn(Block block, TagKey<Block> tagKey) {
        return ForgeRegistries.BLOCKS.tags().getTag(tagKey).contains(block);
    }

    public static boolean isItemIn(Item item, TagKey<Item> tagKey) {
        return ForgeRegistries.ITEMS.tags().getTag(tagKey).contains(item);
    }

    /**
     * Removes specified amount of item from inventory
     *
     * @param type      item
     * @param amount    to remove
     * @param container inventory
     * @return false if the container has fewer items than specified, true on success
     */
    public static boolean removeItems(Item type, int amount, Container container) {
        if (container.countItem(type) < amount)
            return false;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack next = container.getItem(i);
            if (next.getItem() == type) {
                while (amount > 0) {
                    next.shrink(1);
                    amount--;
                    if (next.isEmpty())
                        break;
                }
                if (amount == 0)
                    return true;
            }
        }
        return true;
    }

    /**
     * Removes specified amount of item from item handler
     *
     * @param item        type
     * @param amount      to remove
     * @param itemHandler handler
     * @return false if the handler has fewer items than specified, true on success
     */
    public static boolean removeItems(Item item, int amount, IItemHandler itemHandler) {
        int present = 0;
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack itemStack = itemHandler.getStackInSlot(slot);
            if (itemStack.getItem() == item) {
                present += itemStack.getCount();
            }
            if (present >= amount)
                break;
        }

        if (present < amount)
            return false;

        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack itemStack = itemHandler.getStackInSlot(slot);
            if (itemStack.getItem() == item) {
                while (amount > 0) {
                    itemStack.shrink(1);
                    amount--;
                    if (itemStack.isEmpty())
                        break;
                }
                if (amount == 0)
                    return true;
            }
        }
        return true;
    }

    /**
     * For usage in entity type registration
     */
    @SuppressWarnings("rawtypes")
    public static EntityType cast(EntityType<Entity> entityType) {
        return entityType;
    }

    public static Direction getLookDirectionOf(LivingEntity livingEntity) {
        if (livingEntity.getXRot() > 45)
            return Direction.DOWN;
        else if (livingEntity.getXRot() < -45) {
            return Direction.UP;
        }
        return livingEntity.getDirection();
    }
}
