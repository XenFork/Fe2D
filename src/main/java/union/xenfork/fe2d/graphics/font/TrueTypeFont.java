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

import org.jetbrains.annotations.Nullable;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import union.xenfork.fe2d.file.FileContext;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.stb.STBTruetype.*;

/**
 * The true-type font that loads with {@link org.lwjgl.stb.STBTruetype STBTruetype}.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class TrueTypeFont implements Font {
    private final String codePoints;
    private final int codePointCount;
    private final ByteBuffer fontData;
    private final Map<Integer, Boolean> emptyGlyphCache;
    private final Map<Integer, Integer> glyphIndexCache;
    private final Map<Integer, Integer> glyphBoxX0;
    private final Map<Integer, Integer> glyphBoxY0;
    private final Map<Integer, Integer> glyphBoxX1;
    private final Map<Integer, Integer> glyphBoxY1;
    private final STBTTFontinfo fontInfo;
    private final int ascent, descent, lineGap, yAdvance;
    private ByteBuffer bitmapBuffer;
    private int bitmapW, bitmapH;
    private boolean disposed = false;

    public TrueTypeFont(String codePoints, ByteBuffer data) {
        this.fontInfo = STBTTFontinfo.calloc();
        if (!stbtt_InitFont(fontInfo, data)) {
            fontInfo.free();
            throw new IllegalStateException("Failed to initialize the font!");
        }
        this.fontData = data;

        this.codePoints = codePoints;
        this.codePointCount = codePoints.codePointCount(0, codePoints.length());
        this.emptyGlyphCache = new HashMap<>(codePointCount);
        this.glyphIndexCache = new HashMap<>(codePointCount);
        this.glyphBoxX0 = new HashMap<>(codePointCount);
        this.glyphBoxY0 = new HashMap<>(codePointCount);
        this.glyphBoxX1 = new HashMap<>(codePointCount);
        this.glyphBoxY1 = new HashMap<>(codePointCount);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer px0 = stack.callocInt(1);
            IntBuffer py0 = stack.callocInt(1);
            IntBuffer px1 = stack.callocInt(1);
            IntBuffer py1 = stack.callocInt(1);
            IntBuffer pa = stack.callocInt(1);
            IntBuffer pd = stack.callocInt(1);
            IntBuffer pl = stack.callocInt(1);
            stbtt_GetFontVMetrics(fontInfo, pa, pd, pl);
            ascent = pa.get(0);
            descent = pd.get(0);
            lineGap = pl.get(0);
            yAdvance = ascent - descent + lineGap;
            for (int i = 0; i < codePointCount; i++) {
                int codePoint = codePoints.codePointAt(i);
                if (codePoint < 0x10000 && Character.isSurrogate((char) codePoint)) {
                    emptyGlyphCache.put(codePoint, true);
                    glyphIndexCache.put(codePoint, 0);
                    continue;
                }
                int glyphIndex = stbtt_FindGlyphIndex(fontInfo, codePoint);
                emptyGlyphCache.put(codePoint, stbtt_IsGlyphEmpty(fontInfo, glyphIndex));
                glyphIndexCache.put(codePoint, glyphIndex);
                stbtt_GetGlyphBox(fontInfo, glyphIndex, px0, py0, px1, py1);
                glyphBoxX0.put(codePoint, px0.get(0));
                glyphBoxY0.put(codePoint, py0.get(0));
                glyphBoxX1.put(codePoint, px1.get(0));
                glyphBoxY1.put(codePoint, py1.get(0));
            }
        }
    }

    public static TrueTypeFont load(String codePoints, FileContext context, long bufferSize) {
        return new TrueTypeFont(codePoints, context.loadBinary(bufferSize));
    }

    public static TrueTypeFont load(String codePoints, FileContext context) {
        return load(codePoints, context, FileContext.DEFAULT_BUFFER_SIZE);
    }

    private int findGlyphIndex(int codePoint) {
        return glyphIndexCache.getOrDefault(codePoint, 0);
    }

    @Override
    public String getFontCodePoints() {
        return codePoints;
    }

    @Override
    public int getCodePointCount() {
        return codePointCount;
    }

    @Override
    public boolean isGlyphEmpty(int codePoint) {
        return emptyGlyphCache.getOrDefault(codePoint, Font.super.isGlyphEmpty(codePoint));
    }

    @Override
    public int getGlyphWidth(int codePoint) {
        return glyphBoxX1.getOrDefault(codePoint, 0) - glyphBoxX0.getOrDefault(codePoint, 0);
    }

    @Override
    public int getGlyphHeight(int codePoint) {
        return glyphBoxY1.getOrDefault(codePoint, 0) - glyphBoxY0.getOrDefault(codePoint, 0);
    }

    @Override
    public int getTextWidth(String text) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pAdvance = stack.callocInt(1);
            IntBuffer pBearing = stack.callocInt(1);
            return text.lines().mapToInt(line -> {
                int width = 0;
                for (int i = 0, len = line.codePointCount(0, line.length()); i < len; i++) {
                    int codePoint = line.codePointAt(i);
                    getGlyphHMetrics(codePoint, pAdvance, pBearing);
                    width += pAdvance.get(0) - pBearing.get(0);
                    if (i < len - 1) {
                        width += getKernAdvance(codePoint, line.codePointAt(i + 1));
                    }
                }
                return width;
            }).reduce(0, Integer::max);
        }
    }

    @Override
    public int getTextHeight(String text) {
        int height = 0;
        for (int i = 0, c = (int) text.lines().count(); i < c; i++) {
            height += (i == c - 1) ? (getAscent() - getDescent()) : getAdvanceY();
        }
        return height;
    }

    @Override
    public float getScale(float pixels) {
        return stbtt_ScaleForPixelHeight(fontInfo, pixels);
    }

    @Override
    public int getKernAdvance(int codePoint1, int codePoint2) {
        return stbtt_GetGlyphKernAdvance(fontInfo, findGlyphIndex(codePoint1), findGlyphIndex(codePoint2));
    }

    @Override
    public void getGlyphHMetrics(int codePoint, @Nullable IntBuffer advanceWidth, @Nullable IntBuffer leftSideBearing) {
        stbtt_GetGlyphHMetrics(fontInfo, findGlyphIndex(codePoint), advanceWidth, leftSideBearing);
    }

    public int getAscent() {
        return ascent;
    }

    public int getDescent() {
        return descent;
    }

    public int getLineGap() {
        return lineGap;
    }

    @Override
    public int getAdvanceY() {
        return yAdvance;
    }

    @Override
    public void drawCodePoint(ByteBuffer buffer, int bufWidth, int bufHeight, int colorABGR, float scaleX, float scaleY, int leftSideBearing, int codePoint, int x, int y) {
        int xoff, yoff;
        int width, height;
        int glyphIndex = findGlyphIndex(codePoint);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer ix0 = stack.callocInt(1);
            IntBuffer iy0 = stack.callocInt(1);
            IntBuffer ix1 = stack.callocInt(1);
            IntBuffer iy1 = stack.callocInt(1);
            stbtt_GetGlyphBitmapBox(fontInfo, glyphIndex, scaleX, scaleY, ix0, iy0, ix1, iy1);
            xoff = ix0.get(0);
            yoff = iy1.get(0);
            width = ix1.get(0) - xoff;
            height = yoff - iy0.get(0);
        }
        if (bitmapBuffer == null || width > bitmapW || height > bitmapH) {
            bitmapW = Math.max(width, bitmapW);
            bitmapH = Math.max(height, bitmapH);
            bitmapBuffer = MemoryUtil.memRealloc(bitmapBuffer, bitmapW * bitmapH);
        }
        stbtt_MakeGlyphBitmap(fontInfo, bitmapBuffer, width, height, bitmapW, scaleX, scaleY, glyphIndex);
        FontUtil.drawBitmap(buffer,
            bufWidth, bufHeight,
            colorABGR,
            x + (int) Math.floor(leftSideBearing * scaleX), y - yoff,
            width, height,
            0, 0,
            bitmapBuffer,
            bitmapW, bitmapH);
    }

    /**
     * Gets the font data.
     *
     * @return the font data.
     */
    public ByteBuffer fontData() {
        return fontData;
    }

    @Override
    public void dispose() {
        if (disposed) return;
        disposed = true;
        fontInfo.free();
        MemoryUtil.memFree(bitmapBuffer);
    }
}
