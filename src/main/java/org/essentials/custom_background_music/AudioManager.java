package org.essentials.custom_background_music;

import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.Player;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;

public class AudioManager {
    private static final AudioManager INSTANCE = new AudioManager();

    private String currentFileName = null;
    private File musicFile = null;
    private Player player;
    private Thread musicThread;
    private FileInputStream fis;

    private float volume = 1.0f; // Default 100%
    private long totalLength = 0;
    private long pauseLocation = 0;
    private boolean isPaused = false;

    private AudioManager() {}

    public static AudioManager getInstance() { return INSTANCE; }

    /**
     * Loads the music file and resets tracking variables.
     */
    public boolean loadMusicFile(File file) {
        if (file != null && file.exists()) {
            this.musicFile = file;
            this.currentFileName = file.getName();
            this.pauseLocation = 0;
            this.isPaused = false;
            return true;
        }
        return false;
    }

    /**
     * Starts or Resumes playback in a background thread.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void play() {
        if (musicFile == null) return;

        if (musicThread != null && !isPaused) return;

        musicThread = new Thread(() -> {
            try {
                fis = new FileInputStream(musicFile);
                totalLength = fis.available();

                if (pauseLocation > 0 && pauseLocation < totalLength) {
                    fis.skip(totalLength - pauseLocation);
                }

                player = new Player(fis);
                isPaused = false;
                setVolume(this.volume);

                player.play();

                // FIX: Check if player is null before calling isComplete()
                // The player becomes null if pause() or stop() is called during playback
                if (player != null && player.isComplete()) {
                    cleanup();
                }
            } catch (Exception e) {
                // If the thread is interrupted by player.close(),
                // we don't necessarily want to print a full stack trace.
                System.out.println("Playback thread stopped or interrupted.");
            } finally {
                // Ensure the stream is closed if the thread ends
                try { if (fis != null) fis.close(); } catch (Exception ignored) {}
            }
        });

        musicThread.setDaemon(true);
        musicThread.start();
    }

    /**
     * Pauses the music by capturing the current stream position and closing the player.
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public void pause() {
        if (player != null && !isPaused) {
            try {
                // Store how many bytes were left in the stream
                pauseLocation = fis.available();
                isPaused = true;
                player.close(); // JLayer must close to stop the thread
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Toggles between Pause and Play.
     */
    public void pauseButton() {
        if (isPaused) {
            play();
        } else {
            pause();
        }
    }

    /**
     * Stops the music entirely and resets the playback position.
     */
    public void stop() {
        pauseLocation = 0;
        isPaused = false;
        if (player != null) {
            player.close();
            player = null;
        }
        if (musicThread != null) {
            musicThread.interrupt();
            musicThread = null;
        }
    }

    /**
     * Sets the volume (0.0 to 1.0) using reflection to access the SourceDataLine.
     */
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));

        if (player != null) {
            try {
                AudioDevice device = getAudioDevice();
                if (device instanceof JavaSoundAudioDevice jsDevice) {
                    Field sourceField = JavaSoundAudioDevice.class.getDeclaredField("source");
                    sourceField.setAccessible(true);
                    SourceDataLine source = (SourceDataLine) sourceField.get(jsDevice);

                    if (source != null && source.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        FloatControl gainControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
                        // Convert linear 0-1 to decibels
                        float dB = (float) (Math.log(this.volume <= 0 ? 0.0001 : this.volume) / Math.log(10.0) * 20.0);
                        gainControl.setValue(dB);
                    }
                }
            } catch (Exception e) {
                // Reflection failed or volume not supported
            }
        }
    }

    public float getVolume() { return this.volume; }

    private AudioDevice getAudioDevice() throws Exception {
        Field deviceField = Player.class.getDeclaredField("audio");
        deviceField.setAccessible(true);
        return (AudioDevice) deviceField.get(player);
    }

    public boolean isPlaying() { return player != null && !isPaused; }

    @SuppressWarnings("unused")
    public boolean isPaused() { return isPaused; }

    public boolean hasLoadedMusic() { return currentFileName != null; }

    public String getCurrentFileName() { return currentFileName; }

    /**
     * Resets the manager completely.
     */
    public void cleanup() {
        stop();
        currentFileName = null;
        musicFile = null;
    }
}