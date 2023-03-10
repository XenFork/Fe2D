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

package union.xenfork.fe2d.graphics.texture;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.overrun.binpacking.PackerRegionSize;
import union.xenfork.fe2d.Disposable;
import union.xenfork.fe2d.file.FileContext;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Supplier;

import static org.lwjgl.stb.STBImage.*;

/**
 * The native image buffer wrapper.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class NativeImage implements PackerRegionSize, Disposable {
    private final int width;
    private final int height;
    private final ByteBuffer buffer;
    private boolean disposed = false;

    private NativeImage(int width, int height, ByteBuffer buffer) {
        this.width = width;
        this.height = height;
        this.buffer = buffer;
    }

    /**
     * Creates the image wrapper with the given buffer.
     * <p>
     * The buffer must be allocated with {@link MemoryUtil#memAlloc(int) memAlloc} or {@link MemoryUtil#memCalloc(int) memCalloc}.
     * <p>
     * The buffer will be released on {@link #dispose() disposing}.
     *
     * @param width  the width of the image.
     * @param height the height of the image.
     * @param buffer the buffer.
     * @return the native image.
     */
    public static NativeImage ofRawBuffer(int width, int height, ByteBuffer buffer) {
        return new NativeImage(width, height, buffer);
    }

    /**
     * Loads an image from the given file context.
     *
     * @param context         the file context.
     * @param desiredChannels the desired channels. defaults to 4.
     * @param fail            the buffer will be used if failed to load image. must be allocated with {@link MemoryUtil}.
     *                        you can print a message to warn. defaults to {@code null}.
     * @param bufferSize      the initial buffer size. defaults to {@value FileContext#DEFAULT_BUFFER_SIZE}.
     * @return the native image.
     * @throws IllegalStateException if failed to load the image and <i>{@code fail}</i> is {@code null}.
     * @see #load(FileContext, int)
     * @see #load(FileContext, Supplier)
     * @see #load(FileContext)
     */
    public static NativeImage load(FileContext context, int desiredChannels, @Nullable Supplier<@Nullable ByteBuffer> fail, long bufferSize)
        throws IllegalStateException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            ByteBuffer binary = context.loadBinary(bufferSize);
            ByteBuffer buffer = stbi_load_from_memory(binary, width, height, channels, desiredChannels);
            if (context.shouldFreeBinary()) {
                MemoryUtil.memFree(binary);
            }
            if (buffer == null && (fail == null || (buffer = fail.get()) == null)) {
                throw new IllegalStateException("Failed to load image from context " + context + ". Reason: " + stbi_failure_reason());
            }
            return new NativeImage(width.get(0), height.get(0), buffer);
        }
    }

    /**
     * Loads an image from the given file context.
     *
     * @param context         the file context.
     * @param desiredChannels the desired channels. defaults to 4.
     * @param fail            the buffer will be used if failed to load image. must be allocated with {@link MemoryUtil}.
     *                        you can print a message to warn. defaults to {@code null}.
     * @return the native image.
     * @throws IllegalStateException if failed to load the image and <i>{@code fail}</i> is {@code null}.
     * @see #load(FileContext, int)
     * @see #load(FileContext, Supplier)
     * @see #load(FileContext)
     */
    public static NativeImage load(FileContext context, int desiredChannels, @Nullable Supplier<@Nullable ByteBuffer> fail)
        throws IllegalStateException {
        return load(context, desiredChannels, fail, FileContext.DEFAULT_BUFFER_SIZE);
    }

    /**
     * Loads an image from the given file context.
     *
     * @param context         the file context.
     * @param desiredChannels the desired channels. defaults to 4.
     * @return the native image.
     * @throws IllegalStateException if failed to load the image and <i>{@code fail}</i> is {@code null}.
     * @see #load(FileContext, int, Supplier)
     * @see #load(FileContext, Supplier)
     * @see #load(FileContext)
     */
    public static NativeImage load(FileContext context, int desiredChannels)
        throws IllegalStateException {
        return load(context, desiredChannels, null);
    }

    /**
     * Loads an image from the given file context.
     *
     * @param context the file context.
     * @param fail    the buffer will be used if failed to load image. must be allocated with {@link MemoryUtil}.
     *                you can print a message to warn. defaults to {@code null}.
     * @return the native image.
     * @throws IllegalStateException if failed to load the image and <i>{@code fail}</i> is {@code null}.
     * @see #load(FileContext, int, Supplier)
     * @see #load(FileContext, int)
     * @see #load(FileContext)
     */
    public static NativeImage load(FileContext context, @Nullable Supplier<@Nullable ByteBuffer> fail)
        throws IllegalStateException {
        return load(context, STBI_rgb_alpha, fail);
    }

    /**
     * Loads an image from the given file context.
     *
     * @param context the file context.
     * @return the native image.
     * @throws IllegalStateException if failed to load the image and <i>{@code fail}</i> is {@code null}.
     * @see #load(FileContext, int, Supplier)
     * @see #load(FileContext, int)
     * @see #load(FileContext, Supplier)
     */
    public static NativeImage load(FileContext context)
        throws IllegalStateException {
        return load(context, STBI_rgb_alpha, null);
    }

    /**
     * Gets the width of this image.
     *
     * @return the width.
     */
    @Override
    public int width() {
        return width;
    }

    /**
     * Gets the height of this image.
     *
     * @return the height.
     */
    @Override
    public int height() {
        return height;
    }

    /**
     * Gets the buffer of this image.
     *
     * @return the buffer.
     */
    public ByteBuffer buffer() {
        return buffer;
    }

    @Override
    public void dispose() {
        if (disposed) return;
        disposed = true;
        MemoryUtil.memFree(buffer);
    }
}
