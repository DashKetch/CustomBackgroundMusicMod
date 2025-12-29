package org.essentials.custom_background_music;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = CustomBackgroundMusic.MODID, value = Dist.CLIENT)
public class InputHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        // 1. Open GUI
        while (KeyBindings.OPEN_MUSIC_GUI.consumeClick()) {
            MusicGuiScreen.open();
        }

        // 2. Toggle Pause/Play
        while (KeyBindings.PAUSE_PLAY_MUSIC.consumeClick()) {
            AudioManager audio = AudioManager.getInstance();
            PlaylistManager playlist = PlaylistManager.getInstance();

            if (!audio.hasLoadedMusic() && playlist.hasPlaylistSelected()) {
                playlist.startPlaylist();
            } else {
                audio.togglePause();
            }
        }

        // 3. Next Track (Right Arrow)
        while (KeyBindings.NEXT_TRACK.consumeClick()) {
            if (PlaylistManager.getInstance().hasPlaylistSelected()) {
                PlaylistManager.getInstance().next();
            }
        }

        // 4. Previous Track (Left Arrow)
        while (KeyBindings.PREVIOUS_TRACK.consumeClick()) {
            if (PlaylistManager.getInstance().hasPlaylistSelected()) {
                PlaylistManager.getInstance().previous();
            }
        }
    }
}