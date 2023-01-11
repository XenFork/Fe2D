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

package union.xenfork.fe2d.util;

import java.util.Objects;

/**
 * The resource path that contains a namespace and body.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class ResourcePath {
    /**
     * The default namespace "{@code _fe2d}".
     */
    public static final String DEFAULT_NAMESPACE = "_fe2d";
    /**
     * The prefix of location of asset files.
     */
    public static final String ASSETS = "assets";
    /**
     * The prefix of location of data files.
     */
    public static final String DATA = "data";
    private final String namespace, body;

    /**
     * Creates a resource path with the given string that is separated with colon.
     * <p>
     * When colon not found, {@value #DEFAULT_NAMESPACE} is used.
     *
     * @param path the path string, separated with '{@code :}'.
     */
    public ResourcePath(String path) {
        String[] arr = path.split(":", 2);
        switch (arr.length) {
            case 0 -> {
                namespace = DEFAULT_NAMESPACE;
                body = "";
            }
            case 1 -> {
                namespace = DEFAULT_NAMESPACE;
                body = arr[0];
            }
            default -> {
                namespace = arr[0];
                body = arr[1];
            }
        }
    }

    /**
     * Creates a resource path.
     *
     * @param namespace the namespace of the path.
     * @param body      the body of the path.
     */
    public ResourcePath(String namespace, String body) {
        this.namespace = namespace;
        this.body = body;
    }

    /**
     * Converts to the file location.
     *
     * @param namespace the namespace.
     * @param body      the body.
     * @return the location string.
     */
    public static String toLocation(String namespace, String body) {
        return namespace + '/' + body;
    }

    /**
     * Converts to the file location with the given prefix.
     *
     * @param namespace the namespace.
     * @param body      the body.
     * @param prefix    the prefix of the file location.
     * @return the location string.
     */
    public static String toLocation(String namespace, String body, String prefix) {
        return prefix + '/' + toLocation(namespace, body);
    }

    /**
     * Creates the location string for assets.
     *
     * @param namespace the namespace.
     * @param body      the body.
     * @return the location string.
     */
    public static String assets(String namespace, String body) {
        return toLocation(namespace, body, ASSETS);
    }

    /**
     * Creates the location string for assets.
     *
     * @param path the path string, separated with '{@code :}'.
     * @return the location string.
     */
    public static String assets(String path) {
        return new ResourcePath(path).toLocation(ASSETS);
    }

    /**
     * Creates the location string for data.
     *
     * @param namespace the namespace.
     * @param body      the body.
     * @return the location string.
     */
    public static String data(String namespace, String body) {
        return toLocation(namespace, body, DATA);
    }

    /**
     * Creates the location string for data.
     *
     * @param path the path string, separated with '{@code :}'.
     * @return the location string.
     */
    public static String data(String path) {
        return new ResourcePath(path).toLocation(DATA);
    }

    /**
     * Gets the namespace.
     *
     * @return the namespace.
     */
    public String namespace() {
        return namespace;
    }

    /**
     * Gets the body.
     *
     * @return the body.
     */
    public String body() {
        return body;
    }

    /**
     * Converts to the file location.
     *
     * @return the location string.
     */
    public String toLocation() {
        return toLocation(namespace, body);
    }

    /**
     * Converts to the file location with the given prefix.
     *
     * @param prefix the prefix of the file location.
     * @return the location string.
     */
    public String toLocation(String prefix) {
        return toLocation(namespace, body, prefix);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourcePath that = (ResourcePath) o;
        return Objects.equals(namespace, that.namespace) && Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, body);
    }

    @Override
    public String toString() {
        return namespace + ':' + body;
    }
}
