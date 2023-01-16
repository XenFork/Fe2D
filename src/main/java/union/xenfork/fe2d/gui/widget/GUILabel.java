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

package union.xenfork.fe2d.gui.widget;

import union.xenfork.fe2d.Fe2D;
import union.xenfork.fe2d.graphics.font.Font;
import union.xenfork.fe2d.graphics.font.TextRenderer;

/**
 * The GUI label which is a text rendered on the screen.
 *
 * @author squid233
 * @since 0.1.0
 */
public class GUILabel extends GUIWidget {
    private String text;
    private Font font;

    /**
     * Creates a label with the given position and text.
     *
     * @param x    the position x relative to the left-bottom of the screen.
     * @param y    the position y relative to the right-top of the screen.
     * @param text the text.
     */
    public GUILabel(float x, float y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    /**
     * Creates a label with the given text.
     *
     * @param text the text.
     */
    public GUILabel(String text) {
        this.text = text;
    }

    /**
     * Creates an empty label.
     */
    public GUILabel() {
    }

    /**
     * Renders this label with the font set.
     *
     * @param delta   the normalized time of interval of two rendering.
     *                see {@link union.xenfork.fe2d.Application#render(double) Application} for more information.
     * @param cursorX the cursor x-coordinate, relative to the left edge of the content area.
     * @param cursorY the cursor y-coordinate, relative to the top edge of the content area.
     */
    @Override
    public void render(double delta, double cursorX, double cursorY) {
        TextRenderer renderer = Fe2D.textRenderer();
        boolean notDrawing = !renderer.isDrawing();
        if (notDrawing) {
            renderer.begin();
        }
        renderer.draw(font(), text(), x(), y());
        if (notDrawing) {
            renderer.end();
        }
    }

    /**
     * Sets the text of this label.
     *
     * @param text the text.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the text of this label.
     *
     * @return the text.
     */
    public String text() {
        return text;
    }

    /**
     * Sets the font of this label.
     *
     * @param font the font.
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * Gets the font of this label. If no font was set, {@link Fe2D#defaultFont() default font} is used.
     *
     * @return the font.
     */
    public Font font() {
        if (font == null) {
            font = Fe2D.defaultFont();
        }
        return font;
    }

    @Override
    public int width() {
        return font().getTextWidth(text());
    }

    @Override
    public int height() {
        return font().getTextHeight(text());
    }
}
