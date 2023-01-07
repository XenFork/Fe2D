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

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.overrun.binpacking.PackerRegionSize;
import union.xenfork.fe2d.Disposable;
import union.xenfork.fe2d.file.FileContext;
import union.xenfork.fe2d.graphics.GLStateManager;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.stb.STBImage.*;

/**
 * The 2D texture.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Texture implements Disposable, PackerRegionSize {
    /**
     * The texture with id 0.
     */
    public static final Texture ZERO = new Texture(0);
    private final int id;
    private final int width;
    private final int height;

    private Texture(int id) {
        this.id = id;
        width = 0;
        height = 0;
    }

    /**
     * Creates a texture with the given size.
     *
     * @param width  the width of the texture.
     * @param height the height of the texture.
     */
    protected Texture(int width, int height) {
        id = glGenTextures();
        this.width = width;
        this.height = height;
    }

    /**
     * Creates a texture and loads from the given file.
     *
     * @param fileContext the file context.
     * @param param       the texture parameters.
     * @return the texture.
     */
    public static Texture ofImage(FileContext fileContext, @Nullable TextureParam param) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            ByteBuffer image = loadImage(fileContext, width, height, channels, STBI_rgb_alpha);
            Texture texture = new Texture(width.get(0), height.get(0));
            int currTex = GLStateManager.textureBinding2D();
            GLStateManager.bindTexture2D(texture.id);
            if (param != null) {
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, param.minFilter());
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, param.magFilter());
                for (var e : param.customParamMap().entrySet()) {
                    glTexParameteri(GL_TEXTURE_2D, e.getKey(), e.getValue());
                }
            }
            glTexImage2D(GL_TEXTURE_2D,
                0,
                GL_RGBA8,
                texture.width(),
                texture.height(),
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                image);
            MemoryUtil.memFree(image);
            glGenerateMipmap(GL_TEXTURE_2D);
            GLStateManager.bindTexture2D(currTex);
            return texture;
        }
    }

    /**
     * Creates a texture and loads from the given file.
     *
     * @param fileContext the file context.
     * @return the texture.
     */
    public static Texture ofImage(FileContext fileContext) {
        return ofImage(fileContext, null);
    }

    /**
     * Loads an image from the given file context.
     *
     * @param fileContext     the file context.
     * @param width           stores the return value of width.
     * @param height          stores the return value of height.
     * @param channels        stores the return value of channels.
     * @param desiredChannels 0 or 1..4 to force that many components per pixel.
     * @return the image data. <b>MUST</b> be explicitly {@link MemoryUtil#memFree(Buffer) released}.
     * @throws IllegalStateException when failed to load image.
     */
    public static ByteBuffer loadImage(FileContext fileContext,
                                       IntBuffer width, IntBuffer height,
                                       IntBuffer channels,
                                       @MagicConstant(intValues = {
                                           STBI_default,
                                           STBI_grey,
                                           STBI_grey_alpha,
                                           STBI_rgb,
                                           STBI_rgb_alpha,
                                           0, 1, 2, 3, 4
                                       }) int desiredChannels)
        throws IllegalStateException {
        ByteBuffer buffer = stbi_load_from_memory(fileContext.loadBinary(), width, height, channels, desiredChannels);
        if (buffer == null) {
            throw new IllegalStateException("Failed to load image from context " + fileContext + ". Reason: " + stbi_failure_reason());
        }
        return buffer;
    }

    /**
     * Binds this texture.
     */
    public void bind() {
        GLStateManager.bindTexture2D(id());
    }

    /**
     * Gets the id of this texture.
     *
     * @return the id.
     */
    public int id() {
        return id;
    }

    /**
     * Gets the width of this texture.
     *
     * @return the width.
     */
    @Override
    public int width() {
        return width;
    }

    /**
     * Gets the height of this texture.
     *
     * @return the height.
     */
    @Override
    public int height() {
        return height;
    }

    @Override
    public void dispose() {
        glDeleteTextures(id);
    }
}
