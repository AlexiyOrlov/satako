package dev.buildtool.satako;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import dev.buildtool.satako.integration.JEI;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class TooltipHandler {
    static List<Component> originalTooltip;
    static List<MutableComponent> properties;
    static ItemStack targetStack;
    static int xOffset;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tooltip(ItemTooltipEvent tooltipEvent)
    {
        if(Satako.enableInfoTooltip.get()) {
            if (Screen.hasAltDown()) {
                ItemStack itemStack = tooltipEvent.getItemStack();
                targetStack = itemStack;
                Item item = itemStack.getItem();
                Minecraft minecraft = Minecraft.getInstance();
                Screen currentScreen = minecraft.screen;
                if (currentScreen != null) {
                    originalTooltip = tooltipEvent.getToolTip();
                    ArrayList<String> info = new ArrayList<>();
                    info.add(0, ForgeRegistries.ITEMS.getKey(item).toString());
                    int repairCost = itemStack.getBaseRepairCost();
                    if (repairCost > 0) {
                        info.add("Repair cost: " + repairCost);
                    }

                    ClientLevel world = minecraft.level;

                    if (item instanceof MapItem) {
                        assert world != null;

                        MapItemSavedData mapData = MapItem.getSavedData(itemStack, world);
                        if (mapData != null) {
                            int scale = mapData.scale;
                            info.add("Scale: " + scale + "/4");
                            info.add("Dimension: " + mapData.dimension.location().toString());
                        }
                    } else {
                        int harvestlevel;
                        if (item instanceof BlockItem blockItem) {
                            Block block = blockItem.getBlock();
                            BlockState defstate = block.defaultBlockState();
                            float friction = block.getFriction();
                            if (friction != 0.6F) {
                                info.add("Slipperiness: " + friction);
                            }
                            if (ForgeRegistries.BLOCKS.tags().getTag(BlockTags.MINEABLE_WITH_PICKAXE).contains(block)) {
                                info.add("Harvestable by " + ChatFormatting.YELLOW + "pickaxe");
                            }
                            if (ForgeRegistries.BLOCKS.tags().getTag(BlockTags.MINEABLE_WITH_AXE).contains(block)) {
                                info.add("Harvestable by " + ChatFormatting.YELLOW + "axe");
                            }
                            if (ForgeRegistries.BLOCKS.tags().getTag(BlockTags.MINEABLE_WITH_SHOVEL).contains(block)) {
                                info.add("Harvestable by " + ChatFormatting.YELLOW + "shovel");
                            }
                            if (ForgeRegistries.BLOCKS.tags().getTag(BlockTags.MINEABLE_WITH_HOE).contains(block)) {
                                info.add("Harvestable by " + ChatFormatting.YELLOW + "hoe");
                            }

                            assert world != null;
                            float hardness = defstate.getDestroySpeed(world, BlockPos.ZERO);
                            if (hardness > 0.0F) {
                                info.add("Hardness: " + hardness);
                            } else if (hardness == -1.0F) {
                                info.add("Unbreakable");
                            }

                            float resistance = block.getExplosionResistance();
                            if (resistance > 0.0F) {
                                float compRes = (resistance + 0.3F) * 0.3F;
                                if (compRes > 5.2F) {
                                    info.add("Blast resistance: " + String.format("%.1f", resistance) + " (TNT)");
                                } else if (compRes > 3.9F) {
                                    info.add("Blast resistance: " + String.format("%.1f", resistance) + " (Creeper)");
                                } else {
                                    info.add("Blast resistance: " + String.format("%.1f", resistance));
                                }
                            }

                            if (ForgeRegistries.BLOCKS.tags().getTag(BlockTags.BEACON_BASE_BLOCKS).contains(defstate.getBlock())) {
                                info.add("Can be used for Beacon");
                            }

                            if (defstate.isFlammable(world, BlockPos.ZERO, Direction.UP)) {
                                info.add("Flammable");
                            }

                            PushReaction pushReaction = defstate.getPistonPushReaction();
                            info.add("Push behavior: " + pushReaction);
                            if (defstate.hasBlockEntity()) {
                                info.add("Has block entity");
                            }

                            int lightEmission = defstate.getLightEmission();
                            if (lightEmission > 0) {
                                info.add("Light: " + lightEmission);
                            }

                            if (defstate.isSignalSource()) {
                                info.add("Redstone component");
                            }
                        } else if (item instanceof DiggerItem toolItem) {

                            Tier itemTier = toolItem.getTier();
                            float efficiency = itemTier.getSpeed();
                            int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, itemStack);
                            if (level > 0) {
                                efficiency += (float) (level * level + 1);
                            }

                            info.add("Speed: " + efficiency);
                            harvestlevel = itemTier.getLevel();
                            info.add("Harvest level: " + harvestlevel);
                        }
                    }

                    float saturation;
                    if (item instanceof SwordItem swordItem) {
                        float bane = EnchantmentHelper.getDamageBonus(itemStack, MobType.ARTHROPOD);
                        float smite = EnchantmentHelper.getDamageBonus(itemStack, MobType.UNDEAD);
                        if (bane > 0 || smite > 0) {
                            float damage = swordItem.getDamage();
                            info.add("Max. damage: " + ((bane > 0 ? damage + bane : smite + damage) + 1));
                        }
                    }

                    int durability = itemStack.getMaxDamage();
                    if (durability > 0) {
                        info.add("Max. durability: " + durability);
                        int durabRemain = durability - itemStack.getDamageValue();
                        if ((float) durabRemain / durability <= 0.1f) {
                            info.add("Durability left: " + durabRemain);
                        }
                    }

                    int stacksize = itemStack.getMaxStackSize();
                    if (stacksize == 1) {
                        info.add("Non-stackable");
                    } else if (stacksize != 64) {
                        info.add("Max. stack size: " + stacksize);
                    }

                    if (item.isEdible()) {
                        FoodProperties foodStats = item.getFoodProperties();

                        assert foodStats != null;

                        if (foodStats.canAlwaysEat()) {
                            info.add("Always edible");
                        }

                        if (foodStats.isMeat()) {
                            info.add("Suitable for wolves");
                        }

                        float nutrition = (float) foodStats.getNutrition() / 2.0F;
                        info.add("Restores " + nutrition + " hunger");
                        saturation = foodStats.getSaturationModifier();
                        info.add("Saturation: " + saturation);
                        List<Pair<MobEffectInstance, Float>> effects = foodStats.getEffects();
                        if (!effects.isEmpty()) {
                            info.add(ChatFormatting.YELLOW + "Effects:");
                            for (Pair<MobEffectInstance, Float> pair : effects) {
                                MobEffectInstance effectInstance = pair.getFirst();
                                info.add("   " + I18n.get(effectInstance.getDescriptionId()) + ":");
                                info.add("      Strength: " + effectInstance.getAmplifier());
                                info.add("      Duration: " + effectInstance.getDuration() / 20 + " s.");
                            }
                        }
                    }

                    int enchantability = itemStack.getEnchantmentValue();
                    if (enchantability > 0) {
                        info.add("Enchantability: " + enchantability);
                    }

                    int burnTime = ForgeHooks.getBurnTime(itemStack, null);
                    if (burnTime > 0) {
                        info.add("Burn time: " + burnTime + " (" + burnTime / 200f + " items)");
                    }

                    if (PotionBrewing.isIngredient(itemStack)) {
                        info.add("Potion component");
                    }

                    Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
                    if (!enchantments.isEmpty()) {

                        for (Map.Entry<Enchantment, Integer> integerEntry : enchantments.entrySet()) {
                            if (integerEntry.getKey().getMaxLevel() == integerEntry.getValue() && integerEntry.getValue() > 1) {
                                info.add(I18n.get(integerEntry.getKey().getDescriptionId()) + " is maxed");
                            }
                        }
                    }
                    Set<ITag<Item>> tags = new HashSet<>();
                    ForgeRegistries.ITEMS.tags().forEach(items -> {
                        if (items.contains(item)) {
                            tags.add(items);
                        }
                    });
                    if (!tags.isEmpty()) {
                        info.add(ChatFormatting.AQUA + "Tags:");
                        tags.forEach(resourceLocation -> info.add("   " + resourceLocation.getKey().location()));
                    }

                    if (item instanceof SpawnEggItem spawnEggItem) {
                        EntityType<?> entityType = spawnEggItem.getType(null);
                        if (entityType.fireImmune())
                            info.add("Fire-immune");
                        MobCategory category = entityType.getCategory();
                        info.add("Category: " + category.getName());
                        if (category.isFriendly())
                            info.add("Friendly");
                        if (category.isPersistent())
                            info.add("Persistent");
                        info.add("Size: " + entityType.getWidth() + "x" + entityType.getHeight());
                        info.add("Id: " + ForgeRegistries.ENTITY_TYPES.getKey(entityType).toString());
                        if (entityType.getTags().count() > 0) {
                            info.add(ChatFormatting.AQUA + "Tags:");
                            entityType.getTags().forEach(entityTypeTagKey -> info.add("  " + entityTypeTagKey.location()));
                        }
                    }
                    //end of gathering info

                    properties = info.stream().map(Component::literal).toList();

                    if (Satako.jei && JEI.ingredientListOverlay != null) {
                        Optional<ITypedIngredient<?>> ingredient = JEI.ingredientListOverlay.getIngredientUnderMouse();
                        if (ingredient.isPresent()) {
                            xOffset = 0;
                            ingredient.ifPresent(iTypedIngredient -> iTypedIngredient.getItemStack().ifPresent(itemStack1 -> targetStack = itemStack1));
                        }
                    }
                }
            }

            renderHoveringTooltip(new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource()));
        }
    }

    public static void renderHoveringTooltip(GuiGraphics graphics) {
        if(targetStack!=null && Screen.hasAltDown()) {
            Minecraft minecraft = Minecraft.getInstance();
            Screen currentScreen = minecraft.screen;

            if (!properties.isEmpty()) {

                int theirLongestStringWidth = 0;

                for (Component iTextComponent : originalTooltip) {
                    String string = iTextComponent.getString();
                    int width = minecraft.font.width(string);
                    if (width > theirLongestStringWidth) {
                        theirLongestStringWidth = width;
                    }
                }

                List<Component> copy = new ArrayList<>(originalTooltip.size());
                int ourLongestStringWidth = 0;

                for (Component property : properties) {
                    xOffset = minecraft.font.width(property);
                    if (xOffset > ourLongestStringWidth) {
                        ourLongestStringWidth = xOffset;
                    }
                }

                double mouseX = minecraft.mouseHandler.xpos() * (double) minecraft.getWindow().getGuiScaledWidth() / (double) minecraft.getWindow().getScreenWidth();
                xOffset = (int) (mouseX - (double) ourLongestStringWidth) - 40;
                if (xOffset < 0) {
                    xOffset = 0;
                }

                double mouseY = minecraft.mouseHandler.ypos() * (double) minecraft.getWindow().getGuiScaledHeight() / (double) minecraft.getWindow().getScreenHeight();

                int tooltipTextWidth = 0;
                Font font = minecraft.font;

                int titleLinesCount;
                for (Component textComponent : properties) {
                    titleLinesCount = font.width(textComponent);
                    if (titleLinesCount > tooltipTextWidth) {
                        tooltipTextWidth = titleLinesCount;
                    }
                }

                boolean needsWrap = false;
                assert currentScreen != null;
                int screenWidth = currentScreen.width;
                titleLinesCount = 1;
                int tooltipX = xOffset + 12;
                if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                    tooltipX = xOffset - 16 - tooltipTextWidth;
                    if (tooltipX < 4) {
                        if (xOffset > screenWidth / 2) {
                            tooltipTextWidth = xOffset - 12 - 8;
                        } else {
                            tooltipTextWidth = screenWidth - 16 - xOffset;
                        }

                        needsWrap = true;
                    }
                }

                int tooltipY;
                int screenHeight;
                if (needsWrap) {
                    tooltipY = 0;
                    ArrayList<FormattedText> wrappedTextLines = new ArrayList<>();

                    for (screenHeight = 0; screenHeight < properties.size(); ++screenHeight) {
                        Component textLine = properties.get(screenHeight);
                        List<FormattedText> wrappedLine = font.getSplitter().splitLines(textLine, tooltipTextWidth, Style.EMPTY);
                        if (screenHeight == 0) {
                            titleLinesCount = wrappedLine.size();
                        }

                        FormattedText line;
                        for (FormattedText formattedText : wrappedLine) {
                            line = formattedText;
                            int lineWidth = font.width(line);
                            if (lineWidth > tooltipY) {
                                tooltipY = lineWidth;
                            }
                            wrappedTextLines.add(line);
                        }
                    }

                    properties = wrappedTextLines.stream().map((iTextProperties) -> Component.literal(iTextProperties.getString())).collect(Collectors.toList());
                    if (xOffset > screenWidth / 2) {
                        tooltipX = xOffset - 16 - tooltipY;
                    } else {
                        tooltipX = xOffset + 12;
                    }
                }

                tooltipY = (int) (mouseY - 12.0D);
                int tooltipHeight = 8;
                if (properties.size() > 1) {
                    tooltipHeight += (properties.size() - 1) * 10;
                    if (properties.size() > titleLinesCount) {
                        tooltipHeight += 2;
                    }
                }

                screenHeight = currentScreen.height;
                if (tooltipY < 4) {
                    tooltipY = 4;
                } else if (tooltipY + tooltipHeight + 4 > screenHeight) {
                    tooltipY = screenHeight - tooltipHeight - 4;
                }

                graphics.renderTooltip(minecraft.font, properties.stream().map(mutableComponent -> Component.literal(mutableComponent.getString())).collect(Collectors.toList()), targetStack.getTooltipImage(), targetStack, tooltipX, tooltipY);
            }
        }
    }
}
