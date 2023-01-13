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

package union.xenfork.fe2d.gui.screen;

import union.xenfork.fe2d.gui.Drawable;
import union.xenfork.fe2d.gui.GUIElement;
import union.xenfork.fe2d.gui.GUIParentElement;

import java.util.ArrayList;
import java.util.List;

/**
 * The screen that contains children elements and widgets.
 *
 * @author squid233
 * @since 0.1.0
 */
public abstract class Screen implements GUIParentElement, Drawable {
    private final List<GUIElement> children = new ArrayList<>();
    protected int width, height;

    public void init(int width, int height) {
        onResize(width, height);
        init();
    }

    protected void init() {
    }

    public void onResize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(double delta, double cursorX, double cursorY) {
        for (GUIElement element : children()) {
            if (element instanceof Drawable drawable) {
                drawable.render(delta, cursorX, cursorY);
            }
        }
    }

    public void onClose() {
    }

    public void onRemoved() {
    }

    public boolean shouldCloseOnEsc() {
        return true;
    }

    protected <T extends GUIElement> T addElement(T element) {
        children.add(element);
        return element;
    }

    @Override
    public List<GUIElement> children() {
        return children;
    }
}
