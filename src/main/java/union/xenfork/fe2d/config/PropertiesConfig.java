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

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 * The configuration with a properties file.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class PropertiesConfig extends ManagedConfig<String> {
    private final Properties properties = new Properties(16);

    @Override
    public void put(String name, Object value) {
        properties.put(name, String.valueOf(value));
        if (isAutoSave()) {
            save();
        }
    }

    @Override
    public boolean has(String name) {
        return properties.containsKey(name);
    }

    @Override
    public String get(String name) {
        return properties.getProperty(name);
    }

    @Override
    public String get(String name, String defaultValue) {
        return properties.getProperty(name, defaultValue);
    }

    @Override
    public String getString(String name) {
        return properties.getProperty(name);
    }

    @Override
    public boolean getBoolean(String name) {
        return Boolean.parseBoolean(getString(name));
    }

    @Override
    public byte getByte(String name) {
        return Byte.parseByte(getString(name));
    }

    @Override
    public short getShort(String name) {
        return Short.parseShort(getString(name));
    }

    @Override
    public int getInt(String name) {
        return Integer.parseInt(getString(name));
    }

    @Override
    public long getLong(String name) {
        return Long.parseLong(getString(name));
    }

    @Override
    public float getFloat(String name) {
        return Float.parseFloat(getString(name));
    }

    @Override
    public double getDouble(String name) {
        return Double.parseDouble(getString(name));
    }

    @Override
    public void load(FileContext file) {
        try (var reader = new StringReader(file.loadString())) {
            properties.load(reader);
        } catch (IOException | IllegalStateException e) {
            throw new IllegalStateException("Failed to load the configuration", e);
        }
    }

    @Override
    public void save(FileContext file) throws IllegalStateException {
        try (var writer = file.createWriter()) {
            properties.store(writer, null);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save the configuration", e);
        }
    }
}
