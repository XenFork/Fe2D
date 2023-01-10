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
import union.xenfork.fe2d.graphics.ShaderProgram;
import union.xenfork.fe2d.graphics.texture.Texture;

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
    void draw(Texture texture, float x, float y, Matrix4fc transform);
    void draw(Texture texture, float x, float y, float width, float height);
    void draw(Texture texture, float x, float y);
    void setShader(ShaderProgram shader);
    ShaderProgram shader();
    Matrix4f projectionMatrix();
    Matrix4f modelMatrix();
    void setProjectionMatrix(Matrix4fc projectionMatrix);
    void setModelMatrix(Matrix4fc modelMatrix);
    boolean drawing();
    // TODO: 2023/1/10 Support blending
}
