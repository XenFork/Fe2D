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
import org.joml.Vector2f;
import org.lwjgl.system.MemoryUtil;
import union.xenfork.fe2d.ApplicationConfig;
import union.xenfork.fe2d.Fe2D;
import union.xenfork.fe2d.Game;
import union.xenfork.fe2d.Input;
import union.xenfork.fe2d.config.JsonConfig;
import union.xenfork.fe2d.config.PropertiesConfig;
import union.xenfork.fe2d.file.BinaryData;
import union.xenfork.fe2d.file.BinaryTags;
import union.xenfork.fe2d.file.FileContext;
import union.xenfork.fe2d.graphics.GLStateManager;
import union.xenfork.fe2d.graphics.ShaderProgram;
import union.xenfork.fe2d.graphics.batch.SpriteBatch;
import union.xenfork.fe2d.graphics.font.Font;
import union.xenfork.fe2d.graphics.font.TextRenderer;
import union.xenfork.fe2d.graphics.font.TrueTypeFont;
import union.xenfork.fe2d.graphics.mesh.GeometryMesh;
import union.xenfork.fe2d.graphics.mesh.Mesh;
import union.xenfork.fe2d.graphics.sprite.Sprite;
import union.xenfork.fe2d.graphics.texture.Texture;
import union.xenfork.fe2d.graphics.texture.TextureAtlas;
import union.xenfork.fe2d.graphics.texture.TextureParam;
import union.xenfork.fe2d.gui.layout.Alignment;
import union.xenfork.fe2d.util.ResourcePath;
import union.xenfork.fe2d.util.math.Direction;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
    private static Breakout instance;
    private static final boolean USE_STDERR = true;
    public static final int MAX_LEVEL = 4;
    public static final int GAME_MENU = 0;
    public static final int GAME_ACTIVE = 1;
    public static final int GAME_WIN = 2;
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
    public static final ResourcePath TRUE_TYPE_FONT = new ResourcePath("breakout:test.ttf");
    private static final String XENFORK_STR = "氙叉联盟 (XenFork Union)";
    private ShaderProgram shaderProgram;
    private Mesh backgroundMesh;
    private TextureAtlas textureAtlas;
    private SpriteBatch batch;
    private Sprite player;
    private BallObject ball;
    private TrueTypeFont trueTypeFont;
    private final Vector2f ballCollisionDiff = new Vector2f();
    private final List<Level> levels = new ArrayList<>();
    public int level = 1;
    public int state = GAME_MENU;
    private final Matrix4f projectionMatrix = new Matrix4f().setOrtho2D(0, LEVEL_WIDTH, 0, LEVEL_HEIGHT);
    private final Matrix4fStack modelMatrix = new Matrix4fStack(2);
    public final PropertiesConfig propertiesConfig = new PropertiesConfig();
    public final JsonConfig jsonConfig = new JsonConfig();

    private Level loadLevel(FileContext fileContext, int screenWidth, int screenHeight) {
        Level level = new Level();
        level.load(fileContext.loadString(), screenWidth, screenHeight);
        return level;
    }

    public Level getCurrentLevel() {
        return levels.get(level - 1);
    }

    @Override
    public void onCursorPos(double posX, double posY) {
        super.onCursorPos(posX, posY);
        if (state == GAME_ACTIVE) {
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
    }

    @Override
    public void onKeyPress(int key, int scancode, int mods) {
        super.onKeyPress(key, scancode, mods);
        if (state == GAME_ACTIVE) {
            if (key == Input.KEY_SPACE) {
                ball.stuck = false;
            }
        } else {
            if (key == Input.KEY_ENTER) {
                switch (state) {
                    case GAME_MENU -> {
                        state = GAME_ACTIVE;
                        openScreen(null);
                    }
                    case GAME_WIN -> {
                        state = GAME_MENU;
                        if (screen != null) {
                            screen.onClose();
                        }
                    }
                }
            }
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

        // note: we just simply get the Chinese characters from the constant.
        // the emoji might be white square.
        trueTypeFont = TrueTypeFont.load(Font.ASCII + XENFORK_STR.substring(0, 4) + "🚀", Fe2D.files.local("test.ttf"));
        Fe2D.assets.putAsset(TRUE_TYPE_FONT, trueTypeFont);

        openScreen(new MenuScreen());

        FileContext properties = Fe2D.files.local("breakout.properties");
        log(USE_STDERR, propertiesConfig.getString("breakout"));
        if (Files.exists(Path.of("breakout.properties"))) {
            propertiesConfig.load(properties);
            log(USE_STDERR, propertiesConfig.getString("breakout"));
        }
        propertiesConfig.set("breakout", "1.0.0");
        log(USE_STDERR, propertiesConfig.getString("breakout"));
        propertiesConfig.save(properties);
        log(USE_STDERR, propertiesConfig.getString("breakout"));

        log(USE_STDERR, "---------------");

        jsonConfig.manage(Fe2D.files.local("breakout.json"));
        log(USE_STDERR, jsonConfig.has("breakout") ? jsonConfig.getString("breakout") : "null");
        log(USE_STDERR, jsonConfig.has("level") ? String.valueOf(jsonConfig.getInt("level")) : "null");
        if (Files.exists(Path.of("breakout.json"))) {
            jsonConfig.load();
            log(USE_STDERR, jsonConfig.getString("breakout"));
            log(USE_STDERR, String.valueOf(jsonConfig.getInt("level")));
        }
        jsonConfig.set("breakout", "1.0.0");
        jsonConfig.set("level", 1);
        log(USE_STDERR, jsonConfig.getString("breakout"));
        log(USE_STDERR, String.valueOf(jsonConfig.getInt("level")));
        jsonConfig.save();
        jsonConfig.setAutoSave(true);
        log(USE_STDERR, jsonConfig.getString("breakout"));
        log(USE_STDERR, String.valueOf(jsonConfig.getInt("level")));

        level = Math.clamp(1, MAX_LEVEL, jsonConfig.getInt("level"));

        // write binary data
        try (var oos = new ObjectOutputStream(Fe2D.files.local("breakout.bin").createOutputStream())) {
            BinaryTags tags = BinaryData.ofTags();
            tags.set("breakout", BinaryData.of("0.1.0"));
            tags.set("level", BinaryData.of(1));
            tags.set("int_array", BinaryData.of(new int[]{1, 2, 3, 4}));
            tags.set("string_array", BinaryData.of(new String[]{"breakout", XENFORK_STR}));
            BinaryTags ofTags = BinaryData.ofTags(Map.of("key", BinaryData.of("value")));
            tags.set("data_array", BinaryData.of(new BinaryData[]{BinaryData.of(42), BinaryData.of(XENFORK_STR), ofTags}));
            tags.set("compound", ofTags);
            tags.write(oos);
        } catch (Exception e) {
            log(USE_STDERR, "Failed to write the binary data!");
            e.printStackTrace();
        }
        // read binary data
        try (var ois = new ObjectInputStream(Fe2D.files.local("breakout.bin").createInputStream())) {
            BinaryTags tags = Objects.requireNonNull(BinaryData.read(ois).asTagsSafe());
            log(USE_STDERR, "breakout=" + tags.get("breakout").asString());
            log(USE_STDERR, "level=" + tags.get("level").asInt());
            log(USE_STDERR, "int_array=" + Arrays.toString(tags.get("int_array").asIntArray()));
            log(USE_STDERR, "string_array=" + Arrays.toString(tags.get("string_array").asStringArray()));
            log(USE_STDERR, "data_array=" + tags.get("data_array").asDataArraySafe());
            log(USE_STDERR, "compound=" + tags.get("compound").asTagsSafe());
        } catch (Exception e) {
            log(USE_STDERR, "Failed to read the binary data!");
            e.printStackTrace();
        }
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

    public void resetPlayer() {
        player.position.set((LEVEL_WIDTH - PLAYER_SIZE_WIDTH) * .5f, 0f);
        ball.reset(player.position.x() + player.size.x() * .5f - BallObject.RADIUS, player.position.y() + player.size.y());
    }

    @Override
    public void fixedUpdate() {
        super.fixedUpdate();
        if (state == GAME_ACTIVE) {
            ball.move();
            doCollisions();
        }
    }

    @Override
    public void update() {
        super.update();
        if (state == GAME_ACTIVE) {
            if ((ball.position.y() + ball.radius * 2f) < 0) {
                resetPlayer();
            }
        }
    }

    @Override
    public void lateUpdate() {
        super.lateUpdate();
        if (state == GAME_ACTIVE) {
            if (getCurrentLevel().isCompleted()) {
                state = GAME_WIN;
                openScreen(new GameOverScreen(new MenuScreen(), true));
            }
        }
    }

    @Override
    public void render(double delta) {
        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);
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

        TextRenderer renderer = Fe2D.textRenderer();
        renderer.begin();
        // renders basic ASCII alphabet, Chinese characters, and emoji.
        renderer.draw(Fe2D.defaultFont(),
            String.format("""
                Breakout Test
                Fork Engine 2D
                %1$s 🚀
                THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG,
                the quick brown fox jumps over the lazy dog.""", XENFORK_STR),
            0f, 0f);
        renderer.draw(trueTypeFont,
            String.format("""
                %1$s 🚀
                THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG,
                the quick brown fox jumps over the lazy dog.""", XENFORK_STR),
            0f, 100f);
        String fpsText = Fe2D.graphics.framesPerSecond() + " fps";
        renderer.draw(trueTypeFont,
            fpsText,
            0, Fe2D.graphics.height() - trueTypeFont.getScale(32f) * trueTypeFont.getTextHeight(fpsText),
            Alignment.V.LEFT, 32f);
        renderer.end();

        ShaderProgram.ZERO.use();
        Texture.ZERO.bind();

        super.render(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
        dispose(shaderProgram);
        dispose(backgroundMesh);
        dispose(batch);
        MemoryUtil.memFree(trueTypeFont.fontData());
    }

    public static Breakout getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        ApplicationConfig config = new ApplicationConfig();
        config.useStderr = USE_STDERR;
        config.applicationName = "Breakout";
        config.windowWidth = 1280;
        config.windowHeight = 720;
        (instance = new Breakout()).launch(config);
    }
}
