package org.essentials.custom_background_music;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

import java.io.File;

public class MusicGuiScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final AudioManager audioManager = AudioManager.getInstance();

    private Button playButton;
    private Button pauseButton;
    private Button stopButton;

    public MusicGuiScreen() {
        super(Component.literal("Custom Music Player"));
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new MusicGuiScreen());
    }

    @SuppressWarnings("UnusedAssignment")
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int yPos = 30; // Moved up slightly to fit more controls

        PlaylistManager playlistManager = PlaylistManager.getInstance();

        // 1. Playlist Controls
        // Button to cycle through playlists found in config/custom_music/
        this.addRenderableWidget(Button.builder(Component.literal("Playlist: " + playlistManager.getCurrentPlaylistName()), b -> {
            playlistManager.cyclePlaylist();
            b.setMessage(Component.literal("Playlist: " + playlistManager.getCurrentPlaylistName()));

            // If we switched to a playlist, we can optionally autoload the first track (but not play yet)
            // or just let the user hit Play.
        }).bounds(centerX - 100, yPos, 150, 20).build());

        // Refresh button (in case user added folders while game was running)
        this.addRenderableWidget(Button.builder(Component.literal("Ref"), b -> {
            playlistManager.refreshPlaylists();
            // Force the cycle button to update text (requires finding the button or reopening screen)
            // Simplest way: reopen screen
            MusicGuiScreen.open();
        }).bounds(centerX + 55, yPos, 45, 20).build());

        yPos += 25;

        // 2. Upload Button (Single File Mode)
        this.addRenderableWidget(Button.builder(Component.literal("Select Single File"), b -> openFileChooser())
                .bounds(centerX - 100, yPos, 200, 20).build());

        // 3. Transport Row
        yPos += 30;
        playButton = this.addRenderableWidget(Button.builder(Component.literal("Play"), b -> {
            // Logic: If a playlist is selected and nothing is loaded, start the playlist
            if (playlistManager.hasPlaylistSelected() && !audioManager.hasLoadedMusic()) {
                playlistManager.startPlaylist();
            } else {
                audioManager.play();
            }
        }).bounds(centerX - 100, yPos, 64, 20).build());

        pauseButton = this.addRenderableWidget(Button.builder(Component.literal("Pause"), b -> audioManager.togglePause())
                .bounds(centerX - 32, yPos, 64, 20).build());

        stopButton = this.addRenderableWidget(Button.builder(Component.literal("Stop"), b -> audioManager.stop())
                .bounds(centerX + 36, yPos, 64, 20).build());

        // 4. Volume Row
        yPos += 25;
        this.addRenderableWidget(Button.builder(Component.literal("Vol -"), b -> audioManager.setVolume(audioManager.getVolume() - 0.1f))
                .bounds(centerX - 100, yPos, 98, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Vol +"), b -> audioManager.setVolume(audioManager.getVolume() + 0.1f))
                .bounds(centerX + 2, yPos, 98, 20).build());

        // 5. Clear/Close
        yPos += 30;
        this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(centerX - 100, this.height - 35, 200, 20).build());

        updateButtonStates();
    }

    private void openFileChooser() {
        Thread thread = new Thread(() -> {
            try {
                // JLayer only supports MP3. Loading WAV/OGG will crash.
                // We strictly filter for *.mp3 here.
                String filePath = TinyFileDialogs.tinyfd_openFileDialog(
                        "Select MP3 File",
                        "",
                        null,
                        "MP3 Files (*.mp3)",
                        false
                );

                if (filePath != null) {
                    File file = new File(filePath);
                    Minecraft.getInstance().execute(() -> {
                        if (audioManager.loadMusicFile(file)) {
                            LOGGER.info("Selected file: {}", file.getAbsolutePath());
                        }
                    });
                }
            } catch (Exception e) {
                LOGGER.error("File chooser error", e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void updateButtonStates() {
        boolean hasMusic = audioManager.hasLoadedMusic();
        boolean hasPlaylist = PlaylistManager.getInstance().hasPlaylistSelected();

        // Play button is active if music is loaded OR if we have a playlist ready to start
        if (playButton != null) {
            playButton.active = (hasMusic || hasPlaylist) && !audioManager.isPlaying();
        }

        if (pauseButton != null) {
            pauseButton.active = hasMusic;
            pauseButton.setMessage(Component.literal(audioManager.isPlaying() ? "Pause" : "Resume"));
        }

        if (stopButton != null) stopButton.active = hasMusic;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, centerX, 15, 0xFFFFFF);

        int statusY = this.height - 65;
        String fileName = audioManager.hasLoadedMusic() ? "§a" + audioManager.getCurrentFileName() : "§7None";
        guiGraphics.drawCenteredString(this.font, "File: " + fileName, centerX, statusY, 0xFFFFFF);

        String status;
        if (audioManager.isPlaying()) status = "§6Playing";
        else if (audioManager.hasLoadedMusic() && !audioManager.isPlaying() && audioManager.getCurrentFileName() != null) status = "§ePaused/Ready";
        else status = "§cStopped";

        guiGraphics.drawString(this.font, "Status: " + status, centerX - 95, statusY + 12, 0xFFFFFF);
        guiGraphics.drawString(this.font, String.format("Vol: %.0f%%", audioManager.getVolume() * 100), centerX + 40, statusY + 12, 0xFFFFFF);
    }

    @Override
    public void tick() {
        updateButtonStates();
    }
}