package dev.buildtool.satako;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.stream.Collectors;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


@Mod.EventBusSubscriber
public class Commands {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent registerCommandsEvent) {

        CommandDispatcher<CommandSourceStack> commandDispatcher = registerCommandsEvent.getDispatcher();
        RootCommandNode<CommandSourceStack> rootCommandNode = commandDispatcher.getRoot();
        LiteralArgumentBuilder<CommandSourceStack> summon2 = literal("summon2").requires(commandSource -> commandSource.hasPermission(2));

        SuggestionProvider<CommandSourceStack> namespaces = (context, builder) -> SharedSuggestionProvider.suggest(ForgeRegistries.ENTITIES.getValues().stream().map(entityType -> entityType.getRegistryName().getNamespace()), builder);
        SuggestionProvider<CommandSourceStack> entities = (context, builder) -> SharedSuggestionProvider.suggest(ForgeRegistries.ENTITIES.getValues().stream().filter(entityType -> context.getArgument("namespace", String.class).equals(entityType.getRegistryName().getNamespace())).map(entityType -> entityType.getRegistryName().getPath()), builder);
        RequiredArgumentBuilder<CommandSourceStack, String> namespace = argument("namespace", StringArgumentType.string()).suggests(namespaces);
        RequiredArgumentBuilder<CommandSourceStack, String> entityName = argument("entity", StringArgumentType.string()).suggests(entities);
        entityName.executes(context -> {
            ResourceLocation resourceLocation = new ResourceLocation(context.getArgument("namespace", String.class), context.getArgument("entity", String.class));
            CommandSourceStack commandSource = context.getSource();
            return summonEntity(commandSource, commandSource.getPosition().add(0.5, 0, 0.5), resourceLocation);
        });

        RequiredArgumentBuilder<CommandSourceStack, Coordinates> position = argument("position", Vec3Argument.vec3(true));
        position.executes(context -> {
            ResourceLocation resourceLocation = new ResourceLocation(context.getArgument("namespace", String.class), context.getArgument("entity", String.class));
            CommandSourceStack commandSource = context.getSource();
            Vec3 vector3d = Vec3Argument.getVec3(context, "position");
            return summonEntity(commandSource, vector3d, resourceLocation);
        });

        LiteralCommandNode<CommandSourceStack> built = summon2.build();
        ArgumentCommandNode<CommandSourceStack, String> domain = namespace.build();
        ArgumentCommandNode<CommandSourceStack, String> name = entityName.build();

        built.addChild(domain);
        domain.addChild(name);
        name.addChild(position.build());
        rootCommandNode.addChild(built);

        //fill 2
        SuggestionProvider<CommandSourceStack> blockMods = (context, builder) -> SharedSuggestionProvider.suggest(ForgeRegistries.BLOCKS.getKeys().stream().map(ResourceLocation::getNamespace), builder);
        SuggestionProvider<CommandSourceStack> blockNames = (context, builder) -> SharedSuggestionProvider.suggest(ForgeRegistries.BLOCKS.getKeys().stream().filter(resourceLocation -> resourceLocation.getNamespace().equals(context.getArgument("mod", String.class))).map(ResourceLocation::getPath), builder);

//        LiteralArgumentBuilder<CommandSourceStack> fill2 = literal("fill2").requires(commandSource -> commandSource.hasPermission(2));
//        RequiredArgumentBuilder<CommandSourceStack, Coordinates> from = argument("from", BlockPosArgument.blockPos());
//        RequiredArgumentBuilder<CommandSourceStack, Coordinates> to = argument("to", BlockPosArgument.blockPos());
//        RequiredArgumentBuilder<CommandSourceStack, String> nameSpace = argument("mod", StringArgumentType.string()).suggests(blockMods);
//        RequiredArgumentBuilder<CommandSourceStack, String> path = argument("block", StringArgumentType.string()).suggests(blockNames);

//        path.executes(context -> fillBlocks(context.getSource(),
//                new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(context, "from"),
//                        BlockPosArgument.getLoadedBlockPos(context, "to")),
//                context.getArgument("mod", String.class),
//                context.getArgument("block", String.class), Mode.REPLACE, null));
//
//        RequiredArgumentBuilder<CommandSourceStack, Mode> mode = argument("filter", EnumArgument.enumArgument(Mode.class));
//        mode.executes(context -> {
//            Mode mode1 = context.getArgument("filter", Mode.class);
//            return fillBlocks(context.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(context, "from"),
//                            BlockPosArgument.getLoadedBlockPos(context, "to")),
//                    context.getArgument("mod", String.class),
//                    context.getArgument("block", String.class), mode1, null);
//        });

//        LiteralCommandNode<CommandSourceStack> fill = fill2.build();
//        ArgumentCommandNode<CommandSourceStack, ILocationArgument> start = from.build();
//        ArgumentCommandNode<CommandSourceStack, ILocationArgument> end = to.build();
//        ArgumentCommandNode<CommandSourceStack, String> mod = nameSpace.build();
//        ArgumentCommandNode<CommandSourceStack, String> block = path.build();

//        fill.addChild(start);
//        start.addChild(end);
//        end.addChild(mod);
//        mod.addChild(block);
//        block.addChild(mode.build());
//        rootCommandNode.addChild(fill);

        //give 2
        SuggestionProvider<CommandSourceStack> mods = (context, builder) -> SharedSuggestionProvider.suggest(() -> ForgeRegistries.ITEMS.getKeys().stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet()).iterator(), builder);
        SuggestionProvider<CommandSourceStack> items = (context, builder) -> SharedSuggestionProvider.suggest(() -> ForgeRegistries.ITEMS.getKeys().stream().filter(resourceLocation -> resourceLocation.getNamespace().equals(context.getArgument("mod", String.class))).map(ResourceLocation::getPath).collect(Collectors.toSet()).iterator(), builder);

        LiteralArgumentBuilder<CommandSourceStack> give2 = literal("give2").requires(commandSource -> commandSource.hasPermission(2));
        RequiredArgumentBuilder<CommandSourceStack, EntitySelector> targets = argument("targets", EntityArgument.players());
        RequiredArgumentBuilder<CommandSourceStack, String> itemmod = argument("mod", StringArgumentType.string()).suggests(mods);
        RequiredArgumentBuilder<CommandSourceStack, String> itemPath = argument("item", StringArgumentType.string()).suggests(items);
        itemPath.executes(context -> giveItems(context, 1));
        RequiredArgumentBuilder<CommandSourceStack, Integer> count = argument("count", IntegerArgumentType.integer(1));
        count.executes(context -> giveItems(context, IntegerArgumentType.getInteger(context, "count")));

        LiteralCommandNode<CommandSourceStack> giveNode = give2.build();
        ArgumentCommandNode<CommandSourceStack, EntitySelector> players = targets.build();
        ArgumentCommandNode<CommandSourceStack, String> itemDomain = itemmod.build();
        ArgumentCommandNode<CommandSourceStack, String> item = itemPath.build();
        ArgumentCommandNode<CommandSourceStack, Integer> countNode = count.build();
        giveNode.addChild(players);
        players.addChild(itemDomain);
        itemDomain.addChild(item);
        item.addChild(countNode);
        rootCommandNode.addChild(giveNode);

    }

    private static int giveItems(CommandContext<CommandSourceStack> context, int amount) throws CommandSyntaxException {
        String modName = context.getArgument("mod", String.class);
        String itemName = context.getArgument("item", String.class);
        ResourceLocation resourceLocation = new ResourceLocation(modName, itemName);
        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
        if (item != null) {
            Collection<ServerPlayer> serverPlayerEntities = EntityArgument.getPlayers(context, "targets");
            for (ServerPlayer serverplayerentity : serverPlayerEntities) {
                int i = amount;

                while (i > 0) {
                    int j = Math.min(item.getMaxStackSize(), i);
                    i -= j;
                    ItemStack itemstack = new ItemStack(item, j);
                    boolean flag = serverplayerentity.getInventory().add(itemstack);
                    if (flag && itemstack.isEmpty()) {
                        itemstack.setCount(1);
                        ItemEntity itementity1 = serverplayerentity.drop(itemstack, false);
                        if (itementity1 != null) {
                            itementity1.makeFakeItem();
                        }

                        serverplayerentity.level.playSound(null, serverplayerentity.getX(), serverplayerentity.getY(), serverplayerentity.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((serverplayerentity.getRandom().nextFloat() - serverplayerentity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        serverplayerentity.inventoryMenu.broadcastChanges();
                    } else {
                        ItemEntity itementity = serverplayerentity.drop(itemstack, false);
                        if (itementity != null) {
                            itementity.setNoPickUpDelay();
                            itementity.setOwner(serverplayerentity.getUUID());
                        }
                    }
                }
            }
        }
        return 1;
    }

//    private static int fillBlocks(CommandSource source, MutableBoundingBox mutableBoundingBox, String mod, String block, Mode mode, Predicate<CachedBlockInfo> o) throws CommandSyntaxException {
//        int i = mutableBoundingBox.getXSpan() * mutableBoundingBox.getYSpan() * mutableBoundingBox.getZSpan();
//        if (i > 32768) {
//            throw ERROR_AREA_TOO_LARGE.create(32768, i);
//        }
//        List<BlockPos> list = Lists.newArrayList();
//        ServerWorld serverworld = source.getLevel();
//        int j = 0;
//        Block block1 = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(mod, block));
//        for (BlockPos blockpos : BlockPos.betweenClosed(mutableBoundingBox.x0, mutableBoundingBox.y0, mutableBoundingBox.z0, mutableBoundingBox.x1, mutableBoundingBox.y1, mutableBoundingBox.z1)) {
//            if (o == null || o.test(new CachedBlockInfo(serverworld, blockpos, true))) {
//                BlockStateInput stateInput = mode.filter.filter(mutableBoundingBox, blockpos, new BlockStateInput(block1.defaultBlockState(), Collections.emptySet(), null), serverworld);
//                if (stateInput != null) {
//                    TileEntity tileEntity = serverworld.getBlockEntity(blockpos);
//                    IClearable.tryClear(tileEntity);
//                    if (serverworld.setBlockAndUpdate(blockpos, block1.defaultBlockState())) {
//                        list.add(blockpos.immutable());
//                        ++j;
//                    }
//                }
//            }
//        }
//
//        for (BlockPos blockpos1 : list) {
//            Block b = serverworld.getBlockState(blockpos1).getBlock();
//            serverworld.blockUpdated(blockpos1, b);
//        }
//
//        if (j == 0) {
//            throw ERROR_FAILED.create();
//        } else {
//            source.sendSuccess(new TranslationTextComponent("commands.fill.success", j), true);
//            return j;
//        }
//    }


    private static int summonEntity(CommandSourceStack commandSource, Vec3 position, ResourceLocation resourceLocation) {
        ServerLevel serverWorld = commandSource.getLevel();
        Entity entity = ForgeRegistries.ENTITIES.getValue(resourceLocation).create(serverWorld);
        if (entity == null) {
            commandSource.sendSuccess(new TextComponent("No entity " + resourceLocation.toString()), false);
            return -1;
        }
        entity.setPos(position.x, position.y, position.z);
        if (entity instanceof Mob)
            ((Mob) entity).finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, null, null);
        serverWorld.addFreshEntity(entity);
        commandSource.sendSuccess(new TextComponent("Summoned " + entity.getName().getString() + " at " + (int) position.x + " " + (int) position.y + " " + (int) position.z), true);
        return 1;
    }

//    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((p_208897_0_, p_208897_1_) -> {
//        return new TranslationTextComponent("commands.fill.toobig", p_208897_0_, p_208897_1_);
//    });

//    private static final BlockStateInput HOLLOW_CORE = new BlockStateInput(Blocks.AIR.defaultBlockState(), Collections.emptySet(), null);
//    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.fill.failed"));

//    enum Mode {
//        REPLACE((p_198450_0_, p_198450_1_, p_198450_2_, p_198450_3_) -> p_198450_2_),
//        OUTLINE((mutableBoundingBox, blockPos, blockStateInput, p_198454_3_) -> blockPos.getX() != mutableBoundingBox.x0 && blockPos.getX() != mutableBoundingBox.x1 && blockPos.getY() != mutableBoundingBox.y0 && blockPos.getY() != mutableBoundingBox.y1 && blockPos.getZ() != mutableBoundingBox.z0 && blockPos.getZ() != mutableBoundingBox.z1 ? null : blockStateInput),
//        HOLLOW((p_198453_0_, p_198453_1_, p_198453_2_, p_198453_3_) -> p_198453_1_.getX() != p_198453_0_.x0 && p_198453_1_.getX() != p_198453_0_.x1 && p_198453_1_.getY() != p_198453_0_.y0 && p_198453_1_.getY() != p_198453_0_.y1 && p_198453_1_.getZ() != p_198453_0_.z0 && p_198453_1_.getZ() != p_198453_0_.z1 ? HOLLOW_CORE : p_198453_2_),
//        DESTROY((p_198452_0_, p_198452_1_, p_198452_2_, p_198452_3_) -> {
//            p_198452_3_.destroyBlock(p_198452_1_, true);
//            return p_198452_2_;
//        });
//
//        public final SetBlockCommand.IFilter filter;
//
//        Mode(SetBlockCommand.IFilter p_i47985_3_) {
//            this.filter = p_i47985_3_;
//        }
//    }
}
