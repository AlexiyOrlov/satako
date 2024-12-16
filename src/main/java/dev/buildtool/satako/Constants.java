package dev.buildtool.satako;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.UUID;

/**
 * Holds miscellaneous constants
 */
public class Constants
{
    public static final int ONE_SECOND = 20;
    public static final float CREEPEREXPLOSIONMAXSTRENGTH = 3.9f, TNTMAXSTRENGTH = 5.2f,
            BLOCKCREEPERESISTANCE = CREEPEREXPLOSIONMAXSTRENGTH * 5 / 3 / 0.3f - 0.3f,
            BLOCKTNTRESISTANCE = TNTMAXSTRENGTH * 5 / 3 / 0.3f - 0.3f;
    /**
     * Around the player
     */
    public static final int LOADED_CHUNK_DEFAULT_RADIUS = 10;
    @Deprecated
    public static final UUID NULL_UUID = new UUID(0, 0);
    public static final IntegerColor BLUE = new IntegerColor(0xff008AE6);
    public static final IntegerColor GREEN = new IntegerColor(0xff07913c);
    public static final IntegerColor ORANGE = new IntegerColor(0xffcc7a22);
    public static final IntegerColor BLACK=new IntegerColor(0xff000000);
    public static final IntegerColor WHITE=new IntegerColor(0xffffffff);
    public static final IntegerColor GRAY=new IntegerColor(0xff565656);
    public static final IntegerColor YELLOW=new IntegerColor("F0E43A");
    public static final IntegerColor PURPLE=new IntegerColor("a81ab2");
    public static final IntegerColor DARK=new IntegerColor(0x282828);
    public static final int SLOT_SIZE = 16, SLOTWITHBORDERSIZE = 18, BUTTONHEIGHT = 20;
    public static final int MAXIMUMPLAYERREACH = 7;
    public static final int REDSTONE_TORCH_TICK_RATE = 2;
    public static final double PLAYER_SPRINT_SPEED = 0.43;
    public static final double PLAYER_WALK_SPEED = 0.32;
//    public static final double GRAVITY = NeoForgeMod.ENTITY_GRAVITY.get().getDefaultValue();
    /**
     * 24000 ticks
     */
    public final static int DAY_LENGTH = Functions.minutesToTicks(20);
    /**
     * 10 minutes
     */
    public final static int DAY_HALF = DAY_LENGTH / 2;
    public static final ResourceLocation GREY_SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Satako.ID, "textures/grey_slot.png");
    public static final Direction[] HORIZONTALS = new Direction[]{Direction.WEST, Direction.SOUTH, Direction.NORTH, Direction.EAST};
    public static HashMap<Fluid, FluidStack> FLUID_STACK_CACHE=new HashMap<>();
    public static HashMap<Integer,IntegerColor> COLOR_CACHE=new HashMap<>();
    public static HashMap<Item, ItemStack> ITEM_CACHE=new HashMap<>();
    public static int MAX_DISTANCE_AT_WHICH_TEXT_IS_DISTINGUISHABLE=23;
}
