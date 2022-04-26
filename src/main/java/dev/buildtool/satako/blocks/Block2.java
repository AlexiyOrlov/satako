package dev.buildtool.satako.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.CapabilityItemHandler;

/**
 * Forwards block events to its tile entity.
 * Drops items if it has a tile entity with
 * {@link CapabilityItemHandler#ITEM_HANDLER_CAPABILITY}.
 */
public class Block2 extends Block {
    public Block2(Properties properties, boolean dropsItems) {
        super(properties);
        this.dropsItems = dropsItems;
    }

    boolean dropsItems = true;

    public Block2(Properties properties) {
        super(properties);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (dropsItems && state.hasBlockEntity()) {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
                for (int i = 0; i < iItemHandler.getSlots(); i++) {
                    ItemStack stack = iItemHandler.getStackInSlot(i);
                    if (!stack.isEmpty())
                        Containers.dropItemStack(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);
                }
            });
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    /**
     * Sends and event with type and value limited to {@link Byte#MAX_VALUE} to server, and to client if
     * required
     *
     * @param id    type
     * @param param value
     * @return whether the event should be sent to a client
     */
    @Override
    public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
        return state.hasBlockEntity() && worldIn.getBlockEntity(pos).triggerEvent(id, param);
    }
}
