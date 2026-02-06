package dev.buildtool.satako;

import io.netty.buffer.Unpooled;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;

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

    public static boolean isSurvivalPlayer(Entity entity) {
        return entity instanceof Player && isPlayerInSurvival((Player) entity);
    }

    public static float getDefaultXRightLimbRotation(float limbSwing, float swingAmount) {
        return Mth.cos((float) (limbSwing + Math.PI)) * swingAmount;
    }

    public static float getDefaultXLeftLimbRotation(float limbSwing, float swingAmount) {
        return Mth.cos(limbSwing) * swingAmount;
    }

    /**
     * Angle Y or Z
     */
    public static float getDefaultHeadYaw(float netYaw) {
        return netYaw * 0.017453292F;
    }

    /**
     * Angle X
     */
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

    //    /**
//     * Not sure whether to use this function in {@link Block#neighborChanged(BlockState, Level, BlockPos, Block, BlockPos, boolean)} or {@link Block#tick(BlockState, ServerLevel, BlockPos, RandomSource)}
//     */
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
    public static BlockPos getBlockPositionFrom(ChunkPos chunkPos) {
        return new BlockPos(chunkPos.x << 4, 0, chunkPos.z << 4);
    }

    public static float degreesToRadians(float degrees) {
        return (float) (degrees * Math.PI / 180);
    }

    public static float translateToXcoord(float degrees) {
        if (degrees == 90 || degrees == -90) {
            return 0;
        }
        if (degrees == 180 || degrees == -180) {
            return -1;
        }
        return Mth.cos(degreesToRadians(degrees));
    }

    public static float translateToZcoord(float degrees) {
        if (degrees == 90 || degrees == -90) {
            return 1;
        }
        if (degrees == 180 || degrees == -180) {
            return 0;
        }
        return Mth.sin(degreesToRadians(degrees));
    }

//    /**
//     * @return true if something can generate without cascading at that position
//     */
//    public static boolean canGenerateWithoutCascade(WorldGenLevel world, BlockPos blockPos) {
//        ChunkPos chunkPos = new ChunkPos(blockPos);
//        int cx = chunkPos.x;
//        int cz = chunkPos.z;
//        ChunkStatus empty = ChunkStatus.EMPTY;
//        return world.getChunk(cx, cz).getStatus() != empty && world.getChunk(cx + 1, cz).getStatus() != empty &&
//                world.getChunk(cx - 1, cz).getStatus() != empty
//                && world.getChunk(cx, cz + 1).getStatus() != empty && world.getChunk(cx, cz - 1).getStatus() != empty;
//    }

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
            if (counter > 10) {
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
        if (uuid != null && !uuid.equals(Util.NIL_UUID)) {
            nbtTagCompound.putUUID(key, uuid);
            return true;
        }
        return false;
    }

    public static UUID readUUID(CompoundTag nbtTagCompound, String key) {
        UUID uuid = nbtTagCompound.getUUID(key);
        return uuid.equals(Util.NIL_UUID) ? null : uuid;
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
            if (block == of) {
                if (!positions.contains(side)) {
                    getConnectedBlocks(of, ArrayUtils.removeElement(Direction.values(), checkedSide.getOpposite()), side, world, positions, limit);
                } else {
                    positions.add(side);
                    if (positions.size() >= limit) {
                        return positions;
                    }
                }
            }
        }

        return positions;
    }

    public static boolean isEmpty(Collection<ItemStack> itemStackCollection) {
        for (ItemStack itemStack : itemStackCollection) {
            if (!itemStack.isEmpty()) {
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
    public static boolean isSuperClass(Class<?> subject, Object of) {
        if (of.getClass() == Class.class) {
            return subject.isAssignableFrom((Class<?>) of);
        }
        return subject.isInstance(of);
    }

    /**
     * @return block positions inside the bounding box
     */
    public static List<BlockPos> bbToPositions(AABB axisAlignedBB) {
        List<BlockPos> positions = new ArrayList<>();
        for (double x = axisAlignedBB.minX; x < axisAlignedBB.maxX; x++) {
            for (double y = axisAlignedBB.minY; y < axisAlignedBB.maxY; y++) {
                for (double z = axisAlignedBB.minZ; z < axisAlignedBB.maxZ; z++) {
                    positions.add(new BlockPos((int) x, (int) y, (int) z));
                }
            }
        }
        return positions;
    }

    public static Direction randomHorizontalFacing() {
        return Constants.HORIZONTALS[Methods.RANDOMGENERATOR.nextInt(Constants.HORIZONTALS.length)];
    }

    /**
     * @return true if there is no tile entity or unbreakable block
     */
    public static boolean canReplaceBlock(BlockPos blockPos, Level world) {
        BlockState iBlockState = world.getBlockState(blockPos);
        if (iBlockState.hasBlockEntity())
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

    public static ArrayList<Direction> getOtherDirections(Direction of) {
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
            return ItemStack.isSameItemSameComponents(one, two);
        }
        return false;
    }

    /**
     * @return true if same items and durability and not empty
     */
    public static boolean areItemsEqualIgnoreNbt(ItemStack one, ItemStack two) {
        if (!one.isEmpty() && !two.isEmpty()) {
            return ItemStack.isSameItem(one, two) && one.getDamageValue() == two.getDamageValue();
        }
        return false;
    }

    public static int getFuelValue(@NotNull ItemStack stack) {
        return AbstractFurnaceBlockEntity.getFuel().getOrDefault(stack.getItem(), 0);
    }

//    /**
//     * Checks the weak power on all sides
//     *
//     * @param pos target position
//     * @return direction from where the power is incoming
//     */
//    public static Direction isBlockDirectlyPowered(Level worldIn, BlockPos pos) {
//        for (Direction from : Direction.values()) {
//            BlockPos sidepos = pos.relative(from);
//            BlockState sidestate = worldIn.getBlockState(sidepos);
//            Block sideblock = sidestate.getBlock();
//            if (sideblock.is(sidestate)) {
//                int p = sidestate.getDirectSignal(worldIn, sidepos, from);
//                if (p > 0) {
//                    return from;
//                }
//            }
//        }
//        return null;
//    }

//    /**
//     * Same as {@link #isBlockDirectlyPowered(Level, BlockPos)} with excluded position
//     *
//     * @param side exception
//     * @return side which recieves power
//     */
//    public static Direction isPoweredExceptSide(Direction side, Level world, BlockPos position) {
//        for (Direction facing : Direction.values()) {
//            if (side != facing) {
//                BlockPos sidepos = position.relative(facing);
//                BlockState sidestate = world.getBlockState(sidepos);
//                Block sideblock = sidestate.getBlock();
//                if (sideblock.isSignalSource(sidestate)) {
//                    int p = sidestate.getDirectSignal(world, sidepos, facing);
//                    if (p > 0) {
//                        return facing;
//                    }
//                }
//            }
//        }
//        return null;
//    }

    /**
     * Reliably gets a translated block name
     */
    public static String getBlockName(BlockState blockState) {
        if (blockState.getBlock() instanceof AirBlock) {
            return "Air";
        }
        ItemStack itemStack = new ItemStack(blockState.getBlock(), 1);
        if (!itemStack.isEmpty()) {
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
     * @return a 1-sized stack. Can be empty
     */
    public static ItemStack getStackFromBlockState(BlockState blockState) {

        ItemStack ds;
        ds = new ItemStack(blockState.getBlock(), 1);
        if (ds.isEmpty()) {
            //for blocks that don't have associated ItemBlock
            Item b = Item.byBlock(blockState.getBlock());
            if (b instanceof AirItem) {
//                ds = new ItemStack(blockState.getBlock(),blockState.getBlock().quantityDropped(blockState,0,randomgenerator));
            } else {
                ds = new ItemStack(b);
            }
        }
        return ds;
    }

    public static Method getAnyMethod(Class<?> owner, String name, Class<?>... parameterTypes) {
        Method m = null;
        try {
            m = owner.getDeclaredMethod(name, parameterTypes);
            m.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return m;
    }

    public static int ticksToSeconds(int ticks) {
        return ticks / 20;
    }

    public static int ticksToMinutes(int ticks) {
        return ticksToSeconds(ticks) / 60;
    }

    public static int ticksToHours(int ticks) {
        return ticksToMinutes(ticks) / 60;
    }

    public static int secondsToTicks(int seconds) {
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

    public static Rotation directionToRotation(Direction direction) {
        switch (direction) {
            case NORTH:
                break;
            case SOUTH:
                return Rotation.CLOCKWISE_180;
            case EAST:
                return Rotation.CLOCKWISE_90;
            case WEST:
                return Rotation.COUNTERCLOCKWISE_90;
        }
        return Rotation.NONE;
    }

    public static FriendlyByteBuf emptyBuffer() {
        return new FriendlyByteBuf(Unpooled.buffer());
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

    public static Direction getLookDirectionOf(LivingEntity livingEntity) {
        if (livingEntity.getXRot() > 45)
            return Direction.DOWN;
        else if (livingEntity.getXRot() < -45) {
            return Direction.UP;
        }
        return livingEntity.getDirection();
    }

    public static boolean contains(Item item, ItemContainer handler) {
        for (int i = 0; i < handler.getSlotCount(); ++i) {
            ItemStack next = handler.getStackInSlot(i);
            if (next.is(item)) {
                return true;
            }
        }

        return false;
    }

    public static IntegerColor getCachedColor(int ofColor) {
        return Constants.COLOR_CACHE.computeIfAbsent(ofColor, IntegerColor::new);
    }

    public static ItemStack getCachedStack(Item item) {
        return Constants.ITEM_CACHE.computeIfAbsent(item, ItemStack::new);
    }

    public static ArrayList<Direction> getSideDirections(Direction of) {
        ArrayList<Direction> sidedirections = new ArrayList<>(5);
        switch (of) {
            case DOWN, UP ->
                    Collections.addAll(sidedirections, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH);
            case EAST, WEST ->
                    Collections.addAll(sidedirections, Direction.DOWN, Direction.UP, Direction.SOUTH, Direction.NORTH);
            case SOUTH, NORTH ->
                    Collections.addAll(sidedirections, Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST);
        }
        return sidedirections;
    }

    public static String getFriendlyDirectionName(Direction direction) {
        switch (direction) {
            case UP -> {
                return "Top";
            }
            case DOWN -> {
                return "Bottom";
            }
            default -> {
                return StringUtils.capitalize(direction.getName());
            }
        }
    }

    /***
     * @return rotated voxel shape
     */
    public static VoxelShape rotateY(VoxelShape shape, int rotation) {
        List<VoxelShape> rotatedShapes = new ArrayList<>();

        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            x1 = (x1 * 16) - 8;
            x2 = (x2 * 16) - 8;
            z1 = (z1 * 16) - 8;
            z2 = (z2 * 16) - 8;

            switch (rotation) {
                case 90 -> rotatedShapes.add(boxSafe(8 - z1, y1 * 16, 8 + x1, 8 - z2, y2 * 16, 8 + x2));
                case 180 -> rotatedShapes.add(boxSafe(8 - x1, y1 * 16, 8 - z1, 8 - x2, y2 * 16, 8 - z2));
                case 270 -> rotatedShapes.add(boxSafe(8 + z1, y1 * 16, 8 - x1, 8 + z2, y2 * 16, 8 - x2));
                default ->
                        throw new IllegalArgumentException("invalid rotation " + rotation + " (must be 90,180 or 270)");
            }
        });

        return rotatedShapes.stream().reduce((v1, v2) -> Shapes.joinUnoptimized(v1, v2, BooleanOp.OR)).orElse(shape).optimize();
    }

    /**
     * @return rotated voxel shape
     */
    public static VoxelShape rotateX(VoxelShape shape, int rotation) {
        List<VoxelShape> rotatedShapes = new ArrayList<>();

        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            y1 = (y1 * 16) - 8;
            y2 = (y2 * 16) - 8;
            z1 = (z1 * 16) - 8;
            z2 = (z2 * 16) - 8;

            switch (rotation) {
                case 90 -> rotatedShapes.add(boxSafe(x1 * 16, 8 - z1, 8 + y1, x2 * 16, 8 - z2, 8 + y2));
                case 180 -> rotatedShapes.add(boxSafe(x1 * 16, 8 - z1, 8 - y1, x2 * 16, 8 - z2, 8 - y2));
                case 270 -> rotatedShapes.add(boxSafe(x1 * 16, 8 + z1, 8 - y1, x2 * 16, 8 + z2, 8 - y2));
                default ->
                        throw new IllegalArgumentException("invalid rotation " + rotation + " (must be 90,180 or 270)");
            }
        });

        return rotatedShapes.stream().reduce((v1, v2) -> Shapes.joinUnoptimized(v1, v2, BooleanOp.OR)).orElse(shape).optimize();
    }

    private static VoxelShape boxSafe(double pMinX, double pMinY, double pMinZ, double pMaxX, double pMaxY, double pMaxZ) {
        double x1 = Math.min(pMinX, pMaxX);
        double x2 = Math.max(pMinX, pMaxX);
        double y1 = Math.min(pMinY, pMaxY);
        double y2 = Math.max(pMinY, pMaxY);
        double z1 = Math.min(pMinZ, pMaxZ);
        double z2 = Math.max(pMinZ, pMaxZ);
        return Block.box(x1, y1, z1, x2, y2, z2);
    }

    public static ItemStack findItem(Item item, ItemContainer handler) {
        for (int i = 0; i < handler.getSlotCount(); ++i) {
            ItemStack next = handler.getStackInSlot(i);
            if (next.is(item)) {
                return next;
            }
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack insertItemStacked(ItemContainer inventory, ItemStack stack, boolean simulate) {
        if (inventory == null || stack.isEmpty())
            return stack;

        // not stackable -> just insert into a new slot
        if (!stack.isStackable()) {
            return insertItem(inventory, stack, simulate);
        }

        int sizeInventory = inventory.getSlotCount();

        // go through the inventory and try to fill up already existing items
        for (int i = 0; i < sizeInventory; i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            if (ItemStack.isSameItemSameComponents(slot, stack)) {
                stack = inventory.insertItem(i, stack, simulate);

                if (stack.isEmpty()) {
                    break;
                }
            }
        }

        // insert remainder into empty slots
        if (!stack.isEmpty()) {
            // find empty slot
            for (int i = 0; i < sizeInventory; i++) {
                if (inventory.getStackInSlot(i).isEmpty()) {
                    stack = inventory.insertItem(i, stack, simulate);
                    if (stack.isEmpty()) {
                        break;
                    }
                }
            }
        }

        return stack;
    }

    public static ItemStack insertItem(ItemContainer dest, ItemStack stack, boolean simulate) {
        if (dest == null || stack.isEmpty())
            return stack;

        for (int i = 0; i < dest.getSlotCount(); i++) {
            stack = dest.insertItem(i, stack, simulate);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        return stack;
    }

    /**
     * Extracts an itemstack
     *
     * @return extracted ItemStack
     */
    public static ItemStack extractItems(ItemContainer itemHandler, ItemStack itemStack, boolean simulate) {
        for (int slot = 0; slot < itemHandler.getSlotCount(); slot++) {
            ItemStack presentstack = itemHandler.getStackInSlot(slot);
            if (Functions.areItemTypesEqual(itemStack, presentstack)) {
                return itemHandler.extractItem(slot, itemStack.getCount(), simulate);
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Method for casting objects when needed
     */
    @SuppressWarnings("unchecked")
    public static <E>  E cast(Object o)
    {
        return (E) o;
    }

    @SuppressWarnings("rawtypes")
    public static EntityType cast(EntityType<?> e)
    {
        return e;
    }
}
