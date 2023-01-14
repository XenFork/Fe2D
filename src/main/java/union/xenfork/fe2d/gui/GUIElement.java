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

package union.xenfork.fe2d.gui;

/**
 * The GUI element.
 *
 * @author squid233
 * @since 0.1.0
 */
public interface GUIElement {
    /**
     * This method will be called on key pressed.
     *
     * @param key      the keyboard key that was pressed.
     * @param scancode the platform-specific scancode of the key.
     * @param mods     bitfield describing which modifiers keys were held down.
     * @return {@code true} if an operation has been performed successfully and should finish; {@code false} otherwise.
     */
    boolean onKeyPress(int key, int scancode, int mods);

    /**
     * This method will be called on mouse pressed.
     *
     * @param button the mouse button that was pressed.
     * @param mods   bitfield describing which modifiers keys were held down.
     * @return {@code true} if an operation has been performed successfully and should finish; {@code false} otherwise.
     */
    boolean onMousePress(int button, int mods);

    /**
     * Returns {@code true} if the given cursor position is hovering on this element.
     *
     * @param cursorX the cursor position x.
     * @param cursorY the cursor position y.
     * @return {@code true} if the given cursor position is hovering on this element; {@code false} otherwise.
     */
    boolean isCursorHover(double cursorX, double cursorY);
}
