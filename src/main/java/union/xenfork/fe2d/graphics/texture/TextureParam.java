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
import static org.lwjgl.opengl.GL11C.GL_NEAREST;

/**
 * The texture parameters.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class TextureParam {
    private int minFilter = GL_NEAREST;
    private int magFilter = GL_LINEAR;
    private int baseLevel = 0;
    private int maxLevel = 0;
    private float minLod = 0f;
    private float maxLod = 0f;
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
     * Sets the base level value.
     *
     * @param baseLevel the base level value.
     * @return this.
     */
    public TextureParam baseLevel(int baseLevel) {
        this.baseLevel = baseLevel;
        return this;
    }

    /**
     * Gets the base level value.
     *
     * @return the base level value.
     */
    public int baseLevel() {
        return baseLevel;
    }

    /**
     * Sets the max level value.
     *
     * @param maxLevel the max level value.
     * @return this.
     */
    public TextureParam maxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    /**
     * Gets the max level value.
     *
     * @return the max level value.
     */
    public int maxLevel() {
        return maxLevel;
    }

    /**
     * Sets the min lod value.
     *
     * @param minLod the min lod value.
     * @return this.
     */
    public TextureParam minLod(float minLod) {
        this.minLod = minLod;
        return this;
    }

    /**
     * Gets the min lod value.
     *
     * @return the min lod value.
     */
    public float minLod() {
        return minLod;
    }

    /**
     * Sets the max lod value.
     *
     * @param maxLod the max lod value.
     * @return this.
     */
    public TextureParam maxLod(float maxLod) {
        this.maxLod = maxLod;
        return this;
    }

    /**
     * Gets the max lod value.
     *
     * @return the max lod value.
     */
    public float maxLod() {
        return maxLod;
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
