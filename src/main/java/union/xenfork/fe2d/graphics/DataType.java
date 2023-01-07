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

import static org.lwjgl.opengl.GL11C.*;

/**
 * The GL data types.
 *
 * @author squid233
 * @since 0.1.0
 */
public enum DataType {
    BYTE("Byte", GL_BYTE, 1),
    UNSIGNED_BYTE("Unsigned Byte", GL_UNSIGNED_BYTE, 1),
    SHORT("Short", GL_SHORT, 2),
    UNSIGNED_SHORT("Unsigned Short", GL_UNSIGNED_SHORT, 2),
    INT("Int", GL_INT, 4),
    UNSIGNED_INT("Unsigned Int", GL_UNSIGNED_INT, 4),
    FLOAT("Float", GL_FLOAT, 4),
    DOUBLE("Double", GL_DOUBLE, 8);

    private final String typeName;
    private final int typeEnum;
    private final int bytesSize;

    DataType(String typeName, int typeEnum, int bytesSize) {
        this.typeName = typeName;
        this.typeEnum = typeEnum;
        this.bytesSize = bytesSize;
    }

    /**
     * Gets the enum value.
     *
     * @return the enum value.
     */
    public int typeEnum() {
        return typeEnum;
    }

    /**
     * Gets the bytes size.
     *
     * @return the bytes size.
     */
    public int bytesSize() {
        return bytesSize;
    }

    @Override
    public String toString() {
        return typeName;
    }
}
