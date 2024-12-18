package dev.buildtool.satako;

import dev.buildtool.satako.test.TestClientScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.function.Supplier;

@Mod(value = Satako.ID,dist = Dist.CLIENT)
public class SatakoClient {
    public SatakoClient(IEventBus bus, ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (Supplier<IConfigScreenFactory>) TestClientScreen.Factory::new);
    }
}
