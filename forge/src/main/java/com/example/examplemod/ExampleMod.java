package com.example.examplemod;

import dev.buildtool.satako.Satako;
import net.minecraftforge.fml.common.Mod;

@Mod(Satako.ID)
public class ExampleMod {

    public ExampleMod() {

        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        Satako.LOG.info("Hello Forge world!");
        Satako.run();

    }
}