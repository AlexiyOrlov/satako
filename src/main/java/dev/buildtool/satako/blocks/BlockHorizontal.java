package dev.buildtool.satako.blocks;

import dev.buildtool.satako.SCSync;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class BlockHorizontal extends net.minecraft.block.HorizontalBlock implements SCSync
{
    public BlockHorizontal(Properties properties)
    {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext itemUseContext)
    {
        Direction direction = itemUseContext.getHorizontalDirection();
        return defaultBlockState().setValue(FACING, direction.getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateBuilder)
    {
        super.createBlockStateDefinition(stateBuilder);
        stateBuilder.add(FACING);
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
