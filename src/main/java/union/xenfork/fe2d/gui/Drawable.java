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
 * The drawable element.
 *
 * @author squid233
 * @since 0.1.0
 */
public interface Drawable {
    /**
     * Renders this element.
     *
     * @param delta   the normalized time of interval of two rendering.
     *                see {@link union.xenfork.fe2d.Application#render(double) Application} for more information.
     * @param cursorX the cursor x-coordinate, relative to the left edge of the content area.
     * @param cursorY the cursor y-coordinate, relative to the top edge of the content area.
     * @see union.xenfork.fe2d.Application#render(double) Application::render
     */
    void render(double delta, double cursorX, double cursorY);
}
