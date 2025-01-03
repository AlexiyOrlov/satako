package dev.buildtool.satako.blocks;

import dev.buildtool.satako.platform.Services;
import dev.buildtool.satako.platform.services.IPlatformHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class Block2 extends Block {
    private boolean dropItems=true;
    public Block2(Properties properties) {
        super(properties);
    }

    public Block2(Properties p_49795_, boolean dropItems) {
        super(p_49795_);
        this.dropItems = dropItems;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (dropItems && !state.is(newState.getBlock()) && state.hasBlockEntity()) {
            Services.PLATFORM.dropItemsIfAny(worldIn, pos);
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
