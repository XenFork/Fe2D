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

import org.joml.Math;
import org.joml.Vector2f;

/**
 * Enhanced {@link org.joml.Intersectionf}.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class Intersection {
    /**
     * Test whether the axis-aligned rectangle with minimum corner {@code (minX, minY)} and maximum corner {@code (maxX, maxY)}
     * intersects the circle with the given center {@code (centerX, centerY)} and square radius radiusSquared.
     *
     * @param minX          the x coordinate of the minimum corner of the axis-aligned rectangle.
     * @param minY          the y coordinate of the minimum corner of the axis-aligned rectangle.
     * @param maxX          the x coordinate of the maximum corner of the axis-aligned rectangle.
     * @param maxY          the y coordinate of the maximum corner of the axis-aligned rectangle.
     * @param centerX       the x coordinate of the circle's center.
     * @param centerY       the y coordinate of the circle's center.
     * @param radiusSquared the square of the circle's radius.
     * @param result        a vector that will contain the values of the difference between aar and circle
     *                      (in other say, this is the relative direction of circle to arr).
     *                      the argument is not changed when returning {@code false}.
     * @return {@code true} iff the axis-aligned rectangle intersects the circle; {@code false} otherwise.
     */
    public static boolean intersectAarCircle(
        float minX, float minY,
        float maxX, float maxY,
        float centerX, float centerY,
        float radiusSquared,
        Vector2f result
    ) {
        // aar center
        float aarCx = (maxX + minX) * 0.5f;
        float aarCy = (maxY + minY) * 0.5f;
        // difference
        float dx = centerX - aarCx;
        float dy = centerY - aarCy;
        // clamp
        float cx = Math.clamp((minX - maxX) * 0.5f, (maxX - minX) * 0.5f, dx);
        float cy = Math.clamp((minY - maxY) * 0.5f, (maxY - minY) * 0.5f, dy);
        float closestX = aarCx + cx;
        float closestY = aarCy + cy;
        dx = closestX - centerX;
        dy = closestY - centerY;
        if ((dx * dx + dy * dy) < radiusSquared) {
            result.set(dx, dy);
            return true;
        }
        return false;
    }
}
