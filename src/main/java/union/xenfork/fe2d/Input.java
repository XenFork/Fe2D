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

package union.xenfork.fe2d;

import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The keyboard and mouse input.
 * <h2>Keyboard</h2>
 * There are 2 states for keys: {@link #isKeyDown(int) down} and {@link #isKeyUp(int) up}.
 * <h2>Mouse</h2>
 * For mouse button, use {@link #isMouseButtonDown(int)} or {@link #isTouched()} to check the state.
 * <p>
 * For cursor position, use {@link #cursorX()} or {@link #cursorY()} to get the current position of the cursor,
 * or use {@link #cursorDeltaX()} or {@link #cursorDeltaY()} to get the distance between previous and current position.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class Input {
    private final long window;
    private double cursorX, cursorY, cursorDeltaX, cursorDeltaY;

    /**
     * Creates the input.
     *
     * @param window the window handle.
     */
    public Input(long window) {
        this.window = window;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer px = stack.callocDouble(1);
            DoubleBuffer py = stack.callocDouble(1);
            glfwGetCursorPos(window, px, py);
            cursorX = px.get(0);
            cursorY = py.get(0);
            cursorDeltaX = 0;
            cursorDeltaY = 0;
        }
    }

    /**
     * The key or mouse callback actions.
     *
     * @author squid233
     * @since 0.1.0
     */
    public enum Action {
        /**
         * The key or button was released.
         */
        RELEASE,
        /**
         * The key or button was pressed.
         */
        PRESS,
        /**
         * The key was held down until it repeated.
         */
        REPEAT
    }

    ///////////////////////////////////////////////////////////////////////////
    // Mouse
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns {@code true} if the given mouse button is pressing.
     *
     * @param button the mouse button.
     * @return {@code true} if the given mouse button is pressing.
     */
    public boolean isMouseButtonDown(int button) {
        return glfwGetMouseButton(window, button) == GLFW_PRESS;
    }

    /**
     * Returns {@code true} if left mouse button is pressing.
     *
     * @return {@code true} if left mouse button is pressing.
     */
    public boolean isTouched() {
        return isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT);
    }

    /**
     * Updates the cursor position.
     *
     * @param cursorX the new cursor position x.
     * @param cursorY the new cursor position y.
     */
    public void updateCursorPos(double cursorX, double cursorY) {
        cursorDeltaX = cursorX - this.cursorX;
        cursorDeltaY = cursorY - this.cursorY;
        this.cursorX = cursorX;
        this.cursorY = cursorY;
    }

    /**
     * Gets the cursor position x.
     *
     * @return the cursor position x.
     */
    public double cursorX() {
        return cursorX;
    }

    /**
     * Gets the cursor position y.
     *
     * @return the cursor position y.
     */
    public double cursorY() {
        return cursorY;
    }

    /**
     * Gets the cursor delta position x.
     *
     * @return the cursor delta position x.
     */
    public double cursorDeltaX() {
        return cursorDeltaX;
    }

    /**
     * Gets the cursor delta position y.
     *
     * @return the cursor delta position y.
     */
    public double cursorDeltaY() {
        return cursorDeltaY;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Keyboard
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns {@code true} if the given key is pressing.
     *
     * @param key the key.
     * @return {@code true} if the given key is pressing.
     */
    public boolean isKeyDown(int key) {
        return glfwGetKey(window, key) == GLFW_PRESS;
    }

    /**
     * Returns {@code true} if the given key is releasing.
     *
     * @param key the key.
     * @return {@code true} if the given key is releasing.
     */
    public boolean isKeyUp(int key) {
        return glfwGetKey(window, key) == GLFW_RELEASE;
    }
}
