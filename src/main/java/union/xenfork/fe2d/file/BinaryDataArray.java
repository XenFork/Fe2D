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

package union.xenfork.fe2d.file;

import java.io.IOException;
import java.io.ObjectOutput;

/**
 * The binary data array.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class BinaryDataArray extends BinaryData {
    private final BinaryData[] array;

    BinaryDataArray(BinaryData[] value) {
        super(TYPE_DATA_ARRAY, value);
        array = value;
    }

    @Override
    public void write(ObjectOutput out) throws IOException {
        out.writeByte(type());
        out.writeInt(size());
        for (var v : data()) {
            v.write(out);
        }
    }

    public void set(int index, BinaryData data) {
        array[index] = data;
    }

    public BinaryData get(int index) {
        return array[index];
    }

    public int size() {
        return array.length;
    }

    public BinaryData[] data() {
        return array;
    }
}
