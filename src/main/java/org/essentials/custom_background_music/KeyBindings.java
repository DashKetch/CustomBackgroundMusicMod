package org.essentials.custom_background_music;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "custom_background_music", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {

    // The category in the Options > Controls menu
    public static final String CATEGORY = "key.categories.custom_background_music";

    // The translation key for the specific binding name
    public static final String NAME = "key.custom_background_music.open_music_gui";

    // The actual KeyMapping instance to be checked in your game loop
    public static KeyMapping OPEN_MUSIC_GUI;

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        // Initialize the KeyMapping
        OPEN_MUSIC_GUI = new KeyMapping(
                NAME, // Description (Lang key)
                InputConstants.Type.KEYSYM, // Input Type (Keyboard)
                GLFW.GLFW_KEY_M, // Default Key (set to 'M')
                CATEGORY // Category (Lang key)
        );

        // Register it to the event
        event.register(OPEN_MUSIC_GUI);
    }
}