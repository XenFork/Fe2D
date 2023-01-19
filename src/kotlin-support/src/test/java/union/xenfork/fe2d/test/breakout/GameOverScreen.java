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

import org.jetbrains.annotations.Nullable;
import union.xenfork.fe2d.Fe2D;
import union.xenfork.fe2d.gui.layout.Alignment;
import union.xenfork.fe2d.gui.screen.Screen;
import union.xenfork.fe2d.gui.widget.GUILabel;

/**
 * the game over/win screen.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class GameOverScreen extends Screen {
    private final boolean win;
    private GUILabel label;

    public GameOverScreen(@Nullable Screen parent, boolean win) {
        super(parent);
        this.win = win;
    }

    @Override
    protected void init() {
        super.init();
        label = addWidget(new GUILabel((win ? "You win!" : "Game Over") + "\nPress ENTER or ESCAPE to back to menu"));
        label.setVerticalAlign(Alignment.V.CENTER);
    }

    @Override
    public void onResize(int width, int height) {
        super.onResize(width, height);
        label.setPosition((width - label.width()) * .5f,
            (height - label.height()) * .5f);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (Fe2D.game instanceof Breakout breakout) {
            breakout.getCurrentLevel().reset();
            breakout.resetPlayer();
            breakout.state = Breakout.GAME_MENU;
        }
    }
}
