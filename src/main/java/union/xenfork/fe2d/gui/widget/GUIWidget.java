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

import union.xenfork.fe2d.Input;
import union.xenfork.fe2d.gui.Drawable;
import union.xenfork.fe2d.gui.Focusable;
import union.xenfork.fe2d.gui.GUIElement;

/**
 * The GUI widget, which is a drawable, focusable and interactive element.
 * <p>
 * The GUI widget has a position, and it must be reset
 * on {@link union.xenfork.fe2d.gui.screen.Screen#onResize(int, int) resizing}.
 *
 * @author squid233
 * @since 0.1.0
 */
public abstract class GUIWidget implements GUIElement, Drawable, Focusable {
    private boolean focused;
    /**
     * The widget position x relative to the left-bottom of the screen.
     */
    protected float x;
    /**
     * The widget position y relative to the right-top of the screen.
     */
    protected float y;

    /**
     * Performs the action defined with the subclasses.
     *
     * @return {@code true} if an operation has been performed successfully and should finish; {@code false} otherwise.
     */
    public boolean perform() {
        return false;
    }

    /**
     * Sets the position x of this widget.
     *
     * @param x the new position x.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Sets the position y of this widget.
     *
     * @param y the new position y.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Sets the position of this widget.
     *
     * @param x the new position x.
     * @param y the new position y.
     */
    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    /**
     * Gets the position x of this widget.
     *
     * @return the position x.
     */
    public float x() {
        return x;
    }

    /**
     * Gets the position y of this widget.
     *
     * @return the position y.
     */
    public float y() {
        return y;
    }

    /**
     * Gets the width of this widget.
     *
     * @return the width of this widget.
     */
    public abstract int width();

    /**
     * Gets the height of this widget.
     *
     * @return the height of this widget.
     */
    public abstract int height();

    @Override
    public boolean onKeyPress(int key, int scancode, int mods) {
        return false;
    }

    @Override
    public boolean onMousePress(int button, int mods) {
        return button == Input.MOUSE_BUTTON_LEFT && perform();
    }

    @Override
    public boolean isCursorHover(double cursorX, double cursorY) {
        return cursorX >= x() &&
               cursorY >= y() &&
               cursorX < x() + width() &&
               cursorY < y() + height();
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }
}
