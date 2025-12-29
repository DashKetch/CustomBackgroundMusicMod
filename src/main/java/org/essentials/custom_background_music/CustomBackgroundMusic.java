package org.essentials.custom_background_music;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(CustomBackgroundMusic.MODID)
public class CustomBackgroundMusic {
    public static final String MODID = "custom_background_music";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CustomBackgroundMusic(ModContainer container) {
        createMusicDirectory();

        PlaylistManager.getInstance().refreshPlaylists();

        container.registerConfig(ModConfig.Type.CLIENT, ModConfigs.SPEC);

        NeoForge.EVENT_BUS.register(new MusicHudRenderer());
    }

    private void createMusicDirectory() {
        Path musicPath = FMLPaths.CONFIGDIR.get().resolve("custom_music");
        try {
            if (!Files.exists(musicPath)) {
                Files.createDirectories(musicPath);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to create music directory", e);
        }
    }
}