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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import union.xenfork.fe2d.file.FileLoader;
import union.xenfork.fe2d.graphics.Graphics;

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
     * The input.
     */
    public static Input input;
    /**
     * The global timer.
     */
    public static Timer timer;
}
