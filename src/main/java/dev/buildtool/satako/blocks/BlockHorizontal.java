package dev.buildtool.satako.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class BlockHorizontal extends HorizontalDirectionalBlock {
    public BlockHorizontal(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext itemUseContext) {
        Direction direction = itemUseContext.getHorizontalDirection();
        return defaultBlockState().setValue(FACING, direction.getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        super.createBlockStateDefinition(stateBuilder);
        stateBuilder.add(FACING);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && state.hasBlockEntity()) {
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

    @Override
    public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
        return state.hasBlockEntity() && worldIn.getBlockEntity(pos).triggerEvent(id, param);
    }

    /**
     * Drop itself by default
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder builder) {
        if (builder.getLevel().getServer().getLootTables().get(getLootTable()).getLootTableId() == null) {
            return Collections.singletonList(new ItemStack(this));
        }
        return super.getDrops(p_60537_, builder);
    }
}
