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
    private static final int SPACING = 24; // Increased spacing to prevent squashing

    private final AudioManager audioManager = AudioManager.getInstance();

    // Buttons
    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private Button clearButton;

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
        int startY = this.height / 2 - 60;

        // 1. Upload Button (Top)
        this.addRenderableWidget(Button.builder(
                Component.literal("Upload Music File"),
                button -> openFileChooser()
        ).bounds(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // 2. Play/Pause/Stop Row (Middle)
        int rowY = startY + SPACING + 10;
        int smallBtnWidth = (BUTTON_WIDTH / 3) - 4;

        playButton = this.addRenderableWidget(Button.builder(Component.literal("Play"), b -> audioManager.play())
                .bounds(centerX - BUTTON_WIDTH / 2, rowY, smallBtnWidth, BUTTON_HEIGHT).build());

        pauseButton = this.addRenderableWidget(Button.builder(Component.literal("Pause"), b -> audioManager.pause())
                .bounds(centerX - (smallBtnWidth / 2), rowY, smallBtnWidth, BUTTON_HEIGHT).build());

        stopButton = this.addRenderableWidget(Button.builder(Component.literal("Stop"), b -> audioManager.stop())
                .bounds(centerX + (BUTTON_WIDTH / 2) - smallBtnWidth, rowY, smallBtnWidth, BUTTON_HEIGHT).build());

        // 3. Volume Row
        int volY = rowY + SPACING;
        this.addRenderableWidget(Button.builder(Component.literal("Vol -"), b -> adjustVolume(-0.1f))
                .bounds(centerX - BUTTON_WIDTH / 2, volY, BUTTON_WIDTH / 2 - 2, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(Component.literal("Vol +"), b -> adjustVolume(0.1f))
                .bounds(centerX + 2, volY, BUTTON_WIDTH / 2 - 2, BUTTON_HEIGHT).build());

        // 4. Clear/Close (Bottom)
        clearButton = this.addRenderableWidget(Button.builder(Component.literal("Clear Track"), b -> {
            audioManager.stop();
            audioManager.cleanup();
        }).bounds(centerX - BUTTON_WIDTH / 2, volY + SPACING + 10, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(centerX - BUTTON_WIDTH / 2, volY + (SPACING * 2) + 10, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        updateButtonStates();
    }

    private void adjustVolume(float amount) {
        volume = Math.max(0.0f, Math.min(1.0f, volume + amount));
        audioManager.setVolume(volume);
    }

    private void openFileChooser() {
        // Swing utilities need to run on a separate thread to not freeze Minecraft
        Thread thread = new Thread(() -> {
            JFileChooser chooser = new JFileChooser();
            // Set to user directory or config directory
            chooser.setDialogTitle("Select Music (.ogg, .wav, .mp3)");
            int returnVal = chooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                // Hand the file back to the Main Minecraft Thread
                Minecraft.getInstance().execute(() -> {
                    if (audioManager.loadMusicFile(file)) {
                        LOGGER.info("Successfully loaded: " + file.getName());
                    }
                });
            }
        });
        thread.start();
    }

    private void updateButtonStates() {
        boolean hasMusic = audioManager.hasLoadedMusic();
        if (playButton != null) playButton.active = hasMusic;
        if (pauseButton != null) pauseButton.active = hasMusic;
        if (stopButton != null) stopButton.active = hasMusic;
        if (clearButton != null) clearButton.active = hasMusic;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        String currentFile = audioManager.getCurrentFileName();
        String fileText = (currentFile != null) ? "File: " + currentFile : "No music loaded";
        guiGraphics.drawCenteredString(this.font, fileText, this.width / 2, this.height / 2 - 90, 0xAAFFAA);

        String status = audioManager.isPlaying() ? "Playing" : "Stopped/Paused";
        guiGraphics.drawString(this.font, "Status: " + status, (this.width / 2) - 95, this.height / 2 + 60, 0xFFFFAA);
        guiGraphics.drawString(this.font, String.format("Volume: %.0f%%", volume * 100), (this.width / 2) + 30, this.height / 2 + 60, 0xAAFFFF);
    }

    @Override
    public void tick() {
        updateButtonStates();
    }
}