package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.physics.BodyUserData;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.services.ServiceLocator;
import java.io.*;

/**
 * When this entity touches the player, an additional level will be loaded at the end of the current level
 * This entity is then disposed of.
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
            // HAS COLLIDED WITH PLAYER so load in next level
            System.out.println("READY TO LOAD LEVEL");
            ServiceLocator.getAreaService().load("ragnorok");
            System.out.println("LOADED LEVEL");
            // Dispose the load trigger after the physics step
            Entity target = ((BodyUserData) me.getBody().getUserData()).entity;
            // entityService.disposeAfterStep(target);
        }


    }
}
