package com.deco2800.game.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.physics.BodyUserData;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Render a particle effect.
 */
public class ParticleEffectRenderComponent extends RenderComponent {
    private static final Logger logger = LoggerFactory.getLogger(ParticleEffectRenderComponent.class);
    private final ParticleEffect particleEffect;
    private final short targetLayer;
    private HitboxComponent hitboxComponent;
    private float y;

    /**
     * Play the given particle effect when an entity from the targetLayer collides with this entity.
     * <p>
     * This particle effect is centred on the x coordinate of the entity it is applied to. Its y coordinate is given
     * by the y coordinate of the colliding entity.
     * <p>
     * Whether it plays only once on collision or continuously is given by the information in effectData.
     *
     * @param particleEffect particle effect to play
     * @param targetLayer    PhysicsLayer to play on collision with
     */
    public ParticleEffectRenderComponent(ParticleEffect particleEffect, short targetLayer) {
        this.particleEffect = particleEffect;
        particleEffect.scaleEffect(0.02f);
        this.targetLayer = targetLayer;
        this.y = 0;
    }

    /**
     * Create the component.
     */
    @Override
    public void create() {
        super.create();
        entity.getEvents().addListener("collisionStart", this::onCollisionStart);
        this.hitboxComponent = entity.getComponent(HitboxComponent.class);
    }

    /**
     * Start the particle effect if me == this hitbox and other is in the targetLayer.
     *
     * @param me    first fixture
     * @param other other fixture
     */
    private void onCollisionStart(Fixture me, Fixture other) {
        if (hitboxComponent.getFixture() != me) {
            // Not triggered by hitbox, ignore
            return;
        }

        if (!PhysicsLayer.contains(targetLayer, other.getFilterData().categoryBits)) {
            // Doesn't match our target layer, ignore
            return;
        }

        logger.debug("Registered collision with target layer, starting particle effect");
        this.y = ((BodyUserData) other.getBody().getUserData()).entity.getPosition().y;
        start();
    }

    /**
     * Draw the renderable. Should be called only by the renderer, not manually.
     *
     * @param batch Batch to render to.
     */
    @Override
    protected void draw(SpriteBatch batch) {
        particleEffect.setPosition(entity.getPosition().x, this.y);
        particleEffect.update(Gdx.graphics.getDeltaTime());
        particleEffect.draw(batch);
    }

    /**
     * Start the particle effect.
     */
    public void start() {
        logger.debug("Starting particle effect.");
        particleEffect.start();
    }

    @Override
    public void dispose() {
        super.dispose();
        particleEffect.dispose();
    }
}
