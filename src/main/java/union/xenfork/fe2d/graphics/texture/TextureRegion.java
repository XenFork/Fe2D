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

package union.xenfork.fe2d.graphics.texture;

/**
 * The texture region, which contains non-normalized UV coordinate.
 *
 * @param u0 the first U.
 * @param v0 the first V.
 * @param u1 the second U.
 * @param v1 the second V.
 * @author squid233
 * @since 0.1.0
 */
public record TextureRegion(int u0, int v0, int u1, int v1) {
    /**
     * Creates a texture region for full texture.
     *
     * @param texture the texture.
     * @return the region.
     */
    public static TextureRegion full(Texture texture) {
        return new TextureRegion(0, 0, texture.width(), texture.height());
    }
}
