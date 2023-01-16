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
import org.lwjgl.system.MemoryUtil;
import union.xenfork.fe2d.file.FileContext;
import union.xenfork.fe2d.graphics.Color;
import union.xenfork.fe2d.graphics.texture.NativeImage;

import java.nio.ByteBuffer;
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
    private final ByteBuffer image;
    protected final Map<Integer, Integer> glyphU = new HashMap<>();
    protected final Map<Integer, Integer> glyphV = new HashMap<>();
    private final Map<Integer, Integer> glyphWidths = new HashMap<>();
    private final Map<Integer, Integer> glyphHeights = new HashMap<>();

    /**
     * Creates a bitmap font with the given image and codepoints.
     *
     * @param image      the image.
     * @param codePoints the codepoints that can be rendered with this texture.
     */
    protected BitmapFont(ByteBuffer image, String codePoints) {
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
        BitmapFont font = new BitmapFont(image.buffer(), codePoints);
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
    protected ByteBuffer getImage(int codePoint) {
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
                width += getGlyphWidth(line.codePointAt(i));
            }
            return width;
        }).max().orElse(0);
    }

    @Override
    public int getTextHeight(String text) {
        return text.lines()
            .mapToInt(line ->
                line.codePoints()
                    .map(this::getGlyphHeight)
                    .reduce(Integer::max)
                    .orElse(0)
            ).sum();
    }

    @Override
    public void drawCodePoint(ByteBuffer buffer, int bufWidth, int bufHeight, int colorABGR, int codePoint, float x, float y) {
        // if codepoint is not available, use white square
        if (isGlyphEmpty(codePoint)) {
            // if white square is not available, use space
            if (isGlyphEmpty(WHITE_SQUARE)) {
                codePoint = ' ';
            } else {
                codePoint = WHITE_SQUARE;
            }
        }
        ByteBuffer image = getImage(codePoint);
        int u = glyphU.get(codePoint);
        int v = glyphV.get(codePoint);
        int width = getGlyphWidth(codePoint);
        int height = getGlyphHeight(codePoint);
        int ix = Math.round(x);
        int iy = Math.round(y);
        for (int j = iy, maxY = iy + height; j < maxY; j++) {
            for (int i = ix, maxX = ix + width; i < maxX; i++) {
                if (i >= 0 && i < bufWidth && j >= 0 && j < bufHeight) {
                    int index = j * bufWidth + i;
                    buffer.put(index, (Color.getRedFromABGR(colorABGR)))
                        .put(index + 1, (Color.getBlueFromABGR(colorABGR)))
                        .put(index + 2, (Color.getGreenFromABGR(colorABGR)))
                        // sampling
                        .put(index + 3,
                            // j = iy + 0 -> v = v + 0, j = maxY = iy + height -> v = v + height
                            (byte) (
                                (image.get((v + j - iy) * width + (u + i - ix)) *
                                 ((int) Color.getAlphaFromABGR(colorABGR))) / 255
                            ));
                }
            }
        }
    }

    @Override
    public void dispose() {
        MemoryUtil.memFree(image);
    }
}
