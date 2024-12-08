package dev.buildtool.satako;

import dev.buildtool.satako.test.TestBlock;
import dev.buildtool.satako.test.TestContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created on 11/30/19.
 */
@Mod(Satako.ID)
public class Satako {
    public static final String ID = "satako";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, ID);
    private static final DeferredHolder<Block, Block> TEST_BLOCK = BLOCKS.register("test_block", () -> new TestBlock(BlockBehaviour.Properties.of()));

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ID);
    public static ModConfigSpec.BooleanValue enableInfoTooltip;
    static {
        ITEMS.register("test_block", () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties().stacksTo(1)));
    }

    private static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, ID);
    public static final DeferredHolder<MenuType<?>, MenuType<TestContainer>> TEST_CONTAINER = CONTAINER_TYPES.register("test_block", () -> new MenuType<>(TestContainer::new, FeatureFlags.DEFAULT_FLAGS));
    public static boolean jei;
    public static Logger LOGGER = LogManager.getLogger(ID);
    public Satako(IEventBus eventBus, ModContainer modContainer) {
        CONTAINER_TYPES.register(eventBus);
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);

        if(ModList.get().isLoaded("jei"))
        {
            jei=true;
        }

        modContainer.registerConfig(ModConfig.Type.CLIENT,new ModConfigSpec.Builder().configure(builder -> {
            enableInfoTooltip= builder.define("Enable item info tooltip",true);
            return builder.build();
        }).getRight());

        LOGGER.info("Satako loaded");
    }
}
