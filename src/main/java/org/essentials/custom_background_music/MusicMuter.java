package org.essentials.custom_background_music;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.client.OptionInstance;

public class MusicMuter {

    private static Double originalVolume = null;

    // Removed the unused Player parameter. Minecraft.getInstance() targets the local client.
    public static void muteMinecraftMusic() {
        Minecraft mc = Minecraft.getInstance();

        // Safety check: Only run if the player is actually loaded into a world
        if (mc.player == null) return;

        mc.execute(() -> {
            OptionInstance<Double> musicOption = mc.options.getSoundSourceOptionInstance(SoundSource.MUSIC);

            if (musicOption != null) {
                double current = musicOption.get();

                if (current > 0.0) {
                    originalVolume = current;
                    musicOption.set(0.0);

                    // CRITICAL FIX: Do NOT call mc.options.save() here!
                    // We only want to mute it in memory, not save '0.0' to the user's settings file.

                    // Force sound engine update for the local player
                    mc.getSoundManager().updateSourceVolume(SoundSource.MUSIC, 0.0f);
                }
            }
        });
    }

    public static void unmuteMinecraftMusic() {
        if (originalVolume == null) return;

        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            OptionInstance<Double> musicOption = mc.options.getSoundSourceOptionInstance(SoundSource.MUSIC);

            if (musicOption != null) {
                musicOption.set(originalVolume);

                // Do NOT call mc.options.save() here either.

                // Restore sound engine volume
                mc.getSoundManager().updateSourceVolume(SoundSource.MUSIC, originalVolume.floatValue());
                originalVolume = null;
            }
        });
    }
}