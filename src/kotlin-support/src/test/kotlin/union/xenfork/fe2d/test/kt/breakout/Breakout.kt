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

package union.xenfork.fe2d.test.kt.breakout

import org.joml.Math
import org.joml.Matrix4f
import org.joml.Matrix4fStack
import org.joml.Vector2f
import org.lwjgl.opengl.GL11C.*
import union.xenfork.fe2d.Fe2D
import union.xenfork.fe2d.Input
import union.xenfork.fe2d.config.JsonConfig
import union.xenfork.fe2d.config.PropertiesConfig
import union.xenfork.fe2d.file.BinaryData
import union.xenfork.fe2d.file.FileContext
import union.xenfork.fe2d.game
import union.xenfork.fe2d.graphics.GLStateManager
import union.xenfork.fe2d.graphics.ShaderProgram
import union.xenfork.fe2d.graphics.batch.SpriteBatch
import union.xenfork.fe2d.graphics.font.Font
import union.xenfork.fe2d.graphics.font.TrueTypeFont
import union.xenfork.fe2d.graphics.mesh.GeometryMesh
import union.xenfork.fe2d.graphics.mesh.Mesh
import union.xenfork.fe2d.graphics.sprite.Sprite
import union.xenfork.fe2d.graphics.texture.TextureAtlas
import union.xenfork.fe2d.graphics.texture.TextureParam
import union.xenfork.fe2d.set
import union.xenfork.fe2d.test.breakout.BallObject
import union.xenfork.fe2d.test.breakout.Breakout
import union.xenfork.fe2d.test.breakout.Level
import union.xenfork.fe2d.test.breakout.MenuScreen
import union.xenfork.fe2d.util.ResourcePath
import union.xenfork.fe2d.util.math.Direction
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.abs

const val USE_STDERR = true
const val MAX_LEVEL = 4
const val GAME_MENU = 0
const val GAME_ACTIVE = 1
const val GAME_WIN = 2
const val LEVEL_WIDTH = 1600
const val LEVEL_HEIGHT = 900
const val PLAYER_SIZE_WIDTH = 150f
const val PLAYER_SIZE_HEIGHT = 30f
val TEXTURE_ATLAS = ResourcePath("breakout:texture/atlas")
val BACKGROUND = ResourcePath("breakout:texture/background.jpg")
val BLOCK = ResourcePath("breakout:texture/block.png")
val BLOCK_SOLID = ResourcePath("breakout:texture/block_solid.png")
val FACE = ResourcePath("breakout:texture/face.png")
val PADDLE = ResourcePath("breakout:texture/paddle.png")
val TRUE_TYPE_FONT = ResourcePath("breakout:test.ttf")
const val XENFORK_STR = "氙叉联盟 (XenFork Union)"

private val levels = ArrayList<Level>()

var level = 1
var state = GAME_MENU
val propertiesConfig = PropertiesConfig()
val jsonConfig = JsonConfig()

val currentLevel: Level
    get() = levels[level - 1]

private var shaderProgram: ShaderProgram? = null
private var backgroundMesh: Mesh? = null
private var textureAtlas: TextureAtlas? = null
private var batch: SpriteBatch? = null
private var player: Sprite? = null
private var ball: BallObject? = null
private var trueTypeFont: TrueTypeFont? = null
private val ballCollisionDiff = Vector2f()
private val projectionMatrix = Matrix4f().setOrtho2D(0f, LEVEL_WIDTH.toFloat(), 0f, LEVEL_HEIGHT.toFloat())
private val modelMatrix = Matrix4fStack(2)

fun loadLevel(fileContext: FileContext, screenWidth: Int, screenHeight: Int): Level =
    Level().apply { load(fileContext.loadString(), screenWidth, screenHeight) }

fun doCollisions() {
    ball!!.also { ball ->
        // brick and ball
        currentLevel.bricks.forEach { brick ->
            if (!brick.destroyed) {
                ball.checkCollision(brick, ballCollisionDiff).takeIf { it.first }?.second?.also { dir ->
                    if (!brick.solid) {
                        brick.destroyed = true
                    }
                    if (dir.isOnAxisX) {
                        ball.velocity.x = -ball.velocity.x()
                        val col = ball.radius - abs(ballCollisionDiff.x())
                        when (dir) {
                            Direction.LEFT -> ball.position.x += col
                            Direction.RIGHT -> ball.position.x -= col
                            else -> {}
                        }
                    } else if (dir.isOnAxisY) {
                        ball.velocity.y = -ball.velocity.y()
                        val col = ball.radius - abs(ballCollisionDiff.y())
                        when (dir) {
                            Direction.DOWN -> ball.position.y += col
                            Direction.UP -> ball.position.y -= col
                            else -> {}
                        }
                    }
                }
            }
        }

        // paddle and ball
        if (!ball.stuck) {
            if (ball.checkCollision(player, ballCollisionDiff).first) {
                val centerBoard = player!!.position.x() + player!!.size.x() * 0.5f
                val distance = ball.position.x() + ball.radius - centerBoard
                val percentage = distance / (player!!.size.x() * 0.5f)
                val strength = 2.0f
                val oldVx = ball.velocity.x()
                val oldVy = ball.velocity.y()
                ball.velocity.x = BallObject.INIT_VELOCITY_X * percentage * strength
                ball.velocity.y = abs(ball.velocity.y())
                ball.velocity.normalize().mul(Math.sqrt(oldVx * oldVx + oldVy * oldVy))
            }
        }
    }
}

fun resetPlayer() {
    player!!.also { player ->
        player.position.set((LEVEL_WIDTH - PLAYER_SIZE_WIDTH) * .5f, 0f)
        ball!!.reset(
            player.position.x() + player.size.x() * .5f - BallObject.RADIUS,
            player.position.y() + player.size.y()
        )
    }
}

/**
 * breakout game
 *
 * @author squid233
 * @since 0.1.0
 */
fun main() = game(
    useStderr = true,
    applicationName = "Breakout",
    windowWidth = 1280,
    windowHeight = 720
) {
    onCursorPos { posX, posY, base ->
        base(posX, posY)
        if (state == GAME_ACTIVE) {
            if (Fe2D.input.isTouched) {
                player?.apply {
                    val prevX = position.x
                    position.x = Math.clamp(
                        0f,
                        LEVEL_WIDTH - PLAYER_SIZE_WIDTH,
                        position.x + Fe2D.input.cursorDeltaX().toFloat() * 2.4f
                    )
                    ball?.also {
                        if (it.stuck) {
                            it.position.x += (position.x - prevX)
                        }
                    }
                }
            }
        }
    }
    onKeyPress { key, scancode, mods, base ->
        base(key, scancode, mods)
        if (state == GAME_ACTIVE) {
            if (key == Input.KEY_SPACE) {
                ball?.stuck = false
            }
        } else {
            if (key == Input.KEY_ENTER) {
                when (state) {
                    GAME_MENU -> {
                        state = GAME_ACTIVE
                        openScreen(null)
                    }

                    GAME_WIN -> {
                        state = GAME_MENU
                        currentScreen()?.onClose()
                    }
                }
            }
        }
    }
    onInit { base ->
        base()
        glClearColor(0f, 0f, 0f, 1f)
        GLStateManager.enableBlend()
        GLStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        shaderProgram = ShaderProgram(
            Fe2D.files.internal(ResourcePath.assets("breakout:shader/shader.vert")).loadString(),
            Fe2D.files.internal(ResourcePath.assets("breakout:shader/shader.frag")).loadString(),
            Sprite.LAYOUT
        ).apply { addSampler(0) }

        textureAtlas = TextureAtlas.load(
            TextureParam().minFilter(GL_LINEAR),
            *TextureAtlas.entries(
                Fe2D.files::internal, ResourcePath.ASSETS,
                BACKGROUND,
                BLOCK,
                BLOCK_SOLID,
                FACE,
                PADDLE
            )
        )
        Fe2D.assets[TEXTURE_ATLAS] = textureAtlas!!

        val sprite = Sprite(textureAtlas, textureAtlas!![BACKGROUND])
        backgroundMesh = GeometryMesh.sprites(sprite)

        batch = SpriteBatch().apply { setProjectionMatrix(projectionMatrix) }

        player = Sprite(textureAtlas, textureAtlas!![PADDLE]).apply {
            size.set(PLAYER_SIZE_WIDTH, PLAYER_SIZE_HEIGHT)
            position.set((LEVEL_WIDTH - PLAYER_SIZE_WIDTH) * .5f, 0f)
        }

        ball = BallObject(textureAtlas, textureAtlas!![FACE], BallObject.RADIUS)
            .apply {
                position.set(player!!.position).add(player!!.size.x() * .5f - BallObject.RADIUS, player!!.size.y())
            }


        val one = loadLevel(
            Fe2D.files.internal(ResourcePath.data("breakout:level/one.txt")),
            Breakout.LEVEL_WIDTH,
            Breakout.LEVEL_HEIGHT
        )
        val two = loadLevel(
            Fe2D.files.internal(ResourcePath.data("breakout:level/two.txt")),
            Breakout.LEVEL_WIDTH,
            Breakout.LEVEL_HEIGHT
        )
        val three = loadLevel(
            Fe2D.files.internal(ResourcePath.data("breakout:level/three.txt")),
            Breakout.LEVEL_WIDTH,
            Breakout.LEVEL_HEIGHT
        )
        val four = loadLevel(
            Fe2D.files.internal(ResourcePath.data("breakout:level/four.txt")),
            Breakout.LEVEL_WIDTH,
            Breakout.LEVEL_HEIGHT
        )
        levels += one
        levels += two
        levels += three
        levels += four

        // note: we just simply get the Chinese characters from the constant.
        // the emoji might be white square.
        trueTypeFont = TrueTypeFont.load(Font.ASCII + XENFORK_STR.substring(0, 4) + "🚀", Fe2D.files.local("test.ttf"))
        Fe2D.assets[TRUE_TYPE_FONT] = trueTypeFont!!

        openScreen(MenuScreen())

        val properties: FileContext = Fe2D.files.local("breakout-kt.properties")
        log(USE_STDERR, propertiesConfig.getString("breakout"))
        if (Files.exists(Path.of("breakout-kt.properties"))) {
            propertiesConfig.load(properties)
            log(USE_STDERR, propertiesConfig.getString("breakout"))
        }
        propertiesConfig["breakout"] = "1.0.0"
        log(USE_STDERR, propertiesConfig.getString("breakout"))
        propertiesConfig.save(properties)
        log(USE_STDERR, propertiesConfig.getString("breakout"))

        log(USE_STDERR, "---------------")
        jsonConfig.manage(Fe2D.files.local("breakout.json"))
        log(USE_STDERR, if (jsonConfig.has("breakout")) jsonConfig.getString("breakout") else "null")
        log(USE_STDERR, if (jsonConfig.has("level")) jsonConfig.getInt("level").toString() else "null")
        if (Files.exists(Path.of("breakout.json"))) {
            jsonConfig.load()
            log(USE_STDERR, jsonConfig.getString("breakout"))
            log(USE_STDERR, jsonConfig.getInt("level").toString())
        }
        jsonConfig["breakout"] = "1.0.0"
        jsonConfig["level"] = 1
        log(USE_STDERR, jsonConfig.getString("breakout"))
        log(USE_STDERR, jsonConfig.getInt("level").toString())
        jsonConfig.save()
        jsonConfig.isAutoSave = true
        log(USE_STDERR, jsonConfig.getString("breakout"))
        log(USE_STDERR, jsonConfig.getInt("level").toString())

        level = Math.clamp(1, MAX_LEVEL, jsonConfig.getInt("level"))

        // write binary data
        ObjectOutputStream(Fe2D.files.local("breakout.bin").createOutputStream()).use { oos ->
            BinaryData.ofTags().also { tags ->
                tags["breakout"] = BinaryData.of("0.1.0")
                tags["level"] = BinaryData.of(1)
                tags["int_array"] = BinaryData.of(intArrayOf(1, 2, 3, 4))
                tags["string_array"] = BinaryData.of(arrayOf("breakout", XENFORK_STR))
                BinaryData.ofTags(mapOf("key" to BinaryData.of("value"))).also { ofTags ->
                    tags["data_array"] = BinaryData.of(arrayOf(BinaryData.of(42), BinaryData.of(XENFORK_STR), ofTags))
                    tags["compound"] = ofTags
                }
                tags.write(oos)
            }
        }
        // read binary data
        ObjectInputStream(Fe2D.files.local("breakout.bin").createInputStream()).use { ois ->
            BinaryData.read(ois).asTagsSafe()!!.also { tags ->
                log(USE_STDERR, "breakout=" + tags["breakout"].asString())
                log(USE_STDERR, "level=" + tags["level"].asInt())
                log(USE_STDERR, "int_array=" + tags["int_array"].asIntArray().contentToString())
                log(USE_STDERR, "string_array=" + tags["string_array"].asStringArray().contentToString())
                log(USE_STDERR, "data_array=" + tags["data_array"].asDataArraySafe())
                log(USE_STDERR, "compound=" + tags["compound"].asTagsSafe())
            }
        }
    }
}
