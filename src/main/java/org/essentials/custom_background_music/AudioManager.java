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
    private float volume = 1.0f; // Default 100%

    private AudioManager() {}

    public static AudioManager getInstance() { return INSTANCE; }

    public boolean loadMusicFile(File file) {
        if (file != null && file.exists()) {
            this.musicFile = file;
            this.currentFileName = file.getName();
            return true;
        }
        return false;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void play() {
        if (musicFile == null) return;
        stop();

        musicThread = new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(musicFile);
                player = new Player(fis);
                // Apply volume immediately after start
                setVolume(this.volume);
                player.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        musicThread.setDaemon(true);
        musicThread.start();
    }

    /**
     * Sets the volume of the playback.
     * @param volume A value between 0.0 (silent) and 1.0 (full volume)
     */
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));

        if (player != null) {
            try {
                // We use reflection to access the internal 'source' line in JLayer's AudioDevice
                AudioDevice device = getAudioDevice();
                if (device instanceof JavaSoundAudioDevice jsDevice) {
                    Field sourceField = JavaSoundAudioDevice.class.getDeclaredField("source");
                    sourceField.setAccessible(true);
                    SourceDataLine source = (SourceDataLine) sourceField.get(jsDevice);

                    if (source != null && source.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        FloatControl gainControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
                        // Convert linear 0-1 volume to decibels
                        float dB = (float) (Math.log(this.volume <= 0 ? 0.0001 : this.volume) / Math.log(10.0) * 20.0);
                        gainControl.setValue(dB);
                    }
                }
            } catch (Exception e) {
                // If reflection fails, we log it and continue
            }
        }
    }

    public float getVolume() { return this.volume; }

    private AudioDevice getAudioDevice() throws Exception {
        Field deviceField = Player.class.getDeclaredField("audio");
        deviceField.setAccessible(true);
        return (AudioDevice) deviceField.get(player);
    }

    public void stop() {
        if (player != null) { player.close(); player = null; }
        if (musicThread != null) { musicThread.interrupt(); musicThread = null; }
    }

    public void pause() { stop(); }
    public boolean isPlaying() { return player != null; }
    public boolean hasLoadedMusic() { return currentFileName != null; }
    public String getCurrentFileName() { return currentFileName; }
    public void cleanup() { stop(); currentFileName = null; musicFile = null; }
}