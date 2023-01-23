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

import java.io.*;
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

    IllegalStateException fail(Exception e) {
        return new IllegalStateException("Failed to load file '" + path() + '\'', e);
    }

    static String loadString(BufferedReader br) throws IOException {
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
     * @throws IllegalStateException if failed to load the file.
     */
    public abstract String loadString() throws IllegalStateException;

    /**
     * Loads as binary from this file.
     *
     * @param bufferSize the initial buffer size. defaults to {@value #DEFAULT_BUFFER_SIZE}.
     * @return the binary data.
     * @throws IllegalStateException if failed to load the file.
     * @see #loadBinary()
     */
    public abstract ByteBuffer loadBinary(long bufferSize) throws IllegalStateException;

    /**
     * Loads as binary from this file.
     *
     * @return the binary data.
     * @throws IllegalStateException if failed to load the file.
     * @see #loadBinary(long)
     */
    public ByteBuffer loadBinary() throws IllegalStateException {
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
     * Creates a writer for this file context.
     * <p>
     * The writer is buffered, and must be explicitly closed. The encoding of the writer is UTF-8.
     *
     * @return the buffered writer.
     * @throws IllegalStateException if the file cannot be written, or failed to create the writer.
     */
    public abstract Writer createWriter() throws IllegalStateException;

    /**
     * Creates an input stream for this file context.
     * <p>
     * The input stream is buffered, and must be explicitly closed.
     *
     * @return the input stream.
     * @throws IllegalStateException if failed to create the input stream.
     */
    public abstract InputStream createInputStream() throws IllegalStateException;

    /**
     * Creates an output stream for this file context.
     * <p>
     * The output stream is buffered, and must be explicitly closed.
     *
     * @return the output stream.
     * @throws IllegalStateException if the file cannot be written, or failed to create the output stream.
     */
    public abstract OutputStream createOutputStream() throws IllegalStateException;

    /**
     * Gets the path.
     *
     * @return the path.
     */
    public String path() {
        return path;
    }
}
