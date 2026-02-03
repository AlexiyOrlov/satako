package dev.buildtool.satako;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundInstanceImpl extends AbstractTickableSoundInstance {
    private final float startVolume;
    private final Vec3i position;
    private final float volumeVactor;

    protected SoundInstanceImpl(SoundEvent sound, Vec3i location, float volumeFactor) {
        super(sound, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.volumeVactor = volumeFactor;
        startVolume = volume;
        position = location;
    }

    @Override
    public void tick() {
        double distanceSquared = Minecraft.getInstance().player.distanceToSqr(position.getX(), position.getY(), position.getZ());
        if (distanceSquared < 400) {
            double distance = Math.sqrt(distanceSquared);
            volume = (float) (startVolume / distance) * volumeVactor;
        } else
            volume = 0;
    }
}
