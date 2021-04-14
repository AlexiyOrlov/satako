package dev.buildtool.satako.blocks;

import dev.buildtool.satako.SCSync;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

/**
 * Created on 5/29/18.
 */
public class BlockDirectional extends net.minecraft.block.DirectionalBlock implements SCSync
{

    public BlockDirectional(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(FACING);
    }

    /**
     * Correct if {@link net.minecraftforge.common.extensions.IForgeBlockState#rotate(IWorld, BlockPos, Rotation)} is used
     */
    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if(hasTileEntity(state))
        {
            TileEntity tileEntity=worldIn.getBlockEntity(pos);
            tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
                for (int i = 0; i < iItemHandler.getSlots(); i++) {
                    ItemStack stack=iItemHandler.getStackInSlot(i);
                    if(!stack.isEmpty())
                        InventoryHelper.dropItemStack(worldIn,pos.getX()+0.5,pos.getY(),pos.getZ()+0.5,stack);
                }
            });
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public boolean triggerEvent(BlockState state, World worldIn, BlockPos pos, int id, int param) {
        return onDataReceived(state,worldIn,pos,(byte) id, (byte) param);
    }

    @Override
    public boolean onDataReceived(BlockState state, World worldIn, BlockPos pos, byte id, byte value) {
        return hasTileEntity(state) && worldIn.getBlockEntity(pos).triggerEvent(id, value);
    }
}
