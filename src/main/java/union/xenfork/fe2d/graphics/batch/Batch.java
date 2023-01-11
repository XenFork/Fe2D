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
import union.xenfork.fe2d.graphics.sprite.Sprite;
import union.xenfork.fe2d.graphics.texture.Texture;
import union.xenfork.fe2d.graphics.texture.TextureRegion;

/**
 * The base batch.
 *
 * @author squid233
 * @since 0.1.0
 */
public interface Batch extends Disposable {
    void begin();

    void end();

    void flush();

    void draw(Texture texture, float x, float y, float width, float height, float u0, float v0, float u1, float v1, Matrix4fc transform);

    void draw(Texture texture, float x, float y, float width, float height, float u0, float v0, float u1, float v1);

    void draw(Texture texture, float x, float y, float width, float height);

    void draw(Texture texture, float x, float y);

    void draw(Texture texture, float x, float y, float width, float height, TextureRegion region, Matrix4fc transform);

    void draw(Texture texture, float x, float y, float width, float height, TextureRegion region);

    void draw(Texture texture, float x, float y, TextureRegion region);

    void draw(Sprite sprite);

    void setShader(ShaderProgram shader);

    ShaderProgram shader();

    Matrix4f projectionMatrix();

    Matrix4f modelMatrix();

    void setProjectionMatrix(Matrix4fc projectionMatrix);

    void setModelMatrix(Matrix4fc modelMatrix);

    void setBlendDisabled(boolean blendDisabled);

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
