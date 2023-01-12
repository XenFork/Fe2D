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

import org.joml.Vector2f;
import union.xenfork.fe2d.graphics.sprite.Sprite;
import union.xenfork.fe2d.graphics.texture.Texture;
import union.xenfork.fe2d.graphics.texture.TextureRegion;
import union.xenfork.fe2d.util.Pair;
import union.xenfork.fe2d.util.math.Direction;
import union.xenfork.fe2d.util.math.Intersection;

/**
 * the ball.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class BallObject extends Sprite {
    public static final float INIT_VELOCITY_X = 2f;
    public static final float INIT_VELOCITY_Y = 7f;
    public static final float RADIUS = 15f;
    public final Vector2f velocity = new Vector2f();
    public float radius;
    public boolean stuck = true;

    public BallObject(Texture texture, TextureRegion textureRegion, float radius) {
        super(texture, textureRegion);
        this.radius = radius;
        size.set(radius * 2, radius * 2);
        velocity.set(INIT_VELOCITY_X, INIT_VELOCITY_Y);
    }

    public Pair<Boolean, Direction> checkCollision(Sprite sprite, Vector2f result) {
        boolean test = Intersection.intersectAarCircle(
            sprite.position.x(), sprite.position.y(),
            sprite.position.x() + sprite.size.x(), sprite.position.y() + sprite.size.y(),
            position.x() + radius, position.y() + radius,
            radius * radius,
            result
        );
        Direction direction = Direction.fromVector(result);
        return new Pair<>(test && direction != null, direction);
    }

    public void move() {
        if (!stuck) {
            position.add(velocity);
            if (position.x() <= 0f) {
                velocity.x = -velocity.x();
                position.x = 0f;
            } else if (position.x() + size.x() >= Breakout.LEVEL_WIDTH) {
                velocity.x = -velocity.x();
                position.x = Breakout.LEVEL_WIDTH - size.x();
            } else if (position.y() + size.y() >= Breakout.LEVEL_HEIGHT) {
                velocity.y = -velocity.y();
                position.y = Breakout.LEVEL_HEIGHT - size.y();
            }
        }
    }

    public void reset(float posX, float posY) {
        stuck = true;
        velocity.set(INIT_VELOCITY_X, INIT_VELOCITY_Y);
        position.set(posX, posY);
    }
}
