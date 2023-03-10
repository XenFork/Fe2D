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
    /**
     * The text of this button.
     */
    protected String text;
    /**
     * The text color of this button.
     */
    protected Color textColor = Color.WHITE;
    /**
     * The text font of this button.
     */
    protected Font textFont;
    /**
     * The text pixels height of this button.
     */
    protected float textPixelsHeight = TextRenderer.DEFAULT_PIXELS_HEIGHT;
    /**
     * The text layout of this button.
     */
    protected TextLayout textLayout;
    /**
     * The color of this button.
     */
    protected Color color = Color.WHITE;
    /**
     * The hovering color of this button.
     */
    protected Color hoverColor = Color.WHITE;

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

    /**
     * Gets the text of this button.
     *
     * @return the text of this button.
     */
    public String text() {
        return text;
    }

    /**
     * Sets the text of this button.
     *
     * @param text the text of this button.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the text color of this button.
     *
     * @return the text color of this button.
     */
    public Color textColor() {
        return textColor;
    }

    /**
     * Sets the text color of this button.
     *
     * @param textColor the text color of this button.
     */
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    /**
     * Gets the text font of this button.
     *
     * @return the text font of this button.
     */
    public Font textFont() {
        if (textFont == null) {
            textFont = Fe2D.defaultFont();
        }
        return textFont;
    }

    /**
     * Sets the text font of this button.
     *
     * @param textFont the text font of this button.
     */
    public void setTextFont(Font textFont) {
        this.textFont = textFont;
    }

    /**
     * Gets the text pixels height of this button.
     *
     * @return the text pixels height of this button.
     */
    public float textPixelsHeight() {
        return textPixelsHeight;
    }

    /**
     * Sets the text pixels height of this button.
     *
     * @param textPixelsHeight the text pixels height of this button.
     */
    public void setTextPixelsHeight(float textPixelsHeight) {
        this.textPixelsHeight = textPixelsHeight;
    }

    /**
     * Sets the text font and text pixels height of this button.
     *
     * @param textFont         the text font of this button.
     * @param textPixelsHeight the text pixels height of this button.
     */
    public void setTextFont(Font textFont, float textPixelsHeight) {
        setTextFont(textFont);
        setTextPixelsHeight(textPixelsHeight);
    }

    /**
     * Gets the text layout of this button.
     *
     * @return the text layout of this button.
     */
    public TextLayout textLayout() {
        return textLayout;
    }

    /**
     * Sets the text layout of this button.
     *
     * @param textLayout the text layout of this button.
     */
    public void setTextLayout(TextLayout textLayout) {
        this.textLayout = textLayout;
    }

    /**
     * Gets the color of this button.
     *
     * @return the color of this button.
     */
    public Color color() {
        return color;
    }

    /**
     * Sets the color of this button.
     *
     * @param color the color of this button.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Gets the hover color of this button.
     *
     * @return the hover color of this button.
     */
    public Color hoverColor() {
        return hoverColor;
    }

    /**
     * Sets the hover color of this button.
     *
     * @param hoverColor the hover color of this button.
     */
    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    @Override
    public boolean perform(boolean force) {
        if (force || isCursorHover(Fe2D.input.cursorX(), Fe2D.input.cursorY())) {
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
