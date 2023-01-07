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

import static org.lwjgl.opengl.GL30C.*;

/**
 * The GL state manager.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class GLStateManager {
    ///////////////////////////////////////////////////////////////////////////
    // Texture
    ///////////////////////////////////////////////////////////////////////////

    private static int[] textureBinding2D = new int[glGetInteger(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS)];
    private static int activeTexture = 0;

    /**
     * Returns a single value, the name of the texture currently bound to the target {@link org.lwjgl.opengl.GL11C#GL_TEXTURE_2D GL_TEXTURE_2D}.
     *
     * @return a single value, the name of the texture currently bound to the target {@link org.lwjgl.opengl.GL11C#GL_TEXTURE_2D GL_TEXTURE_2D}.
     */
    public static int textureBinding2D() {
        return textureBinding2D[activeTexture];
    }

    /**
     * Returns a single value indicating the active multitexture unit.
     *
     * @return a single value indicating the active multitexture unit.
     */
    public static int activeTexture() {
        return activeTexture;
    }

    /**
     * Selects which texture unit subsequent texture state calls will affect.
     * <p>
     * The number of texture units an implementation supports is implementation dependent,
     * but must be at least 48 in GL 3 and 80 in GL 4.
     *
     * @param texture which texture unit to make active.
     */
    public static void activeTexture(int texture) {
        if (activeTexture != texture) {
            activeTexture = texture;
            glActiveTexture(GL_TEXTURE0 + texture);
        }
    }

    /**
     * Binds a texture to a texture target.
     * <p>
     * While a texture object is bound, GL operations on the target to which it is bound affect the bound object,
     * and queries of the target to which it is bound return state from the bound object.
     * If texture mapping of the dimensionality of the target to which a texture object is bound is enabled,
     * the state of the bound texture object directs the texturing operation.
     *
     * @param texture the texture object to bind.
     */
    public static void bindTexture2D(int texture) {
        if (textureBinding2D[activeTexture] != texture) {
            textureBinding2D[activeTexture] = texture;
            glBindTexture(GL_TEXTURE_2D, texture);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Vertex array
    ///////////////////////////////////////////////////////////////////////////

    private static int vertexArrayBinding = 0;

    /**
     * Returns a single value, the name of the vertex array object currently bound to the context. If no vertex array object is bound to the context, 0 is returned. The initial value is 0.
     *
     * @return a single value, the name of the vertex array object currently bound to the context. If no vertex array object is bound to the context, 0 is returned. The initial value is 0.
     */
    public static int vertexArrayBinding() {
        return vertexArrayBinding;
    }

    /**
     * Bind a vertex array object.
     *
     * @param array Specifies the name of the vertex array to bind.
     */
    public static void bindVertexArray(int array) {
        if (vertexArrayBinding != array) {
            vertexArrayBinding = array;
            glBindVertexArray(array);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Shader program
    ///////////////////////////////////////////////////////////////////////////

    private static int currentProgram = 0;

    /**
     * Returns one value, the name of the program object that is currently active, or 0 if no program object is active.
     *
     * @return one value, the name of the program object that is currently active, or 0 if no program object is active.
     */
    public static int currentProgram() {
        return currentProgram;
    }

    /**
     * Installs a program object as part of current rendering state.
     *
     * @param program Specifies the handle of the program object whose executables are to be used as part of current rendering state.
     */
    public static void useProgram(int program) {
        if (currentProgram != program) {
            currentProgram = program;
            glUseProgram(program);
        }
    }
}
