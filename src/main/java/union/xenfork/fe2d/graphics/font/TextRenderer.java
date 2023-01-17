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

package union.xenfork.fe2d.graphics.font;

import org.joml.Matrix2f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.system.MemoryStack;
import union.xenfork.fe2d.Disposable;
import union.xenfork.fe2d.Fe2D;
import union.xenfork.fe2d.graphics.Color;
import union.xenfork.fe2d.graphics.ShaderProgram;
import union.xenfork.fe2d.graphics.mesh.Mesh;
import union.xenfork.fe2d.graphics.vertex.VertexAttribute;
import union.xenfork.fe2d.graphics.vertex.VertexLayout;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL12C.*;
import static org.lwjgl.system.MemoryUtil.*;
import static union.xenfork.fe2d.graphics.GLStateManager.*;

/**
 * The text renderer.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class TextRenderer implements Disposable {
    private static final VertexLayout LAYOUT = new VertexLayout(
        VertexAttribute.position2().getImplicit(),
        VertexAttribute.texCoord(0).getImplicit()
    );
    private static TextRenderer instance;
    private final int texture;
    private final ShaderProgram shader;
    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4f modelMatrix = new Matrix4f();
    private final Matrix4f combinedMatrix = new Matrix4f();
    private final Matrix2f textureMatrix = new Matrix2f();
    private final Mesh mesh;
    private ByteBuffer buffer;
    private int width, height;
    private int texWidth, texHeight;
    private int colorBits = Color.WHITE_BITS;
    private int blendSrcRGB = GL_SRC_ALPHA;
    private int blendDstRGB = GL_ONE_MINUS_SRC_ALPHA;
    private int blendSrcAlpha = GL_SRC_ALPHA;
    private int blendDstAlpha = GL_ONE_MINUS_SRC_ALPHA;
    private boolean blendDisabled = false;
    private boolean drawing = false;
    private boolean disposed = false;

    private TextRenderer() {
        shader = new ShaderProgram(String.format("""
            #version 150 core
            in vec2 %1$s;
            in vec2 %2$s;
            out vec2 UV0;
            uniform mat4 %3$s;
            uniform mat2 TextureMatrix;
            void main() {
                gl_Position = %3$s * vec4(%1$s, 0.0, 1.0);
                UV0 = TextureMatrix * %2$s;
            }
            """, VertexAttribute.POSITION_ATTRIB, VertexAttribute.TEX_COORD_ATTRIB + '0', ShaderProgram.U_PROJECTION_VIEW_MODEL_MATRIX
        ), String.format("""
            #version 150 core
            in vec2 UV0;
            out vec4 FragColor;
            uniform sampler2D %1$s;
            void main() {
                FragColor = texture(%1$s, UV0);
            }
            """, ShaderProgram.U_SAMPLER + '0'),
            LAYOUT);
        mesh = Mesh.immutable(builder -> builder
                .floats(0f, 1f, 0f, 1f,
                    0f, 0f, 0f, 0f,
                    1f, 0f, 1f, 0f,
                    1f, 1f, 1f, 1f),
            4,
            new int[]{0, 1, 2, 2, 3, 0},
            LAYOUT);
        texture = glGenTextures();
        bindTexture2D(texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_LOD, 0f);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_LOD, 0f);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
        resize(Fe2D.graphics.width(), Fe2D.graphics.height());
    }

    public static TextRenderer getInstance() {
        if (instance == null) {
            instance = new TextRenderer();
        }
        return instance;
    }

    public void resize(int width, int height) {
        boolean grown = width > this.width || height > this.height;
        this.width = width;
        this.height = height;
        if (grown) {
            texWidth = width;
            texHeight = height;
            buffer = memRealloc(buffer, width * height * 4);
            int currTex = textureBinding2D();
            bindTexture2D(texture);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
            bindTexture2D(currTex);
        }
        projectionMatrix.setOrtho2D(0, width, 0, height);
        modelMatrix.scaling(width, height, 1f);
        textureMatrix.scaling((float) width / Math.max(1, texWidth),
            (float) height / Math.max(1, texHeight));
    }

    public void begin() {
        if (drawing) throw new IllegalStateException("Cannot call TextRenderer.begin while drawing");
        drawing = true;
        memSet(buffer, 0);
    }

    public void end() {
        if (!drawing) throw new IllegalStateException("Can only call TextRenderer.end while drawing");
        flush();
        drawing = false;
    }

    public void flush() {
        int currPrg = currentProgram();
        int currTex = textureBinding2D();
        boolean blend = isBlendEnabled();
        int sRGB = blendSrcRGB();
        int dRGB = blendDstRGB();
        int sAlpha = blendSrcAlpha();
        int dAlpha = blendDstAlpha();
        if (blendDisabled && blend) {
            disableBlend();
        } else if (!blendDisabled) {
            if (!blend) {
                enableBlend();
            }
            blendFuncSeparate(blendSrcRGB, blendDstRGB, blendSrcAlpha, blendDstAlpha);
        }
        shader.use();
        setupMatrices();
        bindTexture2D(texture);
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        mesh.render();
        bindTexture2D(currTex);
        useProgram(currPrg);
        if (blendDisabled && blend) {
            enableBlend();
        } else if (!blendDisabled) {
            if (!blend) {
                disableBlend();
            }
            blendFuncSeparate(sRGB, dRGB, sAlpha, dAlpha);
        }
    }

    private void setupMatrices() {
        projectionMatrix.mul(modelMatrix, combinedMatrix);
        shader.setProjectionViewModelMatrix(combinedMatrix);
        shader.setUniform("TextureMatrix", textureMatrix);
        shader.uploadUniforms();
    }

    public void drawCodePoint(Font font, int codePoint, float x, float y, float scaleX, float scaleY, int leftSideBearing) {
        font.drawCodePoint(buffer,
            texWidth, texHeight,
            colorBits,
            scaleX, scaleY,
            leftSideBearing,
            codePoint,
            (int) Math.floor(x), (int) Math.floor(y));
    }

    public void drawRaw(Font font, String text, float x, float y, float pixelHeight) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pAdvance = stack.callocInt(1);
            IntBuffer pBearing = stack.callocInt(1);
            float scale = font.getScale(pixelHeight);
            for (int i = 0, len = text.codePointCount(0, text.length()); i < len; i++) {
                int codePoint = text.codePointAt(i);
                font.getGlyphHMetrics(codePoint, pAdvance, pBearing);
                drawCodePoint(font, codePoint, x, y, scale, scale, pBearing.get(0));
                x += scale * pAdvance.get(0);
                if (i < len - 1) {
                    x += scale * font.getKernAdvance(codePoint, text.codePointAt(i + 1));
                }
            }
        }
    }

    public void draw(Font font, String text, float x, float y, float pixelHeight) {
        float scale = font.getScale(pixelHeight);
        float yAdvance = scale * font.getAdvanceY();
        String[] lines = text.lines().toArray(String[]::new);
        for (int i = lines.length - 1; i >= 0; i--) {
            drawRaw(font, lines[i], x, y, pixelHeight);
            y += yAdvance;
        }
    }

    public void draw(Font font, String text, float x, float y) {
        draw(font, text, x, y, 20f);
    }

    public Matrix4f projectionMatrix() {
        return projectionMatrix;
    }

    public void setProjectionMatrix(Matrix4fc projectionMatrix) {
        if (drawing) flush();
        this.projectionMatrix.set(projectionMatrix);
        if (drawing) setupMatrices();
    }

    public void setBlendDisabled(boolean blendDisabled) {
        if (drawing) flush();
        this.blendDisabled = blendDisabled;
    }

    public void setBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        if (drawing && !blendDisabled) flush();
        blendSrcRGB = srcRGB;
        blendDstRGB = dstRGB;
        blendSrcAlpha = srcAlpha;
        blendDstAlpha = dstAlpha;
    }

    public void setBlendFunc(int srcFactor, int dstFactor) {
        setBlendFuncSeparate(srcFactor, dstFactor, srcFactor, dstFactor);
    }

    public void setTextColor(Color color) {
        setTextColor(color.packABGR());
    }

    public void setTextColor(float red, float green, float blue, float alpha) {
        setTextColor(Color.rgbaPackABGR(red, green, blue, alpha));
    }

    public void setTextColor(int textColor) {
        this.colorBits = textColor;
    }

    public int textColor() {
        return colorBits;
    }

    public boolean isBlendDisabled() {
        return blendDisabled;
    }

    public boolean isDrawing() {
        return drawing;
    }

    @Override
    public void dispose() {
        if (disposed) return;
        disposed = true;
        shader.dispose();
        mesh.dispose();
        glDeleteTextures(texture);
        memFree(buffer);
    }
}
