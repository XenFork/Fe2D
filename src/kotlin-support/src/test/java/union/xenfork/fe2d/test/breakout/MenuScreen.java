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

package union.xenfork.fe2d.test.breakout;

import union.xenfork.fe2d.Fe2D;
import union.xenfork.fe2d.gui.screen.Screen;
import union.xenfork.fe2d.gui.widget.GUILabel;
import union.xenfork.fe2d.gui.widget.button.GUIButton;
import union.xenfork.fe2d.gui.widget.button.RectButton;

/**
 * the menu screen.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class MenuScreen extends Screen {
    private GUILabel label;
    private GUIButton button;

    /**
     * Creates the screen.
     */
    public MenuScreen() {
        super(null);
    }

    @Override
    protected void init() {
        super.init();
        label = addWidget(new GUILabel("Press ENTER to start"));
        button = addWidget(new RectButton("Pressing\nHovering", 0, 0, 100, 50, System.out::println));
        button.setTextFont(Fe2D.assets.getAsset(Breakout.TRUE_TYPE_FONT));
        button.setTextPixelsHeight(24f);
    }

    @Override
    public void onResize(int width, int height) {
        super.onResize(width, height);
        label.setPosition((width - label.width()) * .5f,
            (height - label.height()) * .5f);
        button.setX(width - button.width());
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
