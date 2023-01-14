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

/**
 * The alignment of a text to be drawn.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class Alignment {
    private Alignment() {
    }

    /**
     * The vertical alignment.
     *
     * @author squid233
     * @since 0.1.0
     */
    public enum V {
        /**
         * Aligns to left. The origin x position is computed as {@code 0}.
         */
        LEFT,
        /**
         * Aligns to center. The origin x position is computed as {@code x + (boxWidth - textWidth) * 0.5}.
         */
        CENTER,
        /**
         * Aligns to right. The origin x position is computed as {@code boxWidth - textWidth}.
         */
        RIGHT
    }

    /**
     * The horizontal alignment.
     *
     * @author squid233
     * @since 0.1.0
     */
    public enum H {
        /**
         * Aligns to top. The origin y position is computed as {@code boxHeight - textHeight}.
         */
        TOP,
        /**
         * Aligns to center. The origin y position is computed as {@code y + (boxHeight - textHeight) * 0.5}.
         */
        CENTER,
        /**
         * Aligns to bottom. The origin y position is computed as {@code 0}.
         */
        BOTTOM
    }
}
