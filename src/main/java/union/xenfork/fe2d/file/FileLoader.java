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

package union.xenfork.fe2d.file;

import union.xenfork.fe2d.util.ResourcePath;

/**
 * The file loader.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class FileLoader {
    private static final FileLoader INSTANCE = new FileLoader();

    private FileLoader() {
    }

    /**
     * Creates an internal file context with the given path.
     *
     * @param path the path.
     * @return the file context which loads files from classpath.
     */
    public FileContext internal(String path) {
        return new InternalFileContext(path);
    }

    /**
     * Creates an internal file context with the given resource path.
     *
     * @param path the path.
     * @return the file context which loads files from classpath.
     */
    public FileContext internal(ResourcePath path) {
        return new InternalFileContext(path.toLocation());
    }

    /**
     * Creates an internal file context with the given resource path.
     *
     * @param path   the path.
     * @param prefix the prefix of the path.
     * @return the file context which loads files from classpath.
     */
    public FileContext internal(ResourcePath path, String prefix) {
        return new InternalFileContext(path.toLocation(prefix));
    }

    /**
     * Gets the instance.
     *
     * @return the instance.
     */
    public static FileLoader getInstance() {
        return INSTANCE;
    }
}
