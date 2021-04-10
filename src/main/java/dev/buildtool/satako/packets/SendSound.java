package dev.buildtool.satako.packets;

import net.minecraft.util.SoundEvent;

/**
 * Created on 3/20/20.
 */
public class SendSound
{
    public float pitch, volume;
    public SoundEvent soundEvent;

    public SendSound(float pitch, float volume, SoundEvent soundEvent)
    {
        this.pitch = pitch;
        this.volume = volume;
        this.soundEvent = soundEvent;
    }
}
