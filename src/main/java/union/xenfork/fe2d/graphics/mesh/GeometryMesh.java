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

import org.joml.Math;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11C;
import union.xenfork.fe2d.graphics.VertexBuilder;
import union.xenfork.fe2d.graphics.sprite.Sprite;
import union.xenfork.fe2d.graphics.texture.Texture;
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
     * @return the immutable mesh.
     */
    public static Mesh quad(Consumer<VertexBuilder> consumer, VertexLayout layout) {
        Mesh mesh = Mesh.immutable(consumer, 4, new int[]{0, 1, 2, 3}, layout);
        mesh.setDefaultDrawMode(GL11C.GL_TRIANGLE_FAN);
        return mesh;
    }

    /**
     * Creates a batched sprites mesh. The {@link Sprite#LAYOUT sprite vertex layout} is used.
     *
     * @param sprites the sprites to be batched.
     * @return the immutable mesh.
     */
    public static Mesh sprites(Sprite... sprites) {
        Texture texture = null;
        for (Sprite sprite : sprites) {
            if (texture != null && sprite.texture != texture) {
                throw new IllegalStateException("Textures of all sprites must be the same!");
            }
            texture = sprite.texture;
        }

        int[] indices = new int[sprites.length * 6];
        for (int i = 0, j = 0; i < indices.length; i += 6, j += 4) {
            indices[i] = j;
            indices[i + 1] = j + 1;
            indices[i + 2] = j + 2;
            indices[i + 3] = j + 2;
            indices[i + 4] = j + 3;
            indices[i + 5] = j;
        }
        return Mesh.immutable(builder -> {
            for (Sprite sprite : sprites) {
                Matrix4f mat = sprite.getTransform();
                float width = sprite.size.x();
                float height = sprite.size.y();
                float x0 = mat.m30();
                float y0 = mat.m31();
                float x1 = Math.fma(mat.m00(), width, Math.fma(mat.m10(), height, mat.m30()));
                float y1 = Math.fma(mat.m01(), width, Math.fma(mat.m11(), height, mat.m31()));
                float invW = 1f / sprite.texture.width();
                float invH = 1f / sprite.texture.height();
                float u0 = sprite.textureRegion.u0() * invW;
                float v0 = sprite.textureRegion.v0() * invH;
                float u1 = sprite.textureRegion.u1() * invW;
                float v1 = sprite.textureRegion.v1() * invH;
                int color = sprite.color.packABGR();
                builder
                    .floats(x0, y1).ints(color).floats(u0, v0)
                    .floats(x0, y0).ints(color).floats(u0, v1)
                    .floats(x1, y0).ints(color).floats(u1, v1)
                    .floats(x1, y1).ints(color).floats(u1, v0);
            }
        }, sprites.length * Sprite.SPRITE_VERTEX, indices, Sprite.LAYOUT);
    }
}
