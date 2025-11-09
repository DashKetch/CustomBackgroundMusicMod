package org.essentials.custom_background_music;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Custom_background_music.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // General Settings
    private static final ModConfigSpec.BooleanValue ENABLE_CUSTOM_MUSIC = BUILDER
            .comment("Enable custom background music (overrides Minecraft's music)")
            .define("enableCustomMusic", true);

    private static final ModConfigSpec.DoubleValue DEFAULT_VOLUME = BUILDER
            .comment("Default volume for custom music (0.0 to 1.0)")
            .defineInRange("defaultVolume", 1.0, 0.0, 1.0);

    private static final ModConfigSpec.BooleanValue AUTO_PLAY_ON_LOAD = BUILDER
            .comment("Automatically play music when a file is loaded")
            .define("autoPlayOnLoad", false);

    private static final ModConfigSpec.BooleanValue LOOP_MUSIC = BUILDER
            .comment("Loop the music when it finishes")
            .define("loopMusic", true);

    private static final ModConfigSpec.BooleanValue BLOCK_VANILLA_MUSIC = BUILDER
            .comment("Block Minecraft's default background music when custom music is playing")
            .define("blockVanillaMusic", true);

    private static final ModConfigSpec.BooleanValue PERSIST_MUSIC_ACROSS_SESSIONS = BUILDER
            .comment("Remember the last loaded music file across game sessions")
            .define("persistMusicAcrossSessions", false);

    private static final ModConfigSpec.ConfigValue<String> LAST_MUSIC_FILE_PATH = BUILDER
            .comment("Path to the last loaded music file (internal use)")
            .define("lastMusicFilePath", "");

    // Volume Controls
    private static final ModConfigSpec.DoubleValue VOLUME_STEP = BUILDER
            .comment("Volume adjustment step when using volume buttons (0.01 to 0.5)")
            .defineInRange("volumeStep", 0.1, 0.01, 0.5);

    // GUI Settings
    private static final ModConfigSpec.BooleanValue SHOW_FILE_NAME_IN_GUI = BUILDER
            .comment("Show the current music file name in the GUI")
            .define("showFileNameInGui", true);

    private static final ModConfigSpec.BooleanValue SHOW_PLAYBACK_STATUS = BUILDER
            .comment("Show playback status (Playing/Stopped) in the GUI")
            .define("showPlaybackStatus", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    // Cached config values
    public static boolean enableCustomMusic;
    public static double defaultVolume;
    public static boolean autoPlayOnLoad;
    public static boolean loopMusic;
    public static boolean blockVanillaMusic;
    public static boolean persistMusicAcrossSessions;
    public static String lastMusicFilePath;
    public static double volumeStep;
    public static boolean showFileNameInGui;
    public static boolean showPlaybackStatus;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        enableCustomMusic = ENABLE_CUSTOM_MUSIC.get();
        defaultVolume = DEFAULT_VOLUME.get();
        autoPlayOnLoad = AUTO_PLAY_ON_LOAD.get();
        loopMusic = LOOP_MUSIC.get();
        blockVanillaMusic = BLOCK_VANILLA_MUSIC.get();
        persistMusicAcrossSessions = PERSIST_MUSIC_ACROSS_SESSIONS.get();
        lastMusicFilePath = LAST_MUSIC_FILE_PATH.get();
        volumeStep = VOLUME_STEP.get();
        showFileNameInGui = SHOW_FILE_NAME_IN_GUI.get();
        showPlaybackStatus = SHOW_PLAYBACK_STATUS.get();

        Custom_background_music.LOGGER.info("Custom Background Music config loaded");
    }

    // Helper method to save the last music file path
    public static void saveLastMusicFilePath(String path) {
        LAST_MUSIC_FILE_PATH.set(path);
        SPEC.save();
    }

    // Helper method to update volume setting
    public static void saveDefaultVolume(double volume) {
        DEFAULT_VOLUME.set(volume);
        SPEC.save();
    }
}