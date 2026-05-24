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

            double current = musicOption.get();

            if (current > 0.0) {
                originalVolume = current;
                musicOption.set(0.0);

                // Force sound engine update for the local player
                mc.getSoundManager().updateSourceVolume(SoundSource.MUSIC, 0.0f);
            }
        });
    }

    public static void unmuteMinecraftMusic() {
        if (originalVolume == null) return;

        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            OptionInstance<Double> musicOption = mc.options.getSoundSourceOptionInstance(SoundSource.MUSIC);

            musicOption.set(originalVolume);

            // Restore sound engine volume
            mc.getSoundManager().updateSourceVolume(SoundSource.MUSIC, originalVolume.floatValue());
            originalVolume = null;
        });
    }
}