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

package union.xenfork.fe2d.gui.layout;

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
         * Aligns to center. The origin x position is computed as {@code (boxWidth - textWidth) * 0.5}.
         */
        CENTER,
        /**
         * Aligns to right. The origin x position is computed as {@code boxWidth - textWidth}.
         */
        RIGHT;


        /**
         * Gets the position x that offset with the vertical alignment of this alignment.
         *
         * @param originX   the origin position x of the text.
         * @param textWidth the text width in scaled coordinates.
         * @param boxWidth  the text box width.
         * @return the position x.
         */
        public float getTextPositionX(float originX, float textWidth, float boxWidth) {
            return switch (this) {
                case LEFT -> originX;
                case CENTER -> originX + (boxWidth - textWidth) * 0.5f;
                case RIGHT -> originX + boxWidth - textWidth;
            };
        }
    }

    /**
     * The horizontal alignment.
     *
     * @author squid233
     * @since 0.1.0
     */
    public enum H {
        /**
         * Aligns to bottom. The origin y position is computed as {@code 0}.
         */
        BOTTOM,
        /**
         * Aligns to center. The origin y position is computed as {@code (boxHeight - textHeight) * 0.5}.
         */
        CENTER,
        /**
         * Aligns to top. The origin y position is computed as {@code boxHeight - textHeight}.
         */
        TOP;

        /**
         * Gets the position y that offset with the horizontal alignment of this alignment.
         *
         * @param originY    the origin position y of the text.
         * @param textHeight the text height in scaled coordinates.
         * @param boxHeight  the text box height.
         * @return the position y.
         */
        public float getTextPositionY(float originY, float textHeight, float boxHeight) {
            return switch (this) {
                case BOTTOM -> originY;
                case CENTER -> originY + (boxHeight - textHeight) * 0.5f;
                case TOP -> originY + boxHeight - textHeight;
            };
        }
    }
}
