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
    /**
     * Creates an {@link IllegalStateException} for non-managed configuration.
     *
     * @return the exception.
     */
    static IllegalStateException notManaged() {
        return new IllegalStateException("This configuration is not managed!");
    }

    /**
     * Sets the value of the given property (and {@link #setAutoSave(boolean) saves} this configuration).
     *
     * @param name  the name of the property.
     * @param value the value.
     */
    void set(String name, Object value);

    /**
     * Returns {@code true} if the given property is present.
     *
     * @param name the name of the property.
     * @return {@code true} if the given property is present; {@code false} otherwise.
     */
    boolean has(String name);

    /**
     * Gets the value of the given property.
     *
     * @param name the name of the property.
     * @return the value of the property.
     */
    T get(String name);

    /**
     * Gets the value of the given property.
     *
     * @param name         the name of the property.
     * @param defaultValue the default value of the property.
     * @return the value of the property.
     */
    T get(String name, T defaultValue);

    String getString(String name);

    boolean getBoolean(String name);

    byte getByte(String name);

    short getShort(String name);

    int getInt(String name);

    long getLong(String name);

    float getFloat(String name);

    double getDouble(String name);

    /**
     * Manages a file context, so we don't need to pass a file when loading or saving.
     *
     * @param file the file context.
     * @see #managedFile()
     */
    void manage(FileContext file);

    /**
     * Gets the managed file context.
     *
     * @return the managed file context.
     * @see #manage(FileContext)
     */
    FileContext managedFile();

    /**
     * Loads from the given file context.
     *
     * @param file the file context.
     * @throws IllegalStateException if failed to load the file.
     */
    void load(FileContext file) throws IllegalStateException;

    /**
     * Loads from the managed file context.
     *
     * @throws IllegalStateException if failed to load the file.
     * @see #managedFile()
     */
    default void load() throws IllegalStateException {
        FileContext file = managedFile();
        if (file == null) {
            throw notManaged();
        }
        load(file);
    }

    /**
     * Saves this configuration to the given file context.
     *
     * @param file the file context.
     * @throws IllegalStateException if the file cannot be written, or failed to create the writer.
     */
    void save(FileContext file) throws IllegalStateException;

    /**
     * Saves this configuration to the managed file context.
     *
     * @throws IllegalStateException if the file cannot be written, or failed to create the writer.
     * @see #managedFile()
     */
    default void save() throws IllegalStateException {
        FileContext file = managedFile();
        if (file == null) {
            throw notManaged();
        }
        save(file);
    }

    /**
     * Sets {@code true} to auto-save when setting properties.
     *
     * @param autoSave auto-save
     */
    void setAutoSave(boolean autoSave);

    /**
     * Returns {@code true} if this configuration auto-saves when setting properties.
     *
     * @return {@code true} if this configuration auto-saves when setting properties.
     */
    boolean isAutoSave();
}
