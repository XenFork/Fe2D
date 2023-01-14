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

import org.lwjgl.opengl.GL44C;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import union.xenfork.fe2d.file.FileContext;
import union.xenfork.fe2d.graphics.texture.TextureAtlas;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.stb.STBTruetype.*;

/**
 * The true-type font.
 * <h2>Baked-bitmap</h2>
 * The true-type font is backed with a baked-bitmap. The renderer access a glyph with a texture region.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class TrueTypeFont implements Font {
    private static final Map<Integer, String> codePointStrCache = new HashMap<>();
    private final STBTTFontinfo fontInfo;
    private final int firstChar, lastChar;

    static {
        for (int i = 0; i < 0xffff; i++) {
            codePointStrCache.put(i, Character.toString(i));
        }
    }

    private TrueTypeFont(STBTTFontinfo fontInfo, int firstChar, int lastChar) {
        this.fontInfo = fontInfo;
        this.firstChar = firstChar;
        this.lastChar = lastChar;
        for (int i = firstChar; i <= lastChar; i++) {
            codePointStrCache.computeIfAbsent(i, Character::toString);
        }
    }

    public static TrueTypeFont init(FileContext file, int firstCodePoint, int lastCodePoint) {
        STBTTFontinfo fontInfo = STBTTFontinfo.calloc();
        stbtt_InitFont(fontInfo, file.loadBinary());
        TrueTypeFont font = new TrueTypeFont(fontInfo, firstCodePoint, lastCodePoint);
        return font;
    }

    @Override
    public int getFirstCodePoint() {
        return firstChar;
    }

    @Override
    public int getLastCodePoint() {
        return lastChar;
    }

    @Override
    public void dispose() {
        fontInfo.free();
    }
}
