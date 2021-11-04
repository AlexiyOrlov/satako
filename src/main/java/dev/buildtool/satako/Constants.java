package dev.buildtool.satako;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    public static final IntegerColor GREEN = new IntegerColor(0xff43CD80);
    public static final IntegerColor PASTEL2 = new IntegerColor(0xffFF8E65);
    public static final int SLOT_SIZE = 16, SLOTWITHBORDERSIZE = 18, BUTTONHEIGHT = 20;
    public static final int MAXIMUMPLAYERREACH = 7;
    public static final int REDSTONE_TORCH_TICK_RATE = 2;
    public static final double PLAYER_SPRINT_SPEED = 0.43;
    public static final double PLAYER_WALK_SPEED = 0.32;
    public static final double GRAVITY = ForgeMod.ENTITY_GRAVITY.get().getDefaultValue();
    /**
     * 24000 ticks
     */
    public final static int DAY_LENGTH = Functions.minutesToTicks(20);
    /**
     * 10 minutes
     */
    public final static int DAY_HALF = DAY_LENGTH / 2;
    public static final ResourceLocation GREY_SLOT_TEXTURE = new ResourceLocation(Satako.ID, "textures/grey_slot.png");
    public static final Direction[] HORIZONTALS = new Direction[]{Direction.WEST, Direction.SOUTH, Direction.NORTH, Direction.EAST};
    static final Logger SATAKO_LOGGER = LogManager.getLogger("Satako");

}
