package org.essentials.custom_background_music;

import java.io.File;

public class AudioManager {
    private static final AudioManager INSTANCE = new AudioManager();
    private String currentFileName = null;
    private boolean playing = false;
    private float volume = 1.0f;

    private AudioManager() {}

    public static AudioManager getInstance() {
        return INSTANCE;
    }

    public boolean loadMusicFile(File file) {
        if (file != null && file.exists()) {
            this.currentFileName = file.getName();
            // Actual music loading logic will be implemented here next
            return true;
        }
        return false;
    }

    public void play() {
        if (currentFileName != null) {
            this.playing = true;
        }
    }

    public void pause() {
        this.playing = false;
    }

    public void stop() {
        this.playing = false;
    }

    public void cleanup() {
        stop();
        this.currentFileName = null;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getVolume() {
        return this.volume;
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean hasLoadedMusic() {
        return currentFileName != null;
    }

    public String getCurrentFileName() {
        return currentFileName;
    }
}