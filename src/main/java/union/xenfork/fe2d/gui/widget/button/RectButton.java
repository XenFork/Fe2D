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
import union.xenfork.fe2d.graphics.texture.Texture;

/**
 * The GUI button with a colored rectangle.
 * @author squid233
 * @since 0.1.0
 */
public class RectButton extends GUIButton {
    /**
     * The color of this button.
     */
    protected Color color = Color.SKY;
    /**
     * The hovering color of this button.
     */
    protected Color hoverColor = Color.SKY;// TODO: 2023/1/18 highlighting

    /**
     * Creates the GUI button with the given position, size and actions.
     *
     * @param x           the initial position x.
     * @param y           the initial position y.
     * @param width       the initial width.
     * @param height      the initial height.
     * @param pressAction the action to be performed on pressing this button.
     * @param hoverAction the action to be performed on hovering this button.
     */
    public RectButton(float x, float y, int width, int height, PressAction pressAction, HoverAction hoverAction) {
        super(x, y, width, height, pressAction, hoverAction);
    }

    /**
     * Creates the GUI button with the given position, size and action.
     *
     * @param x           the initial position x.
     * @param y           the initial position y.
     * @param width       the initial width.
     * @param height      the initial height.
     * @param pressAction the action to be performed on pressing this button.
     */
    public RectButton(float x, float y, int width, int height, PressAction pressAction) {
        super(x, y, width, height, pressAction);
    }

    /**
     * Creates the GUI button with the given position and size.
     *
     * @param x      the initial position x.
     * @param y      the initial position y.
     * @param width  the initial width.
     * @param height the initial height.
     */
    public RectButton(float x, float y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(double delta, double cursorX, double cursorY) {
        SpriteBatch batch = Fe2D.spriteRenderer();
        boolean notDrawing = !batch.isDrawing();
        if (notDrawing) {
            batch.begin();
        }
        int currColor = batch.spriteColor();
        batch.setSpriteColor(isCursorHover(cursorX, cursorY) ? color() : hoverColor());
        batch.draw(Texture.whiteDot(), x(), y(), width(), height());
        batch.setSpriteColor(currColor);
        if (notDrawing) {
            batch.end();
        }
    }

    public Color color() {
        return color;
    }

    public Color hoverColor() {
        return hoverColor;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }
}
