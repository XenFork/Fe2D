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

package union.xenfork.fe2d.test.breakout

import org.joml.Matrix4f
import union.xenfork.fe2d.game

/**
 * breakout game
 *
 * @author squid233
 * @since 0.1.0
 */
fun main() = game(
    useStderr = true,
    applicationName = "Breakout",
    windowWidth = 1280,
    windowHeight = 720
) {
    val guiProjMatrix = Matrix4f()

    onResize { width, height, base ->
        base()
        guiProjMatrix.setOrtho2D(0f, width.toFloat(), 0f, height.toFloat())
    }
}
