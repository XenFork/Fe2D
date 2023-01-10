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
import org.overrun.binpacking.GrowingPacker;
import org.overrun.binpacking.Packer;
import org.overrun.binpacking.PackerFitPos;
import org.overrun.binpacking.PackerRegion;
import union.xenfork.fe2d.file.FileContext;
import union.xenfork.fe2d.graphics.GLStateManager;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL30C.*;

/**
 * The texture atlas.
 *
 * @author squid233
 * @since 0.1.0
 */
public class TextureAtlas extends Texture {
    private final Map<String, TextureRegion> regionMap = new HashMap<>();

    /**
     * Creates a texture with the given size.
     *
     * @param width  the width of the texture.
     * @param height the height of the texture.
     */
    protected TextureAtlas(int width, int height) {
        super(width, height);
    }

    /**
     * Creates an entry with the given file context and name.
     *
     * @param context the file context.
     * @param name    the entry name. defaults to the path of <i>{@code context}</i>.
     * @return the entry.
     * @see #entry(FileContext)
     */
    public static Entry entry(FileContext context, String name) {
        return new Entry(context, name);
    }

    /**
     * Creates an entry with the given file context.
     *
     * @param context the file context.
     * @return the entry.
     * @see #entry(FileContext, String)
     */
    public static Entry entry(FileContext context) {
        return new Entry(context, context.path());
    }

    /**
     * Loads the given textures, and packs into an atlas.
     *
     * @param fail    the buffer will be used if failed to load image. must be allocated with {@link MemoryUtil}.
     *                you can print a message to warn. defaults to {@code null}.
     * @param param   the texture parameters. defaults to {@code null}.
     * @param entries the textures.
     * @return the texture atlas.
     * @see #load(Supplier, Entry...)
     * @see #load(TextureParam, Entry...)
     * @see #load(Entry...)
     */
    @SuppressWarnings("unchecked")
    public static TextureAtlas load(@Nullable Supplier<ByteBuffer> fail, @Nullable TextureParam param, Entry... entries) {
        // Uses array wrapper because lambda cannot modify outside variables
        ByteBuffer[] failBuffer = new ByteBuffer[1];
        List<Entry> entryRegion = new ArrayList<>();
        for (Entry entry : entries) {
            entry.image = NativeImage.load(entry.context, fail != null ? () -> {
                if (failBuffer[0] == null) {
                    failBuffer[0] = fail.get();
                }
                return failBuffer[0];
            } : null);
            entryRegion.add(entry);
        }
        entryRegion.sort(null);

        Packer packer = new GrowingPacker();
        packer.fit((List<PackerRegion<NativeImage>>) (List<?>) entryRegion);

        TextureAtlas atlas = new TextureAtlas(packer.width(), packer.height());
        int currTex = GLStateManager.textureBinding2D();
        GLStateManager.bindTexture2D(atlas.id());
        acceptParameters(param);
        glTexImage2D(GL_TEXTURE_2D,
            0,
            GL_RGBA8,
            packer.width(),
            packer.height(),
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            MemoryUtil.NULL);
        for (var entry : entryRegion) {
            entry.ifFitPresent((r, f) -> {
                glTexSubImage2D(GL_TEXTURE_2D,
                    0,
                    f.x(),
                    f.y(),
                    r.width(),
                    r.height(),
                    GL_RGBA,
                    GL_UNSIGNED_BYTE,
                    entry.image.buffer());
                atlas.regionMap.put(entry.name,
                    new TextureRegion(f.x(),
                        f.y(),
                        f.x() + r.width(),
                        f.y() + r.height()));
            });
            entry.image.dispose();
        }
        glGenerateMipmap(GL_TEXTURE_2D);
        GLStateManager.bindTexture2D(currTex);
        return atlas;
    }

    /**
     * Loads the given textures, and packs into an atlas.
     *
     * @param fail    the buffer will be used if failed to load image. must be allocated with {@link MemoryUtil}.
     *                you can print a message to warn. defaults to {@code null}.
     * @param entries the textures.
     * @return the texture atlas.
     * @see #load(Supplier, TextureParam, Entry...)
     * @see #load(TextureParam, Entry...)
     * @see #load(Entry...)
     */
    public static TextureAtlas load(@Nullable Supplier<ByteBuffer> fail, Entry... entries) {
        return load(fail, null, entries);
    }

    /**
     * Loads the given textures, and packs into an atlas.
     *
     * @param param   the texture parameters. defaults to {@code null}.
     * @param entries the textures.
     * @return the texture atlas.
     * @see #load(Supplier, TextureParam, Entry...)
     * @see #load(Supplier, Entry...)
     * @see #load(Entry...)
     */
    public static TextureAtlas load(@Nullable TextureParam param, Entry... entries) {
        return load(null, param, entries);
    }

    /**
     * Loads the given textures, and packs into an atlas.
     *
     * @param entries the textures.
     * @return the texture atlas.
     * @see #load(Supplier, TextureParam, Entry...)
     * @see #load(Supplier, Entry...)
     * @see #load(TextureParam, Entry...)
     */
    public static TextureAtlas load(Entry... entries) {
        return load(null, null, entries);
    }

    /**
     * The atlas entry.
     *
     * @author squid233
     * @since 0.1.0
     */
    public static final class Entry implements PackerRegion<NativeImage> {
        private final FileContext context;
        private final String name;
        private PackerFitPos fitPos;
        private NativeImage image;

        private Entry(FileContext context, String name) {
            this.context = context;
            this.name = name;
        }

        @Override
        public void setFit(@Nullable PackerFitPos fit) {
            this.fitPos = fit;
        }

        @Override
        public Optional<PackerFitPos> fit() {
            return Optional.ofNullable(fitPos);
        }

        @Override
        public NativeImage userdata() {
            return image;
        }

        @Override
        public int width() {
            return image.width();
        }

        @Override
        public int height() {
            return image.height();
        }
    }

    /**
     * Gets the texture region with the given name.
     *
     * @param name the name of the region.
     * @return the region.
     */
    public Optional<TextureRegion> get(String name) {
        return Optional.ofNullable(regionMap.get(name));
    }
}
