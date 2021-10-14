package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.physics.BodyUserData;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.services.ServiceLocator;

public class BifrostFXComponent extends Component {
    HitboxComponent hitboxComponent;
    EntityService entityService;
    
    public BifrostFXComponent() {
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

        if (PhysicsLayer.contains(PhysicsLayer.PLAYER, other.getFilterData().categoryBits)) {
            // Colided with player, spawn FX!
            Entity target = ((BodyUserData) other.getBody().getUserData()).entity;
            
            //GridPoint2 pos = new GridPoint2(target.getPosition().x, target.getPosition().y);
            int x = Math.round(target.getPosition().x) + 3;
            int y = Math.round(target.getPosition().y);
            //ServiceLocator.getTerminalService().sendTerminal("-spawn "+ x + "," + y + ", skeleton");
            //Fix the above somehow buddy?
        }
    }
}