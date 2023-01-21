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

    public Texture texture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public TextureRegion textureRegion() {
        return textureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    public void setTexture(Texture texture, TextureRegion textureRegion) {
        setTexture(texture);
        setTextureRegion(textureRegion);
    }

    public Texture hoverTexture() {
        return hoverTexture;
    }

    public void setHoverTexture(Texture hoverTexture) {
        this.hoverTexture = hoverTexture;
    }

    public TextureRegion hoverTextureRegion() {
        return hoverTextureRegion;
    }

    public void setHoverTextureRegion(TextureRegion hoverTextureRegion) {
        this.hoverTextureRegion = hoverTextureRegion;
    }

    public void setHoverTexture(Texture hoverTexture, TextureRegion hoverTextureRegion) {
        setHoverTexture(hoverTexture);
        setHoverTextureRegion(hoverTextureRegion);
    }
}
