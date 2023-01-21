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

import org.jetbrains.annotations.Nullable;
import union.xenfork.fe2d.Fe2D;
import union.xenfork.fe2d.Input;
import union.xenfork.fe2d.Updatable;
import union.xenfork.fe2d.graphics.batch.SpriteBatch;
import union.xenfork.fe2d.graphics.font.TextRenderer;
import union.xenfork.fe2d.gui.Drawable;
import union.xenfork.fe2d.gui.GUIElement;
import union.xenfork.fe2d.gui.GUIParentElement;
import union.xenfork.fe2d.gui.widget.GUIWidget;

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
    private final List<GUIWidget> widgets = new ArrayList<>();
    /**
     * The parent/previous screen that opened this screen.
     */
    protected final @Nullable Screen parent;
    /**
     * The width of this screen.
     */
    protected int width;
    /**
     * The height of this screen.
     */
    protected int height;
    /**
     * The index of the focused widget. It will be reset to -1 if it is &ge; the size of widgets.
     */
    protected int focusIndex = -1;

    /**
     * Creates the screen with the given parent screen.
     *
     * @param parent the parent screen.
     */
    public Screen(@Nullable Screen parent) {
        this.parent = parent;
    }

    /**
     * Initializes this screen with the given size.
     *
     * @param width  the width of this screen.
     * @param height the height of this screen.
     */
    public void init(int width, int height) {
        init();
        onResize(width, height);
    }

    /**
     * Initializes this screen. You can add GUI elements and widgets here.
     * Don't forget to use {@link #addElement(GUIElement)} or {@link #addWidget(GUIWidget)}.
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
        TextRenderer textRenderer = Fe2D.textRenderer();
        SpriteBatch spriteBatch = Fe2D.spriteRenderer();
        boolean textNotDrawing = !textRenderer.isDrawing();
        boolean spriteNotDrawing = !spriteBatch.isDrawing();
        if (spriteNotDrawing) {
            spriteBatch.begin();
        }
        if (textNotDrawing) {
            textRenderer.begin();
        }
        for (GUIElement element : children()) {
            if (element instanceof Drawable drawable) {
                drawable.render(delta, cursorX, cursorY);
            }
        }
        if (spriteNotDrawing) {
            spriteBatch.end();
        }
        if (textNotDrawing) {
            textRenderer.end();
        }
    }

    /**
     * This method will be called when closing this screen with escape key.
     *
     * @see #shouldCloseOnEsc()
     */
    public void onClose() {
        if (Fe2D.game != null) {
            Fe2D.game.openScreen(parent);
        }
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
        children().add(element);
        return element;
    }

    /**
     * Adds a widget to this screen.
     *
     * @param widget the widget to be added.
     * @param <T>    the type of the widget.
     * @return the widget.
     */
    protected <T extends GUIWidget> T addWidget(T widget) {
        widgets().add(widget);
        return addElement(widget);
    }

    /**
     * Gets the focused widget.
     *
     * @return the focused widget.
     */
    protected GUIWidget getFocusedWidget() {
        return widgets().get(focusIndex);
    }

    @Override
    public boolean onKeyPress(int key, int scancode, int mods) {
        if (key == Input.KEY_ESCAPE && shouldCloseOnEsc()) {
            onClose();
            return true;
        }
        if (key == Input.KEY_TAB) {
            if (focusIndex != -1) {
                getFocusedWidget().setFocused(false);
            }
            focusIndex++;
            if (focusIndex >= widgets().size()) {
                focusIndex = -1;
            } else {
                getFocusedWidget().setFocused(true);
            }
            return true;
        }
        if (key == Input.KEY_ENTER) {
            if (focusIndex != -1) {
                return getFocusedWidget().perform(true);
            }
        }
        return GUIParentElement.super.onKeyPress(key, scancode, mods);
    }

    @Override
    public List<GUIElement> children() {
        return children;
    }

    /**
     * Gets the widgets.
     *
     * @return the widgets.
     */
    public List<GUIWidget> widgets() {
        return widgets;
    }
}
