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

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.lwjgl.system.MemoryUtil.*;

/**
 * The internal file context.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class InternalFileContext extends FileContext {
    /**
     * Creates the file context with the given path.
     *
     * @param path the path.
     */
    public InternalFileContext(String path) {
        super(path);
    }

    private InputStream getInputStream() throws NullPointerException {
        return Objects.requireNonNull(
            ClassLoader.getSystemResourceAsStream(path()),
            "Failed to get file '" + path() + "' from classpath"
        );
    }

    @Override
    public String loadString() {
        try (var br = new BufferedReader(
            new InputStreamReader(getInputStream(), StandardCharsets.UTF_8)
        )) {
            return loadString(br);
        } catch (IOException | NullPointerException e) {
            throw fail(e);
        }
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        return memRealloc(buffer, newCapacity);
    }

    @Override
    public ByteBuffer loadBinary(long bufferSize) {
        try (var is = getInputStream();
             var rbc = Channels.newChannel(is)) {
            ByteBuffer buffer = memAlloc((int) bufferSize);
            while (true) {
                int bytes = rbc.read(buffer);
                if (bytes == -1) {
                    break;
                }
                if (!buffer.hasRemaining()) {
                    // +50%
                    buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2);
                }
            }
            return memSlice(buffer.flip());
        } catch (IOException | NullPointerException e) {
            throw fail(e);
        }
    }

    @Override
    public boolean shouldFreeBinary() {
        return true;
    }

    @Override
    public Writer createWriter() throws IllegalStateException {
        throw new IllegalStateException("Cannot write files to classpath!");
    }

    @Override
    public InputStream createInputStream() throws IllegalStateException {
        try {
            return new BufferedInputStream(getInputStream());
        } catch (NullPointerException e) {
            throw fail(e);
        }
    }

    @Override
    public OutputStream createOutputStream() throws IllegalStateException {
        throw new IllegalStateException("Cannot write files to classpath!");
    }

    @Override
    public String toString() {
        return "{InternalFileContext: " + path() + '}';
    }
}
