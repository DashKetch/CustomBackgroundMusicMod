package org.essentials.custom_background_music;

import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(CustomBackgroundMusic.MODID)
public class CustomBackgroundMusic {
    public static final String MODID = "custom_background_music";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static KeyMapping OPEN_MUSIC_GUI;

    public CustomBackgroundMusic() {
        // Create the music folder in /config/custom_music
        Path musicPath = FMLPaths.CONFIGDIR.get().resolve("custom_music");
        try {
            if (!Files.exists(musicPath)) {
                Files.createDirectories(musicPath);
                LOGGER.info("Created custom music directory at: {}", musicPath);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to create music directory", e);
        }
    }

    @SuppressWarnings("removal")
    @EventBusSubscriber(modid = CustomBackgroundMusic.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            OPEN_MUSIC_GUI = new KeyMapping(
                    "key.custom_background_music.open_music_gui",
                    GLFW.GLFW_KEY_M,
                    "key.categories.custom_background_music"
            );
            event.register(OPEN_MUSIC_GUI);
        }
    }
}