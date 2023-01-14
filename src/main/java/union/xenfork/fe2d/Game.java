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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import union.xenfork.fe2d.gui.screen.Screen;
import union.xenfork.fe2d.gui.screen.ScreenUtil;

/**
 * The advanced {@link Application} with a screen.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Game extends Application {
    /**
     * The current screen.
     */
    protected @Nullable Screen screen;

    @Override
    public void onResize(int width, int height) {
        super.onResize(width, height);
        if (screen != null) {
            screen.onResize(width, height);
        }
    }

    @Override
    public void onKey(int key, int scancode, Input.@NotNull Action action, int mods) {
        super.onKey(key, scancode, action, mods);
        if (action == Input.Action.PRESS) {
            if (screen == null || !screen.onKeyPress(key, scancode, mods)) {
                onKeyPress(key, scancode, mods);
            }
        }
    }

    @Override
    public void onMouseButton(int button, Input.@NotNull Action action, int mods) {
        super.onMouseButton(button, action, mods);
        if (action == Input.Action.PRESS) {
            if (screen == null || !screen.onMousePress(button, mods)) {
                onMousePress(button, mods);
            }
        }
    }

    /**
     * This method will be called on key pressed if the current screen does not terminate the operation.
     *
     * @param key      the keyboard key that was pressed.
     * @param scancode the platform-specific scancode of the key.
     * @param mods     bitfield describing which modifiers keys were held down.
     */
    public void onKeyPress(int key, int scancode, int mods) {
    }

    /**
     * This method will be called on mouse pressed if the current screen does not terminate the operation.
     *
     * @param button the mouse button that was pressed.
     * @param mods   bitfield describing which modifiers keys were held down.
     */
    public void onMousePress(int button, int mods) {
    }

    /**
     * Opens a screen and {@link Screen#init(int, int) initialize} with the current framebuffer size.
     * <p>
     * The previous screen will be {@link Screen#onRemove() removed}.
     *
     * @param screen the new screen; or {@code null} to close the screen.
     */
    public void openScreen(@Nullable Screen screen) {
        if (this.screen != null) {
            this.screen.onRemove();
        }
        this.screen = screen;
        if (screen != null) {
            screen.init(Fe2D.graphics.width(), Fe2D.graphics.height());
        }
    }

    @Override
    public void fixedUpdate() {
        super.fixedUpdate();
        if (screen != null) {
            screen.fixedUpdate();
        }
    }

    @Override
    public void update() {
        super.update();
        if (screen != null) {
            screen.update();
        }
    }

    @Override
    public void lateUpdate() {
        super.lateUpdate();
        if (screen != null) {
            screen.lateUpdate();
        }
    }

    @Override
    public void render(double delta) {
        super.render(delta);
        if (screen != null) {
            ScreenUtil.clear(ScreenUtil.DEPTH_BUFFER_BIT);
            screen.render(delta, Fe2D.input.cursorX(), Fe2D.input.cursorY());
        }
    }
}
