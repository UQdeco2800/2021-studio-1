package com.deco2800.game.components.powerups;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.deco2800.game.components.player.PlayerActions;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.ProjectileFactory;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.utils.math.Vector2Utils;

/**
 * Controls the behaviour for the spear.
 * The spear may be thrown three times and replenishes on pick up.
 */
public class SpearPowerUpComponent extends PowerUpComponent {
    private Entity spear;
    private Vector2 spearDirection;

    // Update flags to check
    private boolean active;
    private int thrown;

    /**
     * Called when a spear power up is collected, sets the number of times it
     * has been thrown to 0 and enables this component
     */
    public void obtainSpear() {
        thrown = 0;
        enabled = true;
    }

    /**
     * Reset the number of times the spear has been thrown
     */
    public void reset() {
        thrown = 0;
    }

    @Override
    public void create() {
        setEnabled(false);
        spear = null;
        reset();
    }

    /**
     * If a spear exists, delete it if it has stopped or fallen below the
     * world and disable this component  if it has been thrown three times
     */
    @Override
    public void earlyUpdate() {
        if (spear != null) {
            Body spearBod = spear.getComponent(PhysicsComponent.class).getBody();
            // If after flying, the spear stops or goes below y = 0, deactivate and reset
            if ((active && spearBod.getLinearVelocity().isZero()) || spear.getCenterPosition().y < 0) {
                active = false;
                spear.getComponent(AnimationRenderComponent.class).stopAnimation();
                spear.getEvents().trigger("dispose");

                // Disposes the spear after three throws
                if (thrown == 3) {
                    reset();
                    setEnabled(false);
                }
            }
        }
    }

    /**
     * Continue applying a force to the spear if it has been created
     */
    @Override
    public void update() {
        if (active) {
            if (spearDirection.hasSameDirection(Vector2Utils.RIGHT)) {
                spear.getComponent(PhysicsComponent.class).getBody().applyLinearImpulse(
                        new Vector2(5f, 0f),
                        spear.getComponent(PhysicsComponent.class).getBody().getWorldCenter(),
                        true);
            } else {
                spear.getComponent(PhysicsComponent.class).getBody().applyLinearImpulse(
                        new Vector2(-5f, 0f),
                        spear.getComponent(PhysicsComponent.class).getBody().getWorldCenter(),
                        true);
            }
        }
    }

    /**
     * Creates a spear entity on the event the player presses 'K', and
     * applies a force to the spear to have it fly through the air
     */
    @Override
    public void activate() {
        if (!active) {
            active = true;
            thrown += 1;
            spear = ProjectileFactory.createSpearEntity();
            ServiceLocator.getEntityService().register(spear);
            ServiceLocator.getRenderService().register(
                    spear.getComponent(AnimationRenderComponent.class));
            spear.getEvents().addListener("dispose",
                    this::disposeSpear);
            //Determine which direction to apply the force to the spear
            spearDirection = entity.getComponent(PlayerActions.class)
                    .getPreviousDirection();
            if (spearDirection.hasSameDirection(Vector2Utils.RIGHT)) {
                spear.setPosition(entity.getPosition().x + 1f,
                        entity.getPosition().y);
                spear.getComponent(PhysicsComponent.class).getBody()
                        .applyLinearImpulse(new Vector2(10f, 10f),
                        spear.getComponent(PhysicsComponent.class).getBody()
                                .getWorldCenter(), true);
                spear.getComponent(AnimationRenderComponent.class)
                        .stopAnimation();
                spear.getComponent(AnimationRenderComponent.class)
                        .startAnimation("fly-right");
            } else {
                spear.setPosition(entity.getPosition().x - 1f,
                        entity.getPosition().y);
                spear.getComponent(PhysicsComponent.class).getBody()
                        .applyLinearImpulse(
                        new Vector2(-10f, 10f),
                        spear.getComponent(PhysicsComponent.class).getBody()
                                .getWorldCenter(), true);
                spear.getComponent(AnimationRenderComponent.class)
                        .stopAnimation();
                spear.getComponent(AnimationRenderComponent.class)
                        .startAnimation("fly-left");
            }
        }
    }

    /**
     * Triggered when the spear should get deleted
     */
    private void disposeSpear() {
        spear.flagDelete();
        active = false;
    }

    /**
     * Set the active status of the spear
     *
     * @param active - the active status the spear should be set to
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns the active status of the spear
     *
     * @return true if the spear is active and has been thrown, false otherwise
     */
    public boolean getActive() {
        return active;
    }

    /**
     * Returns the spear entity created when it is thrown
     *
     * @return the spear entity
     */
    public Entity getSpear(){
        return this.spear;
    }

    /**
     * Returns the number of times the spear has been thrown
     *
     * @return the number of times the spear has been thrown
     */
    public int getThrown() {
        return thrown;
    }
}