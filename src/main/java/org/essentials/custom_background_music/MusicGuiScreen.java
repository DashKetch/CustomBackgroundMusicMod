package org.essentials.custom_background_music;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

import javax.swing.*;
import java.io.File;

public class MusicGuiScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;

    private final AudioManager audioManager = AudioManager.getInstance();
    private Button uploadButton;
    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private Button deleteButton;
    private float volume = 1.0f;

    public MusicGuiScreen() {
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
        uploadButton = this.addRenderableWidget(Button.builder(
                Component.literal("Upload Music File"),
                button -> openFileChooser()
        ).bounds(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Play button
        playButton = this.addRenderableWidget(Button.builder(
                Component.literal("Play"),
                button -> audioManager.play()
        ).bounds(centerX - BUTTON_WIDTH / 2, startY + 30, BUTTON_WIDTH / 3 - 5, BUTTON_HEIGHT).build());

        // Pause button
        pauseButton = this.addRenderableWidget(Button.builder(
                Component.literal("Pause"),
                button -> audioManager.pause()
        ).bounds(centerX - BUTTON_WIDTH / 2 + BUTTON_WIDTH / 3 + 5, startY + 30, BUTTON_WIDTH / 3 - 5, BUTTON_HEIGHT).build());

        // Stop button
        stopButton = this.addRenderableWidget(Button.builder(
                Component.literal("Stop"),
                button -> audioManager.stop()
        ).bounds(centerX - BUTTON_WIDTH / 2 + 2 * (BUTTON_WIDTH / 3 + 5), startY + 30, BUTTON_WIDTH / 3 - 5, BUTTON_HEIGHT).build());

        // Delete/Cleanup button
        deleteButton = this.addRenderableWidget(Button.builder(
                Component.literal("Clear Loaded Track"),
                button -> {
                    audioManager.stop();
                    audioManager.cleanup();
                }
        ).bounds(centerX - BUTTON_WIDTH / 2, startY + 60, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Volume buttons
        this.addRenderableWidget(Button.builder(Component.literal("Vol +"), b -> {
            volume = Math.min(1.0f, volume + 0.1f);
            audioManager.setVolume(volume);
        }).bounds(centerX - BUTTON_WIDTH / 2, startY + 90, BUTTON_WIDTH / 2 - 5, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(Component.literal("Vol -"), b -> {
            volume = Math.max(0.0f, volume - 0.1f);
            audioManager.setVolume(volume);
        }).bounds(centerX + 5, startY + 90, BUTTON_WIDTH / 2 - 5, BUTTON_HEIGHT).build());

        // Close button
        this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(centerX - BUTTON_WIDTH / 2, startY + 120, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        updateButtonStates();
    }

    private void openFileChooser() {
        Thread fileChooserThread = new Thread(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    Minecraft.getInstance().execute(() -> {
                        if (audioManager.loadMusicFile(selectedFile)) {
                            updateButtonStates();
                        }
                    });
                }
            } catch (Exception e) {
                LOGGER.error("Error opening file chooser", e);
            }
        });
        fileChooserThread.start();
    }

    private void updateButtonStates() {
        boolean hasMusic = audioManager.hasLoadedMusic();
        if (playButton != null) playButton.active = hasMusic;
        if (pauseButton != null) pauseButton.active = hasMusic;
        if (stopButton != null) stopButton.active = hasMusic;
        if (deleteButton != null) deleteButton.active = hasMusic;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        String currentFile = audioManager.getCurrentFileName();
        String fileText = (currentFile != null) ? "Current: " + currentFile : "No music loaded";
        guiGraphics.drawCenteredString(this.font, fileText, this.width / 2, this.height / 2 - 100, (currentFile != null) ? 0xAAFFAA : 0xAAAAAA);

        String status = audioManager.isPlaying() ? "Playing" : "Stopped/Paused";
        guiGraphics.drawCenteredString(this.font, "Status: " + status, this.width / 2, this.height / 2 + 50, 0xFFFFAA);
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