package org.essentials.custom_background_music;

import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class AudioManager {
    private static AudioManager instance;

    private int sourceId = -1;
    private int bufferId = -1;
    private ByteBuffer audioData;
    private String currentFileName;
    private boolean isPlaying = false;

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
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

    public boolean loadMusicFile(File file) {
        try {
            stop();
            cleanup();

            String fileName = file.getName().toLowerCase();

            if (fileName.endsWith(".ogg")) {
                return loadOggFile(file);
            } else if (fileName.endsWith(".wav") || fileName.endsWith(".mp3")) {
                return loadWavOrMp3File(file);
            }

            CustomBackgroundMusic.LOGGER.error("Unsupported audio format: " + fileName);
            return false;

        } catch (Exception e) {
            CustomBackgroundMusic.LOGGER.error("Failed to load music file", e);
            return false;
        }
    }

    private boolean loadOggFile(File file) {
        try {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer channelsBuffer = stack.mallocInt(1);
                IntBuffer sampleRateBuffer = stack.mallocInt(1);

                ShortBuffer rawAudioBuffer = STBVorbis.stb_vorbis_decode_filename(
                        file.getAbsolutePath(),
                        channelsBuffer,
                        sampleRateBuffer
                );

                if (rawAudioBuffer == null) {
                    CustomBackgroundMusic.LOGGER.error("Failed to decode OGG file");
                    return false;
                }

                int channels = channelsBuffer.get(0);
                int sampleRate = sampleRateBuffer.get(0);

                int format = (channels == 1) ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16;

                bufferId = AL10.alGenBuffers();
                AL10.alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

                AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);

                currentFileName = file.getName();

                MemoryUtil.memFree(rawAudioBuffer);

                return true;
            }
        } catch (Exception e) {
            CustomBackgroundMusic.LOGGER.error("Failed to load OGG file", e);
            return false;
        }
    }

    private boolean loadWavOrMp3File(File file) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioInputStream.getFormat();

            // Convert to PCM if needed
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                AudioFormat pcmFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        format.getSampleRate(),
                        16,
                        format.getChannels(),
                        format.getChannels() * 2,
                        format.getSampleRate(),
                        false
                );
                audioInputStream = AudioSystem.getAudioInputStream(pcmFormat, audioInputStream);
                format = pcmFormat;
            }

            byte[] audioBytes = audioInputStream.readAllBytes();
            audioData = MemoryUtil.memAlloc(audioBytes.length);
            audioData.put(audioBytes);
            audioData.flip();

            int alFormat;
            if (format.getChannels() == 1) {
                alFormat = format.getSampleSizeInBits() == 8 ? AL10.AL_FORMAT_MONO8 : AL10.AL_FORMAT_MONO16;
            } else {
                alFormat = format.getSampleSizeInBits() == 8 ? AL10.AL_FORMAT_STEREO8 : AL10.AL_FORMAT_STEREO16;
            }

            bufferId = AL10.alGenBuffers();
            AL10.alBufferData(bufferId, alFormat, audioData, (int) format.getSampleRate());

            AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);

            currentFileName = file.getName();
            audioInputStream.close();

            return true;

        } catch (Exception e) {
            CustomBackgroundMusic.LOGGER.error("Failed to load WAV/MP3 file", e);
            return false;
        }
    }

    public void play() {
        if (bufferId != -1 && sourceId != -1) {
            AL10.alSourcePlay(sourceId);
            isPlaying = true;
            CustomBackgroundMusic.LOGGER.info("Playing custom music: " + currentFileName);
        }
    }

    public void pause() {
        if (sourceId != -1) {
            AL10.alSourcePause(sourceId);
            isPlaying = false;
        }
    }

    public void stop() {
        if (sourceId != -1) {
            AL10.alSourceStop(sourceId);
            isPlaying = false;
        }
    }

    public void setVolume(float volume) {
        if (sourceId != -1) {
            AL10.alSourcef(sourceId, AL10.AL_GAIN, Math.max(0.0f, Math.min(1.0f, volume)));
        }
    }

    public void cleanup() {
        if (bufferId != -1) {
            AL10.alDeleteBuffers(bufferId);
            bufferId = -1;
        }

        if (audioData != null) {
            MemoryUtil.memFree(audioData);
            audioData = null;
        }

        currentFileName = null;
    }

    public void shutdown() {
        cleanup();
        if (sourceId != -1) {
            AL10.alDeleteSources(sourceId);
            sourceId = -1;
        }
    }

    public boolean isPlaying() {
        if (sourceId != -1) {
            int state = AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE);
            return state == AL10.AL_PLAYING;
        }
        return false;
    }

    public String getCurrentFileName() {
        return currentFileName;
    }

    public boolean hasLoadedMusic() {
        return bufferId != -1 && currentFileName != null;
    }
}