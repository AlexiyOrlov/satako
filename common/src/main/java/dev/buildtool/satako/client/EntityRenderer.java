package dev.buildtool.satako.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class EntityRenderer<E extends LivingEntity, M extends EntityModel<E>> extends LivingEntityRenderer<E, M> {

    private final ResourceLocation texture;

    /**
     *
     * @param textureName name without .png
     */
    public EntityRenderer(EntityRendererProvider.Context rendererManager, M entityModelIn, String mod, String textureName, float shadowSizeIn) {
        super(rendererManager, entityModelIn, shadowSizeIn);
        texture = ResourceLocation.fromNamespaceAndPath(mod, "textures/entity/" + textureName + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(E entity) {
        return texture;
    }

    @Override
    protected boolean shouldShowName(E entity) {
        return false;
    }
}
