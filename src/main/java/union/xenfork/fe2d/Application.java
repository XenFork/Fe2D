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

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.IntBuffer;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;

/**
 * The application which is the entrypoint of a game.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Application implements Disposable {
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
                    // Sets callbacks
                    glfwSetFramebufferSizeCallback(window, (handle, width, height) -> {
                        Fe2D.graphics.setSize(width, height);
                        onResize(width, height);
                    });

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
                    try (MemoryStack stack = MemoryStack.stackPush()) {
                        IntBuffer pw = stack.callocInt(1);
                        IntBuffer ph = stack.callocInt(1);
                        glfwGetFramebufferSize(window, pw, ph);
                        Fe2D.graphics.setSize(pw.get(0), ph.get(0));
                        onResize(pw.get(0), ph.get(0));
                    }
                    init();

                    // Game loop
                    while (!glfwWindowShouldClose(window)) {
                        glfwPollEvents();
                        update();
                        render();
                        glfwSwapBuffers(window);
                    }
                    dispose();
                } finally {
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
     * On framebuffer resizing.
     *
     * @param width  the new width.
     * @param height the new height.
     */
    public void onResize(int width, int height) {
        glViewport(0, 0, width, height);
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

    /**
     * Updating game objects per frame.
     */
    public void update() {
    }

    /**
     * Renders game objects per frame.
     */
    public void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
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
