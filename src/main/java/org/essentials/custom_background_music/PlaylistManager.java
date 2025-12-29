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

    // Map of Playlist Name -> List of MP3 Files
    private final Map<String, List<File>> playlists = new HashMap<>();
    private final List<String> playlistNames = new ArrayList<>();

    private String currentPlaylistName = null;
    private List<File> currentQueue = new ArrayList<>();
    private int currentTrackIndex = -1;

    // Filter for MP3 files only
    private final FilenameFilter mp3Filter = (dir, name) -> name.toLowerCase().endsWith(".mp3");

    private PlaylistManager() {}

    public static PlaylistManager getInstance() { return INSTANCE; }

    /**
     * Scans the config/custom_music directory for folders.
     * Each folder becomes a playlist containing its MP3 files.
     */
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
                        // Sort files alphabetically to ensure consistent order
                        List<File> tracks = Arrays.stream(mp3s)
                                .sorted(Comparator.comparing(File::getName))
                                .collect(Collectors.toList());

                        playlists.put(folder.getName(), tracks);
                        playlistNames.add(folder.getName());
                    }
                }
            }
        }

        // Sort playlist names alphabetically
        Collections.sort(playlistNames);

        // Reset selection if the current playlist disappeared
        if (currentPlaylistName != null && !playlists.containsKey(currentPlaylistName)) {
            currentPlaylistName = null;
            currentQueue.clear();
        }

        LOGGER.info("Loaded {} playlists: {}", playlists.size(), playlistNames);
    }

    /**
     * Cycles to the next available playlist.
     */
    public void cyclePlaylist() {
        if (playlistNames.isEmpty()) return;

        int index = playlistNames.indexOf(currentPlaylistName);
        index++;
        if (index >= playlistNames.size()) {
            index = -1; // -1 represents "No Playlist" / Single File Mode
            currentPlaylistName = null;
            currentQueue.clear();
        } else {
            currentPlaylistName = playlistNames.get(index);
            currentQueue = new ArrayList<>(playlists.get(currentPlaylistName));
            // Optional: Shuffle here if you want random playback
            // Collections.shuffle(currentQueue);
        }
    }

    public String getCurrentPlaylistName() {
        return currentPlaylistName == null ? "None (Single File)" : currentPlaylistName;
    }

    public boolean hasPlaylistSelected() {
        return currentPlaylistName != null && !currentQueue.isEmpty();
    }

    /**
     * Starts playing the current playlist from the beginning.
     */
    public void startPlaylist() {
        if (currentQueue.isEmpty()) return;
        currentTrackIndex = 0;
        playCurrentTrack();
    }

    /**
     * Called by AudioManager when a track finishes naturally.
     */
    public void onTrackFinished() {
        if (currentPlaylistName == null) return; // Not in playlist mode

        currentTrackIndex++;
        if (currentTrackIndex < currentQueue.size()) {
            playCurrentTrack();
        } else {
            // End of playlist. Loop? Stop?
            // Let's Loop for now:
            currentTrackIndex = 0;
            playCurrentTrack();
        }
    }

    private void playCurrentTrack() {
        if (currentTrackIndex >= 0 && currentTrackIndex < currentQueue.size()) {
            File nextFile = currentQueue.get(currentTrackIndex);
            AudioManager.getInstance().loadMusicFile(nextFile);
            AudioManager.getInstance().play();
        }
    }
}