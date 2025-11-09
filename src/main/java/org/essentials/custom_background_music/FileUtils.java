package org.essentials.custom_background_music;

import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

public class FileUtils {

    public static ByteBuffer readFileToByteBuffer(File file) throws IOException {
        try (FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            ByteBuffer buffer = MemoryUtil.memAlloc((int) channel.size());
            channel.read(buffer);
            buffer.flip();
            return buffer;
        }
    }
}