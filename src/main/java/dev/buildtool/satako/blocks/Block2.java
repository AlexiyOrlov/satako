package dev.buildtool.satako.blocks;

import dev.buildtool.satako.SCSync;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

/**
 *  Forwards block events to its tile entity.
 *  Drops items if it has a tile entity with
 * {@link CapabilityItemHandler#ITEM_HANDLER_CAPABILITY}.
 */
public class Block2 extends Block implements SCSync
{
    public Block2(Properties properties, boolean dropsItems) {
        super(properties);
        this.dropsItems = dropsItems;
    }

    boolean dropsItems=true;
    public Block2(Properties properties) {
        super(properties);
    }


    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if(dropsItems && hasTileEntity(state))
        {
            TileEntity tileEntity=worldIn.getBlockEntity(pos);
            tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
                for (int i = 0; i < iItemHandler.getSlots(); i++) {
                    ItemStack stack = iItemHandler.getStackInSlot(i);
                    if (!stack.isEmpty())
                        InventoryHelper.dropItemStack(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);
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
    public boolean triggerEvent(BlockState state, World worldIn, BlockPos pos, int id, int param) {
        return onDataReceived(state, worldIn, pos, (byte) id, (byte) param);
    }

    @Override
    public boolean onDataReceived(BlockState state, World worldIn, BlockPos pos, byte id, byte value) {
        return hasTileEntity(state) && worldIn.getBlockEntity(pos).triggerEvent(id, value);
    }
}
