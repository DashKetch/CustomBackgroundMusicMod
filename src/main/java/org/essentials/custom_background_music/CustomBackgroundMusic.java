package org.essentials.custom_background_music;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(CustomBackgroundMusic.MODID)
public class CustomBackgroundMusic {
    public static final String MODID = "custom_background_music";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CustomBackgroundMusic() {
        createMusicDirectory();
    }

    private void createMusicDirectory() {
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
}