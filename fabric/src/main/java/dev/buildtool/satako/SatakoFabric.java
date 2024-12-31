package dev.buildtool.satako;

import dev.buildtool.satako.test.TestBlock;
import dev.buildtool.satako.test.TestMenu;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SatakoFabric implements ModInitializer {
    static MenuType<TestMenu> testMenu= Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(Satako.ID,"test_menu"),new MenuType<>((i, inventory) -> new TestMenu(i,inventory,null), FeatureFlags.DEFAULT_FLAGS));
    @Override
    public void onInitialize() {
        Satako.run();
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> Satako.registerCommands(commandDispatcher));
        Block testBlock=Registry.register(BuiltInRegistries.BLOCK,ResourceLocation.fromNamespaceAndPath(Satako.ID,"test_block"),new TestBlock(BlockBehaviour.Properties.of()));
        Registry.register(BuiltInRegistries.ITEM,ResourceLocation.fromNamespaceAndPath(Satako.ID,"test_item"),new BlockItem(testBlock,new Item.Properties().stacksTo(1)));
    }
}
