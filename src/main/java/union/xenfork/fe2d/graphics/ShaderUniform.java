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

import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL;
import union.xenfork.fe2d.Disposable;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL41C.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * The shader uniform.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class ShaderUniform implements Disposable {
    private final int location;
    private final Type type;
    private boolean dirty = true;
    final ByteBuffer buffer;
    private boolean disposed = false;

    /**
     * Creates the shader uniform.
     *
     * @param location the location of the uniform.
     * @param type     the type of the uniform.
     */
    public ShaderUniform(int location, Type type) {
        this.location = location;
        this.type = type;
        buffer = memCalloc(type.bytesSize());
    }

    ShaderUniform markDirty() {
        dirty = true;
        return this;
    }

    /**
     * Uploads the uniform.
     *
     * @param program the shader program.
     */
    public void upload(@Nullable ShaderProgram program) {
        if (!dirty) {
            return;
        }
        dirty = false;
        int currPrg = GLStateManager.currentProgram();
        boolean arb = program != null && GL.getCapabilities().GL_ARB_separate_shader_objects;
        if (!arb && program != null) {
            GLStateManager.useProgram(program.id());
        }
        switch (type) {
            case INT -> {
                if (arb) glProgramUniform1i(program.id(), location, buffer.getInt(0));
                else glUniform1i(location, buffer.getInt(0));
            }
            case FLOAT -> {
                if (arb) glProgramUniform1f(program.id(), location, buffer.getFloat(0));
                else glUniform1f(location, buffer.getFloat(0));
            }
            case VEC2 -> {
                if (arb) nglProgramUniform2fv(program.id(), location, 2, memAddress(buffer));
                else nglUniform2fv(location, 2, memAddress(buffer));
            }
            case VEC3 -> {
                if (arb) nglProgramUniform3fv(program.id(), location, 3, memAddress(buffer));
                else nglUniform3fv(location, 3, memAddress(buffer));
            }
            case VEC4 -> {
                if (arb) nglProgramUniform4fv(program.id(), location, 4, memAddress(buffer));
                else nglUniform4fv(location, 4, memAddress(buffer));
            }
            case MAT2 -> {
                if (arb) nglProgramUniformMatrix2fv(program.id(), location, 1, false, memAddress(buffer));
                else nglUniformMatrix2fv(location, 1, false, memAddress(buffer));
            }
            case MAT3 -> {
                if (arb) nglProgramUniformMatrix3fv(program.id(), location, 1, false, memAddress(buffer));
                else nglUniformMatrix3fv(location, 1, false, memAddress(buffer));
            }
            case MAT4 -> {
                if (arb) nglProgramUniformMatrix4fv(program.id(), location, 1, false, memAddress(buffer));
                else nglUniformMatrix4fv(location, 1, false, memAddress(buffer));
            }
            default -> throw new IllegalStateException("Unsupported type " + type + " detected! This is a bug!");
        }
        if (!arb) {
            GLStateManager.useProgram(currPrg);
        }
    }

    @Override
    public void dispose() {
        if (disposed) return;
        disposed = true;
        memFree(buffer);
    }

    /**
     * The type of shader uniform.
     *
     * @author squid233
     * @since 0.1.0
     */
    public enum Type {
        INT(1, DataType.INT),
        FLOAT(1, DataType.FLOAT),
        VEC2(2, DataType.FLOAT),
        VEC3(3, DataType.FLOAT),
        VEC4(4, DataType.FLOAT),
        MAT2(4, DataType.FLOAT),
        MAT3(9, DataType.FLOAT),
        MAT4(16, DataType.FLOAT),
        ;

        private final int size;
        private final DataType type;
        private final int bytesSize;

        Type(int size, DataType type) {
            this.size = size;
            this.type = type;
            bytesSize = size * type.bytesSize();
        }

        /**
         * Gets the values size.
         *
         * @return the values size.
         */
        public int size() {
            return size;
        }

        /**
         * Gets the data type.
         *
         * @return the data type.
         */
        public DataType type() {
            return type;
        }

        /**
         * Gets the bytes size.
         *
         * @return the bytes size.
         */
        public int bytesSize() {
            return bytesSize;
        }
    }
}
