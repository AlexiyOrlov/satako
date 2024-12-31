package dev.buildtool.satako;

import dev.buildtool.satako.platform.services.IPlatformHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;

public class FabricPlatformHooks implements IPlatformHooks {

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void dropItemsIfAny(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof Container inventory) {
            Containers.dropContents(level, pos, inventory);
        }
    }

    @Override
    public TextureAtlasSprite getFluidTexture(Fluid fluid, boolean still) {
        TextureAtlasSprite[] fluidSprites = FluidVariantRendering.getSprites(FluidVariant.of(fluid));
        return still ? fluidSprites[0] : fluidSprites[1];
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType()== EnvType.CLIENT;
    }

    @Override
    public boolean isServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }

    @Override
    public MenuType<?> getTestMenu() {
        return SatakoFabric.testMenu;
    }

    @Override
    public boolean isFabric() {
        return true;
    }

    @Override
    public boolean isNeoforge() {
        return false;
    }
}
