package dev.buildtool.satako;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundWithDuration extends SoundEvent {
    private final int durationTicks;
    public SoundWithDuration(ResourceLocation sound, int duration) {
        super(sound, 16, false);
        this.durationTicks =duration;
    }

    public int getDurationTicks()
    {
        return durationTicks;
    }
}
