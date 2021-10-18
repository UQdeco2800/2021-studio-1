package com.deco2800.game.entities.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.deco2800.game.services.ServiceLocator;

/**
 * Factory to create particle effects.
 */
public class ParticleFactory {
    /**
     * Return a new particle effect loaded with the given effect data and particle image.
     *
     * @param effectData   effect data file returned from the LibGDX Particle Editor
     * @param textureAtlas texture atlas file path for the particle image
     * @return particle effect
     */
    public static ParticleEffect createParticleEffect(String effectData, String textureAtlas) {
        FileHandle fileHandle = Gdx.files.internal(effectData);
        TextureAtlas atlas = ServiceLocator.getResourceService().getAsset(textureAtlas, TextureAtlas.class);
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(fileHandle, atlas);
        return particleEffect;
    }
}
