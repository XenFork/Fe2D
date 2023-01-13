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

import java.util.HashMap;
import java.util.Map;

/**
 * The simple asset manager.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class AssetManager implements Disposable {
    private final Map<String, Disposable> assetMap = new HashMap<>();

    /**
     * Puts an asset with the given name.
     *
     * @param name  the name of the asset.
     * @param asset the asset.
     * @return the previous asset; or {@code null} if there was no mapping.
     */
    public @Nullable Disposable putAsset(Object name, Disposable asset) {
        return assetMap.put(String.valueOf(name), asset);
    }

    /**
     * Gets an asset with the given name.
     *
     * @param name the name of the asset.
     * @param <T>  the type of the asset.
     * @return the asset; or {@code null} if there is no mapping.
     */
    @SuppressWarnings("unchecked")
    public <T extends Disposable> T getAsset(Object name) {
        return (T) assetMap.get(String.valueOf(name));
    }

    /**
     * Removes the asset with the given name.
     *
     * @param name the name of the asset.
     * @param <T>  the type of the asset.
     * @return the previous asset; or {@code null} if there was no mapping.
     */
    @SuppressWarnings("unchecked")
    public <T extends Disposable> @Nullable T removeAsset(Object name) {
        return (T) assetMap.remove(String.valueOf(name));
    }

    @Override
    public void dispose() {
        assetMap.values().forEach(Disposable::dispose);
        assetMap.clear();
    }
}
