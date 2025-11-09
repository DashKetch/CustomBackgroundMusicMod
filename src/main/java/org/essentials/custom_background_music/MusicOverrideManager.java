package org.essentials.custom_background_music;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;

public class MusicOverrideManager {
    private static MusicOverrideManager instance;
    private boolean customMusicPlaying = false;

    public static MusicOverrideManager getInstance() {
        if (instance == null) {
            instance = new MusicOverrideManager();
            NeoForge.EVENT_BUS.register(instance);
        }
        return instance;
    }

    private MusicOverrideManager() {
    }

    @SubscribeEvent
    public void onPlaySound(PlaySoundEvent event) {
        SoundInstance sound = event.getSound();

        // Check if this is Minecraft's background music
        if (customMusicPlaying && sound != null && sound.getLocation().getPath().contains("music")) {
            // Cancel Minecraft's music when custom music is playing
            event.setSound(null);
            Custom_background_music.LOGGER.debug("Blocked Minecraft music: " + sound.getLocation());
        }
    }

    public void setCustomMusicPlaying(boolean playing) {
        this.customMusicPlaying = playing;

        if (playing) {
            // Stop any currently playing Minecraft music
            Minecraft.getInstance().getMusicManager().stopPlaying();
        }
    }

    public boolean isCustomMusicPlaying() {
        return customMusicPlaying;
    }
}