package org.essentials.custom_background_music;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class PlaylistManager {
    private static final PlaylistManager INSTANCE = new PlaylistManager();
    private static final Logger LOGGER = LogUtils.getLogger();


    public enum LoopMode {
        NO_LOOP,
        LOOP_PLAYLIST,
        LOOP_SONG
    }

    // Data
    private final Map<String, List<File>> playlists = new HashMap<>();
    private final List<String> playlistNames = new ArrayList<>();

    // State
    private String currentPlaylistName = null;
    private List<File> originalQueue = new ArrayList<>(); // The sorted list (A-Z)
    private List<File> playbackQueue = new ArrayList<>(); // The actual list being played (affected by shuffle)

    private int currentTrackIndex = -1;
    private boolean isShuffle = false;
    private LoopMode loopMode = LoopMode.NO_LOOP;

    private final FilenameFilter mp3Filter = (dir, name) -> name.toLowerCase().endsWith(".mp3");

    private PlaylistManager() {}

    public static PlaylistManager getInstance() { return INSTANCE; }

    public void refreshPlaylists() {
        playlists.clear();
        playlistNames.clear();

        Path configPath = FMLPaths.CONFIGDIR.get().resolve("custom_music");
        File rootDir = configPath.toFile();

        if (rootDir.exists() && rootDir.isDirectory()) {
            File[] directories = rootDir.listFiles(File::isDirectory);
            if (directories != null) {
                for (File folder : directories) {
                    File[] mp3s = folder.listFiles(mp3Filter);
                    if (mp3s != null && mp3s.length > 0) {
                        List<File> tracks = Arrays.stream(mp3s)
                                .sorted(Comparator.comparing(File::getName))
                                .collect(Collectors.toList());

                        playlists.put(folder.getName(), tracks);
                        playlistNames.add(folder.getName());
                        // USE LOGGER HERE
                        LOGGER.info("Loaded playlist '{}' with {} tracks.", folder.getName(), tracks.size());
                    }
                }
            }
        }
        Collections.sort(playlistNames);
    }

    // --- Control Methods ---

    public void cyclePlaylist() {
        if (playlistNames.isEmpty()) return;

        int index = playlistNames.indexOf(currentPlaylistName);
        index++;

        if (index >= playlistNames.size()) {
            // Reset to None
            currentPlaylistName = null;
            originalQueue.clear();
            playbackQueue.clear();
        } else {
            // Load new playlist
            currentPlaylistName = playlistNames.get(index);
            originalQueue = new ArrayList<>(playlists.get(currentPlaylistName));
            rebuildPlaybackQueue();
        }
        // Stop music when switching playlists
        AudioManager.getInstance().stop();
    }

    public void toggleShuffle() {
        this.isShuffle = !this.isShuffle;
        rebuildPlaybackQueue();
    }

    public void cycleLoopMode() {
        // Cycle: NO_LOOP -> PLAYLIST -> SONG -> NO_LOOP
        int nextOrdinal = (loopMode.ordinal() + 1) % LoopMode.values().length;
        this.loopMode = LoopMode.values()[nextOrdinal];
    }

    public void startPlaylist() {
        if (playbackQueue.isEmpty()) return;
        currentTrackIndex = 0;
        playCurrentTrack();
    }

    public void next() {
        if (playbackQueue.isEmpty()) return;

        // If looping song, 'Next' button should force next song, NOT loop the current one
        // So we treat it like a normal advance
        currentTrackIndex++;

        if (currentTrackIndex >= playbackQueue.size()) {
            // End of list
            if (loopMode == LoopMode.LOOP_PLAYLIST) {
                currentTrackIndex = 0;
            } else {
                // Stop if no loop (or single song loop, we usually wrap to start or stop)
                // Let's wrap to 0 but stop
                currentTrackIndex = 0;
                AudioManager.getInstance().stop();
                return;
            }
        }
        playCurrentTrack();
    }

    public void previous() {
        if (playbackQueue.isEmpty()) return;

        currentTrackIndex--;
        if (currentTrackIndex < 0) {
            if (loopMode == LoopMode.LOOP_PLAYLIST) {
                currentTrackIndex = playbackQueue.size() - 1;
            } else {
                currentTrackIndex = 0; // Clamp to start
            }
        }
        playCurrentTrack();
    }

    /**
     * Called automatically when a song ends.
     */
    public void onTrackFinished() {
        if (playbackQueue.isEmpty()) return;

        if (loopMode == LoopMode.LOOP_SONG) {
            // Replay same index
            playCurrentTrack();
        } else {
            // Advance naturally
            next();
        }
    }

    // --- Helpers ---

    private void rebuildPlaybackQueue() {
        // Remember currently playing file to try and keep it playing or find its new spot
        File currentFile = (currentTrackIndex >= 0 && currentTrackIndex < playbackQueue.size())
                ? playbackQueue.get(currentTrackIndex) : null;

        playbackQueue = new ArrayList<>(originalQueue);

        if (isShuffle) {
            long seed = System.nanoTime();
            Collections.shuffle(playbackQueue, new Random(seed));
        }

        // Try to find the new index of the currently playing song so playback flow isn't broken
        if (currentFile != null) {
            currentTrackIndex = playbackQueue.indexOf(currentFile);
        } else {
            currentTrackIndex = 0;
        }
    }

    private void playCurrentTrack() {
        if (currentTrackIndex >= 0 && currentTrackIndex < playbackQueue.size()) {
            AudioManager.getInstance().stop();
            File nextFile = playbackQueue.get(currentTrackIndex);

            // USE LOGGER HERE
            LOGGER.debug("PlaylistManager: Playing track index {} - {}", currentTrackIndex, nextFile.getName());

            AudioManager.getInstance().loadMusicFile(nextFile);
            AudioManager.getInstance().play();
        } else {
            // USE LOGGER HERE
            LOGGER.warn("PlaylistManager: Attempted to play invalid track index: {}", currentTrackIndex);
        }
    }

    // --- Getters for GUI ---

    public String getCurrentPlaylistName() {
        return currentPlaylistName == null ? "None (Single)" : currentPlaylistName;
    }

    public boolean hasPlaylistSelected() {
        return currentPlaylistName != null && !playbackQueue.isEmpty();
    }

    public boolean isShuffle() { return isShuffle; }

    public String getLoopModeString() {
        return switch (loopMode) {
            case NO_LOOP -> "Loop: Off";
            case LOOP_PLAYLIST -> "Loop: List";
            case LOOP_SONG -> "Loop: Song";
        };
    }
}