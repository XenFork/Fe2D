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
 * The text layout.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class TextLayout {
    private Alignment.V verticalAlign = Alignment.V.CENTER;
    private Alignment.H horizontalAlign = Alignment.H.CENTER;

    /**
     * Creates the text layout.
     */
    public TextLayout() {
    }

    /**
     * Creates the text layout with the given arguments.
     *
     * @param verticalAlign   the text vertical alignment.
     * @param horizontalAlign the text horizontal alignment.
     */
    public TextLayout(Alignment.V verticalAlign, Alignment.H horizontalAlign) {
        this.verticalAlign = verticalAlign;
        this.horizontalAlign = horizontalAlign;
    }

    /**
     * Creates a copy of this text layout.
     *
     * @return the copy.
     */
    public TextLayout copy() {
        return new TextLayout(verticalAlign(), horizontalAlign());
    }

    /**
     * Gets the position x that offset with the vertical alignment of this layout.
     *
     * @param textWidth the text width in scaled coordinates.
     * @param boxWidth  the text box width.
     * @return the position x.
     */
    public float getTextPositionX(float originX, float textWidth, float boxWidth) {
        return verticalAlign().getTextPositionX(originX, textWidth, boxWidth);
    }

    /**
     * Gets the position y that offset with the horizontal alignment of this layout.
     *
     * @param textHeight the text height in scaled coordinates.
     * @param boxHeight  the text box height.
     * @return the position x.
     */
    public float getTextPositionY(float originY, float textHeight, float boxHeight) {
        return horizontalAlign().getTextPositionY(originY, textHeight, boxHeight);
    }

    /**
     * Gets the vertical alignment of this layout.
     *
     * @return the vertical alignment of this layout.
     */
    public Alignment.V verticalAlign() {
        return verticalAlign;
    }

    /**
     * Sets the vertical alignment of this layout.
     *
     * @param verticalAlign the vertical alignment of this layout.
     * @return this.
     */
    public TextLayout verticalAlign(Alignment.V verticalAlign) {
        this.verticalAlign = verticalAlign;
        return this;
    }

    /**
     * Gets the horizontal alignment of this layout.
     *
     * @return the horizontal alignment of this layout.
     */
    public Alignment.H horizontalAlign() {
        return horizontalAlign;
    }

    /**
     * Sets the horizontal alignment of this layout.
     *
     * @param horizontalAlign the horizontal alignment of this layout.
     * @return this.
     */
    public TextLayout horizontalAlign(Alignment.H horizontalAlign) {
        this.horizontalAlign = horizontalAlign;
        return this;
    }
}
