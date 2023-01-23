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

package union.xenfork.fe2d.graphics;

import org.jetbrains.annotations.ApiStatus;

/**
 * The graphics mode.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class Graphics {
    private static final Graphics INSTANCE = new Graphics();
    private int width, height;
    private double deltaFrameTime;
    private double framesPerSecond;

    private Graphics() {
    }

    /**
     * Sets the size.
     *
     * @param width  the new width.
     * @param height the new height.
     */
    @ApiStatus.Internal
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the delta frame time.
     *
     * @param deltaFrameTime the delta frame time.
     */
    @ApiStatus.Internal
    public void setDeltaFrameTime(double deltaFrameTime) {
        this.deltaFrameTime = deltaFrameTime;
    }

    /**
     * Sets the frames per second.
     *
     * @param framesPerSecond the frames per second.
     */
    public void setFramesPerSecond(double framesPerSecond) {
        this.framesPerSecond = framesPerSecond;
    }

    /**
     * Gets the width.
     *
     * @return the width.
     */
    public int width() {
        return width;
    }

    /**
     * Gets the height.
     *
     * @return the height.
     */
    public int height() {
        return height;
    }

    /**
     * Gets the delta frame time.
     *
     * @return the delta frame time.
     */
    public double deltaFrameTime() {
        return deltaFrameTime;
    }

    /**
     * Gets the frames per second.
     *
     * @return the frames per second.
     */
    public double framesPerSecond() {
        return framesPerSecond;
    }

    /**
     * Gets the instance.
     *
     * @return the instance.
     */
    public static Graphics getInstance() {
        return INSTANCE;
    }
}
