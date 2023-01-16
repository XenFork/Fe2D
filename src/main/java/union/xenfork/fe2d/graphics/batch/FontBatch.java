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
import union.xenfork.fe2d.graphics.ShaderProgram;
import union.xenfork.fe2d.graphics.font.Font;
import union.xenfork.fe2d.graphics.sprite.Sprite;
import union.xenfork.fe2d.graphics.vertex.VertexAttribute;

/**
 * The font batch.
 *
 * @deprecated The font batch is deprecated for removal because it will be replaced with TextRenderer.
 * @author squid233
 * @since 0.1.0
 */
@Deprecated(since = "0.1.0", forRemoval = true)
public final class FontBatch extends SpriteBatch {
    /**
     * The default value of max characters.
     */
    public static final int DEFAULT_MAX_CHARACTERS = 65535;
    private final boolean ownsShader;

    /**
     * Creates the font batch with the given shader and size.
     *
     * @param defaultShader the custom shader to be used. if no custom shader provided, {@link #createDefaultShader()} is used.
     * @param maxCharacters the max character count. defaults to {@value #DEFAULT_MAX_CHARACTERS}.
     */
    public FontBatch(@Nullable ShaderProgram defaultShader, int maxCharacters) {
        super(defaultShader != null ? defaultShader : createDefaultShader(), maxCharacters);
        this.ownsShader = defaultShader == null;
    }

    /**
     * Creates the font batch with the given size.
     *
     * @param maxCharacters the max character count.
     */
    public FontBatch(int maxCharacters) {
        this(null, maxCharacters);
    }

    /**
     * Creates the font batch with the given shader.
     *
     * @param defaultShader the custom shader to be used. if no custom shader provided, {@link #createDefaultShader()} is used.
     */
    public FontBatch(@Nullable ShaderProgram defaultShader) {
        this(defaultShader, DEFAULT_MAX_CHARACTERS);
    }

    /**
     * Creates the font batch with the default size.
     */
    public FontBatch() {
        this(null, DEFAULT_MAX_CHARACTERS);
    }

    /**
     * Creates the default shader program.
     * <p>
     * Builtin vertex attributes and <a href="../ShaderProgram.html#Builtin_Uniforms">uniforms</a> are used.
     * <p>
     * The red channel of texture is only used.
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
            void main() {
                FragColor = vec4(vertexColor.rgb, vertexColor.a * texture(%1$s, UV0).r);
            }
            """, ShaderProgram.U_SAMPLER + '0'),
            Sprite.LAYOUT);
    }

    /**
     * Draws the given text with the given font and position.
     *
     * @param font the font.
     * @param text the text.
     * @param x    the position x.
     * @param y    the position y.
     */
    public void draw(Font font, String text, float x, float y) {
        font.draw(this, text, x, y);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (ownsShader && shader != null) {
            shader.dispose();
        }
    }
}
