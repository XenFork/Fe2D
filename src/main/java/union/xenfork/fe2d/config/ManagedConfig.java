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
 * The configuration with managed file.
 *
 * @param <T> the type of raw properties.
 * @author squid233
 * @since 0.1.0
 */
public abstract class ManagedConfig<T> implements Configuration<T> {
    private FileContext managedFile = null;
    private boolean autoSave = false;

    @Override
    public void manage(FileContext file) {
        this.managedFile = file;
    }

    @Override
    public FileContext managedFile() {
        return managedFile;
    }

    @Override
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    @Override
    public boolean isAutoSave() {
        return autoSave;
    }
}
