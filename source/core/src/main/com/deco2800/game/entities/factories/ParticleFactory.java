package com.deco2800.game.entities.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.rendering.ParticleEffectRenderComponent;
import com.deco2800.game.services.ServiceLocator;

/**
 * Factory to create particle emitters.
 */
public class ParticleFactory {
    /**
     * Return an entity having the 'spread' particle component.
     *
     * @return entity having a spread particle effect component.
     */
    public static Entity createSpread() {
        //effectData = ServiceLocator.getResourceService().getAsset(effectDataPath,
        //        FileHandle.class);
        // Resource service does not deal with File Handles, so here we are.
        FileHandle effectData = Gdx.files.internal("particles/rainbow_spread_2");
        TextureAtlas particleImage = ServiceLocator.getResourceService()
                .getAsset("particles/particles.atlas", TextureAtlas.class);

       return new Entity()
                .addComponent(new ParticleEffectRenderComponent(effectData, particleImage));
                        //PhysicsLayer.PLAYER));
    }
}
