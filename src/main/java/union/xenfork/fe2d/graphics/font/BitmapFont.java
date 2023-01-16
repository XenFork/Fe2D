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
import union.xenfork.fe2d.file.FileContext;
import union.xenfork.fe2d.graphics.batch.FontBatch;
import union.xenfork.fe2d.graphics.texture.NativeImage;
import union.xenfork.fe2d.graphics.texture.Texture;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntUnaryOperator;

import static org.lwjgl.opengl.GL30C.*;
import static union.xenfork.fe2d.graphics.GLStateManager.*;

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
public class BitmapFont extends Texture implements Font {
    private final String codePoints;
    protected final Map<Integer, Integer> glyphU = new HashMap<>();
    protected final Map<Integer, Integer> glyphV = new HashMap<>();
    private final Map<Integer, Integer> glyphWidths = new HashMap<>();
    private final Map<Integer, Integer> glyphHeights = new HashMap<>();

    /**
     * Creates a bitmap font texture with the given size and codepoints.
     *
     * @param width      the width of the texture.
     * @param height     the height of the texture.
     * @param codePoints the codepoints that can be rendered with this texture.
     */
    protected BitmapFont(int width, int height, String codePoints) {
        super(width, height);
        this.codePoints = codePoints;
    }

    protected static void initTexture(NativeImage image, BitmapFont font) {
        int currTex = textureBinding2D();
        bindTexture2D(font.id());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        int unpackAlign = unpackAlignment();
        setUnpackAlignment(1);
        glTexImage2D(GL_TEXTURE_2D,
            0,
            GL_R8,
            image.width(),
            image.height(),
            0,
            GL_RED,
            GL_UNSIGNED_BYTE,
            image.buffer());
        setUnpackAlignment(unpackAlign);
        glGenerateMipmap(GL_TEXTURE_2D);
        bindTexture2D(currTex);
        image.dispose();
    }

    public static BitmapFont load(FileContext context,
                                  String codePoints,
                                  int firstChar, int lastChar,
                                  int meshWidth, int meshHeight,
                                  IntUnaryOperator widthProvider,
                                  IntUnaryOperator heightProvider) {
        NativeImage image = NativeImage.load(context, STBImage.STBI_grey);
        BitmapFont font = new BitmapFont(image.width(), image.height(), codePoints);
        initTexture(image, font);
        int x = 0;
        int y = 0;
        int width = font.width();
        int height = font.height();
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
     * Gets the texture for the given codepoint.
     *
     * @param codePoint the codepoint.
     * @return the texture.
     */
    protected Texture getTexture(int codePoint) {
        return this;
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
    public void draw(FontBatch batch, String text, float x, float y) {
        String[] lines = text.lines().toArray(String[]::new);

        float yo = y;
        float invTexWidth = 0f;
        float invTexHeight = 0f;
        Texture thisTexture = null;

        for (int j = lines.length - 1; j >= 0; j--) {
            String line = lines[j];
            int maxHeight = 0;
            float xo = x;
            for (int i = 0, len = line.codePointCount(0, line.length()); i < len; i++) {
                int codePoint = line.codePointAt(i);
                Texture texture = getTexture(codePoint);
                // switch texture
                if (thisTexture != texture) {
                    thisTexture = texture;
                    invTexWidth = 1f / thisTexture.width();
                    invTexHeight = 1f / thisTexture.height();
                }
                // if codepoint is not available, use white square
                if (isGlyphEmpty(codePoint)) {
                    // if white square is not available, use space
                    if (isGlyphEmpty(WHITE_SQUARE)) {
                        codePoint = ' ';
                    } else {
                        codePoint = WHITE_SQUARE;
                    }
                }
                int u = glyphU.get(codePoint);
                int v = glyphV.get(codePoint);
                int width = getGlyphWidth(codePoint);
                int height = getGlyphHeight(codePoint);
                if (height > maxHeight) {
                    maxHeight = height;
                }
                batch.draw(thisTexture,
                    xo, yo,
                    width, height,
                    u * invTexWidth, v * invTexHeight,
                    (u + width) * invTexWidth, (v + height) * invTexHeight
                );
                xo += width;
            }
            yo += maxHeight;
        }
    }
}
