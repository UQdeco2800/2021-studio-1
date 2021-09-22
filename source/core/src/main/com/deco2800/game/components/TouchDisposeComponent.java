package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.physics.BodyUserData;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.services.ServiceLocator;

/**
 * When this entity touches another component's hitbox, the other component will be disposed of
 * if it is an object or disabled if it is an NPC.
 * <p>
 * If the object is disposed of, it will be done out of the physics step to avoid crashed.
 * <p>
 * If the other component is an NPC it is disabled to avoid the animation being disposed of and
 * removing the animation from all other entities using it.
 *
 * <p>Requires HitboxComponent on this entity.
 */
public class TouchDisposeComponent extends Component {
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

        if (!(PhysicsLayer.contains(PhysicsLayer.OBSTACLE, other.getFilterData().categoryBits)
                || PhysicsLayer.contains(PhysicsLayer.NPC, other.getFilterData().categoryBits))) {
            // Isn't an obstacle or NPC, don't dispose of
            return;
        }
        Entity target = ((BodyUserData) other.getBody().getUserData()).entity;

        if (entity.getPosition().x < target.getPosition().x) {
            // Entity has not passed to the left of the wall, do not dispose
            return;
        }

        // Dispose target after the physics step
        target.getEvents().trigger("dispose");
        entityService.disposeAfterStep(target);
    }
}
