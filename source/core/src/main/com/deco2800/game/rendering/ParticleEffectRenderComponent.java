package com.deco2800.game.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Fixture;
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
    private ParticleEffect particleEffect;
    private short targetLayer;
    private HitboxComponent hitboxComponent;
    private boolean toStart;
    private boolean running;

    public ParticleEffectRenderComponent(FileHandle effectData, TextureAtlas textureAtlas,
                                         short targetLayer) {
        particleEffect = new ParticleEffect();
        particleEffect.load(effectData, textureAtlas);
        particleEffect.scaleEffect(0.02f);
        this.targetLayer = targetLayer;
        toStart = false;
        running = false;
    }

    @Override
    public void create() {
        ServiceLocator.getRenderService().register(this);
        entity.getEvents().addListener("collisionStart", this::onCollisionStart);
        this.hitboxComponent = entity.getComponent(HitboxComponent.class);
    }

    private void onCollisionStart(Fixture me, Fixture other) {
        if (hitboxComponent.getFixture() != me) {
            // Not triggered by hitbox, ignore
            return;
        }

        logger.debug("Registered collision with me");

        if (!PhysicsLayer.contains(targetLayer, other.getFilterData().categoryBits)) {
            // Doesn't match our target layer, ignore
            return;
        }

        logger.debug("Registered collision with target layer");

        // Start the particle effect
        //particleEffect.start();
        toStart = true;
    }

    @Override
    public void earlyUpdate() {
        if (toStart) {
            this.start();
            toStart = false;
        }
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
    public void render(SpriteBatch batch) {
        particleEffect.setPosition(entity.getPosition().x, entity.getPosition().y);
        if (running)
            particleEffect.update(Gdx.graphics.getDeltaTime());
        particleEffect.draw(batch);
    }

    /**
     * Start the particle effect.
     */
    public void start() {
        logger.debug("Starting particle effect.");
        particleEffect.start();
        running = true;
    }
}
