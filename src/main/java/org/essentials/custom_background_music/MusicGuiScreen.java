package org.essentials.custom_background_music;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.swing.*;
import java.io.File;

public class MusicGuiScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;

    private final AudioManager audioManager = AudioManager.getInstance();
    private Button uploadButton;
    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private Button deleteButton;
    private Button closeButton;
    private float volume = 1.0f;

    protected MusicGuiScreen() {
        super(Component.literal("Custom Music Player"));
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new MusicGuiScreen());
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 2 - 80;

        // Upload button
        uploadButton = Button.builder(
                Component.literal("Upload Music File"),
                button -> openFileChooser()
        ).bounds(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.addRenderableWidget(uploadButton);

        // Play button
        playButton = Button.builder(
                Component.literal("Play"),
                button -> {
                    audioManager.play();
                    MusicOverrideManager.getInstance().setCustomMusicPlaying(true);
                }
        ).bounds(centerX - BUTTON_WIDTH / 2, startY + 30, BUTTON_WIDTH / 3 - 5, BUTTON_HEIGHT).build();
        this.addRenderableWidget(playButton);

        // Pause button
        pauseButton = Button.builder(
                Component.literal("Pause"),
                button -> {
                    audioManager.pause();
                    MusicOverrideManager.getInstance().setCustomMusicPlaying(false);
                }
        ).bounds(centerX - BUTTON_WIDTH / 2 + BUTTON_WIDTH / 3 + 5, startY + 30, BUTTON_WIDTH / 3 - 5, BUTTON_HEIGHT).build();
        this.addRenderableWidget(pauseButton);

        // Stop button
        stopButton = Button.builder(
                Component.literal("Stop"),
                button -> {
                    audioManager.stop();
                    MusicOverrideManager.getInstance().setCustomMusicPlaying(false);
                }
        ).bounds(centerX - BUTTON_WIDTH / 2 + 2 * (BUTTON_WIDTH / 3 + 5), startY + 30, BUTTON_WIDTH / 3 - 5, BUTTON_HEIGHT).build();
        this.addRenderableWidget(stopButton);

        // Delete button
        deleteButton = Button.builder(
                Component.literal("Delete Current Music"),
                button -> {
                    audioManager.stop();
                    audioManager.cleanup();
                    MusicOverrideManager.getInstance().setCustomMusicPlaying(false);
                    CustomBackgroundMusic.LOGGER.info("Deleted custom music");
                }
        ).bounds(centerX - BUTTON_WIDTH / 2, startY + 60, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.addRenderableWidget(deleteButton);

        // Volume up button
        Button volumeUpButton = Button.builder(
                Component.literal("Vol +"),
                button -> {
                    volume = Math.min(1.0f, volume + 0.1f);
                    audioManager.setVolume(volume);
                }
        ).bounds(centerX - BUTTON_WIDTH / 2, startY + 90, BUTTON_WIDTH / 2 - 5, BUTTON_HEIGHT).build();
        this.addRenderableWidget(volumeUpButton);

        // Volume down button
        Button volumeDownButton = Button.builder(
                Component.literal("Vol -"),
                button -> {
                    volume = Math.max(0.0f, volume - 0.1f);
                    audioManager.setVolume(volume);
                }
        ).bounds(centerX + 5, startY + 90, BUTTON_WIDTH / 2 - 5, BUTTON_HEIGHT).build();
        this.addRenderableWidget(volumeDownButton);

        // Close button
        closeButton = Button.builder(
                Component.literal("Close"),
                button -> this.onClose()
        ).bounds(centerX - BUTTON_WIDTH / 2, startY + 120, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.addRenderableWidget(closeButton);

        updateButtonStates();
    }

    private void openFileChooser() {
        Thread fileChooserThread = new Thread(() -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory()) return true;
                        String name = f.getName().toLowerCase();
                        return name.endsWith(".ogg") || name.endsWith(".wav") || name.endsWith(".mp3");
                    }

                    @Override
                    public String getDescription() {
                        return "Audio Files (*.ogg, *.wav, *.mp3)";
                    }
                });

                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    Minecraft.getInstance().execute(() -> {
                        if (audioManager.loadMusicFile(selectedFile)) {
                            CustomBackgroundMusic.LOGGER.info("Loaded music file: " + selectedFile.getName());
                            updateButtonStates();
                        } else {
                            CustomBackgroundMusic.LOGGER.error("Failed to load music file: " + selectedFile.getName());
                        }
                    });
                }
            } catch (Exception e) {
                CustomBackgroundMusic.LOGGER.error("Error opening file chooser", e);
            }
        });
        fileChooserThread.start();
    }

    private void updateButtonStates() {
        boolean hasMusic = audioManager.hasLoadedMusic();
        playButton.active = hasMusic;
        pauseButton.active = hasMusic;
        stopButton.active = hasMusic;
        deleteButton.active = hasMusic;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Draw title
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        // Draw current file info
        String currentFile = audioManager.getCurrentFileName();
        if (currentFile != null) {
            guiGraphics.drawCenteredString(this.font, "Current: " + currentFile, this.width / 2, this.height / 2 - 100, 0xAAFFAA);
        } else {
            guiGraphics.drawCenteredString(this.font, "No music loaded", this.width / 2, this.height / 2 - 100, 0xAAAAAA);
        }

        // Draw status
        String status = audioManager.isPlaying() ? "Playing" : "Stopped";
        guiGraphics.drawCenteredString(this.font, "Status: " + status, this.width / 2, this.height / 2 + 50, 0xFFFFAA);

        // Draw volume
        guiGraphics.drawCenteredString(this.font, String.format("Volume: %.0f%%", volume * 100), this.width / 2, this.height / 2 + 65, 0xAAFFFF);
    }

    @Override
    public void tick() {
        super.tick();
        updateButtonStates();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}