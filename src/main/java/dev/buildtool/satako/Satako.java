package dev.buildtool.satako;

import dev.buildtool.satako.test.TestBlock;
import dev.buildtool.satako.test.TestContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

;

/**
 * Created on 11/30/19.
 */
@Mod(Satako.ID)
public class Satako {
    public static final String ID = "satako";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ID);
    private static final RegistryObject<Block> TEST_BLOCK = BLOCKS.register("test_block", () -> new TestBlock(Block.Properties.of()));

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    public static ForgeConfigSpec.BooleanValue enableInfoTooltip;
    static {
        ITEMS.register("test_block", () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties().stacksTo(1)));
    }

    private static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ID);
    public static final RegistryObject<MenuType<TestContainer>> TEST_CONTAINER = CONTAINER_TYPES.register("test_block", () -> IForgeMenuType.create((windowId, inv, data) -> new TestContainer(windowId, inv)));
    public static boolean jei;
    public static Logger LOGGER = LogManager.getLogger(ID);
    public Satako() {
        MinecraftForge.EVENT_BUS.register(this);

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        CONTAINER_TYPES.register(eventBus);
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);

        if(ModList.get().isLoaded("jei"))
        {
            jei=true;
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT,new ForgeConfigSpec.Builder().configure(builder -> {
            enableInfoTooltip= builder.define("Enable item info tooltip",true);
            return builder.build();
        }).getRight());

        LOGGER.info("Satako loaded");
    }
}
