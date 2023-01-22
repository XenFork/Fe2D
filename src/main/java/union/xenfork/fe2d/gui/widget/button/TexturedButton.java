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
import union.xenfork.fe2d.graphics.batch.SpriteBatch;
import union.xenfork.fe2d.graphics.texture.Texture;
import union.xenfork.fe2d.graphics.texture.TextureRegion;

/**
 * The GUI button with a texture.
 *
 * @author squid233
 * @since 0.1.0
 */
public class TexturedButton extends GUIButton {
    protected Texture texture;
    protected TextureRegion textureRegion;
    protected Texture hoverTexture;
    protected TextureRegion hoverTextureRegion;

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
    public TexturedButton(String text, float x, float y, int width, int height, PressAction pressAction, HoverAction hoverAction) {
        super(text, x, y, width, height, pressAction, hoverAction);
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
    public TexturedButton(String text, float x, float y, int width, int height, PressAction pressAction) {
        super(text, x, y, width, height, pressAction);
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
    public TexturedButton(String text, float x, float y, int width, int height) {
        super(text, x, y, width, height);
    }

    /**
     * Renders the given texture.
     *
     * @param hovered       {@code true} if the cursor is hovering on this button.
     * @param texture       the texture.
     * @param textureRegion the texture region.
     */
    protected void renderTexture(boolean hovered, Texture texture, TextureRegion textureRegion) {
        SpriteBatch batch = Fe2D.spriteRenderer();
        boolean notDrawing = !batch.isDrawing();
        if (notDrawing) {
            batch.begin();
        }
        int currColor = batch.spriteColor();
        batch.setSpriteColor(hovered ? hoverColor() : color());
        batch.draw(texture, x(), y(), width(), height(), textureRegion);
        batch.setSpriteColor(currColor);
        if (notDrawing) {
            batch.end();
        }
    }

    @Override
    public void render(double delta, double cursorX, double cursorY) {
        boolean hovered = isCursorHover(cursorX, cursorY);
        if (hovered) {
            if (hoverTexture() != null) {
                TextureRegion region = hoverTextureRegion();
                if (region == null) {
                    setHoverTextureRegion(region = TextureRegion.full(hoverTexture()));
                }
                renderTexture(true, hoverTexture(), region);
            }
        } else if (texture() != null) {
            TextureRegion region = textureRegion();
            if (region == null) {
                setTextureRegion(region = TextureRegion.full(texture()));
            }
            renderTexture(false, texture(), region);
        }
        renderText();
    }

    /**
     * Gets the texture of this button.
     *
     * @return the texture of this button.
     */
    public Texture texture() {
        return texture;
    }

    /**
     * Sets the texture of this button.
     *
     * @param texture the texture of this button.
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    /**
     * Gets the texture region of this button.
     *
     * @return the texture region of this button.
     */
    public TextureRegion textureRegion() {
        return textureRegion;
    }

    /**
     * Sets the texture region of this button.
     *
     * @param textureRegion the texture region of this button.
     */
    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    /**
     * Sets the texture and texture region of this button.
     *
     * @param texture       the texture of this button.
     * @param textureRegion the texture and texture region of this button.
     */
    public void setTexture(Texture texture, TextureRegion textureRegion) {
        setTexture(texture);
        setTextureRegion(textureRegion);
    }

    /**
     * Gets the hover texture of this button.
     *
     * @return the hover texture of this button.
     */
    public Texture hoverTexture() {
        return hoverTexture;
    }

    /**
     * Sets the hover texture of this button.
     *
     * @param hoverTexture the hover texture of this button.
     */
    public void setHoverTexture(Texture hoverTexture) {
        this.hoverTexture = hoverTexture;
    }

    /**
     * Gets the hover texture region of this button.
     *
     * @return the hover texture region of this button.
     */
    public TextureRegion hoverTextureRegion() {
        return hoverTextureRegion;
    }

    /**
     * Sets the hover texture region of this button.
     *
     * @param hoverTextureRegion the hover texture region of this button.
     */
    public void setHoverTextureRegion(TextureRegion hoverTextureRegion) {
        this.hoverTextureRegion = hoverTextureRegion;
    }

    /**
     * Sets the hover texture and hover texture region of this button.
     *
     * @param hoverTexture       the hover texture of this button.
     * @param hoverTextureRegion the hover texture region of this button.
     */
    public void setHoverTexture(Texture hoverTexture, TextureRegion hoverTextureRegion) {
        setHoverTexture(hoverTexture);
        setHoverTextureRegion(hoverTextureRegion);
    }
}
