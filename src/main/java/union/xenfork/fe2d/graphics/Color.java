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
     * light gray
     */
    public static final Color LIGHT_GRAY = new Color(0xffbfbfbf);
    /**
     * gray
     */
    public static final Color GRAY = new Color(0xff7f7f7f);
    /**
     * dark gray
     */
    public static final Color DARK_GRAY = new Color(0xff3f3f3f);
    /**
     * black
     */
    public static final Color BLACK = new Color(0xff000000);

    /**
     * The packed white color bits.
     */
    public static final int WHITE_BITS = rgbaPackABGR(1f, 1f, 1f, 1f);

    /**
     * clear
     */
    public static final Color CLEAR = new Color(0x00000000);

    /**
     * blue
     */
    public static final Color BLUE = new Color(0xff0000ff);
    /**
     * navy
     */
    public static final Color NAVY = new Color(0xff000080);
    /**
     * royal
     */
    public static final Color ROYAL = new Color(0xff4169e1);
    /**
     * slate
     */
    public static final Color SLATE = new Color(0xff708090);
    /**
     * sky
     */
    public static final Color SKY = new Color(0xff87ceeb);
    /**
     * cyan
     */
    public static final Color CYAN = new Color(0xff00ffff);
    /**
     * teal
     */
    public static final Color TEAL = new Color(0xff008080);

    /**
     * green
     */
    public static final Color GREEN = new Color(0xff00ff00);
    /**
     * chartreuse
     */
    public static final Color CHARTREUSE = new Color(0xff7fff00);
    /**
     * lime
     */
    public static final Color LIME = new Color(0xff32cd32);
    /**
     * forest
     */
    public static final Color FOREST = new Color(0xff228b22);
    /**
     * olive
     */
    public static final Color OLIVE = new Color(0xff6b8e23);

    /**
     * yellow
     */
    public static final Color YELLOW = new Color(0xffffff00);
    /**
     * gold
     */
    public static final Color GOLD = new Color(0xffffd700);
    /**
     * goldenrod
     */
    public static final Color GOLDENROD = new Color(0xffdaa520);
    /**
     * orange
     */
    public static final Color ORANGE = new Color(0xffffa500);

    /**
     * brown
     */
    public static final Color BROWN = new Color(0xff8b4513);
    /**
     * tan
     */
    public static final Color TAN = new Color(0xffd2b48c);
    /**
     * firebrick
     */
    public static final Color FIREBRICK = new Color(0xffb22222);

    /**
     * red
     */
    public static final Color RED = new Color(0xffff0000);
    /**
     * scarlet
     */
    public static final Color SCARLET = new Color(0xffff341c);
    /**
     * coral
     */
    public static final Color CORAL = new Color(0xffff7f50);
    /**
     * salmon
     */
    public static final Color SALMON = new Color(0xfffa8072);
    /**
     * pink
     */
    public static final Color PINK = new Color(0xffff69b4);
    /**
     * magenta
     */
    public static final Color MAGENTA = new Color(0xffff00ff);

    /**
     * purple
     */
    public static final Color PURPLE = new Color(0xffa020f0);
    /**
     * violet
     */
    public static final Color VIOLET = new Color(0xffee82ee);
    /**
     * maroon
     */
    public static final Color MAROON = new Color(0xffb03060);

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
