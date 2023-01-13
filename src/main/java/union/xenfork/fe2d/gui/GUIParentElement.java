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

import java.util.List;

/**
 * The gui element that contains children elements.
 *
 * @author squid233
 * @since 0.1.0
 */
public interface GUIParentElement extends GUIElement {
    /**
     * Gets the children elements.
     *
     * @return the sub-elements.
     */
    List<? extends GUIElement> children();

    @Override
    default boolean onKeyPress(int key, int scancode, int mods) {
        for (GUIElement element : children()) {
            if (element.onKeyPress(key, scancode, mods)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default boolean onMousePress(int button, int mods) {
        for (GUIElement element : children()) {
            if (element.onMousePress(button, mods)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default boolean isCursorHover(double cursorX, double cursorY) {
        for (GUIElement element : children()) {
            if (element.isCursorHover(cursorX, cursorY)) {
                return true;
            }
        }
        return false;
    }
}
