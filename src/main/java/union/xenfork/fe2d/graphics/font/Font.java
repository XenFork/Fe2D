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

import union.xenfork.fe2d.Disposable;
import union.xenfork.fe2d.graphics.batch.FontBatch;

/**
 * The font.
 *
 * @author squid233
 * @since 0.1.0
 */
public interface Font extends Disposable {
    int getFirstCodePoint();

    int getLastCodePoint();

    default int getCodePointCount() {
        return getLastCodePoint() - getFirstCodePoint() + 1;
    }

    int getGlyphWidth(int codePoint);

    int getGlyphHeight(int codePoint);

    int getTextWidth(String text);

    int getTextHeight(String text);

    void draw(FontBatch batch, String text, float x, float y);
}
