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

    public void obtainSpear() {
        thrown = 0;
        enabled = true;
    }

    public void reset() {
        thrown = 0;
    }

    @Override
    public void create() {
        setEnabled(false);
        spear = null;
        reset();
    }

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

    @Override
    public void activate() {
        if (!active) {
            active = true;
            thrown += 1;
            spear = ProjectileFactory.createSpearEntity();
            ServiceLocator.getEntityService().register(spear);
            ServiceLocator.getRenderService().register(
                    spear.getComponent(AnimationRenderComponent.class));
            spear.getEvents().addListener("dispose", this::disposeSpear);
            spearDirection = entity.getComponent(PlayerActions.class).getPreviousDirection();
            if (spearDirection.hasSameDirection(Vector2Utils.RIGHT)) {
                spear.setPosition(entity.getPosition().x + 1f, entity.getPosition().y);
                spear.getComponent(PhysicsComponent.class).getBody().applyLinearImpulse(
                        new Vector2(10f, 10f),
                        spear.getComponent(PhysicsComponent.class).getBody().getWorldCenter(),
                        true);
                spear.getComponent(AnimationRenderComponent.class).stopAnimation();
                spear.getComponent(AnimationRenderComponent.class).startAnimation(
                        "fly-right");
            } else {
                spear.setPosition(entity.getPosition().x - 1f, entity.getPosition().y);
                spear.getComponent(PhysicsComponent.class).getBody().applyLinearImpulse(
                        new Vector2(-10f, 10f),
                        spear.getComponent(PhysicsComponent.class).getBody().getWorldCenter(),
                        true);
                spear.getComponent(AnimationRenderComponent.class).stopAnimation();
                spear.getComponent(AnimationRenderComponent.class).startAnimation(
                        "fly-left");
            }
        }
    }

    private void disposeSpear() {
        spear.flagDelete();
        active = false;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean getActive() {
        return active;
    }

    public Entity getSpear(){
        return this.spear;
    }

    public int getThrown() {
        return thrown;
    }
}