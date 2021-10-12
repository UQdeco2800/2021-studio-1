package com.deco2800.game.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/** Render a particle effect. */
public class ParticleEffectRenderComponent extends RenderComponent {
    private ParticleEffect particleEffect;

    public ParticleEffectRenderComponent(FileHandle effectData,TextureAtlas textureAtlas) {
        particleEffect = new ParticleEffect();
        particleEffect.load(effectData, textureAtlas);
        particleEffect.scaleEffect(0.02f);
    }

    /**
     * Draw the renderable. Should be called only by the renderer, not manually.
     *
     * @param batch Batch to render to.
     */
    @Override
    protected void draw(SpriteBatch batch) {
        particleEffect.draw(batch);
    }

    @Override
    public void render (SpriteBatch batch) {
        particleEffect.setPosition(entity.getPosition().x, entity.getPosition().y);
        particleEffect.update(Gdx.graphics.getDeltaTime());
        particleEffect.draw(batch);
    }

    /**
     * Start the particle effect.
     */
    public void start() {
        particleEffect.start();
    }
}
