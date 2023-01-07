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

    public static Mesh fixed(Consumer<VertexBuilder> consumer, int vertexCount, int[] indices, VertexLayout layout) {
        return fixed(consumer, vertexCount, indices, indices.length, layout);
    }

    public static Mesh dynamic(VertexLayout layout, int vertexCount, int indexCount) {
        Mesh mesh = new Mesh(false, layout, null, null, vertexCount, indexCount);
        layout.forEachAttribute((attribute, index) -> glEnableVertexAttribArray(index));
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        GLStateManager.bindVertexArray(0);
        return mesh;
    }

    public static Mesh dynamic(VertexLayout layout) {
        return dynamic(layout, 0, 0);
    }

    private void checkDynamic() {
        if (fixed) throw new IllegalStateException("Can't modify the data of a fixed mesh!");
    }

    public void setVertexCount(int vertexCount) {
        checkDynamic();
        this.vertexCount = vertexCount;
    }

    public void setIndexCount(int indexCount) {
        checkDynamic();
        this.indexCount = indexCount;
    }

    public void setVertices(Consumer<VertexBuilder> consumer) {
        checkDynamic();
        VertexBuilder builder = new VertexBuilder();
        consumer.accept(builder);
        ByteBuffer newVertexBuffer = builder.buffer();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        // size not enough
        if (vertexBuffer == null || builder.position() > vertexBuffer.capacity()) {
            memFree(vertexBuffer);
            vertexBuffer = newVertexBuffer;
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
            for (long i = 0; i < builder.position(); i++) {
                vertexBuffer.put((int) i, newVertexBuffer.get((int) i));
            }
            memFree(newVertexBuffer);
            nglBufferSubData(GL_ARRAY_BUFFER, 0, builder.position(), memAddress(vertexBuffer));
        }
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

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

    public void render(int primitiveMode) {
        int currBinding = GLStateManager.vertexArrayBinding();
        GLStateManager.bindVertexArray(vao);
        glDrawElements(primitiveMode, indexCount, GL_UNSIGNED_INT, 0);
        GLStateManager.bindVertexArray(currBinding);
    }

    public void render() {
        render(GL_TRIANGLES);
    }

    public int vertexCount() {
        return vertexCount;
    }

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
