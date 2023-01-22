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
import union.xenfork.fe2d.graphics.Color;
import union.xenfork.fe2d.graphics.font.Font;
import union.xenfork.fe2d.graphics.texture.TextureAtlas;
import union.xenfork.fe2d.graphics.texture.TextureRegion;
import union.xenfork.fe2d.gui.screen.Screen;
import union.xenfork.fe2d.gui.widget.GUILabel;
import union.xenfork.fe2d.gui.widget.button.RectButton;
import union.xenfork.fe2d.gui.widget.button.TexturedButton;

/**
 * the menu screen.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class MenuScreen extends Screen {
    private GUILabel label;
    private RectButton button;
    private TexturedButton texturedButton;

    /**
     * Creates the screen.
     */
    public MenuScreen() {
        super(null);
    }

    @Override
    protected void init() {
        super.init();

        Font font = Fe2D.assets.getAsset(Breakout.TRUE_TYPE_FONT);
        TextureAtlas atlas = Fe2D.assets.getAsset(Breakout.TEXTURE_ATLAS);
        TextureRegion region = atlas.get(Breakout.PADDLE);

        label = addWidget(new GUILabel("Press ENTER to start"));

        button = addWidget(new RectButton("Pressing\nHovering", 0, 0, 100, 50, button1 -> {
            int level = Breakout.getInstance().level;
            level++;
            if (level > Breakout.MAX_LEVEL) {
                level = 1;
            }
            Breakout.getInstance().level = level;
            Breakout.getInstance().jsonConfig.put("level", level);
        }));
        button.setTextFont(font, 24f);

        texturedButton = addWidget(new TexturedButton("Textured Button\nPressing\nHovering", 0, 100, 100, 100, System.out::println));
        texturedButton.setTextFont(font, 24f);
        texturedButton.setTexture(atlas, region);
        texturedButton.setHoverTexture(atlas, region);
        texturedButton.setHoverColor(Color.GRAY);
    }

    @Override
    public void onResize(int width, int height) {
        super.onResize(width, height);
        label.setPosition((width - label.width()) * .5f,
            (height - label.height()) * .5f);
        button.setX(width - button.width());
        texturedButton.setX(width - texturedButton.width());
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
