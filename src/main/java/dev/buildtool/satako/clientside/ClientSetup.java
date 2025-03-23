package dev.buildtool.satako.clientside;

import dev.buildtool.satako.Satako;
import dev.buildtool.satako.test.TestScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent clientSetupEvent)
    {
        MenuScreens.register(Satako.TEST_CONTAINER.get(), TestScreen::new);
    }
}
