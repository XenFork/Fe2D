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
