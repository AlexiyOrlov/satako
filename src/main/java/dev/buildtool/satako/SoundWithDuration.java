package dev.buildtool.satako;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundWithDuration extends SoundEvent {
    private final int duration;
    public SoundWithDuration(ResourceLocation sound, int duration) {
        super(sound, 16, false);
        this.duration=duration;
    }

    public int getDuration()
    {
        return duration;
    }
}
