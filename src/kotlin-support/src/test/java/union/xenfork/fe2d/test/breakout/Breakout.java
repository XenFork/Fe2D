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

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector2f;
import union.xenfork.fe2d.ApplicationConfig;
import union.xenfork.fe2d.Fe2D;
import union.xenfork.fe2d.Game;
import union.xenfork.fe2d.Input;
import union.xenfork.fe2d.file.FileContext;
import union.xenfork.fe2d.graphics.GLStateManager;
import union.xenfork.fe2d.graphics.ShaderProgram;
import union.xenfork.fe2d.graphics.batch.FontBatch;
import union.xenfork.fe2d.graphics.batch.SpriteBatch;
import union.xenfork.fe2d.graphics.font.Unifont;
import union.xenfork.fe2d.graphics.mesh.GeometryMesh;
import union.xenfork.fe2d.graphics.mesh.Mesh;
import union.xenfork.fe2d.graphics.sprite.Sprite;
import union.xenfork.fe2d.graphics.texture.Texture;
import union.xenfork.fe2d.graphics.texture.TextureAtlas;
import union.xenfork.fe2d.graphics.texture.TextureParam;
import union.xenfork.fe2d.util.ResourcePath;
import union.xenfork.fe2d.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

import static org.joml.Math.*;
import static org.lwjgl.opengl.GL11C.*;
import static union.xenfork.fe2d.gui.screen.ScreenUtil.*;

/**
 * breakout game
 *
 * @author squid233
 * @since 0.1.0
 */
public final class Breakout extends Game {
    private static final int GAME_MENU = 0;
    private static final int GAME_ACTIVE = 1;
    private static final int GAME_WIN = 1;
    public static final int LEVEL_WIDTH = 1600;
    public static final int LEVEL_HEIGHT = 900;
    private static final float PLAYER_SIZE_WIDTH = 150;
    private static final float PLAYER_SIZE_HEIGHT = 30;
    public static final ResourcePath TEXTURE_ATLAS = new ResourcePath("breakout:texture/atlas");
    public static final ResourcePath BACKGROUND = new ResourcePath("breakout:texture/background.jpg");
    public static final ResourcePath BLOCK = new ResourcePath("breakout:texture/block.png");
    public static final ResourcePath BLOCK_SOLID = new ResourcePath("breakout:texture/block_solid.png");
    public static final ResourcePath FACE = new ResourcePath("breakout:texture/face.png");
    public static final ResourcePath PADDLE = new ResourcePath("breakout:texture/paddle.png");
    private ShaderProgram shaderProgram;
    private Mesh backgroundMesh;
    private TextureAtlas textureAtlas;
    private SpriteBatch batch;
    private Sprite player;
    private BallObject ball;
    private Unifont unifont;
    private FontBatch fontBatch;
    private final Vector2f ballCollisionDiff = new Vector2f();
    private final List<Level> levels = new ArrayList<>();
    private int level = 1;
    private int state = GAME_ACTIVE;
    private final Matrix4f projectionMatrix = new Matrix4f().setOrtho2D(0, LEVEL_WIDTH, 0, LEVEL_HEIGHT);
    private final Matrix4f guiProjMatrix = new Matrix4f();
    private final Matrix4fStack modelMatrix = new Matrix4fStack(2);

    public Level loadLevel(FileContext fileContext, int screenWidth, int screenHeight) {
        Level level = new Level();
        level.load(fileContext.loadString(), screenWidth, screenHeight);
        return level;
    }

    public Level getCurrentLevel() {
        return levels.get(level - 1);
    }

    @Override
    public void onResize(int width, int height) {
        super.onResize(width, height);
        guiProjMatrix.setOrtho2D(0, width, 0, height);
        fontBatch.setProjectionMatrix(guiProjMatrix);
    }

    @Override
    public void onCursorPos(double posX, double posY) {
        super.onCursorPos(posX, posY);
        if (Fe2D.input.isTouched()) {
            float prevX = player.position.x;
            player.position.x = clamp(0f,
                LEVEL_WIDTH - PLAYER_SIZE_WIDTH,
                player.position.x + (float) Fe2D.input.cursorDeltaX() * 2.4f);
            if (ball.stuck) {
                ball.position.x += (player.position.x - prevX);
            }
        }
    }

    @Override
    public void onKeyPress(int key, int scancode, int mods) {
        super.onKeyPress(key, scancode, mods);
        if (key == Input.KEY_SPACE) {
            ball.stuck = false;
        }
    }

    @Override
    public void init() {
        super.init();
        glClearColor(0f, 0f, 0f, 1f);
        GLStateManager.enableBlend();
        GLStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shaderProgram = new ShaderProgram(
            Fe2D.files.internal(ResourcePath.assets("breakout:shader/shader.vsh")).loadString(),
            Fe2D.files.internal(ResourcePath.assets("breakout:shader/shader.fsh")).loadString(),
            Sprite.LAYOUT
        );
        shaderProgram.addSampler(0);

        textureAtlas = TextureAtlas.load(
            new TextureParam().minFilter(GL_LINEAR),
            TextureAtlas.entries(Fe2D.files::internal, ResourcePath.ASSETS,
                BACKGROUND,
                BLOCK,
                BLOCK_SOLID,
                FACE,
                PADDLE
            )
        );
        Fe2D.assets.putAsset(TEXTURE_ATLAS, textureAtlas);

        Sprite sprite = new Sprite(textureAtlas, textureAtlas.get(BACKGROUND));
        backgroundMesh = GeometryMesh.sprites(sprite);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(projectionMatrix);

        player = new Sprite(textureAtlas, textureAtlas.get(PADDLE));
        player.size.set(PLAYER_SIZE_WIDTH, PLAYER_SIZE_HEIGHT);
        player.position.set((LEVEL_WIDTH - PLAYER_SIZE_WIDTH) * .5f, 0f);

        ball = new BallObject(textureAtlas, textureAtlas.get(FACE), BallObject.RADIUS);
        ball.position.set(player.position).add(player.size.x() * .5f - BallObject.RADIUS, player.size.y());

        Level one = loadLevel(Fe2D.files.internal(ResourcePath.data("breakout:level/one.txt")), LEVEL_WIDTH, LEVEL_HEIGHT);
        Level two = loadLevel(Fe2D.files.internal(ResourcePath.data("breakout:level/two.txt")), LEVEL_WIDTH, LEVEL_HEIGHT);
        Level three = loadLevel(Fe2D.files.internal(ResourcePath.data("breakout:level/three.txt")), LEVEL_WIDTH, LEVEL_HEIGHT);
        Level four = loadLevel(Fe2D.files.internal(ResourcePath.data("breakout:level/four.txt")), LEVEL_WIDTH, LEVEL_HEIGHT);
        levels.add(one);
        levels.add(two);
        levels.add(three);
        levels.add(four);

        unifont = Unifont.create();
        fontBatch = new FontBatch();
        guiProjMatrix.setOrtho2D(0, Fe2D.graphics.width(), 0, Fe2D.graphics.height());
    }

    public void doCollisions() {
        // brick and ball
        for (Brick brick : getCurrentLevel().bricks) {
            if (!brick.destroyed) {
                var collision = ball.checkCollision(brick, ballCollisionDiff);
                if (collision.first()) {
                    if (!brick.solid) {
                        brick.destroyed = true;
                    }
                    Direction dir = collision.second();
                    if (dir.isOnAxisX()) {
                        ball.velocity.x = -ball.velocity.x();
                        float col = ball.radius - abs(ballCollisionDiff.x());
                        switch (dir) {
                            case LEFT -> ball.position.x += col;
                            case RIGHT -> ball.position.x -= col;
                        }
                    } else if (dir.isOnAxisY()) {
                        ball.velocity.y = -ball.velocity.y();
                        float col = ball.radius - abs(ballCollisionDiff.y());
                        switch (dir) {
                            case DOWN -> ball.position.y += col;
                            case UP -> ball.position.y -= col;
                        }
                    }
                }
            }
        }

        // paddle and ball
        if (!ball.stuck) {
            var collision = ball.checkCollision(player, ballCollisionDiff);
            if (collision.first()) {
                float centerBoard = player.position.x() + player.size.x() * 0.5f;
                float distance = ball.position.x() + ball.radius - centerBoard;
                float percentage = distance / (player.size.x() * 0.5f);
                float strength = 2.0f;
                float oldVx = ball.velocity.x();
                float oldVy = ball.velocity.y();
                ball.velocity.x = BallObject.INIT_VELOCITY_X * percentage * strength;
                ball.velocity.y = abs(ball.velocity.y());
                ball.velocity.normalize().mul(sqrt(oldVx * oldVx + oldVy * oldVy));
            }
        }
    }

    private void resetLevel() {
        for (Brick brick : getCurrentLevel().bricks) {
            brick.destroyed = false;
        }
    }

    private void resetPlayer() {
        player.position.set((LEVEL_WIDTH - PLAYER_SIZE_WIDTH) * .5f, 0f);
        ball.reset(player.position.x() + player.size.x() * .5f - BallObject.RADIUS, player.position.y() + player.size.y());
    }

    @Override
    public void fixedUpdate() {
        super.fixedUpdate();
        ball.move();
        doCollisions();
    }

    @Override
    public void update() {
        super.update();
        if ((ball.position.y() + ball.radius * 2f) < 0) {
            resetPlayer();
        }
    }

    @Override
    public void lateUpdate() {
        super.lateUpdate();
        if (state == GAME_ACTIVE) {
            if (getCurrentLevel().isCompleted()) {
                level++;
                if (level > 4) {
                    state = GAME_WIN;
                } else {
                    resetPlayer();
                }
            }
        }
    }

    @Override
    public void render(double delta) {
        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);
        if (state == GAME_ACTIVE) {
            shaderProgram.use();
            shaderProgram.setProjectionMatrix(projectionMatrix);
            shaderProgram.setModelMatrix(modelMatrix);
            shaderProgram.uploadUniforms();

            textureAtlas.bind();
            backgroundMesh.render();

            batch.begin();
            getCurrentLevel().render(batch);
            batch.draw(player);
            batch.draw(ball);
            batch.end();

            fontBatch.begin();
            fontBatch.draw(unifont,
                """
                    Breakout Test
                    Fork Engine 2D
                    氙叉联盟 (XenFork Union) 🚀""",
                0f,
                0f);
            fontBatch.end();

            ShaderProgram.ZERO.use();
            Texture.ZERO.bind();
        }

        super.render(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
        dispose(shaderProgram);
        dispose(backgroundMesh);
        dispose(batch);
        dispose(unifont);
        dispose(fontBatch);
    }

    public static void main(String[] args) {
        ApplicationConfig config = new ApplicationConfig();
        config.useStderr = true;
        config.applicationName = "Breakout";
        config.windowWidth = 1280;
        config.windowHeight = 720;
        new Breakout().launch(config);
    }
}
