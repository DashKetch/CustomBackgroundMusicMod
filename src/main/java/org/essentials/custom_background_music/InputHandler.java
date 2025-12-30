package org.essentials.custom_background_music;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = CustomBackgroundMusic.MODID, value = Dist.CLIENT)
public class InputHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        PlaylistManager playlist = PlaylistManager.getInstance();
        // 1. Open GUI
        while (KeyBindings.OPEN_MUSIC_GUI.consumeClick()) {
            MusicGuiScreen.open();
        }

        // 2. Toggle Pause/Play
        while (KeyBindings.PAUSE_PLAY_MUSIC.consumeClick()) {
            AudioManager audio = AudioManager.getInstance();

            if (!audio.hasLoadedMusic() && playlist.hasPlaylistSelected()) {
                playlist.startPlaylist();
            } else {
                audio.togglePause();
            }
        }

        // 3. Next Track
        while (KeyBindings.NEXT_TRACK.consumeClick()) {
            if (PlaylistManager.getInstance().hasPlaylistSelected()) {
                PlaylistManager.getInstance().next();
            }
        }

        // 4. Previous Track
        while (KeyBindings.PREVIOUS_TRACK.consumeClick()) {
            if (PlaylistManager.getInstance().hasPlaylistSelected()) {
                PlaylistManager.getInstance().previous();
            }
        }

        // 5. Volume Up
        while (KeyBindings.VOLUME_UP.consumeClick()) {
            AudioManager audio = AudioManager.getInstance();
            if (audio.hasLoadedMusic()) {
                // Increase by 10%
                audio.setVolume(audio.getVolume() + 0.1f);
            }
        }

        // 6. Volume Down
        while (KeyBindings.VOLUME_DOWN.consumeClick()) {
            AudioManager audio = AudioManager.getInstance();
            if (audio.hasLoadedMusic()) {
                // Decrease by 10%
                audio.setVolume(audio.getVolume() - 0.1f);
            }
        }

        // 7. Stop Music
        while (KeyBindings.STOP_MUSIC.consumeClick()) {
            AudioManager audio = AudioManager.getInstance();
            if (audio.isPlaying() || audio.isPaused()) {
                audio.stop();
            } else if (audio.hasLoadedMusic()) {
                audio.play();
            } else if (!audio.hasLoadedMusic() && playlist.hasPlaylistSelected()) {
                playlist.startPlaylist();
            }
        }
    }
}