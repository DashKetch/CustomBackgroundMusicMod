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
    }
}