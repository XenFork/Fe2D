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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import union.xenfork.fe2d.file.FileContext;

import java.io.IOException;

/**
 * The configuration with a json file.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class JsonConfig extends ManagedConfig<JsonElement> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();
    private final JsonObject jsonObject = new JsonObject();

    @Override
    public <T> void put(String name, T value) {
        if (value instanceof Number number) {
            jsonObject.addProperty(name, number);
        } else if (value instanceof Boolean b) {
            jsonObject.addProperty(name, b);
        } else if (value instanceof String string) {
            jsonObject.addProperty(name, string);
        }
        if (isAutoSave()) {
            save();
        }
    }

    @Override
    public boolean has(String name) {
        return jsonObject.has(name);
    }

    @Override
    public JsonElement get(String name) {
        return jsonObject.get(name);
    }

    @Override
    public JsonElement get(String name, JsonElement defaultValue) {
        if (jsonObject.has(name)) {
            return jsonObject.get(name);
        }
        return defaultValue;
    }

    @Override
    public String getString(String name) {
        return get(name).getAsString();
    }

    @Override
    public boolean getBoolean(String name) {
        return get(name).getAsBoolean();
    }

    @Override
    public byte getByte(String name) {
        return get(name).getAsByte();
    }

    @Override
    public short getShort(String name) {
        return get(name).getAsShort();
    }

    @Override
    public int getInt(String name) {
        return get(name).getAsInt();
    }

    @Override
    public long getLong(String name) {
        return get(name).getAsLong();
    }

    @Override
    public float getFloat(String name) {
        return get(name).getAsFloat();
    }

    @Override
    public double getDouble(String name) {
        return get(name).getAsDouble();
    }

    @Override
    public void load(FileContext file) throws IllegalStateException {
        for (var e : GSON.fromJson(file.loadString(), JsonObject.class).entrySet()) {
            jsonObject.add(e.getKey(), e.getValue());
        }
    }

    @Override
    public void save(FileContext file) throws IllegalStateException {
        try (var writer = file.createWriter()) {
            GSON.toJson(jsonObject, writer);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save the configuration", e);
        }
    }
}
