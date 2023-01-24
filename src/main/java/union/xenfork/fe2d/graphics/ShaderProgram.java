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

package union.xenfork.fe2d.graphics;

import org.joml.*;
import union.xenfork.fe2d.Disposable;
import union.xenfork.fe2d.graphics.vertex.VertexLayout;

import java.util.*;
import java.util.function.IntConsumer;

import static org.lwjgl.opengl.GL20C.*;

/**
 * The shader program with vertex and fragment shader.
 * <h2 id="Builtin_Uniforms">Builtin Uniforms</h2>
 * There are some builtin uniforms are available for conveniently setting values.
 * <p>
 * These uniforms are directly passed to the shader:
 * <ul>
 *     <li>{@value U_PROJECTION_MATRIX}</li>
 *     <li>{@value U_VIEW_MATRIX}</li>
 *     <li>{@value U_MODEL_MATRIX}</li>
 *     <li>{@value U_PROJECTION_VIEW_MATRIX}</li>
 *     <li>{@value U_VIEW_MODEL_MATRIX}</li>
 *     <li>{@value U_PROJECTION_VIEW_MODEL_MATRIX}</li>
 * </ul>
 * These uniforms are passed with arguments:
 * <ul>
 *     <li>{@value U_SAMPLER}: required a texture unit number appended at the end of the uniform name</li>
 * </ul>
 *
 * @author squid233
 * @since 0.1.0
 */
public final class ShaderProgram implements Disposable {
    /**
     * The shader program with id 0.
     */
    public static final ShaderProgram ZERO = new ShaderProgram(0);
    /**
     * The name of the projection matrix uniform.
     */
    public static final String U_PROJECTION_MATRIX = "fe_ProjMatrix";
    /**
     * The name of the view matrix uniform.
     */
    public static final String U_VIEW_MATRIX = "fe_ViewMatrix";
    /**
     * The name of the model matrix uniform.
     */
    public static final String U_MODEL_MATRIX = "fe_ModelMatrix";
    /**
     * The name of the projection view matrix uniform.
     */
    public static final String U_PROJECTION_VIEW_MATRIX = "fe_ProjViewMatrix";
    /**
     * The name of the view model matrix uniform.
     */
    public static final String U_VIEW_MODEL_MATRIX = "fe_ViewModelMatrix";
    /**
     * The name of the projection view model matrix uniform.
     */
    public static final String U_PROJECTION_VIEW_MODEL_MATRIX = "fe_ProjViewModelMatrix";
    /**
     * The name of the sampler uniform.
     */
    public static final String U_SAMPLER = "fe_Sampler";
    private final int id;
    private final Map<String, Integer> attributeIndexMap = new LinkedHashMap<>();
    private final Map<String, ShaderUniform> uniformMap = new HashMap<>();
    private boolean disposed = false;

    private static int compileShader(String typeName, int typeEnum, String source)
        throws IllegalStateException {
        int shader = glCreateShader(typeEnum);
        glShaderSource(shader, source);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            glDeleteShader(shader);
            throw new IllegalStateException("Failed to compile the " + typeName + " shader! " + glGetShaderInfoLog(shader));
        }
        return shader;
    }

    /**
     * Creates the shader program with the given id.
     *
     * @param id the id.
     */
    private ShaderProgram(int id) {
        this.id = id;
    }

    /**
     * Creates a shader program with the given source of shaders.
     *
     * @param vertexShader   the source of vertex shader.
     * @param fragmentShader the source of fragment shader.
     * @param attributeList  the attribute names that is in the shader.
     */
    public ShaderProgram(String vertexShader,
                         String fragmentShader,
                         List<String> attributeList) {
        this.id = init(vertexShader, fragmentShader, null);
        for (String attribute : attributeList) {
            attributeIndexMap.put(attribute, glGetAttribLocation(id, attribute));
        }
    }

    /**
     * Creates a shader program with the given source of shaders.
     *
     * @param vertexShader   the source of vertex shader.
     * @param fragmentShader the source of fragment shader.
     * @param attributeMap   the attribute locations to be bound.
     */
    public ShaderProgram(String vertexShader,
                         String fragmentShader,
                         Map<String, Integer> attributeMap) {
        attributeIndexMap.putAll(attributeMap);
        this.id = init(vertexShader, fragmentShader, id -> {
            for (var e : attributeMap.entrySet()) {
                glBindAttribLocation(id, e.getValue(), e.getKey());
            }
        });
    }

    /**
     * Creates a shader program with the given source of shaders.
     *
     * @param vertexShader   the source of vertex shader.
     * @param fragmentShader the source of fragment shader.
     * @param layout         the attribute locations to be bound.
     */
    public ShaderProgram(String vertexShader,
                         String fragmentShader,
                         VertexLayout layout) {
        this.id = init(vertexShader, fragmentShader, id ->
            layout.forEachAttribute((attribute, index) -> {
                attributeIndexMap.put(attribute.name(), index);
                glBindAttribLocation(id, index, attribute.name());
            }));
    }

    /**
     * Creates a shader program with the given source of shaders.
     *
     * @param vertexShader   the source of vertex shader.
     * @param fragmentShader the source of fragment shader.
     */
    public ShaderProgram(String vertexShader,
                         String fragmentShader) {
        this.id = init(vertexShader, fragmentShader, null);
    }

    private int init(String vertexShader, String fragmentShader, IntConsumer action) {
        int id = glCreateProgram();
        int vsh, fsh;
        try {
            vsh = compileShader("vertex", GL_VERTEX_SHADER, vertexShader);
            fsh = compileShader("fragment", GL_FRAGMENT_SHADER, fragmentShader);
        } catch (IllegalStateException e) {
            dispose();
            throw e;
        }
        glAttachShader(id, vsh);
        glAttachShader(id, fsh);
        if (action != null) {
            action.accept(id);
        }
        glLinkProgram(id);
        if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
            glDeleteShader(vsh);
            glDeleteShader(fsh);
            dispose();
            throw new IllegalStateException("Failed to link the shader program! " + glGetProgramInfoLog(id));
        }
        glDetachShader(id, vsh);
        glDetachShader(id, fsh);
        glDeleteShader(vsh);
        glDeleteShader(fsh);
        return id;
    }

    /**
     * Installs this program object as part of current rendering state.
     */
    public void use() {
        GLStateManager.useProgram(id);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Shader uniform
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Gets a uniform with the given name, or creates a new uniform if not found.
     *
     * @param name the name of the uniform.
     * @param type the type of the uniform.
     * @return the uniform, or empty if not found.
     */
    public Optional<ShaderUniform> getUniform(String name, ShaderUniform.Type type) {
        if (uniformMap.containsKey(name)) {
            return Optional.ofNullable(uniformMap.get(name));
        }
        int location = glGetUniformLocation(id, name);
        if (location == -1) {
            uniformMap.put(name, null);
            return Optional.empty();
        }
        ShaderUniform uniform = new ShaderUniform(location, type);
        uniformMap.put(name, uniform);
        return Optional.of(uniform);
    }

    /**
     * Sets the uniform with the given value.
     *
     * @param name  the name of the uniform.
     * @param value the value.
     */
    public void setUniform(String name, boolean value) {
        setUniform(name, value ? GL_TRUE : GL_FALSE);
    }

    /**
     * Sets the uniform with the given value.
     *
     * @param name  the name of the uniform.
     * @param value the value.
     */
    public void setUniform(String name, int value) {
        getUniform(name, ShaderUniform.Type.INT).orElseThrow()
            .markDirty()
            .buffer.putInt(0, value);
    }

    /**
     * Sets the uniform with the given value.
     *
     * @param name  the name of the uniform.
     * @param value the value.
     */
    public void setUniform(String name, float value) {
        getUniform(name, ShaderUniform.Type.FLOAT).orElseThrow()
            .markDirty()
            .buffer.putFloat(0, value);
    }

    /**
     * Sets the uniform with the given value.
     *
     * @param name the name of the uniform.
     * @param x    the value x.
     * @param y    the value y.
     */
    public void setUniform(String name, float x, float y) {
        getUniform(name, ShaderUniform.Type.VEC2).orElseThrow()
            .markDirty()
            .buffer.putFloat(0, x).putFloat(4, y);
    }

    /**
     * Sets the uniform with the given value.
     *
     * @param name the name of the uniform.
     * @param x    the value x.
     * @param y    the value y.
     * @param z    the value z.
     */
    public void setUniform(String name, float x, float y, float z) {
        getUniform(name, ShaderUniform.Type.VEC3).orElseThrow()
            .markDirty()
            .buffer.putFloat(0, x).putFloat(4, y).putFloat(8, z);
    }

    /**
     * Sets the uniform with the given value.
     *
     * @param name the name of the uniform.
     * @param x    the value x.
     * @param y    the value y.
     * @param z    the value z.
     * @param w    the value w.
     */
    public void setUniform(String name, float x, float y, float z, float w) {
        getUniform(name, ShaderUniform.Type.VEC4).orElseThrow()
            .markDirty()
            .buffer.putFloat(0, x).putFloat(4, y).putFloat(8, z).putFloat(12, w);
    }

    /**
     * Sets the uniform with the given value.
     *
     * @param name  the name of the uniform.
     * @param value the value.
     */
    public void setUniform(String name, Vector2fc value) {
        setUniform(name, value.x(), value.y());
    }

    /**
     * Sets the uniform with the given value.
     *
     * @param name  the name of the uniform.
     * @param value the value.
     */
    public void setUniform(String name, Vector3fc value) {
        setUniform(name, value.x(), value.y(), value.z());
    }

    /**
     * Sets the uniform with the given value.
     *
     * @param name  the name of the uniform.
     * @param value the value.
     */
    public void setUniform(String name, Vector4fc value) {
        setUniform(name, value.x(), value.y(), value.z(), value.w());
    }

    /**
     * Sets the uniform with the given value.
     *
     * @param name  the name of the uniform.
     * @param value the value.
     */
    public void setUniform(String name, Matrix2fc value) {
        value.get(
            getUniform(name, ShaderUniform.Type.MAT2).orElseThrow()
                .markDirty()
                .buffer
        );
    }

    /**
     * Sets the uniform with the given value.
     *
     * @param name  the name of the uniform.
     * @param value the value.
     */
    public void setUniform(String name, Matrix3fc value) {
        value.get(
            getUniform(name, ShaderUniform.Type.MAT3).orElseThrow()
                .markDirty()
                .buffer
        );
    }

    /**
     * Sets the uniform with the given value.
     *
     * @param name  the name of the uniform.
     * @param value the value.
     */
    public void setUniform(String name, Matrix4fc value) {
        value.get(
            getUniform(name, ShaderUniform.Type.MAT4).orElseThrow()
                .markDirty()
                .buffer
        );
    }

    /**
     * Sets the projection matrix uniform with the given value.
     *
     * @param value the value.
     */
    public void setProjectionMatrix(Matrix4fc value) {
        setUniform(U_PROJECTION_MATRIX, value);
    }

    /**
     * Sets the view matrix uniform with the given value.
     *
     * @param value the value.
     */
    public void setViewMatrix(Matrix4fc value) {
        setUniform(U_VIEW_MATRIX, value);
    }

    /**
     * Sets the model matrix uniform with the given value.
     *
     * @param value the value.
     */
    public void setModelMatrix(Matrix4fc value) {
        setUniform(U_MODEL_MATRIX, value);
    }

    /**
     * Sets the projection view matrix uniform with the given value.
     *
     * @param value the value.
     */
    public void setProjectionViewMatrix(Matrix4fc value) {
        setUniform(U_PROJECTION_VIEW_MATRIX, value);
    }

    /**
     * Sets the view model matrix uniform with the given value.
     *
     * @param value the value.
     */
    public void setViewModelMatrix(Matrix4fc value) {
        setUniform(U_VIEW_MODEL_MATRIX, value);
    }

    /**
     * Sets the projection view model uniform with the given value.
     *
     * @param value the value.
     */
    public void setProjectionViewModelMatrix(Matrix4fc value) {
        setUniform(U_PROJECTION_VIEW_MODEL_MATRIX, value);
    }

    /**
     * Adds a sampler uniform with the given unit.
     *
     * @param unit the sampler unit.
     */
    public void addSampler(int unit) {
        setUniform(U_SAMPLER + unit, unit);
    }

    /**
     * Uploads the uniforms that are set.
     */
    public void uploadUniforms() {
        for (ShaderUniform uniform : uniformMap.values()) {
            if (uniform != null) {
                uniform.upload(this);
            }
        }
    }

    /**
     * Gets the index of the given attribute.
     *
     * @param name the name of the attribute.
     * @return the index of the attribute.
     */
    public int getAttributeIndex(String name) {
        return attributeIndexMap.getOrDefault(name, -1);
    }

    /**
     * Gets the id of this shader program.
     *
     * @return the program id.
     */
    public int id() {
        return id;
    }

    @Override
    public void dispose() {
        if (disposed) return;
        disposed = true;
        glDeleteProgram(id);
        for (ShaderUniform uniform : uniformMap.values()) {
            if (uniform != null) {
                uniform.dispose();
            }
        }
    }
}
