package dev.buildtool.satako.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Created on 6/26/18.
 */
public abstract class BlockBasicLeaves extends LeavesBlock
{

    public BlockBasicLeaves(Properties blockProperties)
    {
        super(blockProperties);
    }


    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nullable Player player, ItemStack item, Level world, BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack(this));
    }


}
