package dev.buildtool.satako;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.*;
import net.minecraft.command.impl.SetBlockCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IClearable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.command.EnumArgument;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

@Mod.EventBusSubscriber
public class Commands {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent registerCommandsEvent) {

        CommandDispatcher<CommandSource> commandDispatcher = registerCommandsEvent.getDispatcher();
        RootCommandNode<CommandSource> rootCommandNode = commandDispatcher.getRoot();
        LiteralArgumentBuilder<CommandSource> summon2 = literal("summon2").requires(commandSource -> commandSource.hasPermission(2));

        SuggestionProvider<CommandSource> namespaces = (context, builder) -> ISuggestionProvider.suggest(ForgeRegistries.ENTITIES.getValues().stream().map(entityType -> entityType.getRegistryName().getNamespace()), builder);
        SuggestionProvider<CommandSource> entities = (context, builder) -> ISuggestionProvider.suggest(ForgeRegistries.ENTITIES.getValues().stream().filter(entityType -> context.getArgument("namespace", String.class).equals(entityType.getRegistryName().getNamespace())).map(entityType -> entityType.getRegistryName().getPath()), builder);
        RequiredArgumentBuilder<CommandSource, String> namespace = argument("namespace", StringArgumentType.string()).suggests(namespaces);
        RequiredArgumentBuilder<CommandSource, String> entityName = argument("entity", StringArgumentType.string()).suggests(entities);
        entityName.executes(context -> {
            ResourceLocation resourceLocation = new ResourceLocation(context.getArgument("namespace", String.class), context.getArgument("entity", String.class));
            CommandSource commandSource = context.getSource();
            return summonEntity(commandSource, commandSource.getPosition().add(0.5, 0, 0.5), resourceLocation);
        });

        RequiredArgumentBuilder<CommandSource, ILocationArgument> position = argument("position", Vec3Argument.vec3(true));
        position.executes(context -> {
            ResourceLocation resourceLocation = new ResourceLocation(context.getArgument("namespace", String.class), context.getArgument("entity", String.class));
            CommandSource commandSource = context.getSource();
            Vector3d vector3d = Vec3Argument.getVec3(context, "position");
            return summonEntity(commandSource, vector3d, resourceLocation);
        });

        LiteralCommandNode<CommandSource> built = summon2.build();
        ArgumentCommandNode<CommandSource, String> domain = namespace.build();
        ArgumentCommandNode<CommandSource, String> name = entityName.build();

        built.addChild(domain);
        domain.addChild(name);
        name.addChild(position.build());
        rootCommandNode.addChild(built);

        //fill 2
        SuggestionProvider<CommandSource> blockMods = (context, builder) -> ISuggestionProvider.suggest(ForgeRegistries.BLOCKS.getKeys().stream().map(ResourceLocation::getNamespace), builder);
        SuggestionProvider<CommandSource> blockNames = (context, builder) -> ISuggestionProvider.suggest(ForgeRegistries.BLOCKS.getKeys().stream().filter(resourceLocation -> resourceLocation.getNamespace().equals(context.getArgument("mod", String.class))).map(ResourceLocation::getPath), builder);

        LiteralArgumentBuilder<CommandSource> fill2 = literal("fill2").requires(commandSource -> commandSource.hasPermission(2));
        RequiredArgumentBuilder<CommandSource, ILocationArgument> from = argument("from", BlockPosArgument.blockPos());
        RequiredArgumentBuilder<CommandSource, ILocationArgument> to = argument("to", BlockPosArgument.blockPos());
        RequiredArgumentBuilder<CommandSource, String> nameSpace = argument("mod", StringArgumentType.string()).suggests(blockMods);
        RequiredArgumentBuilder<CommandSource, String> path = argument("block", StringArgumentType.string()).suggests(blockNames);

        path.executes(context -> fillBlocks(context.getSource(),
                new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(context, "from"),
                        BlockPosArgument.getLoadedBlockPos(context, "to")),
                context.getArgument("mod", String.class),
                context.getArgument("block", String.class), Mode.REPLACE, null));

        RequiredArgumentBuilder<CommandSource, Mode> mode = argument("filter", EnumArgument.enumArgument(Mode.class));
        mode.executes(context -> {
            Mode mode1 = context.getArgument("filter", Mode.class);
            return fillBlocks(context.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(context, "from"),
                            BlockPosArgument.getLoadedBlockPos(context, "to")),
                    context.getArgument("mod", String.class),
                    context.getArgument("block", String.class), mode1, null);
        });

        LiteralCommandNode<CommandSource> fill = fill2.build();
        ArgumentCommandNode<CommandSource, ILocationArgument> start = from.build();
        ArgumentCommandNode<CommandSource, ILocationArgument> end = to.build();
        ArgumentCommandNode<CommandSource, String> mod = nameSpace.build();
        ArgumentCommandNode<CommandSource, String> block = path.build();

        fill.addChild(start);
        start.addChild(end);
        end.addChild(mod);
        mod.addChild(block);
        block.addChild(mode.build());
        rootCommandNode.addChild(fill);

        //give 2
        SuggestionProvider<CommandSource> mods = (context, builder) -> ISuggestionProvider.suggest(() -> ForgeRegistries.ITEMS.getKeys().stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet()).iterator(), builder);
        SuggestionProvider<CommandSource> items = (context, builder) -> ISuggestionProvider.suggest(() -> ForgeRegistries.ITEMS.getKeys().stream().filter(resourceLocation -> resourceLocation.getNamespace().equals(context.getArgument("mod", String.class))).map(ResourceLocation::getPath).collect(Collectors.toSet()).iterator(), builder);

        LiteralArgumentBuilder<CommandSource> give2 = literal("give2").requires(commandSource -> commandSource.hasPermission(2));
        RequiredArgumentBuilder<CommandSource, EntitySelector> targets = argument("targets", EntityArgument.players());
        RequiredArgumentBuilder<CommandSource, String> itemmod = argument("mod", StringArgumentType.string()).suggests(mods);
        RequiredArgumentBuilder<CommandSource, String> itemPath = argument("item", StringArgumentType.string()).suggests(items);
        itemPath.executes(context -> giveItems(context, 1));
        RequiredArgumentBuilder<CommandSource, Integer> count = argument("count", IntegerArgumentType.integer(1));
        count.executes(context -> giveItems(context, IntegerArgumentType.getInteger(context, "count")));

        LiteralCommandNode<CommandSource> giveNode = give2.build();
        ArgumentCommandNode<CommandSource, EntitySelector> players = targets.build();
        ArgumentCommandNode<CommandSource, String> itemDomain = itemmod.build();
        ArgumentCommandNode<CommandSource, String> item = itemPath.build();
        ArgumentCommandNode<CommandSource, Integer> countNode = count.build();
        giveNode.addChild(players);
        players.addChild(itemDomain);
        itemDomain.addChild(item);
        item.addChild(countNode);
        rootCommandNode.addChild(giveNode);

//        commandDispatcher.register(net.minecraft.command.Commands.literal("f").requires((p_198471_0_) -> {
//            return p_198471_0_.hasPermission(2);
//        }).then(net.minecraft.command.Commands.argument("from", BlockPosArgument.blockPos()).then(net.minecraft.command.Commands.argument("to", BlockPosArgument.blockPos()).then(net.minecraft.command.Commands.argument("block", BlockStateArgument.block()).executes((commandContext) -> {
//            return fillBlocks(commandContext.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"), BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), Mode.REPLACE, null);
//        }).then(net.minecraft.command.Commands.literal("replace").executes((commandContext) -> {
//            return fillBlocks(commandContext.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"), BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), Mode.REPLACE, null);
//        }).then(net.minecraft.command.Commands.argument("filter", BlockPredicateArgument.blockPredicate()).executes((commandContext) -> {
//            return fillBlocks(commandContext.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"), BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), Mode.REPLACE, BlockPredicateArgument.getBlockPredicate(commandContext, "filter"));
//        }))).then(net.minecraft.command.Commands.literal("keep").executes((commandContext) -> {
//            return fillBlocks(commandContext.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"), BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), Mode.REPLACE, (p_198469_0_) -> {
//                return p_198469_0_.getLevel().isEmptyBlock(p_198469_0_.getPos());
//            });
//        })).then(net.minecraft.command.Commands.literal("outline").executes((commandContext) -> {
//            return fillBlocks(commandContext.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"), BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), Mode.OUTLINE, null);
//        })).then(net.minecraft.command.Commands.literal("hollow").executes((commandContext) -> {
//            return fillBlocks(commandContext.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"), BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), Mode.HOLLOW, null);
//        })).then(net.minecraft.command.Commands.literal("destroy").executes((commandContext) -> {
//            return fillBlocks(commandContext.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"), BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), Mode.DESTROY, null);
//        }))))));
    }

    private static int giveItems(CommandContext<CommandSource> context, int amount) throws CommandSyntaxException {
        String modName = context.getArgument("mod", String.class);
        String itemName = context.getArgument("item", String.class);
        ResourceLocation resourceLocation = new ResourceLocation(modName, itemName);
        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
        if (item != null) {
            Collection<ServerPlayerEntity> serverPlayerEntities = EntityArgument.getPlayers(context, "targets");
            for (ServerPlayerEntity serverplayerentity : serverPlayerEntities) {
                int i = amount;

                while (i > 0) {
                    int j = Math.min(item.getMaxStackSize(), i);
                    i -= j;
                    ItemStack itemstack = new ItemStack(item, j);
                    boolean flag = serverplayerentity.inventory.add(itemstack);
                    if (flag && itemstack.isEmpty()) {
                        itemstack.setCount(1);
                        ItemEntity itementity1 = serverplayerentity.drop(itemstack, false);
                        if (itementity1 != null) {
                            itementity1.makeFakeItem();
                        }

                        serverplayerentity.level.playSound(null, serverplayerentity.getX(), serverplayerentity.getY(), serverplayerentity.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((serverplayerentity.getRandom().nextFloat() - serverplayerentity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
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

    private static int fillBlocks(CommandSource source, MutableBoundingBox mutableBoundingBox, String mod, String block, Mode mode, Predicate<CachedBlockInfo> o) throws CommandSyntaxException {
        int i = mutableBoundingBox.getXSpan() * mutableBoundingBox.getYSpan() * mutableBoundingBox.getZSpan();
        if (i > 32768) {
            throw ERROR_AREA_TOO_LARGE.create(32768, i);
        }
        List<BlockPos> list = Lists.newArrayList();
        ServerWorld serverworld = source.getLevel();
        int j = 0;
        Block block1 = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(mod, block));
        for (BlockPos blockpos : BlockPos.betweenClosed(mutableBoundingBox.x0, mutableBoundingBox.y0, mutableBoundingBox.z0, mutableBoundingBox.x1, mutableBoundingBox.y1, mutableBoundingBox.z1)) {
            if (o == null || o.test(new CachedBlockInfo(serverworld, blockpos, true))) {
                BlockStateInput stateInput = mode.filter.filter(mutableBoundingBox, blockpos, new BlockStateInput(block1.defaultBlockState(), Collections.emptySet(), null), serverworld);
                if (stateInput != null) {
                    TileEntity tileEntity = serverworld.getBlockEntity(blockpos);
                    IClearable.tryClear(tileEntity);
                    if (serverworld.setBlockAndUpdate(blockpos, block1.defaultBlockState())) {
                        list.add(blockpos.immutable());
                        ++j;
                    }
                }
            }
        }

        for (BlockPos blockpos1 : list) {
            Block b = serverworld.getBlockState(blockpos1).getBlock();
            serverworld.blockUpdated(blockpos1, b);
        }

        if (j == 0) {
            throw ERROR_FAILED.create();
        } else {
            source.sendSuccess(new TranslationTextComponent("commands.fill.success", j), true);
            return j;
        }
    }


    private static int summonEntity(CommandSource commandSource, Vector3d position, ResourceLocation resourceLocation) {
        ServerWorld serverWorld = commandSource.getLevel().getWorldServer();
        Entity entity = ForgeRegistries.ENTITIES.getValue(resourceLocation).create(serverWorld);
        if (entity == null) {
            commandSource.sendSuccess(new StringTextComponent("No entity " + resourceLocation.toString()), false);
            return -1;
        }
        entity.setPos(position.x, position.y, position.z);
        if (entity instanceof MobEntity)
            ((MobEntity) entity).finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(entity.blockPosition()), SpawnReason.COMMAND, null, null);
        serverWorld.addFreshEntity(entity);
        commandSource.sendSuccess(new StringTextComponent("Summoned " + entity.getName().getString() + " at " + (int) position.x + " " + (int) position.y + " " + (int) position.z), true);
        return 1;
    }

    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((p_208897_0_, p_208897_1_) -> {
        return new TranslationTextComponent("commands.fill.toobig", p_208897_0_, p_208897_1_);
    });

    private static final BlockStateInput HOLLOW_CORE = new BlockStateInput(Blocks.AIR.defaultBlockState(), Collections.emptySet(), null);
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.fill.failed"));

    enum Mode {
        REPLACE((p_198450_0_, p_198450_1_, p_198450_2_, p_198450_3_) -> p_198450_2_),
        OUTLINE((p_198454_0_, p_198454_1_, p_198454_2_, p_198454_3_) -> p_198454_1_.getX() != p_198454_0_.x0 && p_198454_1_.getX() != p_198454_0_.x1 && p_198454_1_.getY() != p_198454_0_.y0 && p_198454_1_.getY() != p_198454_0_.y1 && p_198454_1_.getZ() != p_198454_0_.z0 && p_198454_1_.getZ() != p_198454_0_.z1 ? null : p_198454_2_),
        HOLLOW((p_198453_0_, p_198453_1_, p_198453_2_, p_198453_3_) -> p_198453_1_.getX() != p_198453_0_.x0 && p_198453_1_.getX() != p_198453_0_.x1 && p_198453_1_.getY() != p_198453_0_.y0 && p_198453_1_.getY() != p_198453_0_.y1 && p_198453_1_.getZ() != p_198453_0_.z0 && p_198453_1_.getZ() != p_198453_0_.z1 ? HOLLOW_CORE : p_198453_2_),
        DESTROY((p_198452_0_, p_198452_1_, p_198452_2_, p_198452_3_) -> {
            p_198452_3_.destroyBlock(p_198452_1_, true);
            return p_198452_2_;
        });

        public final SetBlockCommand.IFilter filter;

        Mode(SetBlockCommand.IFilter p_i47985_3_) {
            this.filter = p_i47985_3_;
        }
    }
}
