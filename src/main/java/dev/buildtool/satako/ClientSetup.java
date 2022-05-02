package dev.buildtool.satako;

import dev.buildtool.satako.test.TestContainer;
import dev.buildtool.satako.test.TestScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent clientSetupEvent)
    {
        ScreenManager.register(Satako.TEST_CONTAINER.get(),(ScreenManager.IScreenFactory<TestContainer, TestScreen>) TestScreen::new);
    }
}
