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

package union.xenfork.fe2d;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import union.xenfork.fe2d.graphics.batch.SpriteBatch;

import java.nio.IntBuffer;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The application which is the entrypoint of a game.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Application implements Updatable, Disposable {
    /**
     * The logger that is initialized with the application name.
     */
    protected Logger logger;
    /**
     * The window handle.
     */
    protected long window;

    /**
     * Prints a message.
     *
     * @param useStderr {@code true} uses standard error output stream.
     * @param msg       the message to be printed.
     */
    protected void log(boolean useStderr, String msg) {
        if (useStderr) {
            System.err.println(msg);
        } else {
            logger.debug(msg);
        }
    }

    /**
     * Launches this application with the given configuration.
     *
     * @param config the configuration.
     */
    public void launch(ApplicationConfig config) {
        Fe2D.application = this;
        logger = LoggerFactory.getLogger(config.applicationName);
        GLFWErrorCallback.create(new GLFWErrorCallbackI() {
            private final Map<Integer, String> ERROR_CODES =
                APIUtil.apiClassTokens((field, value) -> 0x10000 < value && value < 0x20000,
                    null,
                    org.lwjgl.glfw.GLFW.class);

            @Override
            public void invoke(int error, long description) {
                String msg = GLFWErrorCallback.getDescription(description);
                StringBuilder sb = new StringBuilder(512);
                sb.append("[Fork Engine 2D] ").append(ERROR_CODES.get(error)).append(" error\n");
                sb.append("    Description: ").append(msg);
                sb.append("\n    Stacktrace: \n");
                StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                for (int i = 4; i < stack.length; i++) {
                    sb.append("    ").append(stack[i].toString()).append('\n');
                }
                log(config.useStderr, sb.toString());
            }
        }).set();
        try {
            if (!glfwInit()) {
                throw new IllegalStateException("Failed to initialize GLFW");
            }
            try {
                start();
                window = glfwCreateWindow(config.windowWidth,
                    config.windowHeight,
                    config.windowTitle != null ? config.windowTitle : config.applicationName,
                    MemoryUtil.NULL,
                    MemoryUtil.NULL);
                if (window == MemoryUtil.NULL) {
                    throw new IllegalStateException("Failed to create the GLFW window");
                }
                try {
                    Fe2D.input = new Input(window);

                    // Sets callbacks
                    glfwSetFramebufferSizeCallback(window, (handle, width, height) -> {
                        Fe2D.graphics.setSize(width, height);
                        if (Fe2D.hasTextRenderer()) {
                            Fe2D.textRenderer().resize(width, height);
                        }
                        if (Fe2D.hasSpriteBatch()) {
                            SpriteBatch oldBatch = Fe2D.spriteRenderer();
                            // reset to null to get sprite batch
                            Fe2D.setSpriteRenderer(null);
                            Fe2D.spriteRenderer()
                                .setProjectionMatrix(Fe2D.spriteRenderer()
                                    .projectionMatrix()
                                    .setOrtho2D(0, width, 0, height));
                            // restore
                            Fe2D.setSpriteRenderer(oldBatch);
                        }
                        onResize(width, height);
                    });
                    glfwSetCursorPosCallback(window, (handle, xpos, ypos) -> {
                        Fe2D.input.updateCursorPos(xpos, ypos);
                        onCursorPos(xpos, ypos);
                    });
                    glfwSetKeyCallback(window, (handle, key, scancode, action, mods) ->
                        onKey(key, scancode, switch (action) {
                            case GLFW_PRESS -> Input.Action.PRESS;
                            case GLFW_REPEAT -> Input.Action.REPEAT;
                            default -> Input.Action.RELEASE;
                        }, mods));
                    glfwSetMouseButtonCallback(window, (handle, button, action, mods) ->
                        onMouseButton(button,
                            action == GLFW_PRESS ? Input.Action.PRESS : Input.Action.RELEASE,
                            mods));

                    // Makes center
                    //if (config.windowMonitor != MemoryUtil.NULL) {
                    GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
                    if (vidMode != null) {
                        glfwSetWindowPos(window,
                            (vidMode.width() - config.windowWidth) / 2,
                            (vidMode.height() - config.windowHeight) / 2);
                    }
                    //}

                    // Creates GL context
                    glfwMakeContextCurrent(window);
                    GL.createCapabilities(true);
                    if (config.vsync) {
                        glfwSwapInterval(1);
                    }
                    try (MemoryStack stack = MemoryStack.stackPush()) {
                        IntBuffer pw = stack.callocInt(1);
                        IntBuffer ph = stack.callocInt(1);
                        glfwGetFramebufferSize(window, pw, ph);
                        Fe2D.graphics.setSize(pw.get(0), ph.get(0));
                        GL11C.glViewport(0, 0, pw.get(0), ph.get(0));
                    }
                    init();

                    // Game loop
                    Fe2D.timer = new Timer();
                    double time = glfwGetTime();
                    while (!glfwWindowShouldClose(window)) {
                        glfwPollEvents();
                        double delta = Fe2D.timer.advanceTime(this::fixedUpdate);
                        update();
                        lateUpdate();
                        render(delta);
                        glfwSwapBuffers(window);
                        double currTime = glfwGetTime();
                        Fe2D.graphics.setDeltaFrameTime(currTime - time);
                        time = currTime;
                    }
                    Fe2D.dispose();
                    dispose();
                } finally {
                    Callbacks.glfwFreeCallbacks(window);
                    glfwDestroyWindow(window);
                }
            } finally {
                glfwTerminate();
            }
        } finally {
            Objects.requireNonNull(glfwSetErrorCallback(null)).close();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Callbacks
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Will be called when the framebuffer of the specified window is resized.
     *
     * @param width  the new width, in pixels, of the framebuffer.
     * @param height the new height, in pixels, of the framebuffer.
     */
    public void onResize(int width, int height) {
        GL11C.glViewport(0, 0, width, height);
    }

    /**
     * Will be called when the cursor is moved.
     * <p>
     * The callback function receives the cursor position, measured in screen coordinates but relative to the top-left corner
     * of the window client area. On platforms that provide it, the full sub-pixel cursor position is passed on.
     *
     * @param posX the new cursor x-coordinate, relative to the left edge of the content area.
     * @param posY the new cursor y-coordinate, relative to the top edge of the content area.
     */
    public void onCursorPos(double posX, double posY) {
    }

    /**
     * Will be called when a key is pressed, repeated or released.
     *
     * @param key      the keyboard key that was pressed or released.
     * @param scancode the platform-specific scancode of the key.
     * @param action   the key action.
     * @param mods     bitfield describing which modifiers keys were held down.
     */
    public void onKey(int key, int scancode, @NotNull Input.Action action, int mods) {
    }

    /**
     * Will be called when a mouse button is pressed or released.
     *
     * @param button the mouse button that was pressed or released.
     * @param action the button action.
     * @param mods   bitfield describing which modifiers keys were held down.
     */
    public void onMouseButton(int button, @NotNull Input.Action action, int mods) {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Pre-loop
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Before creating window.
     */
    public void start() {
    }

    /**
     * After creating GL context and before game looping.
     */
    public void init() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Loop
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void fixedUpdate() {
    }

    @Override
    public void update() {
    }

    @Override
    public void lateUpdate() {
    }

    /**
     * Renders game objects per frame.
     *
     * @param delta the normalized time of interval of two rendering. can be used for linear interpolation
     *              to implement smooth moving.
     */
    public void render(double delta) {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Post-loop
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Disposes a resource, when it is not {@code null}.
     *
     * @param disposable the resource to be disposed.
     */
    protected static void dispose(@Nullable Disposable disposable) {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public void dispose() {
    }
}
