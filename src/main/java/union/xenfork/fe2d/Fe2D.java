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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import union.xenfork.fe2d.file.FileLoader;
import union.xenfork.fe2d.graphics.Graphics;
import union.xenfork.fe2d.graphics.batch.SpriteBatch;
import union.xenfork.fe2d.graphics.font.Font;
import union.xenfork.fe2d.graphics.font.TextRenderer;
import union.xenfork.fe2d.graphics.font.Unifont;

/**
 * The global objects of Fork Engine 2D.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class Fe2D {
    /**
     * The file loader.
     */
    public static final FileLoader files = FileLoader.getInstance();
    /**
     * The graphics mode.
     */
    public static final Graphics graphics = Graphics.getInstance();
    /**
     * The default asset manager. The assets in this manager are auto-disposed.
     */
    public static final AssetManager assets = new AssetManager();
    /**
     * The global logger for internal logging.
     */
    public static final Logger logger = LoggerFactory.getLogger("Fork Engine 2D");
    /**
     * The current application instance.
     */
    public static Application application;
    /**
     * The current {@link #application} instance as {@link Game}.
     */
    public static @Nullable Game game;
    /**
     * The input.
     */
    public static Input input;
    /**
     * The global timer.
     */
    public static Timer timer;
    private static TextRenderer textRenderer;
    private static Font defaultFont;
    private static Unifont unifont;
    private static SpriteBatch spriteBatch;
    private static SpriteBatch spriteRenderer;

    /**
     * Returns {@code true} if the text renderer is created.
     *
     * @return {@code true} if the text renderer is created.
     */
    public static boolean hasTextRenderer() {
        return textRenderer != null;
    }

    /**
     * Returns {@code true} if the sprite batch is created.
     *
     * @return {@code true} if the sprite batch is created.
     */
    public static boolean hasSpriteBatch() {
        return spriteBatch != null;
    }

    /**
     * Gets the text renderer, or creates a new one if it is not created.
     *
     * @return the text renderer.
     */
    public static TextRenderer textRenderer() {
        if (textRenderer == null) {
            textRenderer = TextRenderer.getInstance();
        }
        return textRenderer;
    }

    /**
     * Sets the current sprite batch.
     *
     * @param batch the sprite batch.
     */
    public static void setSpriteRenderer(@Nullable SpriteBatch batch) {
        spriteRenderer = batch;
    }

    /**
     * Gets the current sprite batch, or gets the default one if it is no sprite batch was set.
     *
     * @return the sprite batch.
     */
    public static SpriteBatch spriteRenderer() {
        if (spriteRenderer != null) {
            return spriteRenderer;
        }
        if (spriteBatch == null) {
            spriteBatch = new SpriteBatch();
            spriteRenderer = spriteBatch;
        }
        return spriteBatch;
    }

    /**
     * Sets the default font.
     *
     * @param defaultFont the default font.
     */
    public static void setDefaultFont(Font defaultFont) {
        Fe2D.defaultFont = defaultFont;
    }

    /**
     * Gets the default font, or creates Unifont if it is not created.
     *
     * @return the default font.
     */
    public static Font defaultFont() {
        if (defaultFont != null) {
            return defaultFont;
        }
        if (unifont == null) {
            unifont = Unifont.create();
            defaultFont = unifont;
        }
        return unifont;
    }

    /**
     * Disposes the global resources.
     */
    public static void dispose() {
        assets.dispose();
        if (textRenderer != null) {
            textRenderer.dispose();
        }
        if (unifont != null) {
            unifont.dispose();
        }
        if (spriteBatch != null) {
            spriteBatch.dispose();
        }
    }
}
