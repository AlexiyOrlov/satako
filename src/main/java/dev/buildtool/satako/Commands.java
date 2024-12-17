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
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeConfig;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


@EventBusSubscriber
public class Commands {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent registerCommandsEvent) {

        CommandDispatcher<CommandSourceStack> commandDispatcher = registerCommandsEvent.getDispatcher();
        RootCommandNode<CommandSourceStack> rootCommandNode = commandDispatcher.getRoot();
        LiteralArgumentBuilder<CommandSourceStack> summon2 = literal("summon2").requires(commandSource -> commandSource.hasPermission(2));

        SuggestionProvider<CommandSourceStack> namespaces = (context, builder) -> SharedSuggestionProvider.suggest(BuiltInRegistries.ENTITY_TYPE.stream().map(entityType -> BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getNamespace()), builder);
        SuggestionProvider<CommandSourceStack> entities = (context, builder) -> SharedSuggestionProvider.suggest(BuiltInRegistries.ENTITY_TYPE.stream().filter(entityType -> context.getArgument("namespace", String.class).equals(BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getNamespace())).map( entityType -> BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath()), builder);
        RequiredArgumentBuilder<CommandSourceStack, String> namespace = argument("namespace", StringArgumentType.string()).suggests(namespaces);
        RequiredArgumentBuilder<CommandSourceStack, String> entityName = argument("entity", StringArgumentType.string()).suggests(entities);
        entityName.executes(context -> {
            ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(context.getArgument("namespace", String.class), context.getArgument("entity", String.class));
            CommandSourceStack commandSource = context.getSource();
            return summonEntity(commandSource, commandSource.getPosition().add(0.5, 0, 0.5), resourceLocation);
        });

        RequiredArgumentBuilder<CommandSourceStack, Coordinates> position = argument("position", Vec3Argument.vec3(true));
        position.executes(context -> {
            ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(context.getArgument("namespace", String.class), context.getArgument("entity", String.class));
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


        //give 2
//        SuggestionProvider<CommandSourceStack> mods = (context, builder) -> SharedSuggestionProvider.suggest(() -> BuiltInRegistries.ITEM.keySet().stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet()).iterator(), builder);
        SuggestionProvider<CommandSourceStack> items = (context, builder) -> SharedSuggestionProvider.suggest(() -> BuiltInRegistries.ITEM.keySet().stream().filter(resourceLocation -> resourceLocation.getNamespace().equals(context.getArgument("mod", String.class))).map(ResourceLocation::getPath).collect(Collectors.toSet()).iterator(), builder);

        LiteralArgumentBuilder<CommandSourceStack> give2 = literal("give2").requires(commandSource -> commandSource.hasPermission(2));
        RequiredArgumentBuilder<CommandSourceStack, EntitySelector> targets = argument("targets", EntityArgument.players());
        RequiredArgumentBuilder<CommandSourceStack, String> itemmod = argument("mod", StringArgumentType.string()).suggests(namespaces);
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

        //kill 2
//        SuggestionProvider<CommandSourceStack> mods2=(context, builder) -> SharedSuggestionProvider.suggest(BuiltInRegistries.ENTITY_TYPE.keySet().stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet()), builder);
        SuggestionProvider<CommandSourceStack> entities2=(context, builder) -> SharedSuggestionProvider.suggest(BuiltInRegistries.ENTITY_TYPE.keySet().stream().filter(resourceLocation -> resourceLocation.getNamespace().equals(context.getArgument("mod",String.class))).map(ResourceLocation::getPath).collect(Collectors.toSet()),builder);
        LiteralArgumentBuilder<CommandSourceStack> kill2=literal("killall").requires(commandSourceStack -> commandSourceStack.hasPermission(2));
        RequiredArgumentBuilder<CommandSourceStack,String> entityMod=argument("mod",StringArgumentType.string()).suggests(namespaces);
        RequiredArgumentBuilder<CommandSourceStack,String> entityPath=argument("entity",StringArgumentType.string()).suggests(entities2);
        kill2.executes(commandContext -> {
            ServerLevel serverLevel=commandContext.getSource().getLevel();
            double d0 = 150;
            AABB aabb = new AABB(-d0, -d0, -d0, d0 + 1.0D, d0 + 1.0D, d0 + 1.0D);
            List<LivingEntity> entityList = serverLevel.getEntitiesOfClass(LivingEntity.class,aabb, living -> !(living instanceof Player));
            entityList.forEach(Entity::kill);
            return entityList.size();
        });
        entityPath.executes(context -> {
            ServerLevel serverLevel=context.getSource().getLevel();
            double d0 = 150;
            EntityType<?> entityType=BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(context.getArgument("mod",String.class),context.getArgument("entity",String.class)));
            AABB aabb = new AABB(-d0, -d0, -d0, d0 + 1.0D, d0 + 1.0D, d0 + 1.0D);
            List<? extends Entity> entityList = serverLevel.getEntities(entityType, aabb, entity -> true);
            entityList.forEach(Entity::kill);
            if (entityList.size() == 1) {
                context.getSource().sendSuccess(() -> Component.translatable("commands.kill.success.single", entityList.iterator().next().getDisplayName()), true);
            } else {
                context.getSource().sendSuccess(() -> Component.translatable("commands.kill.success.multiple", entityList.size()), true);
            }
            return entityList.size();
        });
        LiteralCommandNode<CommandSourceStack> killNode=kill2.build();
        ArgumentCommandNode<CommandSourceStack,String> modsNode=entityMod.build();
        ArgumentCommandNode<CommandSourceStack,String> entitiesNode=entityPath.build();
        killNode.addChild(modsNode);
        modsNode.addChild(entitiesNode);
        rootCommandNode.addChild(killNode);

//        SuggestionProvider<CommandSourceStack> mods3=(context, builder) -> SharedSuggestionProvider.suggest(BuiltInRegistries.ENTITY_TYPE.keySet().stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet()), builder);
        SuggestionProvider<CommandSourceStack> entities3=(context, builder) -> SharedSuggestionProvider.suggest(BuiltInRegistries.ENTITY_TYPE.keySet().stream().filter(resourceLocation -> resourceLocation.getNamespace().equals(context.getArgument("mod",String.class))).map(ResourceLocation::getPath).collect(Collectors.toSet()),builder);
        LiteralArgumentBuilder<CommandSourceStack> discard=literal("removeall").requires(commandSourceStack -> commandSourceStack.hasPermission(2));
        RequiredArgumentBuilder<CommandSourceStack,String> entityMod2=argument("mod",StringArgumentType.string()).suggests(namespaces);
        RequiredArgumentBuilder<CommandSourceStack,String> entityPath2=argument("entity",StringArgumentType.string()).suggests(entities3);
        discard.executes(commandContext -> {
            ServerLevel serverLevel=commandContext.getSource().getLevel();
            double d0 = 150;
            AABB aabb = new AABB(-d0, -d0, -d0, d0 + 1.0D, d0 + 1.0D, d0 + 1.0D);
            List<LivingEntity> entityList = serverLevel.getEntitiesOfClass(LivingEntity.class,aabb,living -> !(living instanceof Player));
            entityList.forEach(Entity::discard);
            return entityList.size();
        });
        entityPath2.executes(context -> {
            ServerLevel serverLevel=context.getSource().getLevel();
            double d0 = 150;
            EntityType<?> entityType= BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(context.getArgument("mod",String.class),context.getArgument("entity",String.class)));
            AABB aabb = new AABB(-d0, -d0, -d0, d0 + 1.0D, d0 + 1.0D, d0 + 1.0D);
            List<? extends Entity> entityList = serverLevel.getEntities(entityType, aabb, entity -> true);
            entityList.forEach(Entity::discard);
            if (entityList.size() == 1) {
                context.getSource().sendSuccess(() -> Component.translatable("satako.discard.success.single", entityList.iterator().next().getDisplayName()), true);
            } else {
                context.getSource().sendSuccess(() -> Component.translatable("satako.discard.success.multiple", entityList.size()), true);
            }
            return entityList.size();
        });

        LiteralCommandNode<CommandSourceStack> discardNode=discard.build();
        ArgumentCommandNode<CommandSourceStack,String> modsNode2=entityMod2.build();
        ArgumentCommandNode<CommandSourceStack,String> entitiesNode2=entityPath2.build();
        discardNode.addChild(modsNode2);
        modsNode2.addChild(entitiesNode2);
        rootCommandNode.addChild(discardNode);
    }

    private static int giveItems(CommandContext<CommandSourceStack> context, int amount) throws CommandSyntaxException {
        String modName = context.getArgument("mod", String.class);
        String itemName = context.getArgument("item", String.class);
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(modName, itemName);
        Item item = BuiltInRegistries.ITEM.get(resourceLocation);
        Collection<ServerPlayer> serverPlayerEntities = EntityArgument.getPlayers(context, "targets");
        for (ServerPlayer serverplayerentity : serverPlayerEntities) {
            int i = amount;

            while (i > 0) {
                int j = Math.min(item.getMaxStackSize(ItemStack.EMPTY), i);
                i -= j;
                ItemStack itemstack = new ItemStack(item, j);
                boolean flag = serverplayerentity.getInventory().add(itemstack);
                if (flag && itemstack.isEmpty()) {
                    itemstack.setCount(1);
                    ItemEntity itementity1 = serverplayerentity.drop(itemstack, false);
                    if (itementity1 != null) {
                        itementity1.makeFakeItem();
                    }

                    serverplayerentity.serverLevel().playSound(null, serverplayerentity.getX(), serverplayerentity.getY(), serverplayerentity.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((serverplayerentity.getRandom().nextFloat() - serverplayerentity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    serverplayerentity.inventoryMenu.broadcastChanges();
                } else {
                    ItemEntity itementity = serverplayerentity.drop(itemstack, false);
                    if (itementity != null) {
                        itementity.setNoPickUpDelay();
                        itementity.setThrower(serverplayerentity);
                    }
                }
            }
        }
        return 1;
    }

    private static int summonEntity(CommandSourceStack commandSource, Vec3 position, ResourceLocation resourceLocation) {
        ServerLevel serverWorld = commandSource.getLevel();
        Entity entity = BuiltInRegistries.ENTITY_TYPE.get(resourceLocation).create(serverWorld);
        if (entity == null) {
            commandSource.sendSuccess(() -> Component.literal("No entity " + resourceLocation.toString()), false);
            return -1;
        }
        entity.setPos(position.x, position.y, position.z);
        if (entity instanceof Mob)
            ((Mob) entity).finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, null);
        serverWorld.addFreshEntity(entity);
        commandSource.sendSuccess(() -> Component.literal("Summoned " + entity.getName().getString() + " at " + (int) position.x + " " + (int) position.y + " " + (int) position.z), true);
        return 1;
    }

}
