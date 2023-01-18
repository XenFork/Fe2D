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
import org.lwjgl.system.MemoryUtil;
import org.overrun.binpacking.PackerRegionSize;
import union.xenfork.fe2d.Disposable;
import union.xenfork.fe2d.file.FileContext;
import union.xenfork.fe2d.graphics.GLStateManager;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL30C.*;

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
    /**
     * A simple dot.
     */
    private static Texture whiteDot;
    private final int id;
    private final int width;
    private final int height;
    private boolean disposed = false;

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
     * Sets the parameters for the current binding texture.
     *
     * @param param the texture parameters.
     */
    protected static void acceptParameters(@Nullable TextureParam param) {
        if (param != null) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, param.minFilter());
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, param.magFilter());
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, param.baseLevel());
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, param.maxLevel());
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_LOD, param.minLod());
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_LOD, param.maxLod());
            for (var e : param.customParamMap().entrySet()) {
                glTexParameteri(GL_TEXTURE_2D, e.getKey(), e.getValue());
            }
        }
    }

    /**
     * Returns {@code true} if the white dot texture is created.
     *
     * @return {@code true} if the white dot texture is created.
     */
    public static boolean hasWhiteDot() {
        return whiteDot != null;
    }

    /**
     * Gets the white dot texture, or creates a new one if it is not created.
     *
     * @return the white dot texture.
     */
    public static Texture whiteDot() {
        if (whiteDot == null) {
            ByteBuffer buffer = MemoryUtil.memAlloc(4).putInt(0, 0xffffffff);
            NativeImage image = NativeImage.ofRawBuffer(1, 1, buffer);
            whiteDot = ofImage(image, null, GL_RGBA8, GL_RGBA);
            image.dispose();
        }
        return whiteDot;
    }

    /**
     * Creates a texture and loads from the given image.
     *
     * @param image          the image. will not be disposed.
     * @param param          the texture parameters.
     * @param internalFormat the texture internal format.
     * @param format         the texel data format.
     * @return the texture.
     */
    public static Texture ofImage(NativeImage image, @Nullable TextureParam param, int internalFormat, int format) {
        Texture texture = new Texture(image.width(), image.height());
        int currTex = GLStateManager.textureBinding2D();
        GLStateManager.bindTexture2D(texture.id);
        acceptParameters(param);
        glTexImage2D(GL_TEXTURE_2D,
            0,
            internalFormat,
            image.width(),
            image.height(),
            0,
            format,
            GL_UNSIGNED_BYTE,
            image.buffer());
        glGenerateMipmap(GL_TEXTURE_2D);
        GLStateManager.bindTexture2D(currTex);
        return texture;
    }

    /**
     * Creates a texture and loads from the given image.
     *
     * @param image the image. will not be disposed.
     * @param param the texture parameters.
     * @return the texture.
     */
    public static Texture ofImage(NativeImage image, @Nullable TextureParam param) {
        return ofImage(image, param, GL_RGBA8, GL_RGBA);
    }

    /**
     * Creates a texture and loads from the given image.
     *
     * @param image the image. will not be disposed.
     * @return the texture.
     */
    public static Texture ofImage(NativeImage image) {
        return ofImage(image, null);
    }

    /**
     * Creates a texture and loads from the given file.
     *
     * @param context        the file context.
     * @param param          the texture parameters.
     * @param internalFormat the texture internal format.
     * @param format         the texel data format.
     * @return the texture.
     */
    public static Texture ofFile(FileContext context, @Nullable TextureParam param, int internalFormat, int format) {
        NativeImage image = NativeImage.load(context);
        Texture texture = ofImage(image, param, internalFormat, format);
        image.dispose();
        return texture;
    }

    /**
     * Creates a texture and loads from the given file.
     *
     * @param context the file context.
     * @param param   the texture parameters.
     * @return the texture.
     */
    public static Texture ofFile(FileContext context, @Nullable TextureParam param) {
        return ofFile(context, param, GL_RGBA8, GL_RGBA);
    }

    /**
     * Creates a texture and loads from the given file.
     *
     * @param context the file context.
     * @return the texture.
     */
    public static Texture ofFile(FileContext context) {
        return ofFile(context, null);
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
        if (disposed) return;
        disposed = true;
        glDeleteTextures(id);
    }
}
