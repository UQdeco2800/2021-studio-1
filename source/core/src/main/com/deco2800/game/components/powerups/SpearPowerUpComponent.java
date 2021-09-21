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
    private Vector2 spearDirection;

    // Update flags to check
    private boolean active;
    private int thrown;

    /**
     * Creates the spear component
     */
    @Override
    public void create() {
        setEnabled(false);
        spear = null;
        thrown = 0;
    }

    public void obtainSpear() {
        System.out.println("Reset throws");
        thrown = 0;
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
            if ((active && (spearBod.getLinearVelocity().isZero()) || spear.getCenterPosition().y < 0)) {
                if (thrown >= 3) {
                    thrown = 0;
                    setEnabled(false);
                }
                disposeSpear();
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
        if (thrown >= 3) {
            thrown = 0;
            setEnabled(false);
        } else if (!active ) {
            active = true;
            thrown += 1;
            spear = ProjectileFactory.createSpearEntity();
            ServiceLocator.getEntityService().register(spear);

            spear.getEvents().addListener("collisionStart", this::killEnemy);
            spear.getEvents().addListener("dispose", this::disposeSpear);

            if (entity.getComponent(PlayerActions.class).getPreviousDirection().hasSameDirection(Vector2Utils.RIGHT)) {
                spear.setPosition(entity.getPosition().x + 1f, entity.getPosition().y);
                spearDirection = Vector2Utils.RIGHT.cpy();
                spear.getComponent(PhysicsComponent.class).getBody().applyLinearImpulse(
                        new Vector2(10f, 10f),
                        spear.getComponent(PhysicsComponent.class).getBody().getWorldCenter(),
                        true);
                spear.getComponent(AnimationRenderComponent.class).stopAnimation();
                spear.getComponent(AnimationRenderComponent.class).startAnimation(
                        "fly-right");

            } else {
                spear.setPosition(entity.getPosition().x - 1f,
                        entity.getPosition().y);
                spearDirection = Vector2Utils.LEFT.cpy();
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
        active = false;
        spear.getComponent(AnimationRenderComponent.class).stopAnimation();
        spear.flagDelete();
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
     * Returns the number of times the current spear has been thrown
     *
     * @return the number of throws
     */
    public int getThrown() { return thrown;}

    /**
     * Deletes enemy when spear hits them
     *
     * @param spearFixture - the fixture attached to the spear body
     * @param otherFixture - the fixture attached to the enemies body
     */
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