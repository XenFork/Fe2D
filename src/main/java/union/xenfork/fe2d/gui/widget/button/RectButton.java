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

package union.xenfork.fe2d.gui.widget.button;

import union.xenfork.fe2d.Fe2D;
import union.xenfork.fe2d.graphics.Color;
import union.xenfork.fe2d.graphics.batch.SpriteBatch;

/**
 * The GUI button with a colored rectangle.
 *
 * @author squid233
 * @since 0.1.0
 */
public class RectButton extends GUIButton {
    /**
     * The default button color.
     */
    public static final Color DEFAULT_COLOR = new Color(0xff2c974b);
    /**
     * The default button hover color.
     */
    public static final Color DEFAULT_HOVER_COLOR = new Color(0xff2da44e);

    /**
     * Creates a GUI button with the given position, size and actions.
     *
     * @param text        the initial text.
     * @param x           the initial position x.
     * @param y           the initial position y.
     * @param width       the initial width.
     * @param height      the initial height.
     * @param pressAction the action to be performed on pressing this button.
     * @param hoverAction the action to be performed on hovering this button.
     */
    public RectButton(String text, float x, float y, int width, int height, PressAction pressAction, HoverAction hoverAction) {
        super(text, x, y, width, height, pressAction, hoverAction);
        setColor(DEFAULT_COLOR);
        setHoverColor(DEFAULT_HOVER_COLOR);
    }

    /**
     * Creates a GUI button with the given position, size and action.
     *
     * @param text        the initial text.
     * @param x           the initial position x.
     * @param y           the initial position y.
     * @param width       the initial width.
     * @param height      the initial height.
     * @param pressAction the action to be performed on pressing this button.
     */
    public RectButton(String text, float x, float y, int width, int height, PressAction pressAction) {
        super(text, x, y, width, height, pressAction);
        setColor(DEFAULT_COLOR);
        setHoverColor(DEFAULT_HOVER_COLOR);
    }

    /**
     * Creates a GUI button with the given position and size.
     *
     * @param text   the initial text.
     * @param x      the initial position x.
     * @param y      the initial position y.
     * @param width  the initial width.
     * @param height the initial height.
     */
    public RectButton(String text, float x, float y, int width, int height) {
        super(text, x, y, width, height);
        setColor(DEFAULT_COLOR);
        setHoverColor(DEFAULT_HOVER_COLOR);
    }

    @Override
    public void render(double delta, double cursorX, double cursorY) {
        SpriteBatch batch = Fe2D.spriteRenderer();
        boolean notDrawing = !batch.isDrawing();
        if (notDrawing) {
            batch.begin();
        }
        int currColor = batch.spriteColor();
        batch.setSpriteColor(isCursorHover(cursorX, cursorY) ? hoverColor() : color());
        batch.draw(null, x(), y(), width(), height());
        batch.setSpriteColor(currColor);
        if (notDrawing) {
            batch.end();
        }
        renderText();
    }
}
