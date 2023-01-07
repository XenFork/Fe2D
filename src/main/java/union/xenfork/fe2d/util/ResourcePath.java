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
    private final String namespace, body;

    /**
     * Creates a resource path with the given string that is separated with colon.
     * <p>
     * When colon not found, {@link #DEFAULT_NAMESPACE default namespace} is used.
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
        return namespace + '/' + body;
    }

    /**
     * Converts to the file location with the given prefix.
     *
     * @param prefix the prefix of the file location.
     * @return the location string.
     */
    public String toLocation(String prefix) {
        return prefix + '/' + namespace + '/' + body;
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
