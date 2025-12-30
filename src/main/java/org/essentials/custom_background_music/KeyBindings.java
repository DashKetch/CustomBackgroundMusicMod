package org.essentials.custom_background_music;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = CustomBackgroundMusic.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {
    public static final String CATEGORY = "key.categories.custom_background_music";
    public static KeyMapping OPEN_MUSIC_GUI;
    public static KeyMapping PAUSE_PLAY_MUSIC;
    public static KeyMapping NEXT_TRACK;
    public static KeyMapping PREVIOUS_TRACK;
    public static KeyMapping VOLUME_UP;
    public static KeyMapping VOLUME_DOWN;

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        OPEN_MUSIC_GUI = new KeyMapping(
                "key.custom_background_music.open_music_gui", // This is the ID causing the crash
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                CATEGORY
        );
        PAUSE_PLAY_MUSIC = new KeyMapping(
                "key.custom_background_music.pause_play_music", // Your translation key
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                CATEGORY
        );
        NEXT_TRACK = new KeyMapping(
                "key.custom_background_music.next_track",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT, // Default to Right Arrow
                CATEGORY
        );

        PREVIOUS_TRACK = new KeyMapping(
                "key.custom_background_music.previous_track",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT, // Default to Left Arrow
                CATEGORY
        );
        VOLUME_UP = new KeyMapping(
                "key.custom_background_music.volume_up",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UP, // Default to Up Arrow
                CATEGORY
        );

        VOLUME_DOWN = new KeyMapping(
                "key.custom_background_music.volume_down",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_DOWN, // Default to Down Arrow
                CATEGORY
        );
        event.register(OPEN_MUSIC_GUI);
        event.register(PAUSE_PLAY_MUSIC);
        event.register(NEXT_TRACK);
        event.register(PREVIOUS_TRACK);
        event.register(VOLUME_UP);
        event.register(VOLUME_DOWN);
    }
}