package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.physics.BodyUserData;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BifrostFXComponent extends Component {
    private static final Logger logger = LoggerFactory.getLogger(BifrostFXComponent.class);
    HitboxComponent hitboxComponent;
    EntityService entityService;
    
    public BifrostFXComponent() {
        this.entityService = ServiceLocator.getEntityService();
    }

    @Override
    public void create() {
        entity.getEvents().addListener("collisionStart", this::onCollisionEnd);
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
            //int x = Math.round(target.getPosition().x) + 3;
            int y = Math.round(target.getPosition().y) - 3;
            if (y < 0)
                y = 0;

            String line = String.format("-spawn [%d,%d] (bifrostFX)", -1, y);
            //logger.info(line);
            ServiceLocator.getTerminalService().sendTerminal(line);
            //Fix the above somehow buddy?
        }
    }
}