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

import union.xenfork.fe2d.Input;
import union.xenfork.fe2d.Updatable;
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
public abstract class Screen implements GUIParentElement, Drawable, Updatable {
    private final List<GUIElement> children = new ArrayList<>();
    /**
     * The width of this screen.
     */
    protected int width;
    /**
     * The height of this screen.
     */
    protected int height;

    /**
     * Initializes this screen with the given size.
     *
     * @param width  the width of this screen.
     * @param height the height of this screen.
     */
    public void init(int width, int height) {
        onResize(width, height);
        init();
    }

    /**
     * Initializes this screen.
     */
    protected void init() {
    }

    /**
     * This method will be called on resizing.
     *
     * @param width  the new width.
     * @param height the new height.
     */
    public void onResize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void fixedUpdate() {
        for (GUIElement element : children()) {
            if (element instanceof Updatable updatable) {
                updatable.fixedUpdate();
            }
        }
    }

    @Override
    public void update() {
        for (GUIElement element : children()) {
            if (element instanceof Updatable updatable) {
                updatable.update();
            }
        }
    }

    @Override
    public void lateUpdate() {
        for (GUIElement element : children()) {
            if (element instanceof Updatable updatable) {
                updatable.lateUpdate();
            }
        }
    }

    @Override
    public void render(double delta, double cursorX, double cursorY) {
        for (GUIElement element : children()) {
            if (element instanceof Drawable drawable) {
                drawable.render(delta, cursorX, cursorY);
            }
        }
    }

    /**
     * This method will be called when closing this screen with escape key.
     *
     * @see #shouldCloseOnEsc()
     */
    public void onClose() {
    }

    /**
     * This method will be called when a new screen is opened.
     */
    public void onRemove() {
    }

    /**
     * Returns {@code true} if this screen should be closed on pressing escape key; {@code false} otherwise.
     *
     * @return {@code true} if this screen should be closed on pressing escape key; {@code false} otherwise.
     * @see #onClose()
     */
    public boolean shouldCloseOnEsc() {
        return true;
    }

    /**
     * Adds an element to this screen.
     *
     * @param element the element to be added.
     * @param <T>     the type of the element.
     * @return the element.
     */
    protected <T extends GUIElement> T addElement(T element) {
        children.add(element);
        return element;
    }

    @Override
    public boolean onKeyPress(int key, int scancode, int mods) {
        if (key == Input.KEY_ESCAPE && shouldCloseOnEsc()) {
            onClose();
            return true;
        }
        return GUIParentElement.super.onKeyPress(key, scancode, mods);
    }

    @Override
    public List<GUIElement> children() {
        return children;
    }
}
