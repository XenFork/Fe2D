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

package union.xenfork.fe2d.util.math;

import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Vector2fc;

import java.util.StringJoiner;

/**
 * The direction.
 *
 * @author squid233
 * @since 0.1.0
 */
public enum Direction {
    /**
     * -x
     */
    LEFT(0, 1, -1f, 0f),
    /**
     * +x
     */
    RIGHT(1, 0, 1f, 0f),
    /**
     * -y
     */
    DOWN(2, 3, 0f, -1f),
    /**
     * +y
     */
    UP(3, 2, 0f, 1f);

    private static final Direction[] values = values();
    private final int id;
    private final int oppositeId;
    private final float x;
    private final float y;

    Direction(int id, int oppositeId, float x, float y) {
        this.id = id;
        this.oppositeId = oppositeId;
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the direction with the given id.
     *
     * @param id the id.
     * @return the direction.
     */
    public static Direction fromId(int id) {
        return values[id];
    }

    /**
     * Gets the nearest direction with the given vector.
     *
     * @param x the vector x.
     * @param y the vector y.
     * @return the direction; or {@code null} if not found or an error occurred.
     */
    public static @Nullable Direction fromVector(float x, float y) {
        float mag = Math.invsqrt(x * x + y * y);
        float nx = x * mag;
        float ny = y * mag;
        float max = Float.NEGATIVE_INFINITY;
        Direction bestMatch = null;
        for (Direction dir : values) {
            float dot = nx * dir.x + ny * dir.y;
            if (dot > max) {
                max = dot;
                bestMatch = dir;
            }
        }
        return bestMatch;
    }

    /**
     * Gets the nearest direction with the given vector.
     *
     * @param vec the vector.
     * @return the direction; or {@code null} if not found or an error occurred.
     */
    public static @Nullable Direction fromVector(Vector2fc vec) {
        return fromVector(vec.x(), vec.y());
    }

    /**
     * Gets the id.
     *
     * @return the id.
     */
    public int id() {
        return id;
    }

    /**
     * Gets the id of the opposite direction.
     *
     * @return the id of the opposite direction.
     */
    public int oppositeId() {
        return oppositeId;
    }

    /**
     * Gets the opposite direction.
     *
     * @return the opposite direction.
     */
    public Direction opposite() {
        return fromId(oppositeId);
    }

    /**
     * Returns {@code true} if this direction is on axis-x; {@code false} otherwise.
     *
     * @return {@code true} if this direction is on axis-x; {@code false} otherwise.
     */
    public boolean isOnAxisX() {
        return this == LEFT || this == RIGHT;
    }

    /**
     * Returns {@code true} if this direction is on axis-y; {@code false} otherwise.
     *
     * @return {@code true} if this direction is on axis-y; {@code false} otherwise.
     */
    public boolean isOnAxisY() {
        return this == DOWN || this == UP;
    }

    /**
     * Gets the direction vector x.
     *
     * @return the direction vector x.
     */
    public float x() {
        return x;
    }

    /**
     * Gets the direction vector y.
     *
     * @return the direction vector y.
     */
    public float y() {
        return y;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Direction.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("oppositeId=" + oppositeId)
            .add("x=" + x)
            .add("y=" + y)
            .toString();
    }
}
