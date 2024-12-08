package dev.buildtool.satako.test;

import dev.buildtool.satako.blocks.Block2;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class TestBlock extends Block2 implements MenuProvider {
    public TestBlock(BlockBehaviour.Properties properties) {
        super(properties, false);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player instanceof ServerPlayer serverPlayer)
            serverPlayer.openMenu(this);
        return InteractionResult.SUCCESS;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Test block");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory playerInventory, Player playerEntity) {
        return new TestContainer(p_createMenu_1_, playerInventory);
    }
}
