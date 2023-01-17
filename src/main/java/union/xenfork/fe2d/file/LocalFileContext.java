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
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The local file context.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class LocalFileContext extends FileContext {
    /**
     * Creates the file context with the given path.
     *
     * @param path the path.
     */
    public LocalFileContext(String path) {
        super(path);
    }

    @Override
    public String loadString() {
        try (var br = new BufferedReader(
            new FileReader(path(), StandardCharsets.UTF_8)
        )) {
            return loadString(br);
        } catch (IOException e) {
            throw fail(e);
        }
    }

    @Override
    public ByteBuffer loadBinary(long bufferSize) {
        Path path = Path.of(path());
        if (Files.isReadable(path)) {
            try (var fc = Files.newByteChannel(path)) {
                ByteBuffer buffer = MemoryUtil.memAlloc((int) fc.size() + 1);
                //noinspection StatementWithEmptyBody
                while (fc.read(buffer) != -1) ;
                return MemoryUtil.memSlice(buffer.flip());
            } catch (IOException e) {
                throw fail(e);
            }
        } else {
            throw new IllegalStateException(path() + " is not readable!");
        }
    }

    @Override
    public boolean shouldFreeBinary() {
        return true;
    }

    @Override
    public String toString() {
        return "{LocalFileContext: " + path() + '}';
    }
}
