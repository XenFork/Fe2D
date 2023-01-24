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

package union.xenfork.fe2d.graphics.batch;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import union.xenfork.fe2d.Disposable;
import union.xenfork.fe2d.graphics.Color;
import union.xenfork.fe2d.graphics.ShaderProgram;
import union.xenfork.fe2d.graphics.texture.Texture;
import union.xenfork.fe2d.graphics.texture.TextureRegion;

/**
 * The base batch.
 *
 * @author squid233
 * @since 0.1.0
 */
public interface Batch extends Disposable {
    /**
     * Begins drawing. Can only be called before drawing.
     */
    void begin();

    /**
     * Ends drawing. Can only be called while drawing.
     */
    void end();

    /**
     * Flushes the buffer without ending drawing. Can only be called while drawing.
     */
    void flush();

    void draw(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, float u0, float v0, float u1, float v1, boolean flipX, boolean flipY);

    void draw(Texture texture, float x, float y, float width, float height, float u0, float v0, float u1, float v1, boolean flipX, boolean flipY);

    void draw(Texture texture, float x, float y, float width, float height, float u0, float v0, float u1, float v1);

    void draw(Texture texture, float x, float y, float width, float height);

    void draw(Texture texture, float x, float y);

    void draw(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, TextureRegion region, boolean flipX, boolean flipY);

    void draw(Texture texture, float x, float y, float width, float height, TextureRegion region, boolean flipX, boolean flipY);

    void draw(Texture texture, float x, float y, float width, float height, TextureRegion region);

    void draw(Texture texture, float x, float y, TextureRegion region);

    /**
     * Sets the custom shader.
     * <p>
     * This operation causes {@link #flush() flushing}.
     *
     * @param shader the custom shader.
     */
    void setShader(ShaderProgram shader);

    /**
     * Gets the shader.
     *
     * @return the custom shader user set; or the default shader.
     */
    ShaderProgram shader();

    /**
     * Gets the projection matrix.
     *
     * @return the projection matrix.
     */
    Matrix4f projectionMatrix();

    /**
     * Gets the model matrix.
     *
     * @return the model matrix.
     */
    Matrix4f modelMatrix();

    void setProjectionMatrix(Matrix4fc projectionMatrix);

    void setModelMatrix(Matrix4fc modelMatrix);

    void enableBlend();

    void disableBlend();

    void setBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha);

    void setBlendFunc(int srcFactor, int dstFactor);

    default void setSpriteColor(Color color) {
        setSpriteColor(color.packABGR());
    }

    default void setSpriteColor(float red, float green, float blue, float alpha) {
        setSpriteColor(Color.rgbaPackABGR(red, green, blue, alpha));
    }

    void setSpriteColor(int packedColor);

    int spriteColor();

    boolean isBlendDisabled();

    boolean isDrawing();
}
