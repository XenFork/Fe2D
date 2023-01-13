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

import org.lwjgl.glfw.GLFW;

/**
 * The timer.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class Timer {
    private double previous = GLFW.glfwGetTime();
    private double lag = 0.0;
    /**
     * The time in seconds to be spent per update.
     */
    public double secondsPerUpdate = 0.02;

    /**
     * Advances the timer.
     *
     * @param action the action to be performed per update.
     * @return the normalized time to the next rendering.
     */
    public double advanceTime(Runnable action) {
        double currTime = GLFW.glfwGetTime();
        double elapsed = currTime - previous;
        previous = currTime;

        lag += elapsed;
        while (lag >= secondsPerUpdate) {
            action.run();
            lag -= secondsPerUpdate;
        }
        return lag / secondsPerUpdate;
    }
}
