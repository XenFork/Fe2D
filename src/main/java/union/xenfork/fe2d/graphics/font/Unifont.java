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

package union.xenfork.fe2d.graphics.font;

import org.lwjgl.stb.STBImage;
import union.xenfork.fe2d.Fe2D;
import union.xenfork.fe2d.graphics.texture.NativeImage;
import union.xenfork.fe2d.graphics.texture.Texture;
import union.xenfork.fe2d.graphics.texture.TextureParam;

import static org.lwjgl.opengl.GL30C.*;

/**
 * The unifont bitmap font.
 * <p>
 * Supported to Plane 0 and 1.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class Unifont extends BitmapFont {
    private static final TextureParam PARAM = new TextureParam().minFilter(GL_NEAREST).magFilter(GL_NEAREST);
    private static final int MESH_SIZE = 16;
    private final boolean japanese;
    private final Texture plane0jp;
    private final Texture plane1;

    private Unifont(boolean japanese) {
        super(4096, 4096, 0, 0x1fffd);
        this.japanese = japanese;
        if (japanese) {
            NativeImage image = NativeImage.load(Fe2D.files.internal("_fe2d/texture/font/unifont_0_jp.png"), STBImage.STBI_grey);
            plane0jp = ofImage(image, PARAM, GL_R8, GL_RED);
            image.dispose();
        } else {
            plane0jp = null;
        }
        NativeImage image = NativeImage.load(Fe2D.files.internal("_fe2d/texture/font/unifont_1.png"), STBImage.STBI_grey);
        plane1 = ofImage(image, PARAM, GL_R8, GL_RED);
        image.dispose();
    }

    /**
     * Creates the unifont.
     *
     * @param japanese {@code true} to use Japanese glyph. defaults to {@code false}.
     * @return the unifont.
     */
    public static Unifont create(boolean japanese) {
        NativeImage image = NativeImage.load(Fe2D.files.internal("_fe2d/texture/font/unifont_0.png"), STBImage.STBI_grey);
        Unifont font = new Unifont(japanese);
        initTexture(image, font);
        int x = 0;
        int y = 0;
        int width = font.width();
        int height = font.height();
        for (int i = font.getFirstCodePoint(), last = font.getLastCodePoint(); i <= last; i++) {
            font.glyphU.put(i, x);
            font.glyphV.put(i, y);
            x += MESH_SIZE;
            if (x >= width) {
                x = 0;
                y += MESH_SIZE;
                if (y >= height) {
                    y = 0;
                }
            }
        }
        return font;
    }

    /**
     * Creates the unifont.
     *
     * @return the unifont.
     */
    public static Unifont create() {
        return create(false);
    }

    @Override
    protected Texture getTexture(int codePoint) {
        if (codePoint >= 0x10000) {
            return plane1;
        }
        if (japanese) {
            return plane0jp;
        }
        return super.getTexture(codePoint);
    }

    @Override
    public int getGlyphWidth(int codePoint) {
        // half-width characters
        if (codePoint >= 0x0020 && codePoint <= 0x00ff) return 8;
        // ZWNJ
        if (codePoint == 0x200c) return 0;
        return 16;
    }

    @Override
    public int getGlyphHeight(int codePoint) {
        return 16;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (plane0jp != null) {
            plane0jp.dispose();
        }
        plane1.dispose();
    }
}
