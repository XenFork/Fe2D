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

import union.xenfork.fe2d.Disposable;

import static org.lwjgl.opengl.GL20C.*;

/**
 * The shader program with vertex and fragment shader.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class ShaderProgram implements Disposable {
    /**
     * The shader program with id 0.
     */
    public static final ShaderProgram ZERO = new ShaderProgram(0);
    private final int id;

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

    private ShaderProgram(int id) {
        this.id = id;
    }

    /**
     * Creates a shader program with the given source of shaders.
     *
     * @param vertexShader   the source of vertex shader.
     * @param fragmentShader the source of fragment shader.
     */
    public ShaderProgram(String vertexShader,
                         String fragmentShader) {
        this(glCreateProgram());
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
    }

    /**
     * Installs this program object as part of current rendering state.
     */
    public void use() {glVertexAttribPointer(0,0,0,false,0,0);
        glUseProgram(id);
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
        glDeleteProgram(id);
    }
}
