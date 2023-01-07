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

package union.xenfork.fe2d.graphics.texture;

import org.lwjgl.opengl.GL11C;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11C.GL_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_NEAREST_MIPMAP_LINEAR;

/**
 * The texture parameters.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class TextureParam {
    private int minFilter = GL_NEAREST_MIPMAP_LINEAR;
    private int magFilter = GL_LINEAR;
    private final Map<Integer, Integer> customParam = new HashMap<>();

    /**
     * Sets the min filter value.
     *
     * @param minFilter the min filter value. One of: <ul>
     *                  <li>{@link GL11C#GL_NEAREST GL_NEAREST}</li>
     *                  <li>{@link GL11C#GL_LINEAR GL_LINEAR}</li>
     *                  <li>{@link GL11C#GL_NEAREST_MIPMAP_NEAREST GL_NEAREST_MIPMAP_NEAREST}</li>
     *                  <li>{@link GL11C#GL_LINEAR_MIPMAP_NEAREST GL_LINEAR_MIPMAP_NEAREST}</li>
     *                  <li>{@link GL11C#GL_NEAREST_MIPMAP_LINEAR GL_NEAREST_MIPMAP_LINEAR}</li>
     *                  <li>{@link GL11C#GL_LINEAR_MIPMAP_LINEAR GL_LINEAR_MIPMAP_LINEAR}</li>
     *                  </ul>
     * @return this.
     */
    public TextureParam minFilter(int minFilter) {
        this.minFilter = minFilter;
        return this;
    }

    /**
     * Gets the min filter value.
     *
     * @return the min filter value.
     */
    public int minFilter() {
        return minFilter;
    }

    /**
     * Sets the mag filter value.
     *
     * @param magFilter the mag filter value. One of: <ul>
     *                  <li>{@link GL11C#GL_NEAREST GL_NEAREST}</li>
     *                  <li>{@link GL11C#GL_LINEAR GL_LINEAR}</li>
     *                  </ul>
     * @return this.
     */
    public TextureParam magFilter(int magFilter) {
        this.magFilter = magFilter;
        return this;
    }

    /**
     * Gets the mag filter value.
     *
     * @return the mag filter value.
     */
    public int magFilter() {
        return magFilter;
    }

    /**
     * Sets a custom parameter that is currently not supported by Fe2D.
     *
     * @param pname the symbolic name of a single-valued texture parameter.
     * @param param the value.
     * @return this.
     */
    public TextureParam customParam(int pname, int param) {
        customParam.put(pname, param);
        return this;
    }

    /**
     * Gets the custom parameters map.
     *
     * @return the map.
     */
    public Map<Integer, Integer> customParamMap() {
        return customParam;
    }
}
