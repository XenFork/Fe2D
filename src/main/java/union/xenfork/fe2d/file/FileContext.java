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

import java.nio.ByteBuffer;

/**
 * The file context.
 *
 * @author squid233
 * @since 0.1.0
 */
public sealed abstract class FileContext permits InternalFileContext {
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

    /**
     * Loads as string from this file.
     *
     * @return the string.
     */
    public abstract String loadString();

    /**
     * Loads as binary from this file.
     *
     * @param bufferSize the initial buffer size. defaults to {@link #DEFAULT_BUFFER_SIZE}.
     * @return the binary data.
     */
    public abstract ByteBuffer loadBinary(long bufferSize);

    /**
     * Loads as binary from this file.
     *
     * @return the binary data.
     */
    public ByteBuffer loadBinary() {
        return loadBinary(DEFAULT_BUFFER_SIZE);
    }

    /**
     * Gets the path.
     *
     * @return the path.
     */
    public String path() {
        return path;
    }
}
