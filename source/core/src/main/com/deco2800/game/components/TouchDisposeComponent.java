package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.physics.BodyUserData;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.services.ServiceLocator;

/**
 * When this entity touches another component's hitbox, the other component will be disposed of
 * the next step after the contact is finished (disposed out of the physics step).
 *
 * <p>Requires HitboxComponent on this entity.
 */
public class TouchDisposeComponent extends Component{
    HitboxComponent hitboxComponent;
    EntityService entityService;

    /**
     * Create a component which disposes entities on collision finish
     */
    public TouchDisposeComponent() {
        this.entityService = ServiceLocator.getEntityService();
    }

    @Override
    public void create() {
        entity.getEvents().addListener("collisionEnd", this::onCollisionEnd);
        hitboxComponent = entity.getComponent(HitboxComponent.class);
    }

    private void onCollisionEnd(Fixture me, Fixture other) {
        if (hitboxComponent.getFixture() != me) {
            // Not triggered by hitbox, ignore
            return;
        }

        if (!PhysicsLayer.contains(PhysicsLayer.OBSTACLE, other.getFilterData().categoryBits)) {
            // Isn't an obstacle or NPC, don't dispose of
            return;
        }

        Entity target = ((BodyUserData) other.getBody().getUserData()).entity;

        if (entity.getPosition().x < target.getPosition().x) {
            // Entity has not passed to the left of the wall, do not dispose
            return;
        }

        if (PhysicsLayer.contains(PhysicsLayer.NPC, other.getFilterData().categoryBits)) {
            // This is an NPC and may contain an atlas file, which if disposed of will remove the
            // atlas file from use for any other entities using it. Disable it instead.
            target.setEnabled(false);
            return;
        }

        // Dispose target after the physics step
        entityService.disposeAfterStep(target);
    }
}
