package com.deco2800.game.components.powerups;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.components.player.PlayerActions;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.entities.factories.ProjectileFactory;
import com.deco2800.game.physics.BodyUserData;
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

    // Update flags to check
    private boolean active;
    private int thrown;

    public Entity getSpear(){
        return this.spear;
    }

    @Override
    public void create() {
        setEnabled(false);
        spear = null;
        thrown = 0;
    }

    @Override
    public void earlyUpdate() {
        if (spear != null) {
            Body spearBod = spear.getComponent(PhysicsComponent.class).getBody();
            // If after flying, the spear stops or goes below y = 0, deactivate and reset
            if ((active && spearBod.getLinearVelocity().isZero()) || spear.getCenterPosition().y < 0) {
                active = false;
                spear.getComponent(AnimationRenderComponent.class).stopAnimation();
                disposeSpear();

                // Disposes the spear after three throws
                if (thrown == 3) {
                    thrown = 0;
                    setEnabled(false);
                    spear.getComponent(AnimationRenderComponent.class).stopAnimation();
                    disposeSpear();
                }
            }
        }
    }

    @Override
    public void update() {
        if (active) {
            if (entity.getComponent(PlayerActions.class).getPreviousDirection().hasSameDirection(Vector2Utils.RIGHT)) {
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
            spear = ProjectileFactory.createSpearEntity();
            ServiceLocator.getEntityService().register(spear);

            spear.getEvents().addListener("collisionStart", this::killEnemy);

            if (entity.getComponent(PlayerActions.class).getPreviousDirection().hasSameDirection(Vector2Utils.RIGHT)) {
                spear.setPosition(entity.getPosition().x + 1f, entity.getPosition().y);
                spear.getComponent(PhysicsComponent.class).getBody().applyLinearImpulse(
                        new Vector2(10f, 0f),
                        spear.getComponent(PhysicsComponent.class).getBody().getWorldCenter(),
                        true);
                spear.getComponent(AnimationRenderComponent.class).stopAnimation();
                spear.getComponent(AnimationRenderComponent.class).startAnimation(
                        "fly-right");
            } else {
                spear.setPosition(entity.getPosition().x - 1f, entity.getPosition().y);
                spear.getComponent(PhysicsComponent.class).getBody().applyLinearImpulse(
                        new Vector2(-10f, 0f),
                        spear.getComponent(PhysicsComponent.class).getBody().getWorldCenter(),
                        true);
                spear.getComponent(AnimationRenderComponent.class).stopAnimation();
                spear.getComponent(AnimationRenderComponent.class).startAnimation(
                        "fly-left");
            }
            thrown += 1;
        }
    }

    private void disposeSpear() {
        spear.flagDelete();
        active = false;
    }

    public boolean getActive() {
        return active;
    }

    public int getThrown() { return thrown;}

    private void killEnemy (Fixture spearFixture, Fixture otherFixture) {
        BodyUserData spearBody = (BodyUserData) spearFixture.getBody().getUserData();
        BodyUserData otherBody = (BodyUserData) otherFixture.getBody().getUserData();

        if (otherBody.entity.getType() == EntityTypes.FIRESPIRIT
                || otherBody.entity.getType() == EntityTypes.SKELETON
                || otherBody.entity.getType() == EntityTypes.WOLF) {

            otherBody.entity.flagDelete();
            spearBody.entity.getEvents().trigger("dispose");
        }
    }
}