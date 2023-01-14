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

package union.xenfork.fe2d;

/**
 * The updatable element.
 *
 * @author squid233
 * @since 0.1.0
 */
public interface Updatable {
    /**
     * The fixed updating is performed as a fixed time step. Suitable for physical engine and AI pathfinder.
     */
    void fixedUpdate();

    /**
     * Updating game objects per frame.
     */
    void update();

    /**
     * Just updating, but it will be called after {@link #update()}.
     */
    void lateUpdate();
}
