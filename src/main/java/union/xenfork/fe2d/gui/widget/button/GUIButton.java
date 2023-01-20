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

package union.xenfork.fe2d.gui.widget.button;

import union.xenfork.fe2d.Fe2D;
import union.xenfork.fe2d.Updatable;
import union.xenfork.fe2d.graphics.Color;
import union.xenfork.fe2d.graphics.font.Font;
import union.xenfork.fe2d.graphics.font.TextRenderer;
import union.xenfork.fe2d.gui.layout.Alignment;
import union.xenfork.fe2d.gui.layout.TextLayout;
import union.xenfork.fe2d.gui.widget.GUIWidget;

/**
 * The GUI button.
 * <p>
 * The default button is not drawable, and you need to use the subclasses, such as {@link RectButton} or {@link TexturedButton}.
 *
 * @author squid233
 * @since 0.1.0
 */
public abstract class GUIButton extends GUIWidget implements Updatable {
    private static final PressAction DEFAULT_PRESS_ACTION = button -> {
    };
    private static final HoverAction DEFAULT_HOVER_ACTION = (button, cursorX, cursorY) -> {
    };
    /**
     * The width of this button.
     */
    protected int width;
    /**
     * The height of this button.
     */
    protected int height;
    /**
     * The action to be performed on pressing this button.
     */
    protected PressAction pressAction;
    /**
     * The action to be performed on hovering on this button.
     */
    protected HoverAction hoverAction;
    protected String text;
    protected Color textColor = Color.WHITE;
    protected Font textFont;
    protected float textPixelsHeight = TextRenderer.DEFAULT_PIXELS_HEIGHT;
    protected TextLayout textLayout;

    /**
     * Creates a GUI button with the given text, position, size and actions.
     *
     * @param text        the initial text.
     * @param x           the initial position x.
     * @param y           the initial position y.
     * @param width       the initial width.
     * @param height      the initial height.
     * @param pressAction the action to be performed on pressing this button.
     * @param hoverAction the action to be performed on hovering this button.
     */
    public GUIButton(String text, float x, float y, int width, int height, PressAction pressAction, HoverAction hoverAction) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pressAction = pressAction;
        this.hoverAction = hoverAction;
    }

    /**
     * Creates a GUI button with the given text, position, size and action.
     *
     * @param text        the initial text.
     * @param x           the initial position x.
     * @param y           the initial position y.
     * @param width       the initial width.
     * @param height      the initial height.
     * @param pressAction the action to be performed on pressing this button.
     */
    public GUIButton(String text, float x, float y, int width, int height, PressAction pressAction) {
        this(text, x, y, width, height, pressAction, DEFAULT_HOVER_ACTION);
    }

    /**
     * Creates a GUI button with the given text, position and size.
     *
     * @param text   the initial text.
     * @param x      the initial position x.
     * @param y      the initial position y.
     * @param width  the initial width.
     * @param height the initial height.
     */
    public GUIButton(String text, float x, float y, int width, int height) {
        this(text, x, y, width, height, DEFAULT_PRESS_ACTION);
    }

    /**
     * The press action to be performed.
     *
     * @author squid233
     * @since 0.1.0
     */
    @FunctionalInterface
    public interface PressAction {
        /**
         * Performs the action.
         *
         * @param button the button that was pressed.
         */
        void onPress(GUIButton button);
    }

    @FunctionalInterface
    public interface HoverAction {
        void onHover(GUIButton button, double cursorX, double cursorY);
    }

    /**
     * Renders the text of this button.
     */
    protected void renderText() {
        if (text() == null) {
            return;
        }
        TextRenderer textRenderer = Fe2D.textRenderer();
        boolean notDrawing = !textRenderer.isDrawing();
        if (notDrawing) {
            textRenderer.begin();
        }
        int currColor = textRenderer.textColor();
        textRenderer.setTextColor(textColor);
        float x = x();
        float y = y();
        float scale = textFont().getScale(textPixelsHeight());
        Alignment.V verticalAlign;
        Alignment.H horizontalAlign;
        if (textLayout() != null) {
            verticalAlign = textLayout().verticalAlign();
            horizontalAlign = textLayout().horizontalAlign();
        } else {
            verticalAlign = Alignment.V.CENTER;
            horizontalAlign = Alignment.H.CENTER;
        }
        x = verticalAlign.getTextPositionX(x, scale * textFont().getTextWidth(text()), width());
        y = horizontalAlign.getTextPositionY(y, scale * textFont().getTextHeight(text()), height());
        y -= scale * textFont().getDescent();
        textRenderer.draw(textFont(),
            text(),
            x, y,
            verticalAlign,
            textPixelsHeight());
        textRenderer.setTextColor(currColor);
        if (notDrawing) {
            textRenderer.end();
        }
    }

    public String text() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color textColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public Font textFont() {
        if (textFont == null) {
            textFont = Fe2D.defaultFont();
        }
        return textFont;
    }

    public void setTextFont(Font textFont) {
        this.textFont = textFont;
    }

    public float textPixelsHeight() {
        return textPixelsHeight;
    }

    public void setTextPixelsHeight(float textPixelsHeight) {
        this.textPixelsHeight = textPixelsHeight;
    }

    public TextLayout textLayout() {
        return textLayout;
    }

    public void setTextLayout(TextLayout textLayout) {
        this.textLayout = textLayout;
    }

    @Override
    public boolean perform() {
        if (isCursorHover(Fe2D.input.cursorX(), Fe2D.input.cursorY())) {
            pressAction.onPress(this);
            return true;
        }
        return false;
    }

    @Override
    public void fixedUpdate() {
    }

    @Override
    public void update() {
        if (isCursorHover(Fe2D.input.cursorX(), Fe2D.input.cursorY())) {
            hoverAction.onHover(this, Fe2D.input.cursorX(), Fe2D.input.cursorY());
        }
    }

    @Override
    public void lateUpdate() {
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }
}
