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
 * if it is an object or disabled if it is an NPC.
 * <p>
 * If the object is disposed of, it will be done out of the physics step to avoid crashed.
 * <p>
 * If the other component is an NPC it is disabled to avoid the animation being disposed of and
 * removing the animation from all other entities using it.
 *
 * <p>Requires HitboxComponent on this entity.
 */
public class LevelLoadTriggerComponent extends Component {
    HitboxComponent hitboxComponent;
    EntityService entityService;

    /**
     * Create a component which disposes entities on collision finish
     */
    public LevelLoadTriggerComponent() {
        this.entityService = ServiceLocator.getEntityService();
    }

    @Override
    public void create() {
        entity.getEvents().addListener("collisionStart", this::onCollisionStart);
        hitboxComponent = entity.getComponent(HitboxComponent.class);
    }

    private void onCollisionStart(Fixture me, Fixture other) {
        if (hitboxComponent.getFixture() != me) {
            // Not triggered by hitbox, ignore
            return;
        }

        


        if (PhysicsLayer.contains(PhysicsLayer.PLAYER, other.getFilterData().categoryBits)) {
            // HAS COLLIDED WITH PLAYER
            System.out.println("Collided with player");
            
            // Dispose the load trigger after the physics step
            Entity target = ((BodyUserData) me.getBody().getUserData()).entity;
            entityService.disposeAfterStep(target);
        }


    }
}
