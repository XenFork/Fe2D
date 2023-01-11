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

/**
 * The color.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class Color {
    /**
     * white
     */
    public static final Color WHITE = new Color(0xffffffff);
    /**
     * The packed white color bits.
     */
    public static final int WHITE_BITS = rgbaPackABGR(1f, 1f, 1f, 1f);
    private static final float INV_BYTE_TO_FLOAT = 1.0f / 255.0f;
    private final byte r, g, b, a;

    /**
     * Creates the color with the given integer packed in ARGB.
     *
     * @param argb the color.
     */
    public Color(int argb) {
        this.r = (byte) (argb >>> 16);
        this.g = (byte) (argb >>> 8);
        this.b = (byte) argb;
        this.a = (byte) (argb >>> 24);
    }

    /**
     * Creates the color with the given components.
     *
     * @param r the red component.
     * @param g the green component.
     * @param b the blue component.
     * @param a the alpha component.
     */
    public Color(float r, float g, float b, float a) {
        this.r = floatToByte(r);
        this.g = floatToByte(g);
        this.b = floatToByte(b);
        this.a = floatToByte(a);
    }

    /**
     * Converts the normalized color to unsigned byte.
     *
     * @param color the color value.
     * @return the byte value.
     */
    public static byte floatToByte(float color) {
        return (byte) ((int) Math.min(Math.floor(color * 256f), 255f));
    }

    /**
     * Converts the unsigned byte color to normalized color.
     *
     * @param color the color value.
     * @return the float value.
     */
    public static float normalize(byte color) {
        return Byte.toUnsignedInt(color) * INV_BYTE_TO_FLOAT;
    }

    /**
     * Packs the RGBA color into an integer.
     *
     * @param r the red value.
     * @param g the green value.
     * @param b the blue value.
     * @param a the alpha value.
     * @return the packed value.
     */
    public static int rgbaPackABGR(byte r, byte g, byte b, byte a) {
        return (Byte.toUnsignedInt(a) << 24) |
               (Byte.toUnsignedInt(b) << 16) |
               (Byte.toUnsignedInt(g) << 8) |
               Byte.toUnsignedInt(r);
    }

    /**
     * Packs the RGBA color into an integer.
     *
     * @param r the red value.
     * @param g the green value.
     * @param b the blue value.
     * @param a the alpha value.
     * @return the packed value.
     */
    public static int rgbaPackABGR(float r, float g, float b, float a) {
        return rgbaPackABGR(floatToByte(r), floatToByte(g), floatToByte(b), floatToByte(a));
    }

    /**
     * Gets the red value from packed integer that is ordered in ABGR.
     *
     * @param color the packed value.
     * @return the red value.
     */
    public static byte getRedFromABGR(int color) {
        return (byte) color;
    }

    /**
     * Gets the green value from packed integer that is ordered in ABGR.
     *
     * @param color the packed value.
     * @return the green value.
     */
    public static byte getGreenFromABGR(int color) {
        return (byte) (color >>> 8);
    }

    /**
     * Gets the blue value from packed integer that is ordered in ABGR.
     *
     * @param color the packed value.
     * @return the blue value.
     */
    public static byte getBlueFromABGR(int color) {
        return (byte) (color >>> 16);
    }

    /**
     * Gets the alpha value from packed integer that is ordered in ABGR.
     *
     * @param color the packed value.
     * @return the alpha value.
     */
    public static byte getAlphaFromABGR(int color) {
        return (byte) (color >>> 24);
    }

    /**
     * Packs this color into an integer.
     *
     * @return the packed value in ABGR.
     */
    public int packABGR() {
        return rgbaPackABGR(r, g, b, a);
    }

    /**
     * Gets the red component.
     *
     * @return the red component.
     */
    public byte red() {
        return r;
    }

    /**
     * Gets the green component.
     *
     * @return the green component.
     */
    public byte green() {
        return g;
    }

    /**
     * Gets the blue component.
     *
     * @return the blue component.
     */
    public byte blue() {
        return b;
    }

    /**
     * Gets the alpha component.
     *
     * @return the alpha component.
     */
    public byte alpha() {
        return a;
    }
}
