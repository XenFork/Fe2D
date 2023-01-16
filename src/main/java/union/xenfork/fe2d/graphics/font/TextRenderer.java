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

import union.xenfork.fe2d.Disposable;
import union.xenfork.fe2d.Fe2D;
import union.xenfork.fe2d.graphics.Color;
import union.xenfork.fe2d.graphics.GLStateManager;
import union.xenfork.fe2d.graphics.ShaderProgram;

import java.nio.ByteBuffer;

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
    private static TextRenderer instance;
    private final int texture;
    private final ShaderProgram shader;
    private ByteBuffer buffer;
    private int width, height;
    private int texWidth, texHeight;
    private int colorBits = Color.WHITE_BITS;
    private boolean drawing = false;

    private TextRenderer() {
        shader = new ShaderProgram();
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
            buffer = memRealloc(buffer, width * height);
            int currTex = textureBinding2D();
            bindTexture2D(texture);
            int unpackAlign = unpackAlignment();
            setUnpackAlignment(1);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
            setUnpackAlignment(unpackAlign);
            bindTexture2D(currTex);
        }
    }

    public void begin() {
        if (drawing) throw new IllegalStateException("Cannot call TextRenderer.begin while drawing");
        drawing = true;
        memSet(buffer, 0);
    }

    public void end() {
        if (!drawing) throw new IllegalStateException("Can only call TextRenderer.end while drawing");
        drawing = false;
        int currTex = textureBinding2D();
        bindTexture2D(texture);
        int unpackAlign = unpackAlignment();
        setUnpackAlignment(1);
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        setUnpackAlignment(unpackAlign);
        bindTexture2D(currTex);
    }

    public void drawCodePoint(Font font, int codePoint, float x, float y) {
        font.drawCodePoint(buffer, texWidth, texHeight, colorBits, codePoint, x, y);
    }

    public void drawRaw(Font font, String text, float x, float y) {
        int advance = font.getGlyphHMetrics();
        for (int i = 0, len = text.codePointCount(0, text.length()); i < len; i++) {
            int codePoint = text.codePointAt(i);
            drawCodePoint(font, codePoint, x, y);
            x +=;
        }
    }

    public void draw(Font font, String text, float x, float y) {
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

    public boolean isDrawing() {
        return drawing;
    }

    @Override
    public void dispose() {
        shader.dispose();
        glDeleteTextures(texture);
        memFree(buffer);
    }
}
