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

import union.xenfork.fe2d.graphics.Color;
import union.xenfork.fe2d.graphics.sprite.Sprite;
import union.xenfork.fe2d.graphics.texture.Texture;
import union.xenfork.fe2d.graphics.texture.TextureRegion;

/**
 * the brick
 *
 * @author squid233
 * @since 0.1.0
 */
public final class Brick extends Sprite {
    public static final int BLANK = 0;
    public static final int SOLID = 1;
    public static final int GREY = 2;
    public static final int RED = 3;
    public static final int GREEN = 4;
    public static final int BLUE = 5;
    public static final int CYAN = 6;
    public static final int PURPLE = 7;
    public static final int YELLOW = 8;
    public static final int WHITE = 9;
    public static final Color SOLID_COLOR = new Color(0.8f, 0.8f, 0.7f, 1.0f);
    public static final Color GREY_COLOR = new Color(0xff808080);
    public static final Color RED_COLOR = new Color(0xffff0000);
    public static final Color GREEN_COLOR = new Color(0xff00b200);
    public static final Color BLUE_COLOR = new Color(0xff3399ff);
    public static final Color CYAN_COLOR = new Color(0xff00ffff);
    public static final Color PURPLE_COLOR = new Color(0xffff00ff);
    public static final Color YELLOW_COLOR = new Color(0xffcccc66);
    public boolean solid = false;

    public Brick(Texture texture, TextureRegion textureRegion) {
        super(texture, textureRegion);
    }
}
