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

package union.xenfork.fe2d.graphics;

import org.lwjgl.opengl.GL11C;

import static org.lwjgl.opengl.GL30C.*;

/**
 * The GL state manager.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class GLStateManager {
    ///////////////////////////////////////////////////////////////////////////
    // Pixel store
    ///////////////////////////////////////////////////////////////////////////

    private static int unpackAlignment = 4;

    /**
     * Returns one value, the byte alignment used for reading pixel data from memory. The initial value is 4.
     *
     * @return one value, the byte alignment used for reading pixel data from memory. The initial value is 4.
     * @see #setUnpackAlignment(int)
     */
    public static int unpackAlignment() {
        return unpackAlignment;
    }

    /**
     * Specifies the alignment requirements for the start of each pixel row in memory.
     * The allowable values are 1 (byte-alignment), 2 (rows aligned to even-numbered bytes), 4 (word-alignment),
     * and 8 (rows start on double-word boundaries).
     *
     * @param param Specifies the value that {@link #unpackAlignment() unpackAlignment} is set to.
     */
    public static void setUnpackAlignment(int param) {
        if (unpackAlignment != param) {
            unpackAlignment = param;
            glPixelStorei(GL_UNPACK_ALIGNMENT, param);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Blend
    ///////////////////////////////////////////////////////////////////////////

    private static boolean blend = false;
    private static int blendSrcRGB = GL_ONE;
    private static int blendDstRGB = GL_ZERO;
    private static int blendSrcAlpha = GL_ONE;
    private static int blendDstAlpha = GL_ZERO;

    /**
     * Determines if {@link GL11C#GL_BLEND GL_BLEND} is currently enabled (as with {@link #enableBlend() Enable}) or disabled.
     *
     * @return If enabled, blend the computed fragment color values with the values in the color buffers.
     */
    public static boolean isBlendEnabled() {
        return blend;
    }

    /**
     * Returns one value, the symbolic constant identifying the RGB source blend function. The initial value is {@code GL_ONE}.
     *
     * @return one value, the symbolic constant identifying the RGB source blend function. The initial value is {@code GL_ONE}.
     */
    public static int blendSrcRGB() {
        return blendSrcRGB;
    }

    /**
     * Returns one value, the symbolic constant identifying the RGB destination blend function. The initial value is {@code GL_ZERO}.
     *
     * @return one value, the symbolic constant identifying the RGB destination blend function. The initial value is {@code GL_ZERO}.
     */
    public static int blendDstRGB() {
        return blendDstRGB;
    }

    /**
     * Returns one value, the symbolic constant identifying the alpha source blend function. The initial value is {@code GL_ONE}.
     *
     * @return one value, the symbolic constant identifying the alpha source blend function. The initial value is {@code GL_ONE}.
     */
    public static int blendSrcAlpha() {
        return blendSrcAlpha;
    }

    /**
     * Returns one value, the symbolic constant identifying the alpha destination blend function. The initial value is {@code GL_ZERO}.
     *
     * @return one value, the symbolic constant identifying the alpha destination blend function. The initial value is {@code GL_ZERO}.
     */
    public static int blendDstAlpha() {
        return blendDstAlpha;
    }

    /**
     * Enables blend.
     */
    public static void enableBlend() {
        if (!blend) {
            blend = true;
            glEnable(GL_BLEND);
        }
    }

    /**
     * Disables blend.
     */
    public static void disableBlend() {
        if (blend) {
            blend = false;
            glDisable(GL_BLEND);
        }
    }

    /**
     * Specifies pixel arithmetic for RGB and alpha components separately.
     *
     * @param sfactorRGB   how the red, green, and blue blending factors are computed. The initial value is GL_ONE.
     * @param dfactorRGB   how the red, green, and blue destination blending factors are computed. The initial value is GL_ZERO.
     * @param sfactorAlpha how the alpha source blending factor is computed. The initial value is GL_ONE.
     * @param dfactorAlpha how the alpha destination blending factor is computed. The initial value is GL_ZERO.
     */
    public static void blendFuncSeparate(int sfactorRGB, int dfactorRGB, int sfactorAlpha, int dfactorAlpha) {
        if (blendSrcRGB != sfactorRGB ||
            blendDstRGB != dfactorRGB ||
            blendSrcAlpha != sfactorAlpha ||
            blendDstAlpha != dfactorAlpha) {
            blendSrcRGB = sfactorRGB;
            blendDstRGB = dfactorRGB;
            blendSrcAlpha = sfactorAlpha;
            blendDstAlpha = dfactorAlpha;
            glBlendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
        }
    }

    /**
     * Specifies the weighting factors used by the blend equation, for both RGB and alpha functions and for all draw buffers.
     *
     * @param sfactor the source weighting factor.
     * @param dfactor the destination weighting factor.
     */
    public static void blendFunc(int sfactor, int dfactor) {
        blendFuncSeparate(sfactor, dfactor, sfactor, dfactor);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Texture
    ///////////////////////////////////////////////////////////////////////////

    private static final int[] textureBinding2D = new int[glGetInteger(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS)];
    private static int activeTexture = 0;

    /**
     * Returns a single value, the name of the texture currently bound to the target {@link GL11C#GL_TEXTURE_2D GL_TEXTURE_2D}.
     *
     * @return a single value, the name of the texture currently bound to the target {@link GL11C#GL_TEXTURE_2D GL_TEXTURE_2D}.
     */
    public static int textureBinding2D() {
        return textureBinding2D[activeTexture];
    }

    /**
     * Returns a single value indicating the active multitexture unit.
     *
     * @return a single value indicating the active multitexture unit.
     */
    public static int activeTexture() {
        return activeTexture;
    }

    /**
     * Selects which texture unit subsequent texture state calls will affect.
     * <p>
     * The number of texture units an implementation supports is implementation dependent,
     * but must be at least 48 in GL 3 and 80 in GL 4.
     *
     * @param texture which texture unit to make active.
     */
    public static void activeTexture(int texture) {
        if (activeTexture != texture) {
            activeTexture = texture;
            glActiveTexture(GL_TEXTURE0 + texture);
        }
    }

    /**
     * Binds a texture to a texture target.
     * <p>
     * While a texture object is bound, GL operations on the target to which it is bound affect the bound object,
     * and queries of the target to which it is bound return state from the bound object.
     * If texture mapping of the dimensionality of the target to which a texture object is bound is enabled,
     * the state of the bound texture object directs the texturing operation.
     *
     * @param texture the texture object to bind.
     */
    public static void bindTexture2D(int texture) {
        if (textureBinding2D[activeTexture] != texture) {
            textureBinding2D[activeTexture] = texture;
            glBindTexture(GL_TEXTURE_2D, texture);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Vertex array
    ///////////////////////////////////////////////////////////////////////////

    private static int vertexArrayBinding = 0;

    /**
     * Returns a single value, the name of the vertex array object currently bound to the context. If no vertex array object is bound to the context, 0 is returned. The initial value is 0.
     *
     * @return a single value, the name of the vertex array object currently bound to the context. If no vertex array object is bound to the context, 0 is returned. The initial value is 0.
     */
    public static int vertexArrayBinding() {
        return vertexArrayBinding;
    }

    /**
     * Bind a vertex array object.
     *
     * @param array Specifies the name of the vertex array to bind.
     */
    public static void bindVertexArray(int array) {
        if (vertexArrayBinding != array) {
            vertexArrayBinding = array;
            glBindVertexArray(array);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Shader program
    ///////////////////////////////////////////////////////////////////////////

    private static int currentProgram = 0;

    /**
     * Returns one value, the name of the program object that is currently active, or 0 if no program object is active.
     *
     * @return one value, the name of the program object that is currently active, or 0 if no program object is active.
     */
    public static int currentProgram() {
        return currentProgram;
    }

    /**
     * Installs a program object as part of current rendering state.
     *
     * @param program Specifies the handle of the program object whose executables are to be used as part of current rendering state.
     */
    public static void useProgram(int program) {
        if (currentProgram != program) {
            currentProgram = program;
            glUseProgram(program);
        }
    }
}
