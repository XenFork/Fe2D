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

/**
 * The application launch configurations.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class ApplicationConfig {
    /**
     * Set to {@code true} to use {@link System#out} instead of {@link org.slf4j.Logger SLF4J Logger}. Defaults to {@code false}.
     */
    public boolean useStderr = false;
    /**
     * The application name, which is the default value of the window title. Defaults to {@code "Fork Engine 2D Game"}.
     */
    public String applicationName = "Fork Engine 2D Game";
    /**
     * The initial width of window. Defaults to {@code 800}.
     */
    public int windowWidth = 800;
    /**
     * The initial height of window. Defaults to {@code 600}.
     */
    public int windowHeight = 600;
    /**
     * The window title. This option overrides {@link #applicationName}. Defaults to {@code null}.
     */
    public String windowTitle = null;
    /**
     * Set to {@code true} to use vertical-synchronization, which limits the max frames per second
     * to the refresh rate of your monitor. Defaults to {@code true}.
     */
    public boolean vsync = true;
    /**
     * Set to {@code true} to allow users resize the window. Defaults to {@code true}.
     */
    public boolean resizable = true;
}
