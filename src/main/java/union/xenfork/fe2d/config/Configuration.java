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

package union.xenfork.fe2d.config;

import union.xenfork.fe2d.file.FileContext;

/**
 * The configuration for {@link union.xenfork.fe2d.Application applications}.
 *
 * @param <T> the type of raw properties.
 * @author squid233
 * @since 0.1.0
 */
public interface Configuration<T> {
    static IllegalStateException notManaged() {
        return new IllegalStateException("This configuration is not managed!");
    }

    <E> void put(String name, E value);

    boolean has(String name);

    T get(String name);

    T get(String name, T defaultValue);

    String getString(String name);

    boolean getBoolean(String name);

    byte getByte(String name);

    short getShort(String name);

    int getInt(String name);

    long getLong(String name);

    float getFloat(String name);

    double getDouble(String name);

    void manage(FileContext file);

    FileContext managedFile();

    void load(FileContext file) throws IllegalStateException;

    default void load() throws IllegalStateException {
        FileContext file = managedFile();
        if (file == null) {
            throw notManaged();
        }
        load(file);
    }

    void save(FileContext file) throws IllegalStateException;

    default void save() throws IllegalStateException {
        FileContext file = managedFile();
        if (file == null) {
            throw notManaged();
        }
        save(file);
    }

    void setAutoSave(boolean autoSave);

    boolean isAutoSave();
}
