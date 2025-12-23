package org.essentials.custom_background_music;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Import the specific class


public class MusicMuter {
    private static final Logger LOGGER = LogManager.getLogger();

    // Store the original volume to restore it later
    private final float originalMusicVolume = 1.0f;

    public MusicMuter() {
        if (FMLEnvironment.dist.isClient()) {
            // Logic for handling the mute state
            LOGGER.info("MusicMuter initialized for Minecraft 1.21.1");
        }
    }

    /**
     * Call this when org.essentials.custom_background_music.AudioManager.play() is invoked.
     */

    public static void muteMinecraftMusic() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options != null) {
            // Get current volume setting
            float currentVolume = mc.options.getSoundSourceVolume(SoundSource.MUSIC);

            // Mute by setting to 0
            mc.options.getSoundSourceVolume(SoundSource.MUSIC);
            mc.options.save();

            // Force sound engine to update immediately
            mc.getSoundManager().updateSourceVolume(SoundSource.MUSIC, 0.0f);
        }
    }

    /**
     * Call this when org.essentials.custom_background_music.AudioManager.stop() is invoked.
     */
    public static void unmuteMinecraftMusic(float previousVolume) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options != null) {
            // Restore volume (defaulting to 1.0f or a saved variable)
            mc.options.getSoundSourceVolume(SoundSource.MUSIC);
            mc.options.save();

            mc.getSoundManager().updateSourceVolume(SoundSource.MUSIC, previousVolume);
        }
    }
}