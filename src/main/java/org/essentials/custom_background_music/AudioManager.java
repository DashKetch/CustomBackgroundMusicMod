package org.essentials.custom_background_music;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.loading.FMLPaths;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

public class AudioManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static AudioManager instance;

    private int sourceId = -1;
    private int bufferId = -1;
    private ByteBuffer audioData;
    private String currentFileName;

    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    private AudioManager() {
        createSource();
    }

    private void createSource() {
        if (sourceId == -1) {
            sourceId = AL10.alGenSources();
            AL10.alSourcef(sourceId, AL10.AL_GAIN, 1.0f);
            AL10.alSourcei(sourceId, AL10.AL_LOOPING, AL10.AL_TRUE);
        }
    }

    public List<File> getMusicFiles() {
        List<File> musicFiles = new ArrayList<>();
        File folder = FMLPaths.CONFIGDIR.get().resolve("custom_music").toFile();
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> {
                String lowercase = name.toLowerCase();
                return lowercase.endsWith(".ogg") || lowercase.endsWith(".mp3") || lowercase.endsWith(".wav");
            });
            if (files != null) {
                for (File f : files) musicFiles.add(f);
            }
        }
        return musicFiles;
    }

    public boolean loadMusicFile(File file) {
        String fileName = file.getName().toLowerCase();
        stop();
        cleanup();

        if (fileName.endsWith(".ogg")) {
            return loadOggFile(file);
        } else if (fileName.endsWith(".wav") || fileName.endsWith(".mp3")) {
            return loadWavOrMp3File(file);
        }
        return false;
    }

    private boolean loadOggFile(File file) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer channelsBuffer = stack.mallocInt(1);
            IntBuffer sampleRateBuffer = stack.mallocInt(1);
            ShortBuffer rawAudioBuffer = STBVorbis.stb_vorbis_decode_filename(file.getAbsolutePath(), channelsBuffer, sampleRateBuffer);

            if (rawAudioBuffer == null) return false;

            int format = (channelsBuffer.get(0) == 1) ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16;
            bufferId = AL10.alGenBuffers();
            AL10.alBufferData(bufferId, format, rawAudioBuffer, sampleRateBuffer.get(0));
            AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);
            currentFileName = file.getName();
            MemoryUtil.memFree(rawAudioBuffer);
            return true;
        } catch (Exception e) { return false; }
    }

    private boolean loadWavOrMp3File(File file) {
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            AudioFormat baseFormat = in.getFormat();
            AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
            AudioInputStream din = AudioSystem.getAudioInputStream(targetFormat, in);
            byte[] rawData = din.readAllBytes();
            audioData = MemoryUtil.memAlloc(rawData.length);
            audioData.put(rawData).flip();

            int alFormat = (targetFormat.getChannels() == 1) ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16;
            bufferId = AL10.alGenBuffers();
            AL10.alBufferData(bufferId, alFormat, audioData, (int) targetFormat.getSampleRate());
            AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);
            currentFileName = file.getName();
            din.close();
            return true;
        } catch (UnsupportedAudioFileException | IOException e) { return false; }
    }

    public void play() {
        if (bufferId != -1) AL10.alSourcePlay(sourceId);
    }

    public void pause() {
        if (sourceId != -1) AL10.alSourcePause(sourceId);
    }

    public void stop() {
        if (sourceId != -1) AL10.alSourceStop(sourceId);
    }

    public void setVolume(float volume) {
        if (sourceId != -1) AL10.alSourcef(sourceId, AL10.AL_GAIN, Math.max(0.0f, Math.min(1.0f, volume)));
    }

    public boolean isPlaying() {
        return sourceId != -1 && AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    public String getCurrentFileName() { return currentFileName; }

    public boolean hasLoadedMusic() { return bufferId != -1; }

    public void cleanup() {
        if (sourceId != -1) AL10.alSourcei(sourceId, AL10.AL_BUFFER, 0);
        if (bufferId != -1) { AL10.alDeleteBuffers(bufferId); bufferId = -1; }
        if (audioData != null) { MemoryUtil.memFree(audioData); audioData = null; }
    }
}