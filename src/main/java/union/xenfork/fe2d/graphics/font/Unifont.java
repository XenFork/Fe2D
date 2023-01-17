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

import java.util.HashMap;
import java.util.Map;

/**
 * The unifont bitmap font.
 * <p>
 * Supported to Plane 0 and 1.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class Unifont extends BitmapFont {
    private static final String AVAILABLE_CODEPOINTS;
    private static final int MESH_SIZE = 16;
    private static final Map<Integer, Boolean> emptyGlyphCache = new HashMap<>(0x1f7fe);
    private final boolean japanese;
    private final NativeImage plane0jp;
    private final NativeImage plane1;

    static {
        StringBuilder sb = new StringBuilder(0x1f7fe);
        for (int i = 0; i <= 0x1fffd; i++) {
            if (i < 0x10000 && Character.isSurrogate((char) i)) {
                emptyGlyphCache.put(i, true);
                continue;
            }
            sb.appendCodePoint(i);
            emptyGlyphCache.put(i, false);
        }
        AVAILABLE_CODEPOINTS = sb.toString();
    }

    private Unifont(NativeImage image, boolean japanese) {
        super(image, AVAILABLE_CODEPOINTS);
        this.japanese = japanese;
        plane0jp = japanese ? NativeImage.load(Fe2D.files.internal("_fe2d/texture/font/unifont_0_jp.png"),
            STBImage.STBI_grey,
            null,
            868 << 10) : null;
        plane1 = NativeImage.load(Fe2D.files.internal("_fe2d/texture/font/unifont_1.png"),
            STBImage.STBI_grey,
            null,
            252 << 10);
    }

    /**
     * Creates the unifont.
     *
     * @param japanese {@code true} to use Japanese glyph. defaults to {@code false}.
     * @return the unifont.
     */
    public static Unifont create(boolean japanese) {
        // image is managed by font
        NativeImage image = NativeImage.load(Fe2D.files.internal("_fe2d/texture/font/unifont_0.png"),
            STBImage.STBI_grey,
            null,
            800 << 10);
        Unifont font = new Unifont(image, japanese);
        int x = 0;
        int y = 0;
        int width = image.width();
        int height = image.height();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j <= 0xffff; j++) {
                int codePoint = j | (i << 16);
                if (!font.isGlyphEmpty(codePoint)) {
                    font.glyphU.put(codePoint, x);
                    font.glyphV.put(codePoint, y);
                }
                x += MESH_SIZE;
                if (x >= width) {
                    x = 0;
                    y += MESH_SIZE;
                    if (y >= height) {
                        y = 0;
                    }
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
    protected NativeImage getImage(int codePoint) {
        if (codePoint >= 0x10000) {
            return plane1;
        }
        if (japanese) {
            return plane0jp;
        }
        return super.getImage(codePoint);
    }

    @Override
    public boolean isGlyphEmpty(int codePoint) {
        return emptyGlyphCache.getOrDefault(codePoint, true);
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
    public int getAscent() {
        return 16;
    }

    @Override
    public int getAdvanceY() {
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
