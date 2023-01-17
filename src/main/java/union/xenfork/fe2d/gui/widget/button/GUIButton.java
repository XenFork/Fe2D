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

package union.xenfork.fe2d.gui.widget.button;

import union.xenfork.fe2d.gui.widget.GUIWidget;

/**
 * The GUI button.
 * <p>
 * The default button is not drawable, and you need to use the subclasses, such as {@link RectButton} or {@link TexturedButton}.
 *
 * @author squid233
 * @since 0.1.0
 */
public abstract class GUIButton extends GUIWidget {
    private static final PressAction DEFAULT_ACTION = button -> {
    };
    protected int width;
    protected int height;
    /**
     * The action to be performed.
     */
    protected PressAction pressAction;

    public GUIButton(float x, float y, int width, int height, PressAction pressAction) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pressAction = pressAction;
    }

    public GUIButton(float x, float y, int width, int height) {
        this(x, y, width, height, DEFAULT_ACTION);
    }

    /**
     * The press action to be performed.
     *
     * @author squid233
     * @since 0.1.0
     */
    @FunctionalInterface
    public interface PressAction {
        /**
         * Performs the action.
         *
         * @param button the button that was pressed.
         */
        void onPress(GUIButton button);
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }
}
