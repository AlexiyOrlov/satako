package dev.buildtool.satako.test;

//import com.github.wintersteve25.tau.renderer.ScreenUIRenderer;
import dev.buildtool.satako.blocks.Block2;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class TestBlock extends Block2 implements MenuProvider {
    public TestBlock(BlockBehaviour.Properties properties) {
        super(properties, false);
    }

    @Override
    public InteractionResult use(BlockState p_225533_1_, Level world, BlockPos p_225533_3_, Player playerEntity, InteractionHand p_225533_5_, BlockHitResult p_225533_6_) {
        if (playerEntity instanceof ServerPlayer)
            NetworkHooks.openScreen((ServerPlayer) playerEntity, this);
//        if(world.isClientSide)
//            screen();
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

    @OnlyIn(Dist.CLIENT)
    private void screen()
    {
//        Minecraft.getInstance().setScreen(new ScreenUIRenderer(new TestTauScreen()));
    }
}
