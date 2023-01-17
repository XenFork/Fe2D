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

import union.xenfork.fe2d.graphics.Color;

import java.nio.ByteBuffer;

import static java.lang.Byte.toUnsignedInt;

/**
 * The internal font utilities.
 *
 * @author squid233
 * @since 0.1.0
 */
final class FontUtil {
    static void drawBitmap(ByteBuffer buffer,
                           int bufWidth, int bufHeight,
                           int colorABGR,
                           int x, int y,
                           int width, int height,
                           int u, int v,
                           ByteBuffer bitmapBuffer,
                           int bitmapW, int bitmapH) {
        for (int j = y, maxY = y + height; j < maxY; j++) {
            for (int i = x, maxX = x + width; i < maxX; i++) {
                if (i < 0 || i >= bufWidth || j < 0 || j >= bufHeight) {
                    continue;
                }
                int bu = (u + i - x);
                int bv = (v + height - 1 + y - j);
                if (bu < 0 || bu >= bitmapW || bv < 0 || bv >= bitmapH) {
                    continue;
                }
                int index = (j * bufWidth + i) * 4;
                int dstR = toUnsignedInt(buffer.get(index));
                int dstG = toUnsignedInt(buffer.get(index + 1));
                int dstB = toUnsignedInt(buffer.get(index + 2));
                int dstA = toUnsignedInt(buffer.get(index + 3));
                int srcR = toUnsignedInt(Color.getRedFromABGR(colorABGR));
                int srcG = toUnsignedInt(Color.getGreenFromABGR(colorABGR));
                int srcB = toUnsignedInt(Color.getBlueFromABGR(colorABGR));
                // sampling
                // j = y + 0 -> v = v + height - 1, j = maxY = y + height -> v = v + 0
                // i = x + 0 -> u = u + 0,          i = maxX = x + width  -> u = u + width
                int srcA = toUnsignedInt(Color.getAlphaFromABGR(colorABGR)) *
                           toUnsignedInt(bitmapBuffer.get(bv * bitmapW + bu)) /
                           255;
                int dstF = 255 - srcA;
                buffer.put(index, (byte) ((srcR * srcA + dstR * dstF) / 255))
                    .put(index + 1, (byte) ((srcG * srcA + dstG * dstF) / 255))
                    .put(index + 2, (byte) ((srcB * srcA + dstB * dstF) / 255))
                    .put(index + 3, (byte) ((srcA * srcA + dstA * dstF) / 255));
            }
        }
    }
}
