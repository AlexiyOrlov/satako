package dev.buildtool.satako.integration;

import dev.buildtool.satako.Satako;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IIngredientListOverlay;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEI implements IModPlugin {
    public static IIngredientListOverlay ingredientListOverlay;
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Satako.ID,"jei_compat");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        ingredientListOverlay=jeiRuntime.getIngredientListOverlay();
    }
}
