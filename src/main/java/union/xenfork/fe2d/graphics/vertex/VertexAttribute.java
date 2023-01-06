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

package union.xenfork.fe2d.graphics.vertex;

/**
 * The vertex attribute.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class VertexAttribute {
    /**
     * The name of position attribute.
     */
    public static final String POSITION_ATTRIB = "fe_Position";
    /**
     * The name of color attribute.
     */
    public static final String COLOR_ATTRIB = "fe_Color";
    /**
     * The name of texture coordinate attribute.
     */
    public static final String TEX_COORD_ATTRIB = "fe_TexCoord";
    /**
     * The name of vertex normal attribute.
     */
    public static final String NORMAL_ATTRIB = "fe_Normal";

    private final int index;
    private final String name;
    private final int size;
    private final boolean normalized;
    private final boolean shaderDefined;
    private final boolean direct;

    private VertexAttribute(int index,
                            String name,
                            int size,
                            boolean normalized,
                            boolean shaderDefined,
                            boolean direct) {
        this.index = index;
        this.name = name;
        this.size = size;
        this.normalized = normalized;
        this.shaderDefined = shaderDefined;
        this.direct = direct;
    }

    /**
     * Creates a vertex attribute, without explicitly defining index that is not defined in the shader
     * and defining with vertex layout.
     * <p>
     * Code-defined: no<br/>
     * Shader-defined: no<br/>
     * Layout-defined: yes
     *
     * @param name       the name of the vertex attribute.
     * @param size       the component size of the vertex attribute.
     * @param normalized whether fixed-point data values should be normalized or converted directly as fixed-point values
     *                   when they are accessed.
     * @return the vertex attribute.
     * @see #ofShaderDefined(String, int, boolean) shaderDefined
     * @see #ofDirect(int, String, int, boolean) direct
     */
    public static VertexAttribute ofImplicit(String name, int size, boolean normalized) {
        return new VertexAttribute(-1, name, size, normalized, false, false);
    }

    /**
     * Creates a vertex attribute, without directly defining index that is defined in the shader.
     * <p>
     * Code-defined: no<br/>
     * Shader-defined: yes<br/>
     * Layout-defined: no
     *
     * @param name       the name of the vertex attribute.
     * @param size       the component size of the vertex attribute.
     * @param normalized whether fixed-point data values should be normalized or converted directly as fixed-point values
     *                   when they are accessed.
     * @return the vertex attribute.
     * @see #ofImplicit(String, int, boolean) implicit
     * @see #ofDirect(int, String, int, boolean) direct
     */
    public static VertexAttribute ofShaderDefined(String name, int size, boolean normalized) {
        return new VertexAttribute(-1, name, size, normalized, true, false);
    }

    /**
     * Creates a vertex attribute, with directly defining index.
     * <p>
     * Code-defined: yes<br/>
     * Shader-defined: no<br/>
     * Layout-defined: no
     *
     * @param index      the index of the vertex attribute.
     * @param name       the name of the vertex attribute.
     * @param size       the component size of the vertex attribute.
     * @param normalized whether fixed-point data values should be normalized or converted directly as fixed-point values
     *                   when they are accessed.
     * @return the vertex attribute.
     * @see #ofImplicit(String, int, boolean) implicit
     * @see #ofShaderDefined(String, int, boolean) shaderDefined
     */
    public static VertexAttribute ofDirect(int index, String name, int size, boolean normalized) {
        return new VertexAttribute(index, name, size, normalized, true, true);
    }

    public static Builtin position() {
        return new Builtin(POSITION_ATTRIB, 3, false);
    }

    public static Builtin color() {
        return new Builtin(COLOR_ATTRIB, 4, false);
    }

    public static Builtin colorPacked() {
        return new Builtin(COLOR_ATTRIB, 4, true);
    }

    public static Builtin texCoord(int unit) {
        return new Builtin(TEX_COORD_ATTRIB + unit, 2, false);
    }

    public static Builtin normal() {
        return new Builtin(NORMAL_ATTRIB, 3, true);
    }

    /**
     * The builtin attribute selector.
     *
     * @author squid233
     * @since 0.1.0
     */
    public static final class Builtin {
        private final String name;
        private final int size;
        private final boolean normalized;

        private Builtin(String name, int size, boolean normalized) {
            this.name = name;
            this.size = size;
            this.normalized = normalized;
        }

        /**
         * Creates the implicit attribute.
         *
         * @return the vertex attribute.
         * @see #ofImplicit(String, int, boolean)
         */
        public VertexAttribute getImplicit() {
            return ofImplicit(name, size, normalized);
        }

        /**
         * Creates the shader defined attribute.
         *
         * @return the vertex attribute.
         * @see #ofShaderDefined(String, int, boolean)
         */
        public VertexAttribute getShaderDefined() {
            return ofShaderDefined(name, size, normalized);
        }

        /**
         * Creates the direct attribute.
         *
         * @param index the index of the vertex attribute.
         * @return the vertex attribute.
         * @see #ofDirect(int, String, int, boolean)
         */
        public VertexAttribute getDirect(int index) {
            return ofDirect(index, name, size, normalized);
        }
    }

    public int index() {
        return index;
    }

    public String name() {
        return name;
    }

    public int size() {
        return size;
    }

    public boolean normalized() {
        return normalized;
    }

    public boolean explicit() {
        return shaderDefined;
    }

    public boolean direct() {
        return direct;
    }
}
