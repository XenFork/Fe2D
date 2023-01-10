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

package union.xenfork.fe2d.graphics.mesh;

import union.xenfork.fe2d.Disposable;
import union.xenfork.fe2d.graphics.GLStateManager;
import union.xenfork.fe2d.graphics.VertexBuilder;
import union.xenfork.fe2d.graphics.vertex.VertexLayout;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * The mesh.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class Mesh implements Disposable {
    private final boolean fixed;
    private final VertexLayout layout;
    private ByteBuffer vertexBuffer;
    private IntBuffer indexBuffer;
    private int vertexCount, indexCount;
    private final int vao, vbo, ebo;

    private Mesh(boolean fixed,
                 VertexLayout layout,
                 ByteBuffer vertexBuffer,
                 IntBuffer indexBuffer,
                 int vertexCount,
                 int indexCount) {
        this.fixed = fixed;
        this.layout = layout;
        this.vertexBuffer = vertexBuffer;
        this.indexBuffer = indexBuffer;
        this.vertexCount = vertexCount;
        this.indexCount = indexCount;

        // Creates GL objects
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();
        GLStateManager.bindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
    }

    /**
     * Creates a fixed mesh with the given vertices and indices.
     *
     * @param consumer    the vertex builder.
     * @param vertexCount the vertex count.
     * @param indices     the indices.
     * @param indexCount  the index count.
     * @param layout      the vertex layout.
     * @return the mesh.
     */
    public static Mesh fixed(Consumer<VertexBuilder> consumer, int vertexCount, int[] indices, int indexCount, VertexLayout layout) {
        VertexBuilder vertexBuilder = new VertexBuilder();
        consumer.accept(vertexBuilder);
        ByteBuffer vertexBuffer = vertexBuilder.buffer();

        IntBuffer indexBuffer = memAllocInt(indices.length);
        for (int index : indices) {
            indexBuffer.put(index);
        }

        Mesh mesh = new Mesh(true, layout, vertexBuffer, indexBuffer.flip(), vertexCount, indexCount);

        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        layout.forEachAttribute((attribute, index) -> {
            glEnableVertexAttribArray(index);
            glVertexAttribPointer(index,
                attribute.size(),
                attribute.type().typeEnum(),
                attribute.normalized(),
                layout.stride(),
                layout.getPointer(index));
        });
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        GLStateManager.bindVertexArray(0);

        return mesh;
    }

    /**
     * Creates a fixed mesh with the given vertices and indices.
     *
     * @param consumer    the vertex builder.
     * @param vertexCount the vertex count.
     * @param indices     the indices.
     * @param layout      the vertex layout.
     * @return the mesh.
     */
    public static Mesh fixed(Consumer<VertexBuilder> consumer, int vertexCount, int[] indices, VertexLayout layout) {
        return fixed(consumer, vertexCount, indices, indices.length, layout);
    }

    /**
     * Creates a dynamic mesh with the given layout.
     *
     * @param layout      the vertex layout.
     * @param vertexCount the initial vertex count.
     * @param indexCount  the initial index count.
     * @return the mesh.
     */
    public static Mesh dynamic(VertexLayout layout, int vertexCount, int indexCount) {
        Mesh mesh = new Mesh(false, layout, null, null, vertexCount, indexCount);
        layout.forEachAttribute((attribute, index) -> glEnableVertexAttribArray(index));
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        GLStateManager.bindVertexArray(0);
        return mesh;
    }

    /**
     * Creates a dynamic mesh with the given layout.
     *
     * @param layout the vertex layout.
     * @return the mesh.
     */
    public static Mesh dynamic(VertexLayout layout) {
        return dynamic(layout, 0, 0);
    }

    private void checkDynamic() {
        if (fixed) throw new IllegalStateException("Can't modify the data of a fixed mesh!");
    }

    /**
     * Sets the vertex count. Only dynamic mesh.
     *
     * @param vertexCount the new vertex count.
     */
    public void setVertexCount(int vertexCount) {
        checkDynamic();
        this.vertexCount = vertexCount;
    }

    /**
     * Sets the index count. Only dynamic mesh.
     *
     * @param indexCount the new index count.
     */
    public void setIndexCount(int indexCount) {
        checkDynamic();
        this.indexCount = indexCount;
    }

    /**
     * Sets the vertices. Only dynamic mesh.
     *
     * @param consumer the new vertices.
     */
    public void setVertices(Consumer<VertexBuilder> consumer) {
        checkDynamic();
        VertexBuilder builder = new VertexBuilder(vertexBuffer);
        consumer.accept(builder);
        ByteBuffer newVertexBuffer = builder.buffer();
        long oldCapacity = vertexBuffer == null ? 0 : vertexBuffer.capacity();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        // size not enough
        if (builder.position() > oldCapacity) {
            if (vertexBuffer != newVertexBuffer) {
                memFree(vertexBuffer);
                vertexBuffer = newVertexBuffer;
            }
            int currBinding = GLStateManager.vertexArrayBinding();
            GLStateManager.bindVertexArray(vao);
            nglBufferData(GL_ARRAY_BUFFER, builder.position(), memAddress(vertexBuffer), GL_DYNAMIC_DRAW);
            layout.forEachAttribute((attribute, index) ->
                glVertexAttribPointer(index,
                    attribute.size(),
                    attribute.type().typeEnum(),
                    attribute.normalized(),
                    layout.stride(),
                    layout.getPointer(index))
            );
            GLStateManager.bindVertexArray(currBinding);
        } else {
            vertexBuffer = newVertexBuffer;
            nglBufferSubData(GL_ARRAY_BUFFER, 0, builder.position(), memAddress(vertexBuffer));
        }
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /**
     * Sets the indices. Only dynamic mesh.
     *
     * @param indices the new indices.
     */
    public void setIndices(int... indices) {
        checkDynamic();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        // size not enough
        if (indexBuffer == null || indices.length > indexBuffer.capacity()) {
            indexBuffer = memRealloc(indexBuffer, indices.length);
            for (int index : indices) {
                indexBuffer.put(index);
            }
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.flip(), GL_DYNAMIC_DRAW);
        } else {
            indexBuffer.clear();
            for (int index : indices) {
                indexBuffer.put(index);
            }
            glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, indexBuffer.flip());
        }
    }

    /**
     * Renders this mesh with the given primitive mode.
     *
     * @param primitiveMode the kind of primitives being constructed.
     * @see #render()
     */
    public void render(int primitiveMode) {
        int currBinding = GLStateManager.vertexArrayBinding();
        GLStateManager.bindVertexArray(vao);
        glDrawElements(primitiveMode, indexCount, GL_UNSIGNED_INT, 0);
        GLStateManager.bindVertexArray(currBinding);
    }

    /**
     * Renders this mesh.
     *
     * @see #render(int)
     */
    public void render() {
        render(GL_TRIANGLES);
    }

    /**
     * Gets the vertex buffer for direct operation.
     *
     * @return the vertex buffer.
     */
    public ByteBuffer vertexBuffer() {
        return vertexBuffer;
    }

    /**
     * Gets the index buffer for direct operation.
     *
     * @return the index buffer.
     */
    public IntBuffer indexBuffer() {
        return indexBuffer;
    }

    /**
     * Gets the vertex count.
     *
     * @return the vertex count.
     */
    public int vertexCount() {
        return vertexCount;
    }

    /**
     * Gets the index count.
     *
     * @return the index count.
     */
    public int indexCount() {
        return indexCount;
    }

    @Override
    public void dispose() {
        memFree(vertexBuffer);
        memFree(indexBuffer);
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }
}
