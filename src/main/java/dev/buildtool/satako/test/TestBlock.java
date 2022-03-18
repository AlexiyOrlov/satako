package dev.buildtool.satako.test;

import dev.buildtool.satako.blocks.Block2;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class TestBlock extends Block2 implements INamedContainerProvider {
    public TestBlock(Properties properties) {
        super(properties, false);
    }

    @Override
    public ActionResultType use(BlockState p_225533_1_, World world, BlockPos p_225533_3_, PlayerEntity playerEntity, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
        if(playerEntity instanceof ServerPlayerEntity)
            NetworkHooks.openGui((ServerPlayerEntity) playerEntity,this);
        return ActionResultType.SUCCESS;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Test block");
    }

    @Nullable
    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new TestContainer(p_createMenu_1_,playerInventory);
    }
}
