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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

/**
 * The binary data tags.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class BinaryTags extends BinaryData {
    private final Map<String, BinaryData> map;

    BinaryTags(Map<String, BinaryData> map) {
        super(TYPE_TAGS, map);
        this.map = map;
    }

    BinaryTags() {
        this(new HashMap<>(16));
    }

    @Override
    public void write(ObjectOutput out) throws IOException {
        out.writeByte(type());
        out.writeInt(size());
        for (var e : data().entrySet()) {
            out.writeUTF(e.getKey());
            e.getValue().write(out);
        }
    }

    public void set(String name, BinaryData data) {
        map.put(name, data);
    }

    public boolean has(String name) {
        return map.containsKey(name);
    }

    public BinaryData get(String name) {
        return map.get(name);
    }

    public int size() {
        return map.size();
    }

    public Map<String, BinaryData> data() {
        return map;
    }

    @Override
    public @NotNull BinaryTags asTagsSafe() {
        return this;
    }

    @Override
    public boolean isTags() {
        return true;
    }

    private static void appendString(StringBuilder sb, Map.Entry<String, BinaryData> e) {
        sb.append(e.getKey()).append(": ").append(e.getValue());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(512).append('{');
        var iterator = data().entrySet().iterator();
        if (iterator.hasNext()) {
            var e = iterator.next();
            appendString(sb, e);
            while (iterator.hasNext()) {
                e = iterator.next();
                appendString(sb.append(", "), e);
            }
        }
        return sb.append('}').toString();
    }
}
