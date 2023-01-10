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

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import union.xenfork.fe2d.graphics.ShaderProgram;
import union.xenfork.fe2d.graphics.mesh.Mesh;
import union.xenfork.fe2d.graphics.vertex.VertexAttribute;
import union.xenfork.fe2d.graphics.vertex.VertexLayout;

/**
 * The sprite batch.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class SpriteBatch implements Batch {
    /**
     * The vertex layout.
     */
    public static final VertexLayout LAYOUT = new VertexLayout(
        VertexAttribute.position().getImplicit(),
        VertexAttribute.colorPacked().getImplicit(),
        VertexAttribute.texCoord(0).getImplicit()
    );
    /**
     * The default value of max sprites.
     */
    public static final int DEFAULT_MAX_SPRITES = 1000;
    /**
     * The max sprites.
     */
    public static final int MAX_SPRITES = Integer.MAX_VALUE / 6;
    private final Mesh mesh;
    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4f modelMatrix = new Matrix4f();
    private final Matrix4f combinedMatrix = new Matrix4f();
    private final ShaderProgram shader;
    private ShaderProgram customShader;
    private final boolean ownsShader;
    private boolean drawing = false;

    public SpriteBatch(@Nullable ShaderProgram defaultShader, int maxSprites) {
        // note: since 0x7FFFFFFF / 4 * 6 overflows, we use 0x7FFFFFFF / 6 as the max count.
        int size = Math.min(maxSprites, MAX_SPRITES);
        this.mesh = Mesh.dynamic(LAYOUT, size * 4, size * 6);
        this.shader = defaultShader != null ? defaultShader : createDefaultShader();
        this.ownsShader = defaultShader == null;

        int[] indices = new int[size * 6];
        for (int i = 0, j = 0; i < indices.length; i += 6, j += 4) {
            indices[i] = j;
            indices[i + 1] = j + 1;
            indices[i + 2] = j + 2;
            indices[i + 3] = j + 2;
            indices[i + 4] = j + 3;
            indices[i + 5] = j;
        }
        mesh.setIndexCount(indices.length);
        mesh.setIndices(indices);
    }

    public SpriteBatch(int maxSprites) {
        this(null, maxSprites);
    }

    public SpriteBatch(@Nullable ShaderProgram defaultShader) {
        this(defaultShader, DEFAULT_MAX_SPRITES);
    }

    public SpriteBatch() {
        this(null, DEFAULT_MAX_SPRITES);
    }

    public static ShaderProgram createDefaultShader() {
        return new ShaderProgram(String.format("""
                #version 150 core
                in vec3 %1$s;
                in vec4 %2$s;
                in vec2 %3$s;
                out vec4 vertexColor;
                out vec2 UV0;
                uniform mat4 %4$s;
                void main() {
                    gl_Position = %4$s * vec4(%1$s, 1.0);
                    vertexColor = %2$s;
                    UV0 = %3$s;
                }
                """, VertexAttribute.POSITION_ATTRIB,
            VertexAttribute.COLOR_ATTRIB,
            VertexAttribute.TEX_COORD_ATTRIB + '0',
            ShaderProgram.U_PROJECTION_VIEW_MODEL_MATRIX),
            String.format("""
                #version 150 core
                in vec4 vertexColor;
                in vec2 UV0;
                out vec4 FragColor;
                uniform sampler2D %1$s;
                void main() {
                    FragColor = vertexColor * texture(%1$s, UV0);
                }
                """, ShaderProgram.U_SAMPLER + '0'),
            LAYOUT);
    }

    private void setupMatrices() {
        projectionMatrix.mul(modelMatrix, combinedMatrix);
        if (customShader != null) {
            customShader.setUniform(ShaderProgram.U_PROJECTION_VIEW_MODEL_MATRIX, combinedMatrix);
            customShader.uploadUniforms();
        } else {
            shader.setUniform(ShaderProgram.U_PROJECTION_VIEW_MODEL_MATRIX, combinedMatrix);
            shader.uploadUniforms();
        }
    }

    @Override
    public void setShader(ShaderProgram shader) {
        if (shader == customShader) return;
        if (drawing) flush();
        customShader = shader;
        if (drawing) {
            if (customShader != null) {
                customShader.use();
            } else {
                this.shader.use();
            }
            setupMatrices();
        }
    }

    @Override
    public ShaderProgram shader() {
        return customShader != null ? customShader : shader;
    }

    @Override
    public Matrix4f projectionMatrix() {
        return projectionMatrix;
    }

    @Override
    public Matrix4f modelMatrix() {
        return modelMatrix;
    }

    @Override
    public void setProjectionMatrix(Matrix4fc projectionMatrix) {
        if (drawing) flush();
        this.projectionMatrix.set(projectionMatrix);
        if (drawing) setupMatrices();
    }

    @Override
    public void setModelMatrix(Matrix4fc modelMatrix) {
        if (drawing) flush();
        this.modelMatrix.set(modelMatrix);
        if (drawing) setupMatrices();
    }

    @Override
    public boolean drawing() {
        return drawing;
    }

    @Override
    public void dispose() {
        mesh.dispose();
        if (ownsShader&&shader!=null) {
            shader.dispose();
        }
    }
}
