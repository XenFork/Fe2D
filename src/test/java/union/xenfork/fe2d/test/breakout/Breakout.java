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

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import union.xenfork.fe2d.Application;
import union.xenfork.fe2d.ApplicationConfig;
import union.xenfork.fe2d.Fe2D;
import union.xenfork.fe2d.file.FileContext;
import union.xenfork.fe2d.graphics.Color;
import union.xenfork.fe2d.graphics.GLStateManager;
import union.xenfork.fe2d.graphics.ShaderProgram;
import union.xenfork.fe2d.graphics.batch.SpriteBatch;
import union.xenfork.fe2d.graphics.mesh.GeometryMesh;
import union.xenfork.fe2d.graphics.mesh.Mesh;
import union.xenfork.fe2d.graphics.sprite.Sprite;
import union.xenfork.fe2d.graphics.texture.Texture;
import union.xenfork.fe2d.graphics.texture.TextureAtlas;
import union.xenfork.fe2d.graphics.texture.TextureParam;
import union.xenfork.fe2d.util.ResourcePath;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11C.*;

/**
 * breakout game
 *
 * @author squid233
 * @since 0.1.0
 */
public final class Breakout extends Application {
    private static final int GAME_ACTIVE = 1;
    private static final int LEVEL_WIDTH = 1600;
    private static final int LEVEL_HEIGHT = 900;
    private static final float PLAYER_SIZE_WIDTH = 150;
    private static final float PLAYER_SIZE_HEIGHT = 30;
    private ShaderProgram shaderProgram;
    private Mesh backgroundMesh;
    private Mesh paddleMesh;
    private TextureAtlas textureAtlas;
    private SpriteBatch batch;
    private Sprite player;
    private final List<Level> levels = new ArrayList<>();
    private int level = 1;
    private int state = GAME_ACTIVE;
    private final Matrix4f projectionMatrix = new Matrix4f().setOrtho2D(0, LEVEL_WIDTH, 0, LEVEL_HEIGHT);
    private final Matrix4fStack modelMatrix = new Matrix4fStack(2);

    /**
     * the game level
     *
     * @author squid233
     * @since 0.1.0
     */
    public final class Level {
        private final List<Brick> bricks = new ArrayList<>();

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
                batch.draw(brick);
            }
        }

        public boolean isCompleted() {
            for (Brick brick : bricks) {
                if (!brick.solid) {
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
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int tile = tileData.get(y).get(x);
                    if (tile == Brick.SOLID) {
                        Brick brick = new Brick(textureAtlas, textureAtlas.get("block_solid"));
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
                        Brick brick = new Brick(textureAtlas, textureAtlas.get("block"));
                        brick.position.set(unitWidth * x, screenHeight - unitHeight * (y + 1));
                        brick.size.set(unitWidth, unitHeight);
                        brick.color = color;
                        bricks.add(brick);
                    }
                }
            }
        }
    }

    public Level loadLevel(FileContext fileContext, int screenWidth, int screenHeight) {
        Level level = new Level();
        level.load(fileContext.loadString(), screenWidth, screenHeight);
        return level;
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
            TextureAtlas.entry(Fe2D.files.internal(ResourcePath.assets("breakout:texture/background.jpg")), "background"),
            TextureAtlas.entry(Fe2D.files.internal(ResourcePath.assets("breakout:texture/block.png")), "block"),
            TextureAtlas.entry(Fe2D.files.internal(ResourcePath.assets("breakout:texture/block_solid.png")), "block_solid"),
            TextureAtlas.entry(Fe2D.files.internal(ResourcePath.assets("breakout:texture/face.png")), "face"),
            TextureAtlas.entry(Fe2D.files.internal(ResourcePath.assets("breakout:texture/paddle.png")), "paddle")
        );

        Sprite sprite = new Sprite(textureAtlas, textureAtlas.get("background"));
        backgroundMesh = GeometryMesh.sprites(sprite);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(projectionMatrix);

        player = new Sprite(textureAtlas, textureAtlas.get("paddle"));
        player.size.set(PLAYER_SIZE_WIDTH, PLAYER_SIZE_HEIGHT);
        paddleMesh = GeometryMesh.sprites(player);
        player.position.set((LEVEL_WIDTH - PLAYER_SIZE_WIDTH) * .5f, 0f);

        Level one = loadLevel(Fe2D.files.internal(ResourcePath.data("breakout:level/one.txt")), LEVEL_WIDTH, LEVEL_HEIGHT);
        levels.add(one);
    }

    @Override
    public void onCursorPos(double posX, double posY) {
        super.onCursorPos(posX, posY);
        if (Fe2D.input.isTouched()) {
            player.position.x = Math.clamp(0f,
                LEVEL_WIDTH - PLAYER_SIZE_WIDTH,
                player.position.x + (float) Fe2D.input.cursorDeltaX() * 2.5f);
        }
    }

    @Override
    public void render() {
        super.render();

        if (state == GAME_ACTIVE) {
            shaderProgram.use();
            shaderProgram.setProjectionMatrix(projectionMatrix);
            shaderProgram.setModelMatrix(modelMatrix);
            shaderProgram.uploadUniforms();

            textureAtlas.bind();
            backgroundMesh.render();

            modelMatrix.pushMatrix().mul(player.getTransform());
            shaderProgram.setModelMatrix(modelMatrix);
            modelMatrix.popMatrix();
            shaderProgram.uploadUniforms();
            paddleMesh.render();

            batch.begin();
            levels.get(level - 1).render(batch);
            batch.end();

            ShaderProgram.ZERO.use();
            Texture.ZERO.bind();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        dispose(shaderProgram);
        dispose(backgroundMesh);
        dispose(paddleMesh);
        dispose(textureAtlas);
        dispose(batch);
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
