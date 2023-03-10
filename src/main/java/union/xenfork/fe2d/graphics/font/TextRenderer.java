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

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.system.MemoryStack;
import union.xenfork.fe2d.Disposable;
import union.xenfork.fe2d.Fe2D;
import union.xenfork.fe2d.graphics.Color;
import union.xenfork.fe2d.graphics.GLStateManager;
import union.xenfork.fe2d.graphics.ShaderProgram;
import union.xenfork.fe2d.graphics.mesh.Mesh;
import union.xenfork.fe2d.graphics.vertex.VertexAttribute;
import union.xenfork.fe2d.graphics.vertex.VertexLayout;
import union.xenfork.fe2d.gui.layout.Alignment;

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
    /**
     * The default pixels height.
     */
    public static final float DEFAULT_PIXELS_HEIGHT = 20f;
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
            void main() {
                gl_Position = %3$s * vec4(%1$s, 0.0, 1.0);
                UV0 = %2$s;
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
            new int[]{0, 1, 2, 3},
            LAYOUT);
        mesh.setDefaultDrawMode(GL_TRIANGLE_FAN);
        texture = glGenTextures();
        int currTex = textureBinding2D();
        bindTexture2D(texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_LOD, 0f);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_LOD, 0f);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
        resize(Fe2D.graphics.width(), Fe2D.graphics.height());
        bindTexture2D(currTex);
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
            texWidth = Math.max(width, texWidth);
            texHeight = Math.max(height, texHeight);
            buffer = memRealloc(buffer, texWidth * texHeight * 4);
            int currTex = textureBinding2D();
            bindTexture2D(texture);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, texWidth, texHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
            bindTexture2D(currTex);
        }
        projectionMatrix.setOrtho2D(0, width, 0, height);
        modelMatrix.scaling(texWidth, texHeight, 1f);
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
        checkDrawing();
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
        shader.use();
        setupMatrices();
        bindTexture2D(texture);
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, texWidth, texHeight, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        mesh.render();
        bindTexture2D(currTex);
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
            throw new IllegalStateException("Can only call TextRenderer.draw or flush between begin and end (while drawing)");
    }

    private void setupMatrices() {
        projectionMatrix.mul(modelMatrix, combinedMatrix);
        shader.setProjectionViewModelMatrix(combinedMatrix);
        shader.uploadUniforms();
    }

    public void drawCodePoint(Font font, int codePoint, float x, float y, float scaleX, float scaleY, int leftSideBearing) {
        checkDrawing();
        font.drawCodePoint(buffer,
            texWidth, texHeight,
            colorBits,
            scaleX, scaleY,
            leftSideBearing,
            codePoint,
            (int) Math.floor(x), (int) Math.floor(y));
    }

    public void drawRaw(Font font, String text, float x, float y, float pixelHeight) {
        checkDrawing();
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

    public void draw(Font font, String text, float x, float y, Alignment.V verticalAlign, float pixelHeight) {
        checkDrawing();
        float scale = font.getScale(pixelHeight);
        float yAdvance = scale * font.getAdvanceY();
        float boxWidth = scale * font.getTextWidth(text);
        String[] lines = text.lines().toArray(String[]::new);
        for (int i = lines.length - 1; i >= 0; i--) {
            String line = lines[i];
            drawRaw(font,
                line,
                verticalAlign.getTextPositionX(x, scale * font.getTextWidth(line), boxWidth),
                y,
                pixelHeight);
            y += yAdvance;
        }
    }

    public void draw(Font font, String text, float x, float y, Alignment.V verticalAlign) {
        checkDrawing();
        draw(font, text, x, y, verticalAlign, DEFAULT_PIXELS_HEIGHT);
    }

    public void draw(Font font, String text, float x, float y) {
        checkDrawing();
        draw(font, text, x, y, Alignment.V.LEFT);
    }

    public Matrix4f projectionMatrix() {
        return projectionMatrix;
    }

    public void setProjectionMatrix(Matrix4fc projectionMatrix) {
        if (drawing) flush();
        this.projectionMatrix.set(projectionMatrix);
        if (drawing) {
            int currProgram = currentProgram();
            shader.use();
            setupMatrices();
            useProgram(currProgram);
        }
    }

    public void enableBlend() {
        if (!blendDisabled) return;
        flush();
        blendDisabled = false;
    }

    public void disableBlend() {
        if (blendDisabled) return;
        flush();
        blendDisabled = true;
    }

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
