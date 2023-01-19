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

package union.xenfork.fe2d.graphics.batch;

import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import union.xenfork.fe2d.Fe2D;
import union.xenfork.fe2d.graphics.Color;
import union.xenfork.fe2d.graphics.GLStateManager;
import union.xenfork.fe2d.graphics.ShaderProgram;
import union.xenfork.fe2d.graphics.mesh.Mesh;
import union.xenfork.fe2d.graphics.sprite.Sprite;
import union.xenfork.fe2d.graphics.texture.Texture;
import union.xenfork.fe2d.graphics.texture.TextureRegion;
import union.xenfork.fe2d.graphics.vertex.VertexAttribute;

import static org.lwjgl.opengl.GL11C.*;
import static union.xenfork.fe2d.graphics.GLStateManager.*;

/**
 * The sprite batch.
 *
 * @author squid233
 * @since 0.1.0
 */
public class SpriteBatch implements Batch {
    /**
     * The default value of max sprites.
     */
    public static final int DEFAULT_MAX_SPRITES = 1000;
    /**
     * The max sprites.
     */
    public static final int MAX_SPRITES = Integer.MAX_VALUE / Sprite.SPRITE_SIZE / Sprite.SPRITE_VERTEX;
    private static final Matrix4fc IDENTITY_MAT = new Matrix4f();
    private final Mesh mesh;
    private final int maxVertexBytesSize;
    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4f modelMatrix = new Matrix4f();
    private final Matrix4f combinedMatrix = new Matrix4f();
    private final Matrix4f spriteMatrix = new Matrix4f();
    final ShaderProgram shader;
    private ShaderProgram customShader;
    private final boolean ownsShader;
    private int blendSrcRGB = GL_SRC_ALPHA;
    private int blendDstRGB = GL_ONE_MINUS_SRC_ALPHA;
    private int blendSrcAlpha = GL_SRC_ALPHA;
    private int blendDstAlpha = GL_ONE_MINUS_SRC_ALPHA;
    private boolean blendDisabled = false;
    private boolean drawing = false;
    private int colorBits = Color.WHITE_BITS;
    private int vertexBufferPos = 0;
    private int drawnSpriteCount = 0;
    private Texture lastTexture;
    private float invTexWidth, invTexHeight;
    private boolean disposed = false;

    /**
     * Creates the sprite batch with the given shader and size.
     *
     * @param defaultShader the custom shader to be used. if no custom shader provided, {@link #createDefaultShader()} is used.
     * @param maxSprites    the max sprite count. defaults to {@value #DEFAULT_MAX_SPRITES}.
     */
    public SpriteBatch(@Nullable ShaderProgram defaultShader, int maxSprites) {
        // note: since 0x7FFFFFFF / 4 * SPRITE_SIZE overflows,
        // we use 0x7FFFFFFF / SPRITE_SIZE / SPRITE_VERTEX as the max count.
        int size = Math.clamp(1, MAX_SPRITES, maxSprites);
        this.mesh = Mesh.fixedSize(Sprite.LAYOUT, size * Sprite.SPRITE_VERTEX, size * 6);
        this.maxVertexBytesSize = mesh.vertexCount() * Sprite.LAYOUT.stride();
        this.shader = defaultShader != null ? defaultShader : createDefaultShader();
        this.ownsShader = defaultShader == null;

        projectionMatrix.setOrtho2D(0, Fe2D.graphics.width(), 0, Fe2D.graphics.height());

        int[] indices = new int[size * 6];
        for (int i = 0, j = 0; i < indices.length; i += 6, j += 4) {
            indices[i] = j;
            indices[i + 1] = j + 1;
            indices[i + 2] = j + 2;
            indices[i + 3] = j + 2;
            indices[i + 4] = j + 3;
            indices[i + 5] = j;
        }
        mesh.setIndices(indices);
    }

    /**
     * Creates the sprite batch with the given size.
     *
     * @param maxSprites the max sprite count.
     */
    public SpriteBatch(int maxSprites) {
        this(null, maxSprites);
    }

    /**
     * Creates the sprite batch with the given shader.
     *
     * @param defaultShader the custom shader to be used. if no custom shader provided, {@link #createDefaultShader()} is used.
     */
    public SpriteBatch(@Nullable ShaderProgram defaultShader) {
        this(defaultShader, DEFAULT_MAX_SPRITES);
    }

    /**
     * Creates the sprite batch with the default size.
     */
    public SpriteBatch() {
        this(null, DEFAULT_MAX_SPRITES);
    }

    /**
     * Creates the default shader program.
     * <p>
     * Builtin vertex attributes and <a href="../ShaderProgram.html#Builtin_Uniforms">uniforms</a> are used.
     * <p>
     * An additional uniform {@code HasTexture0} is used.
     *
     * @return the shader program.
     */
    public static ShaderProgram createDefaultShader() {
        return new ShaderProgram(String.format("""
            #version 150 core
            in vec2 %1$s;
            in vec4 %2$s;
            in vec2 %3$s;
            out vec4 vertexColor;
            out vec2 UV0;
            uniform mat4 %4$s;
            void main() {
                gl_Position = %4$s * vec4(%1$s, 0.0, 1.0);
                vertexColor = %2$s;
                UV0 = %3$s;
            }
            """, VertexAttribute.POSITION_ATTRIB, VertexAttribute.COLOR_ATTRIB, VertexAttribute.TEX_COORD_ATTRIB + '0', ShaderProgram.U_PROJECTION_VIEW_MODEL_MATRIX
        ), String.format("""
            #version 150 core
            in vec4 vertexColor;
            in vec2 UV0;
            out vec4 FragColor;
            uniform sampler2D %1$s;
            uniform int HasTexture0;
            void main() {
                // If HasTexture0 is 0, only 1.0 will be multiplier.
                FragColor = vertexColor * (HasTexture0 * (texture(%1$s, UV0) - 1.0) + 1.0);
            }
            """, ShaderProgram.U_SAMPLER + '0'),
            Sprite.LAYOUT);
    }

    @Override
    public void begin() {
        if (drawing) throw new IllegalStateException("Cannot call SpriteBatch.begin while drawing");
        drawing = true;
        vertexBufferPos = 0;
        drawnSpriteCount = 0;
    }

    @Override
    public void end() {
        if (!drawing) throw new IllegalStateException("Can only call SpriteBatch.end while drawing");
        if (vertexBufferPos > 0) flush();
        lastTexture = null;
        drawing = false;
    }

    @Override
    public void flush() {
        if (vertexBufferPos == 0) return;
        checkDrawing();
        mesh.updateVertices(vertexBufferPos);
        int currPrg = currentProgram();
        int currTex = textureBinding2D();
        boolean blend = isBlendEnabled();
        int sRGB = blendSrcRGB();
        int dRGB = blendDstRGB();
        int sAlpha = blendSrcAlpha();
        int dAlpha = blendDstAlpha();
        if (blendDisabled && blend) {
            GLStateManager.disableBlend();
        } else if (!blendDisabled) {
            if (!blend) {
                GLStateManager.enableBlend();
            }
            blendFuncSeparate(blendSrcRGB, blendDstRGB, blendSrcAlpha, blendDstAlpha);
        }
        shader().use();
        setupMatrices();
        boolean hasTexture = lastTexture != null;
        shader().setUniform("HasTexture0", hasTexture ? 1 : 0);
        if (hasTexture) {
            lastTexture.bind();
        }
        mesh.render(GL_TRIANGLES, drawnSpriteCount * 6);
        bindTexture2D(currTex);
        drawnSpriteCount = 0;
        vertexBufferPos = 0;
        useProgram(currPrg);
        if (blendDisabled && blend) {
            GLStateManager.enableBlend();
        } else if (!blendDisabled) {
            if (!blend) {
                GLStateManager.disableBlend();
            }
            blendFuncSeparate(sRGB, dRGB, sAlpha, dAlpha);
        }
    }

    private void checkDrawing() {
        if (!drawing)
            throw new IllegalStateException("Can only call SpriteBatch.draw or flush between begin and end (while drawing)");
    }

    private void switchTexture(Texture texture) {
        flush();
        lastTexture = texture;
        if (texture != null) {
            invTexWidth = 1f / texture.width();
            invTexHeight = 1f / texture.height();
        } else {
            invTexWidth = 0;
            invTexHeight = 0;
        }
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height, float u0, float v0, float u1, float v1, Matrix4fc transform) {
        checkDrawing();
        if (texture != lastTexture)
            switchTexture(texture);
        else if (vertexBufferPos >= maxVertexBytesSize)
            flush();

        float x0, y0, x1, y1;
        if ((transform.properties() & Matrix4fc.PROPERTY_IDENTITY) != 0) {
            x0 = x;
            y0 = y;
            x1 = x + width;
            y1 = y + height;
        } else {
            x0 = Math.fma(transform.m00(), x, Math.fma(transform.m10(), y, transform.m30()));
            y0 = Math.fma(transform.m01(), x, Math.fma(transform.m11(), y, transform.m31()));
            x1 = Math.fma(transform.m00(), x + width, Math.fma(transform.m10(), y + height, transform.m30()));
            y1 = Math.fma(transform.m01(), x + width, Math.fma(transform.m11(), y + height, transform.m31()));
        }

        mesh.vertexBuffer()
            // left-top
            .putFloat(vertexBufferPos, x0).putFloat(vertexBufferPos + 4, y1)
            .putInt(vertexBufferPos + 8, colorBits)
            .putFloat(vertexBufferPos + 12, u0).putFloat(vertexBufferPos + 16, v0)
            // left-bottom
            .putFloat(vertexBufferPos + 20, x0).putFloat(vertexBufferPos + 24, y0)
            .putInt(vertexBufferPos + 28, colorBits)
            .putFloat(vertexBufferPos + 32, u0).putFloat(vertexBufferPos + 36, v1)
            // right-bottom
            .putFloat(vertexBufferPos + 40, x1).putFloat(vertexBufferPos + 44, y0)
            .putInt(vertexBufferPos + 48, colorBits)
            .putFloat(vertexBufferPos + 52, u1).putFloat(vertexBufferPos + 56, v1)
            // right-top
            .putFloat(vertexBufferPos + 60, x1).putFloat(vertexBufferPos + 64, y1)
            .putInt(vertexBufferPos + 68, colorBits)
            .putFloat(vertexBufferPos + 72, u1).putFloat(vertexBufferPos + 76, v0);
        vertexBufferPos += Sprite.SPRITE_SIZE;
        drawnSpriteCount++;
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height, float u0, float v0, float u1, float v1) {
        draw(texture, x, y, width, height, u0, v0, u1, v1, IDENTITY_MAT);
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height) {
        draw(texture, x, y, width, height, 0f, 0f, 1f, 1f);
    }

    @Override
    public void draw(Texture texture, float x, float y) {
        draw(texture, x, y, texture.width(), texture.height());
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height, TextureRegion region, Matrix4fc transform) {
        // important: this.invTexWidth/Height is set in switchTexture. we must compute them first
        if (texture != lastTexture)
            switchTexture(texture);
        draw(texture, x, y, width, height,
            region.u0() * invTexWidth, region.v0() * invTexHeight,
            region.u1() * invTexWidth, region.v1() * invTexHeight,
            transform);
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height, TextureRegion region) {
        draw(texture, x, y, width, height, region, IDENTITY_MAT);
    }

    @Override
    public void draw(Texture texture, float x, float y, TextureRegion region) {
        draw(texture, x, y, region.u1() - region.u0(), region.v1() - region.v0(), region);
    }

    /**
     * Draws a sprite with the given transformation.
     *
     * @param sprite    the sprite to be drawn.
     * @param transform the matrix transformation.
     */
    public void draw(Sprite sprite, Matrix4fc transform) {
        int currColor = spriteColor();
        setSpriteColor(sprite.color);
        draw(sprite.texture,
            0f, 0f,
            sprite.size.x(), sprite.size.y(),
            sprite.textureRegion,
            transform.mul(sprite.getTransform(), spriteMatrix));
        setSpriteColor(currColor);
    }

    /**
     * Draws a sprite.
     *
     * @param sprite the sprite to be drawn.
     */
    public void draw(Sprite sprite) {
        int currColor = spriteColor();
        setSpriteColor(sprite.color);
        draw(sprite.texture,
            0f, 0f,
            sprite.size.x(), sprite.size.y(),
            sprite.textureRegion,
            sprite.getTransform());
        setSpriteColor(currColor);
    }

    private void setupMatrices() {
        projectionMatrix.mul(modelMatrix, combinedMatrix);
        shader().setProjectionViewModelMatrix(combinedMatrix);
        shader().uploadUniforms();
    }

    @Override
    public void setShader(ShaderProgram shader) {
        if (shader == customShader) return;
        if (drawing) flush();
        customShader = shader;
        if (drawing) {
            int currProgram = currentProgram();
            shader().use();
            setupMatrices();
            useProgram(currProgram);
        }
    }

    @Override
    public ShaderProgram shader() {
        return customShader != null ? customShader : shader;
    }

    @Override
    public Matrix4f projectionMatrix() {
        return projectionMatrix;
    }

    @Override
    public Matrix4f modelMatrix() {
        return modelMatrix;
    }

    @Override
    public void setProjectionMatrix(Matrix4fc projectionMatrix) {
        if (drawing) flush();
        this.projectionMatrix.set(projectionMatrix);
        if (drawing) {
            int currProgram = currentProgram();
            shader().use();
            setupMatrices();
            useProgram(currProgram);
        }
    }

    @Override
    public void setModelMatrix(Matrix4fc modelMatrix) {
        if (drawing) flush();
        this.modelMatrix.set(modelMatrix);
        if (drawing) {
            int currProgram = currentProgram();
            shader().use();
            setupMatrices();
            useProgram(currProgram);
        }
    }

    @Override
    public void enableBlend() {
        if (!blendDisabled) return;
        flush();
        blendDisabled = false;
    }

    @Override
    public void disableBlend() {
        if (blendDisabled) return;
        flush();
        blendDisabled = true;
    }

    @Override
    public void setBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        if (blendSrcRGB != srcRGB ||
            blendDstRGB != dstRGB ||
            blendSrcAlpha != srcAlpha ||
            blendDstAlpha != dstAlpha) {
            flush();
            blendSrcRGB = srcRGB;
            blendDstRGB = dstRGB;
            blendSrcAlpha = srcAlpha;
            blendDstAlpha = dstAlpha;
        }
    }

    @Override
    public void setBlendFunc(int srcFactor, int dstFactor) {
        setBlendFuncSeparate(srcFactor, dstFactor, srcFactor, dstFactor);
    }

    @Override
    public void setSpriteColor(int packedColor) {
        this.colorBits = packedColor;
    }

    @Override
    public int spriteColor() {
        return colorBits;
    }

    @Override
    public boolean isBlendDisabled() {
        return blendDisabled;
    }

    @Override
    public boolean isDrawing() {
        return drawing;
    }

    @Override
    public void dispose() {
        if (disposed) return;
        disposed = true;
        mesh.dispose();
        if (ownsShader && shader != null) {
            shader.dispose();
        }
    }
}
