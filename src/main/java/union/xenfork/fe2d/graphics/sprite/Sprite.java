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

package union.xenfork.fe2d.graphics.sprite;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import union.xenfork.fe2d.graphics.Color;
import union.xenfork.fe2d.graphics.texture.Texture;
import union.xenfork.fe2d.graphics.texture.TextureRegion;
import union.xenfork.fe2d.graphics.vertex.VertexAttribute;
import union.xenfork.fe2d.graphics.vertex.VertexLayout;

/**
 * The sprite, which contains a texture, color and transformation.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Sprite {
    /**
     * The vertex layout.
     */
    public static final VertexLayout LAYOUT = new VertexLayout(
        VertexAttribute.position2().getImplicit(),
        VertexAttribute.colorPacked().getImplicit(),
        VertexAttribute.texCoord(0).getImplicit()
    );
    /**
     * The sprite vertex count.
     */
    public static final int SPRITE_VERTEX = 4;
    /**
     * The sprite vertex size in bytes.
     */
    public static final int SPRITE_SIZE = SPRITE_VERTEX * (2 * Float.BYTES + 4 * Byte.BYTES + 2 * Float.BYTES);
    /**
     * The sprite texture.
     */
    public Texture texture;
    /**
     * The sprite texture region.
     */
    public TextureRegion textureRegion;
    /**
     * The sprite position.
     */
    public final Vector2f position = new Vector2f();
    /**
     * The sprite rotation anchor.
     */
    public final Vector3f anchor = new Vector3f();
    /**
     * The sprite rotation.
     */
    public final Quaternionf rotation = new Quaternionf();
    /**
     * The sprite size.
     */
    public final Vector2f size = new Vector2f();
    /**
     * The sprite scale size.
     */
    public final Vector2f scale = new Vector2f(1f);
    /**
     * The sprite color.
     */
    public Color color = Color.WHITE;
    private final Matrix4f transform = new Matrix4f();

    /**
     * Creates a sprite with the given texture and texture region.
     *
     * @param texture       the texture.
     * @param textureRegion the texture region.
     */
    public Sprite(Texture texture, TextureRegion textureRegion) {
        this.texture = texture;
        this.textureRegion = textureRegion;
        size.set(textureRegion.u1() - textureRegion.u0(), textureRegion.v1() - textureRegion.v0());
    }

    /**
     * Creates a sprite with the given texture.
     *
     * @param texture the texture.
     */
    public Sprite(Texture texture) {
        this(texture, TextureRegion.full(texture));
    }

    /**
     * Creates a sprite. The texture must be set later.
     */
    public Sprite() {
    }

    /**
     * Computes and gets the transformation.
     *
     * @return the transformation matrix.
     */
    public Matrix4f getTransform() {
        return transform.translation(position.x(), position.y(), 0f)
            .scale(scale.x(), scale.y(), 1f)
            .translate(anchor.x(), anchor.y(), anchor.z())
            .rotate(rotation)
            .translate(-anchor.x(), -anchor.y(), -anchor.z());
    }
}
