package org.essentials.custom_background_music;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@Mod(CustomBackgroundMusic.MODID)
public class CustomBackgroundMusic {
    public static final String MODID = "custom_background_music";

    // 1. Declare the KeyMapping here so it can be accessed globally (e.g., from your ClientTickEvent)
    public static KeyMapping OPEN_MUSIC_GUI;

    // 2. Use a static inner class for Client events.
    // 'value = Dist.CLIENT' ensures this code never loads on the Server, preventing crashes.
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            // Initialize the KeyMapping
            OPEN_MUSIC_GUI = new KeyMapping(
                    "key.custom_background_music.open_music_gui", // Name (Lang key)
                    InputConstants.Type.KEYSYM,                     // Type (Keyboard)
                    GLFW.GLFW_KEY_M,                                // Default Key (M)
                    "key.categories.custom_background_music"        // Category (Lang key)
            );

            // Register the key to the game
            event.register(OPEN_MUSIC_GUI);
        }
    }
}