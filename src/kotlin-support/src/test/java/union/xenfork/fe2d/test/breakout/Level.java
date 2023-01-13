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
import union.xenfork.fe2d.graphics.batch.SpriteBatch;
import union.xenfork.fe2d.graphics.texture.TextureAtlas;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * the game level
 *
 * @author squid233
 * @since 0.1.0
 */
public final class Level {
    public final List<Brick> bricks = new ArrayList<>();

    public void load(String fileContent, int screenWidth, int screenHeight) {
        bricks.clear();
        List<List<Integer>> tileData = new ArrayList<>();
        fileContent.lines().forEachOrdered(s -> {
            s = s.trim();
            int c0 = s.codePointAt(0);
            if (c0 < '0' || c0 > '9') return;
            tileData.add(s.codePoints().mapToObj(codePoint -> codePoint - '0').collect(Collectors.toList()));
        });
        if (tileData.size() > 0) {
            init(tileData, screenWidth, screenHeight);
        }
    }

    public void render(SpriteBatch batch) {
        for (Brick brick : bricks) {
            if (!brick.destroyed) {
                batch.draw(brick);
            }
        }
    }

    public boolean isCompleted() {
        for (Brick brick : bricks) {
            if (!brick.solid && !brick.destroyed) {
                return false;
            }
        }
        return true;
    }

    private void init(List<List<Integer>> tileData, int screenWidth, int screenHeight) {
        int width = tileData.get(0).size();
        int height = tileData.size();
        float unitWidth = (float) screenWidth / width;
        float unitHeight = (float) screenHeight * .5f / height;
        TextureAtlas atlas = Fe2D.assets.getAsset(Breakout.TEXTURE_ATLAS);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int tile = tileData.get(y).get(x);
                if (tile == Brick.SOLID) {
                    Brick brick = new Brick(atlas, atlas.get(Breakout.BLOCK_SOLID));
                    brick.position.set(unitWidth * x, screenHeight - unitHeight * (y + 1));
                    brick.size.set(unitWidth, unitHeight);
                    brick.color = Brick.SOLID_COLOR;
                    brick.solid = true;
                    bricks.add(brick);
                } else if (tile > Brick.SOLID) {
                    Color color = switch (tile) {
                        case Brick.GREY -> Brick.GREY_COLOR;
                        case Brick.RED -> Brick.RED_COLOR;
                        case Brick.GREEN -> Brick.GREEN_COLOR;
                        case Brick.BLUE -> Brick.BLUE_COLOR;
                        case Brick.CYAN -> Brick.CYAN_COLOR;
                        case Brick.PURPLE -> Brick.PURPLE_COLOR;
                        case Brick.YELLOW -> Brick.YELLOW_COLOR;
                        default -> Color.WHITE;
                    };
                    Brick brick = new Brick(atlas, atlas.get(Breakout.BLOCK));
                    brick.position.set(unitWidth * x, screenHeight - unitHeight * (y + 1));
                    brick.size.set(unitWidth, unitHeight);
                    brick.color = color;
                    bricks.add(brick);
                }
            }
        }
    }
}
