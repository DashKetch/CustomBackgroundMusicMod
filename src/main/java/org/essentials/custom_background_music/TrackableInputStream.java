package org.essentials.custom_background_music;

import org.jetbrains.annotations.NotNull;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple wrapper that keeps track of the total bytes read from the stream.
 */
public class TrackableInputStream extends FilterInputStream {
    private long bytesRead = 0;

    public TrackableInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        int b = super.read();
        if (b != -1) bytesRead++;
        return b;
    }

    @Override
    public int read(byte @NotNull [] b, int off, int len) throws IOException {
        int read = super.read(b, off, len);
        if (read != -1) bytesRead += read;
        return read;
    }

    public long getBytesRead() {
        return bytesRead;
    }
}