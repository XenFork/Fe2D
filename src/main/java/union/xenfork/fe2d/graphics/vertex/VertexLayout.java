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

import org.jetbrains.annotations.Nullable;
import union.xenfork.fe2d.graphics.ShaderProgram;

import java.util.*;
import java.util.function.ObjIntConsumer;

/**
 * The vertex layout which contains a set of {@link VertexAttribute}.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class VertexLayout {
    private final Map<String, Integer> indexMap = new HashMap<>();
    private final Map<Integer, VertexAttribute> attributeMap = new LinkedHashMap<>();
    private final Map<Integer, Long> pointerMap = new HashMap<>();
    private final int stride;

    /**
     * Creates the vertex layout.
     *
     * @param program    the shader program to be used when the attribute is shader defined.
     * @param attributes the vertex attributes.
     */
    public VertexLayout(@Nullable ShaderProgram program, VertexAttribute... attributes) {
        int nextIndex = 0;
        int pointer = 0;
        for (VertexAttribute attribute : attributes) {
            String name = attribute.name();
            int index;
            if (attribute.direct()) {
                index = attribute.index();
                if (index > nextIndex) {
                    nextIndex = index;
                }
            } else if (attribute.shaderDefined()) {
                index = Objects.requireNonNull(program, "program must not be null when attribute index is shader-defined!")
                    .getAttributeIndex(name);
                if (index != -1 && index > nextIndex) {
                    nextIndex = index;
                }
            } else {
                index = nextIndex++;
            }
            if (index != -1) {
                indexMap.put(name, index);
                attributeMap.put(index, attribute);
                pointerMap.put(index, (long) pointer);
            }
            pointer += attribute.size() * attribute.type().bytesSize();
        }
        stride = pointer;
    }

    /**
     * Creates the vertex layout.
     *
     * @param attributes the vertex attributes.
     */
    public VertexLayout(VertexAttribute... attributes) {
        this(null, attributes);
    }

    /**
     * Gets the index with the given name of a vertex attribute.
     *
     * @param name the name of the vertex attribute.
     * @return the index, or -1 if not found.
     */
    public int getIndex(String name) {
        return indexMap.getOrDefault(name, -1);
    }

    /**
     * Gets the vertex attribute with the given index.
     *
     * @param index the index.
     * @return the vertex attribute.
     */
    public VertexAttribute getAttribute(int index) {
        return attributeMap.get(index);
    }

    /**
     * Gets a set of vertex attributes.
     *
     * @return the set.
     */
    public Set<Map.Entry<Integer, VertexAttribute>> getAttributes() {
        return attributeMap.entrySet();
    }

    /**
     * Performs the given action for each vertex attribute.
     *
     * @param action the action to be performed.
     */
    public void forEachAttribute(ObjIntConsumer<VertexAttribute> action) {
        for (var e : attributeMap.entrySet()) {
            action.accept(e.getValue(), e.getKey());
        }
    }

    /**
     * Gets the offset with the given index of the vertex attribute.
     *
     * @param index the index.
     * @return the offset.
     */
    public long getPointer(int index) {
        return pointerMap.get(index);
    }

    /**
     * Gets the stride.
     *
     * @return the stride.
     */
    public int stride() {
        return stride;
    }
}
