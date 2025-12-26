package dev.buildtool.satako;


import dev.buildtool.satako.test.TestBlock;
import dev.buildtool.satako.test.TestMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;

@Mod(Satako.ID)
public class SatakoNeoforge {
    public static HashMap<Fluid, FluidStack> FLUID_STACK_CACHE = new HashMap<>();
    static final  DeferredRegister<MenuType<?>> menus=DeferredRegister.create(Registries.MENU,Satako.ID);

    public static final DeferredHolder<MenuType<?>,MenuType<TestMenu>> TEST_MENU=menus.register("test_menu",() -> IMenuTypeExtension.create(TestMenu::new));
    public SatakoNeoforge(IEventBus eventBus) {
        Satako.run();
        DeferredRegister<Block> blocks=DeferredRegister.createBlocks(Satako.ID);
        DeferredHolder<Block,Block> testBlock= blocks.register("test_block",() -> new TestBlock(BlockBehaviour.Properties.of()));
        blocks.register(eventBus);
        menus.register(eventBus);
        DeferredRegister<Item> items=DeferredRegister.createItems(Satako.ID);
        items.register("test_item",() -> new BlockItem(testBlock.get(),new Item.Properties().stacksTo(1)));
        items.register(eventBus);
    }
}