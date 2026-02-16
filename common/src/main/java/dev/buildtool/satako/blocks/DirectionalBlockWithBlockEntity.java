package dev.buildtool.satako.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class DirectionalBlockWithBlockEntity extends BlockDirectional implements EntityBlock {
    public DirectionalBlockWithBlockEntity(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? this::clientTick : this::serverTick;
    }

    protected <B extends BlockEntity> void clientTick(Level level,BlockPos pos,BlockState blockState,B be)
    {

    }

    protected <B extends BlockEntity> void serverTick(Level level,BlockPos pos,BlockState blockState,B be)
    {

    }
}
