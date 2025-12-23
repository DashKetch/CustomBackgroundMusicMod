package org.essentials.custom_background_music;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.client.OptionInstance;

public class MusicMuter {

    private static Double originalVolume = null;

    public static void muteMinecraftMusic() {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            // Use the getter method to access the private option
            OptionInstance<Double> musicOption = mc.options.getSoundSourceOptionInstance(SoundSource.MUSIC);

            if (musicOption != null) {
                double current = musicOption.get();

                if (current > 0.0) {
                    originalVolume = current;
                    musicOption.set(0.0);
                    mc.options.save();

                    // Force sound engine update
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
                mc.options.save();

                mc.getSoundManager().updateSourceVolume(SoundSource.MUSIC, originalVolume.floatValue());
                originalVolume = null;
            }
        });
    }
}