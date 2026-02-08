package dev.buildtool.satako;

import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;

import java.util.HashMap;

public class SoundController {
    public static SoundEngine soundEngine;
    private static final HashMap<BlockPos, SoundInstanceImpl> soundMap = new HashMap<>();

    public static void startBlockSound(SoundEvent sound, BlockPos pos, float volume) {
        SoundInstanceImpl soundInstance = soundMap.get(pos);
        if (soundInstance == null || !soundEngine.isActive(soundInstance)) {
            soundInstance = new SoundInstanceImpl(sound, pos, volume);
            soundEngine.soundManager.play(soundInstance);
            soundMap.put(pos, soundInstance);
        }
    }

    public static void stopBlockSound(BlockPos pos) {
        SoundInstanceImpl existing = soundMap.get(pos);
        if (existing != null) {
            soundEngine.soundManager.stop(existing);
            soundMap.remove(pos);
        }
    }
}
