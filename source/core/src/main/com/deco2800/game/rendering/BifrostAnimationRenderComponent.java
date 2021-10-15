package com.deco2800.game.rendering;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class BifrostAnimationRenderComponent extends AnimationRenderComponent {
    private float zIndex;

    public BifrostAnimationRenderComponent(TextureAtlas atlas) {
        super(atlas);
        this.zIndex = -Float.MAX_VALUE + 7;
    }

    @Override
    public float getZIndex() {
        // Have this sitting as far back as possible.
        return this.zIndex;
    }
}
