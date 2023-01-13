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
    /**
     * The unknown key.
     */
    public static final int KEY_UNKNOWN = GLFW_KEY_UNKNOWN;

    /**
     * Printable keys.
     */
    public static final int
        KEY_SPACE = GLFW_KEY_SPACE,
        KEY_APOSTROPHE = GLFW_KEY_APOSTROPHE,
        KEY_COMMA = GLFW_KEY_COMMA,
        KEY_MINUS = GLFW_KEY_MINUS,
        KEY_PERIOD = GLFW_KEY_PERIOD,
        KEY_SLASH = GLFW_KEY_SLASH,
        KEY_0 = GLFW_KEY_0,
        KEY_1 = GLFW_KEY_1,
        KEY_2 = GLFW_KEY_2,
        KEY_3 = GLFW_KEY_3,
        KEY_4 = GLFW_KEY_4,
        KEY_5 = GLFW_KEY_5,
        KEY_6 = GLFW_KEY_6,
        KEY_7 = GLFW_KEY_7,
        KEY_8 = GLFW_KEY_8,
        KEY_9 = GLFW_KEY_9,
        KEY_SEMICOLON = GLFW_KEY_SEMICOLON,
        KEY_EQUAL = GLFW_KEY_EQUAL,
        KEY_A = GLFW_KEY_A,
        KEY_B = GLFW_KEY_B,
        KEY_C = GLFW_KEY_C,
        KEY_D = GLFW_KEY_D,
        KEY_E = GLFW_KEY_E,
        KEY_F = GLFW_KEY_F,
        KEY_G = GLFW_KEY_G,
        KEY_H = GLFW_KEY_H,
        KEY_I = GLFW_KEY_I,
        KEY_J = GLFW_KEY_J,
        KEY_K = GLFW_KEY_K,
        KEY_L = GLFW_KEY_L,
        KEY_M = GLFW_KEY_M,
        KEY_N = GLFW_KEY_N,
        KEY_O = GLFW_KEY_O,
        KEY_P = GLFW_KEY_P,
        KEY_Q = GLFW_KEY_Q,
        KEY_R = GLFW_KEY_R,
        KEY_S = GLFW_KEY_S,
        KEY_T = GLFW_KEY_T,
        KEY_U = GLFW_KEY_U,
        KEY_V = GLFW_KEY_V,
        KEY_W = GLFW_KEY_W,
        KEY_X = GLFW_KEY_X,
        KEY_Y = GLFW_KEY_Y,
        KEY_Z = GLFW_KEY_Z,
        KEY_LEFT_BRACKET = GLFW_KEY_LEFT_BRACKET,
        KEY_BACKSLASH = GLFW_KEY_BACKSLASH,
        KEY_RIGHT_BRACKET = GLFW_KEY_RIGHT_BRACKET,
        KEY_GRAVE_ACCENT = GLFW_KEY_GRAVE_ACCENT,
        KEY_WORLD_1 = GLFW_KEY_WORLD_1,
        KEY_WORLD_2 = GLFW_KEY_WORLD_2;

    /**
     * Function keys.
     */
    public static final int
        KEY_ESCAPE = GLFW_KEY_ESCAPE,
        KEY_ENTER = GLFW_KEY_ENTER,
        KEY_TAB = GLFW_KEY_TAB,
        KEY_BACKSPACE = GLFW_KEY_BACKSPACE,
        KEY_INSERT = GLFW_KEY_INSERT,
        KEY_DELETE = GLFW_KEY_DELETE,
        KEY_RIGHT = GLFW_KEY_RIGHT,
        KEY_LEFT = GLFW_KEY_LEFT,
        KEY_DOWN = GLFW_KEY_DOWN,
        KEY_UP = GLFW_KEY_UP,
        KEY_PAGE_UP = GLFW_KEY_PAGE_UP,
        KEY_PAGE_DOWN = GLFW_KEY_PAGE_DOWN,
        KEY_HOME = GLFW_KEY_HOME,
        KEY_END = GLFW_KEY_END,
        KEY_CAPS_LOCK = GLFW_KEY_CAPS_LOCK,
        KEY_SCROLL_LOCK = GLFW_KEY_SCROLL_LOCK,
        KEY_NUM_LOCK = GLFW_KEY_NUM_LOCK,
        KEY_PRINT_SCREEN = GLFW_KEY_PRINT_SCREEN,
        KEY_PAUSE = GLFW_KEY_PAUSE,
        KEY_F1 = GLFW_KEY_F1,
        KEY_F2 = GLFW_KEY_F2,
        KEY_F3 = GLFW_KEY_F3,
        KEY_F4 = GLFW_KEY_F4,
        KEY_F5 = GLFW_KEY_F5,
        KEY_F6 = GLFW_KEY_F6,
        KEY_F7 = GLFW_KEY_F7,
        KEY_F8 = GLFW_KEY_F8,
        KEY_F9 = GLFW_KEY_F9,
        KEY_F10 = GLFW_KEY_F10,
        KEY_F11 = GLFW_KEY_F11,
        KEY_F12 = GLFW_KEY_F12,
        KEY_F13 = GLFW_KEY_F13,
        KEY_F14 = GLFW_KEY_F14,
        KEY_F15 = GLFW_KEY_F15,
        KEY_F16 = GLFW_KEY_F16,
        KEY_F17 = GLFW_KEY_F17,
        KEY_F18 = GLFW_KEY_F18,
        KEY_F19 = GLFW_KEY_F19,
        KEY_F20 = GLFW_KEY_F20,
        KEY_F21 = GLFW_KEY_F21,
        KEY_F22 = GLFW_KEY_F22,
        KEY_F23 = GLFW_KEY_F23,
        KEY_F24 = GLFW_KEY_F24,
        KEY_F25 = GLFW_KEY_F25,
        KEY_KP_0 = GLFW_KEY_KP_0,
        KEY_KP_1 = GLFW_KEY_KP_1,
        KEY_KP_2 = GLFW_KEY_KP_2,
        KEY_KP_3 = GLFW_KEY_KP_3,
        KEY_KP_4 = GLFW_KEY_KP_4,
        KEY_KP_5 = GLFW_KEY_KP_5,
        KEY_KP_6 = GLFW_KEY_KP_6,
        KEY_KP_7 = GLFW_KEY_KP_7,
        KEY_KP_8 = GLFW_KEY_KP_8,
        KEY_KP_9 = GLFW_KEY_KP_9,
        KEY_KP_DECIMAL = GLFW_KEY_KP_DECIMAL,
        KEY_KP_DIVIDE = GLFW_KEY_KP_DIVIDE,
        KEY_KP_MULTIPLY = GLFW_KEY_KP_MULTIPLY,
        KEY_KP_SUBTRACT = GLFW_KEY_KP_SUBTRACT,
        KEY_KP_ADD = GLFW_KEY_KP_ADD,
        KEY_KP_ENTER = GLFW_KEY_KP_ENTER,
        KEY_KP_EQUAL = GLFW_KEY_KP_EQUAL,
        KEY_LEFT_SHIFT = GLFW_KEY_LEFT_SHIFT,
        KEY_LEFT_CONTROL = GLFW_KEY_LEFT_CONTROL,
        KEY_LEFT_ALT = GLFW_KEY_LEFT_ALT,
        KEY_LEFT_SUPER = GLFW_KEY_LEFT_SUPER,
        KEY_RIGHT_SHIFT = GLFW_KEY_RIGHT_SHIFT,
        KEY_RIGHT_CONTROL = GLFW_KEY_RIGHT_CONTROL,
        KEY_RIGHT_ALT = GLFW_KEY_RIGHT_ALT,
        KEY_RIGHT_SUPER = GLFW_KEY_RIGHT_SUPER,
        KEY_MENU = GLFW_KEY_MENU,
        KEY_LAST = GLFW_KEY_LAST;

    /**
     * If this bit is set one or more Shift keys were held down.
     */
    public static final int MOD_SHIFT = GLFW_MOD_SHIFT;

    /**
     * If this bit is set one or more Control keys were held down.
     */
    public static final int MOD_CONTROL = GLFW_MOD_CONTROL;

    /**
     * If this bit is set one or more Alt keys were held down.
     */
    public static final int MOD_ALT = GLFW_MOD_ALT;

    /**
     * If this bit is set one or more Super keys were held down.
     */
    public static final int MOD_SUPER = GLFW_MOD_SUPER;

    /**
     * If this bit is set the Caps Lock key is enabled and the {@link #LOCK_KEY_MODS LOCK_KEY_MODS} input mode is set.
     */
    public static final int MOD_CAPS_LOCK = GLFW_MOD_CAPS_LOCK;

    /**
     * If this bit is set the Num Lock key is enabled and the {@link #LOCK_KEY_MODS LOCK_KEY_MODS} input mode is set.
     */
    public static final int MOD_NUM_LOCK = GLFW_MOD_NUM_LOCK;

    /**
     * Mouse buttons. See <a target="_blank" href="http://www.glfw.org/docs/latest/input.html#input_mouse_button">mouse button input</a> for how these are used.
     */
    public static final int
        MOUSE_BUTTON_1 = GLFW_MOUSE_BUTTON_1,
        MOUSE_BUTTON_2 = GLFW_MOUSE_BUTTON_2,
        MOUSE_BUTTON_3 = GLFW_MOUSE_BUTTON_3,
        MOUSE_BUTTON_4 = GLFW_MOUSE_BUTTON_4,
        MOUSE_BUTTON_5 = GLFW_MOUSE_BUTTON_5,
        MOUSE_BUTTON_6 = GLFW_MOUSE_BUTTON_6,
        MOUSE_BUTTON_7 = GLFW_MOUSE_BUTTON_7,
        MOUSE_BUTTON_8 = GLFW_MOUSE_BUTTON_8,
        MOUSE_BUTTON_LAST = GLFW_MOUSE_BUTTON_LAST,
        MOUSE_BUTTON_LEFT = GLFW_MOUSE_BUTTON_LEFT,
        MOUSE_BUTTON_RIGHT = GLFW_MOUSE_BUTTON_RIGHT,
        MOUSE_BUTTON_MIDDLE = GLFW_MOUSE_BUTTON_MIDDLE;

    /**
     * Input options.
     */
    public static final int
        CURSOR = GLFW_CURSOR,
        STICKY_KEYS = GLFW_STICKY_KEYS,
        STICKY_MOUSE_BUTTONS = GLFW_STICKY_MOUSE_BUTTONS,
        LOCK_KEY_MODS = GLFW_LOCK_KEY_MODS,
        RAW_MOUSE_MOTION = GLFW_RAW_MOUSE_MOTION;

    /**
     * Cursor state.
     */
    public static final int
        CURSOR_NORMAL = GLFW_CURSOR_NORMAL,
        CURSOR_HIDDEN = GLFW_CURSOR_HIDDEN,
        CURSOR_DISABLED = GLFW_CURSOR_DISABLED;

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
