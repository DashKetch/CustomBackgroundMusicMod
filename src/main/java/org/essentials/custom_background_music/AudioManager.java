package org.essentials.custom_background_music;

import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.Player;

import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;

public class AudioManager {
    private static final AudioManager INSTANCE = new AudioManager();

    private String currentFileName = null;
    private File musicFile = null;
    private Player player;
    private Thread musicThread;
    private TrackableInputStream trackableStream;

    // Playback state
    private float volume = 1.0f;
    private long pauseLocation = 0;
    private boolean isPaused = false;

    private AudioManager() {}

    public static AudioManager getInstance() { return INSTANCE; }

    public boolean loadMusicFile(File file) {
        if (file != null && file.exists()) {
            cleanup();
            this.musicFile = file;
            this.currentFileName = file.getName();
            return true;
        }
        return false;
    }

    public synchronized void play() {
        if (musicFile == null || isPlaying()) return;

        MusicMuter.muteMinecraftMusic();

        musicThread = new Thread(() -> {
            try (FileInputStream fis = new FileInputStream(musicFile)) {
                if (pauseLocation > 0) {
                    long skipped = fis.skip(pauseLocation);
                    if (skipped < pauseLocation) pauseLocation = skipped;
                }

                trackableStream = new TrackableInputStream(new BufferedInputStream(fis));
                player = new Player(trackableStream);
                isPaused = false;

                setVolume(this.volume);
                player.play();

                // --- CHANGED SECTION START ---
                if (player != null && player.isComplete()) {
                    // Song finished naturally.
                    // We call stop() to clean up resources, then notify the playlist manager.
                    stop();

                    // Trigger the next song in the playlist
                    PlaylistManager.getInstance().onTrackFinished();
                }
                // --- CHANGED SECTION END ---

            } catch (Exception e) {
                System.out.println("Audio stream closed.");
            }
        });

        musicThread.setDaemon(true);
        musicThread.start();
    }

    public synchronized void pause() {
        if (player != null && !isPaused) {
            isPaused = true;
            if (trackableStream != null) {
                pauseLocation += trackableStream.getBytesRead();
            }
            player.close();
            player = null;
            trackableStream = null;
        }
        MusicMuter.unmuteMinecraftMusic();
    }

    public void togglePause() {
        if (isPaused) {
            play();
        } else if (isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    public synchronized void stop() {
        isPaused = false;
        pauseLocation = 0;
        if (player != null) {
            player.close();
            player = null;
        }
        trackableStream = null;
        if (musicThread != null) {
            musicThread.interrupt();
            musicThread = null;
        }
        MusicMuter.unmuteMinecraftMusic();
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        if (player == null) return;
        try {
            AudioDevice device = getAudioDevice();
            if (device instanceof JavaSoundAudioDevice jsDevice) {
                Field sourceField = JavaSoundAudioDevice.class.getDeclaredField("source");
                sourceField.setAccessible(true);
                SourceDataLine source = (SourceDataLine) sourceField.get(jsDevice);
                if (source != null && source.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
                    float dB = (float) (Math.log(this.volume <= 0.0f ? 0.0001f : this.volume) / Math.log(10.0) * 20.0);
                    gainControl.setValue(dB);
                }
            }
        } catch (Exception ignored) {}
    }

    public float getVolume() { return this.volume; }

    private AudioDevice getAudioDevice() throws Exception {
        Field deviceField = Player.class.getDeclaredField("audio");
        deviceField.setAccessible(true);
        return (AudioDevice) deviceField.get(player);
    }

    public boolean isPlaying() {
        return player != null && musicThread != null && musicThread.isAlive() && !isPaused;
    }

    // --- GUI SUPPORT METHODS ---

    public boolean isPaused() { return isPaused; }

    public boolean hasLoadedMusic() {
        return musicFile != null && musicFile.exists();
    }

    public String getCurrentFileName() {
        return currentFileName != null ? currentFileName : "None";
    }

    public void cleanup() {
        stop();
        currentFileName = null;
        musicFile = null;
    }
}