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

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        OPEN_MUSIC_GUI = new KeyMapping(
                "key.custom_background_music.open_music_gui", // This is the ID causing the crash
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                CATEGORY
        );
        event.register(OPEN_MUSIC_GUI);
    }
}