package com.deco2800.game.rendering;

import com.badlogic.gdx.graphics.Texture;

public class BackgroundRenderComponent extends TextureRenderComponent {
    /**
     * @param texturePath internal path of static teture to render. Will be scaled to the entity's
     *                    scale.
     */
    public BackgroundRenderComponent(String texturePath) {
        super(texturePath);
    }

    /**
     * @param texture Static texture to render. Will be scaled to the entity's scale.
     */
    public BackgroundRenderComponent(Texture texture) {
        super(texture);
    }

    @Override
    public float getZIndex() {
        // Have this sitting as far back as possible.
        return -1000000f;
    }
}
