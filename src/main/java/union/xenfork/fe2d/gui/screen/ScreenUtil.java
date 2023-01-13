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

package union.xenfork.fe2d.gui.screen;

import static org.lwjgl.opengl.GL11C.*;

/**
 * The utilities of screen.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class ScreenUtil {
    /**
     * AttribMask
     */
    public static final int
        DEPTH_BUFFER_BIT = GL_DEPTH_BUFFER_BIT,
        STENCIL_BUFFER_BIT = GL_STENCIL_BUFFER_BIT,
        COLOR_BUFFER_BIT = GL_COLOR_BUFFER_BIT;

    /**
     * Sets portions of every pixel in a particular buffer to the same value. The value to which each buffer is cleared depends on the setting of the clear
     * value for that buffer.
     *
     * @param mask Zero or the bitwise OR of one or more values indicating which buffers are to be cleared.
     *             One or more of:<br><table><tr>
     *             <td style="padding:3px;">{@link #COLOR_BUFFER_BIT COLOR_BUFFER_BIT}</td>
     *             <td style="padding:3px;">{@link #DEPTH_BUFFER_BIT DEPTH_BUFFER_BIT}</td>
     *             <td style="padding:3px;">{@link #STENCIL_BUFFER_BIT STENCIL_BUFFER_BIT}</td>
     *             </tr></table>
     */
    public static void clear(int mask) {
        glClear(mask);
    }
}
