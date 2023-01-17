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
import union.xenfork.fe2d.Disposable;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * The font.
 *
 * @author squid233
 * @since 0.1.0
 */
public interface Font extends Disposable {
    /**
     * The white square/blank quad character.
     */
    int WHITE_SQUARE = '□';
    /**
     * The basic ascii characters.
     */
    String ASCII = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    static String makeCodePoints(int from, int to) {
        StringBuilder sb = new StringBuilder(to - from + 1);
        for (int i = from; i <= to; i++) {
            if (i < 0x10000 && Character.isSurrogate((char) i)) {
                continue;
            }
            sb.appendCodePoint(i);
        }
        return sb.toString();
    }

    String getFontCodePoints();

    default int getCodePointCount() {
        String codePoints = getFontCodePoints();
        return codePoints.codePointCount(0, codePoints.length());
    }

    default boolean isGlyphEmpty(int codePoint) {
        return getFontCodePoints().codePoints().anyMatch(value -> value == codePoint);
    }

    int getGlyphWidth(int codePoint);

    int getGlyphHeight(int codePoint);

    int getTextWidth(String text);

    int getTextHeight(String text);

    float getScale(float pixels);

    int getKernAdvance(int codePoint1, int codePoint2);

    void getGlyphHMetrics(int codePoint, @Nullable IntBuffer advanceWidth, @Nullable IntBuffer leftSideBearing);

    int getAscent();

    int getDescent();

    int getLineGap();

    int getAdvanceY();

    void drawCodePoint(ByteBuffer buffer, int bufWidth, int bufHeight,
                       int colorABGR,
                       float scaleX, float scaleY,
                       int leftSideBearing,
                       int codePoint,
                       int x, int y);
}
