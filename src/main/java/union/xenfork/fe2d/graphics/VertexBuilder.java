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

package union.xenfork.fe2d.graphics;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

/**
 * The vertex builder.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class VertexBuilder {
    private ByteBuffer buffer;
    private long position;

    /**
     * Creates a vertex builder.
     */
    public VertexBuilder() {
    }

    /**
     * Creates a vertex builder with the given buffer.
     *
     * @param buffer the buffer.
     */
    public VertexBuilder(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    private void growBuffer(long grown) {
        if (buffer == null) {
            buffer = MemoryUtil.memCalloc((int) grown);
        } else if (position + grown > buffer.capacity()) {
            buffer = MemoryUtil.memRealloc(buffer, Math.max(Math.max(buffer.capacity() + (int) grown, buffer.capacity() * 3 / 2), 2));
        }
    }

    public VertexBuilder bytes(byte... bytes) {
        growBuffer(bytes.length);
        for (byte v : bytes) {
            buffer.put((int) position, v);
            position++;
        }
        return this;
    }

    public VertexBuilder ints(int... ints) {
        growBuffer(ints.length * 4L);
        for (int v : ints) {
            buffer.putInt((int) position, v);
            position += 4L;
        }
        return this;
    }

    public VertexBuilder floats(float... floats) {
        growBuffer(floats.length * 4L);
        for (float v : floats) {
            buffer.putFloat((int) position, v);
            position += 4L;
        }
        return this;
    }

    public ByteBuffer buffer() {
        return buffer;
    }

    public long position() {
        return position;
    }
}
