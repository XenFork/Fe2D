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
import java.util.Map;

/**
 * The binary data tree.
 *
 * @author squid233
 * @since 0.1.0
 */
public sealed class BinaryData permits BinaryDataArray, BinaryTags {
    public static final byte TYPE_BYTE = 0;
    public static final byte TYPE_SHORT = 1;
    public static final byte TYPE_INT = 2;
    public static final byte TYPE_LONG = 3;
    public static final byte TYPE_FLOAT = 4;
    public static final byte TYPE_DOUBLE = 5;
    public static final byte TYPE_STRING = 6;
    public static final byte TYPE_BYTE_ARRAY = 7;
    public static final byte TYPE_SHORT_ARRAY = 8;
    public static final byte TYPE_INT_ARRAY = 9;
    public static final byte TYPE_LONG_ARRAY = 10;
    public static final byte TYPE_FLOAT_ARRAY = 11;
    public static final byte TYPE_DOUBLE_ARRAY = 12;
    public static final byte TYPE_STRING_ARRAY = 13;
    public static final byte TYPE_DATA_ARRAY = 14;
    public static final byte TYPE_TAGS = 15;
    private final int type;
    private final Object value;

    protected BinaryData(int type, Object value) {
        this.type = type;
        this.value = value;
    }

    public static BinaryData of(byte value) {
        return new BinaryData(TYPE_BYTE, value);
    }

    public static BinaryData of(short value) {
        return new BinaryData(TYPE_SHORT, value);
    }

    public static BinaryData of(int value) {
        return new BinaryData(TYPE_INT, value);
    }

    public static BinaryData of(long value) {
        return new BinaryData(TYPE_LONG, value);
    }

    public static BinaryData of(float value) {
        return new BinaryData(TYPE_FLOAT, value);
    }

    public static BinaryData of(double value) {
        return new BinaryData(TYPE_DOUBLE, value);
    }

    public static BinaryData of(String value) {
        return new BinaryData(TYPE_STRING, value);
    }

    public static BinaryData of(byte[] value) {
        return new BinaryData(TYPE_BYTE_ARRAY, value);
    }

    public static BinaryData of(short[] value) {
        return new BinaryData(TYPE_SHORT_ARRAY, value);
    }

    public static BinaryData of(int[] value) {
        return new BinaryData(TYPE_INT_ARRAY, value);
    }

    public static BinaryData of(long[] value) {
        return new BinaryData(TYPE_LONG_ARRAY, value);
    }

    public static BinaryData of(float[] value) {
        return new BinaryData(TYPE_FLOAT_ARRAY, value);
    }

    public static BinaryData of(double[] value) {
        return new BinaryData(TYPE_DOUBLE_ARRAY, value);
    }

    public static BinaryData of(String[] value) {
        return new BinaryData(TYPE_STRING_ARRAY, value);
    }

    public static BinaryDataArray of(BinaryData[] value) {
        return new BinaryDataArray(value);
    }

    public static BinaryTags ofTags(Map<String, BinaryData> map) {
        return new BinaryTags(map);
    }

    public static BinaryTags ofTags() {
        return new BinaryTags();
    }

    public void write(ObjectOutput out) throws IOException {
        out.writeByte(type);
        switch (type) {
            case TYPE_BYTE -> out.writeByte((byte) value);
            case TYPE_SHORT -> out.writeShort((short) value);
            case TYPE_INT -> out.writeInt((int) value);
            case TYPE_LONG -> out.writeLong((long) value);
            case TYPE_FLOAT -> out.writeFloat((float) value);
            case TYPE_DOUBLE -> out.writeDouble((double) value);
            case TYPE_STRING -> out.writeUTF((String) value);
            case TYPE_BYTE_ARRAY -> {
                byte[] arr = (byte[]) value;
                out.writeInt(arr.length);
                for (var v : arr) {
                    out.writeByte(v);
                }
            }
            case TYPE_SHORT_ARRAY -> {
                short[] arr = (short[]) value;
                out.writeInt(arr.length);
                for (var v : arr) {
                    out.writeShort(v);
                }
            }
            case TYPE_INT_ARRAY -> {
                int[] arr = (int[]) value;
                out.writeInt(arr.length);
                for (var v : arr) {
                    out.writeInt(v);
                }
            }
            case TYPE_LONG_ARRAY -> {
                long[] arr = (long[]) value;
                out.writeInt(arr.length);
                for (var v : arr) {
                    out.writeLong(v);
                }
            }
            case TYPE_FLOAT_ARRAY -> {
                float[] arr = (float[]) value;
                out.writeInt(arr.length);
                for (var v : arr) {
                    out.writeFloat(v);
                }
            }
            case TYPE_DOUBLE_ARRAY -> {
                double[] arr = (double[]) value;
                out.writeInt(arr.length);
                for (var v : arr) {
                    out.writeDouble(v);
                }
            }
            case TYPE_STRING_ARRAY -> {
                String[] arr = (String[]) value;
                out.writeInt(arr.length);
                for (var v : arr) {
                    out.writeUTF(v);
                }
            }
            // implicitly calling subclass method
            case TYPE_DATA_ARRAY, TYPE_TAGS -> write(out);
            default -> throw new IllegalStateException("Unexpected type " + type + " detected! This is a bug!");
        }
    }

    public int type() {
        return type;
    }

    public Object value() {
        return value;
    }
}
