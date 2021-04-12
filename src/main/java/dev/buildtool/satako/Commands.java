package dev.buildtool.satako;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
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
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.command.impl.SetBlockCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.inventory.IClearable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.ResourceLocation;
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

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

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
//                BlockStateInput blockstateinput = mode.filter.filter(mutableBoundingBox, blockpos,, serverworld);
//                if (blockstateinput != null) {
//                    TileEntity tileentity = serverworld.getBlockEntity(blockpos);
//                    IClearable.tryClear(tileentity);
//                    if (blockstateinput.place(serverworld, blockpos, 2)) {
//                        list.add(blockpos.immutable());
//                        ++j;
//                    }
//                }
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


    static int summonEntity(CommandSource commandSource, Vector3d position, ResourceLocation resourceLocation) {
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

//    private static int fillBlocks(CommandSource source, MutableBoundingBox mutableBoundingBox, BlockStateInput blockStateInput, Mode p_198463_3_, @Nullable Predicate<CachedBlockInfo> p_198463_4_) throws CommandSyntaxException {
//        int i = mutableBoundingBox.getXSpan() * mutableBoundingBox.getYSpan() * mutableBoundingBox.getZSpan();
//        if (i > 32768) {
//            throw ERROR_AREA_TOO_LARGE.create(32768, i);
//        } else {
//            List<BlockPos> list = Lists.newArrayList();
//            ServerWorld serverworld = source.getLevel();
//            int j = 0;
//
//            for (BlockPos blockpos : BlockPos.betweenClosed(mutableBoundingBox.x0, mutableBoundingBox.y0, mutableBoundingBox.z0, mutableBoundingBox.x1, mutableBoundingBox.y1, mutableBoundingBox.z1)) {
//                if (p_198463_4_ == null || p_198463_4_.test(new CachedBlockInfo(serverworld, blockpos, true))) {
//                    BlockStateInput blockstateinput = p_198463_3_.filter.filter(mutableBoundingBox, blockpos, blockStateInput, serverworld);
//                    if (blockstateinput != null) {
//                        TileEntity tileentity = serverworld.getBlockEntity(blockpos);
//                        IClearable.tryClear(tileentity);
//                        if (blockstateinput.place(serverworld, blockpos, 2)) {
//                            list.add(blockpos.immutable());
//                            ++j;
//                        }
//                    }
//                }
//            }
//
//            for (BlockPos blockpos1 : list) {
//                Block block = serverworld.getBlockState(blockpos1).getBlock();
//                serverworld.blockUpdated(blockpos1, block);
//            }
//
//            if (j == 0) {
//                throw ERROR_FAILED.create();
//            } else {
//                source.sendSuccess(new TranslationTextComponent("commands.fill.success", j), true);
//                return j;
//            }
//        }
//    }

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
