package com.deco2800.game.rendering;

import com.badlogic.gdx.graphics.Texture;

public class BackgroundRenderComponent extends TextureRenderComponent {
    private float zIndex;

    /**
     * @param texturePath internal path of static texture to render. Will be scaled to the entity's
     *                    scale.
     */
    public BackgroundRenderComponent(String texturePath) {
        super(texturePath);
        this.zIndex = -Float.MAX_VALUE;
    }

    /**
     * @param texture Static texture to render. Will be scaled to the entity's scale.
     */
    public BackgroundRenderComponent(Texture texture) {
        super(texture);
        this.zIndex = -Float.MAX_VALUE;
    }

    @Override
    public float getZIndex() {
        // Have this sitting as far back as possible.
        return this.zIndex;
    }
}
