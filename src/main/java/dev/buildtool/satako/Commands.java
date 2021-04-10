package dev.buildtool.satako;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

@Mod.EventBusSubscriber
public class Commands {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent registerCommandsEvent) {
        // SuggestionProviders.register(new ResourceLocation(Satako.ID, "mods"),
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
        commandSource.sendSuccess(new StringTextComponent("Summoned " + entity.getName().getString() + " at " + (float) position.x + " " + (float) position.y + " " + (float) position.z), true);
        return 1;
    }
}
