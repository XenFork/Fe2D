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
import org.lwjgl.stb.STBImage;
import union.xenfork.fe2d.file.FileContext;
import union.xenfork.fe2d.graphics.texture.NativeImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntUnaryOperator;

/**
 * The bitmap font that is loaded from an image. The glyphs cannot be scaled.
 * <p>
 * This is not recommend, and you should use the true-type font that is more flexible.
 * <p>
 * The bitmap font is non-scalable, excepts {@link Unifont}, it can be proportional scaled.
 *
 * @author squid233
 * @since 0.1.0
 */
public class BitmapFont implements Font {
    private final String codePoints;
    private final NativeImage image;
    protected final Map<Integer, Integer> glyphU = new HashMap<>();
    protected final Map<Integer, Integer> glyphV = new HashMap<>();
    private final Map<Integer, Integer> glyphWidths = new HashMap<>();
    private final Map<Integer, Integer> glyphHeights = new HashMap<>();
    private boolean disposed = false;

    /**
     * Creates a bitmap font with the given image and codepoints.
     *
     * @param image      the image.
     * @param codePoints the codepoints that can be rendered with this texture.
     */
    protected BitmapFont(NativeImage image, String codePoints) {
        this.image = image;
        this.codePoints = codePoints;
    }

    public static BitmapFont load(FileContext context,
                                  String codePoints,
                                  int firstChar, int lastChar,
                                  int meshWidth, int meshHeight,
                                  IntUnaryOperator widthProvider,
                                  IntUnaryOperator heightProvider) {
        // image is managed by font
        NativeImage image = NativeImage.load(context, STBImage.STBI_grey);
        BitmapFont font = new BitmapFont(image, codePoints);
        int x = 0;
        int y = 0;
        int width = image.width();
        int height = image.height();
        for (int i = firstChar; i <= lastChar; i++) {
            if (!font.isGlyphEmpty(i)) {
                font.glyphU.put(i, x);
                font.glyphV.put(i, y);
                font.glyphWidths.put(i, widthProvider.applyAsInt(i));
                font.glyphHeights.put(i, heightProvider.applyAsInt(i));
            }
            x += meshWidth;
            if (x >= width) {
                x = 0;
                y += meshHeight;
                if (y >= height) {
                    y = 0;
                }
            }
        }
        return font;
    }

    public static BitmapFont load(FileContext context,
                                  String codePoints,
                                  int firstChar, int lastChar,
                                  int meshWidth, int meshHeight,
                                  int defaultWidth, int defaultHeight,
                                  Map<Integer, Integer> specialWidth,
                                  Map<Integer, Integer> specialHeight) {
        return load(context,
            codePoints,
            firstChar, lastChar,
            meshWidth, meshHeight,
            codePoint -> specialWidth.getOrDefault(codePoint, defaultWidth),
            codePoint -> specialHeight.getOrDefault(codePoint, defaultHeight));
    }

    /**
     * Gets the image for the given codepoint.
     *
     * @param codePoint the codepoint.
     * @return the image.
     */
    protected NativeImage getImage(int codePoint) {
        return image;
    }

    @Override
    public String getFontCodePoints() {
        return codePoints;
    }

    @Override
    public int getGlyphWidth(int codePoint) {
        return glyphWidths.get(codePoint);
    }

    @Override
    public int getGlyphHeight(int codePoint) {
        return glyphHeights.get(codePoint);
    }

    @Override
    public int getTextWidth(String text) {
        return text.lines().mapToInt(line -> {
            int width = 0;
            for (int i = 0, len = line.codePointCount(0, line.length()); i < len; i++) {
                int codePoint = line.codePointAt(i);
                width += getGlyphWidth(codePoint);
                if (i < len - 1) {
                    width += getKernAdvance(codePoint, line.codePointAt(i + 1));
                }
            }
            return width;
        }).reduce(0, Integer::max);
    }

    @Override
    public int getTextHeight(String text) {
        return text.lines()
            .mapToInt(line ->
                line.codePoints()
                    .map(this::getGlyphHeight)
                    .reduce(0, Integer::max)
            ).sum();
    }

    /**
     * Returns 1.
     *
     * @param pixels ignored.
     * @return 1.
     */
    @Override
    public float getScale(float pixels) {
        return 1;
    }

    @Override
    public int getKernAdvance(int codePoint1, int codePoint2) {
        return 0;
    }

    @Override
    public void getGlyphHMetrics(int codePoint, @Nullable IntBuffer advanceWidth, @Nullable IntBuffer leftSideBearing) {
        if (advanceWidth != null) {
            advanceWidth.put(0, getGlyphWidth(codePoint));
        }
        if (leftSideBearing != null) {
            leftSideBearing.put(0, 0);
        }
    }

    @Override
    public int getAscent() {
        return getGlyphHeight('!');
    }

    @Override
    public int getDescent() {
        return 0;
    }

    @Override
    public int getLineGap() {
        return 0;
    }

    @Override
    public int getAdvanceY() {
        return getAscent() - getDescent() + getLineGap();
    }

    @Override
    public void drawCodePoint(ByteBuffer buffer, int bufWidth, int bufHeight, int colorABGR, float scaleX, float scaleY, int leftSideBearing, int codePoint, int x, int y) {
        // if codepoint is not available, use white square
        if (isGlyphEmpty(codePoint)) {
            // if white square is not available, use space
            if (isGlyphEmpty(WHITE_SQUARE)) {
                codePoint = ' ';
            } else {
                codePoint = WHITE_SQUARE;
            }
        }
        NativeImage image = getImage(codePoint);
        int u = glyphU.get(codePoint);
        int v = glyphV.get(codePoint);
        int width = getGlyphWidth(codePoint);
        int height = getGlyphHeight(codePoint);
        FontUtil.drawBitmap(buffer, bufWidth, bufHeight, colorABGR, x, y, width, height, u, v, image.buffer(), image.width(), image.height());
    }

    @Override
    public void dispose() {
        if (disposed) return;
        disposed = true;
        image.dispose();
    }
}
