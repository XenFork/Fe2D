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

package union.xenfork.fe2d.graphics.mesh;

import union.xenfork.fe2d.graphics.VertexBuilder;
import union.xenfork.fe2d.graphics.vertex.VertexLayout;

import java.util.function.Consumer;

/**
 * The basic geometry figures creator.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class GeometryMesh {
    /**
     * Creates a quad mesh.
     *
     * @param consumer the vertices.
     * @param layout   the vertex layout.
     * @return the fixed mesh.
     */
    public static Mesh quad(Consumer<VertexBuilder> consumer, VertexLayout layout) {
        return Mesh.fixed(consumer, 4, new int[]{0, 1, 2, 0, 2, 3}, layout);
    }
}
