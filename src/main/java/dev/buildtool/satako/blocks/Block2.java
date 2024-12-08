package dev.buildtool.satako.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

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
        if (dropsItems && !state.is(newState.getBlock()) && state.hasBlockEntity()) {

            IItemHandler itemHandler= worldIn.getCapability(Capabilities.ItemHandler.BLOCK,pos,null);
            if(itemHandler!=null) {
                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    ItemStack stack = itemHandler.getStackInSlot(i);
                    if (!stack.isEmpty())
                        Containers.dropItemStack(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);
                }
            }
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
