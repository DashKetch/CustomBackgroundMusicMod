package org.essentials.custom_background_music;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.swing.*;
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

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int yPos = 40; // The "Anchor" point for the top of the UI

        // 1. Upload Button
        this.addRenderableWidget(Button.builder(Component.literal("Upload Music File"), b -> openFileChooser())
                .bounds(centerX - 100, yPos, 200, 20).build());

        // 2. Play/Pause/Stop Row (30 pixels below Upload)
        yPos += 30;
        playButton = this.addRenderableWidget(Button.builder(Component.literal("Play"), b -> audioManager.play())
                .bounds(centerX - 100, yPos, 64, 20).build());

        pauseButton = this.addRenderableWidget(Button.builder(Component.literal("Pause"), b -> audioManager.pause())
                .bounds(centerX - 32, yPos, 64, 20).build());

        stopButton = this.addRenderableWidget(Button.builder(Component.literal("Stop"), b -> audioManager.stop())
                .bounds(centerX + 36, yPos, 64, 20).build());

        // 3. Volume Row (25 pixels below Transport)
        yPos += 25;
        this.addRenderableWidget(Button.builder(Component.literal("Vol -"), b -> audioManager.setVolume(audioManager.getVolume() - 0.1f))
                .bounds(centerX - 100, yPos, 98, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Vol +"), b -> audioManager.setVolume(audioManager.getVolume() + 0.1f))
                .bounds(centerX + 2, yPos, 98, 20).build());

        // 4. Clear Button (30 pixels below Volume)
        yPos += 30;
        this.addRenderableWidget(Button.builder(Component.literal("Clear Track"), b -> audioManager.cleanup())
                .bounds(centerX - 100, yPos, 200, 20).build());

        // 5. Close Button (Fixed at the very bottom area with space for text)
        this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(centerX - 100, this.height - 40, 200, 20).build());

        updateButtonStates();
    }

    private void openFileChooser() {
        Thread thread = new Thread(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                JFileChooser chooser = new JFileChooser();
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    Minecraft.getInstance().execute(() -> audioManager.loadMusicFile(file));
                }
            } catch (Exception e) {
                LOGGER.error("File chooser error", e);
            }
        });
        thread.start();
    }

    private void updateButtonStates() {
        boolean hasMusic = audioManager.hasLoadedMusic();
        if (playButton != null) playButton.active = hasMusic;
        if (pauseButton != null) pauseButton.active = hasMusic;
        if (stopButton != null) stopButton.active = hasMusic;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, centerX, 15, 0xFFFFFF);

        // Status area: Placed safely between buttons and the Close button
        int statusY = this.height - 70;
        String fileName = audioManager.hasLoadedMusic() ? "§a" + audioManager.getCurrentFileName() : "§7None";
        guiGraphics.drawCenteredString(this.font, "File: " + fileName, centerX, statusY, 0xFFFFFF);

        String status = audioManager.isPlaying() ? "§6Playing" : "§cStopped";
        guiGraphics.drawString(this.font, "Status: " + status, centerX - 95, statusY + 12, 0xFFFFFF);
        guiGraphics.drawString(this.font, String.format("Vol: %.0f%%", audioManager.getVolume() * 100), centerX + 40, statusY + 12, 0xFFFFFF);
    }

    @Override
    public void tick() {
        updateButtonStates();
    }
}