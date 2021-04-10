package dev.buildtool.satako.blocks;

import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    public List<ItemStack> onSheared(@Nullable PlayerEntity player,  ItemStack item, World world, BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack(this));
    }


}
