/*
 * Fork Engine 2D
 * Copyright (C) 2023 XenFork Union
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package union.xenfork.fe2d.file;

import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * The file context.
 *
 * @author squid233
 * @since 0.1.0
 */
public sealed abstract class FileContext permits InternalFileContext, LocalFileContext {
    /**
     * The default buffer size.
     */
    public static final long DEFAULT_BUFFER_SIZE = 8192;
    private final String path;

    /**
     * Creates the file context with the given path.
     *
     * @param path the path.
     */
    public FileContext(String path) {
        this.path = path;
    }

    IllegalStateException fail(IOException e) {
        return new IllegalStateException("Failed to load file '" + path() + '\'', e);
    }

    String loadString(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder(512);
        String line = br.readLine();
        if (line != null) {
            sb.append(line);
        }
        while ((line = br.readLine()) != null) {
            sb.append('\n').append(line);
        }
        return sb.toString();
    }

    /**
     * Loads as string from this file.
     *
     * @return the string.
     */
    public abstract String loadString();

    /**
     * Loads as binary from this file.
     *
     * @param bufferSize the initial buffer size. defaults to {@value #DEFAULT_BUFFER_SIZE}.
     * @return the binary data.
     * @see #loadBinary()
     */
    public abstract ByteBuffer loadBinary(long bufferSize);

    /**
     * Loads as binary from this file.
     *
     * @return the binary data.
     * @see #loadBinary(long)
     */
    public ByteBuffer loadBinary() {
        return loadBinary(DEFAULT_BUFFER_SIZE);
    }

    /**
     * Returns {@code true} if user should explicitly {@link MemoryUtil#memFree(Buffer) release} the buffer
     * that loaded from {@link #loadBinary(long) loadBinary}.
     *
     * @return {@code true} if user should explicitly {@link MemoryUtil#memFree(Buffer) release} the buffer
     * that loaded from {@link #loadBinary(long) loadBinary}.
     */
    public abstract boolean shouldFreeBinary();

    /**
     * Gets the path.
     *
     * @return the path.
     */
    public String path() {
        return path;
    }
}
