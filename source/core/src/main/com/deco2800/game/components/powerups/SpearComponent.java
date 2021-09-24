package com.deco2800.game.components.powerups;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.components.Component;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.physics.BodyUserData;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpearComponent extends Component {
    private Entity player;
    private boolean makeSensor;
    private int collision;
    private static final Logger logger = LoggerFactory.getLogger(SpearComponent.class);

    public void create() {
        makeSensor = false;
        entity.getEvents().addListener("collisionStart", this::handleSpearCollision);
    }

    public void update() {
        //entity.getComponent(ColliderComponent.class).setSensor(makeSensor);
        if (entity.getComponent(PhysicsComponent.class).getBody().getType() == BodyDef.BodyType.StaticBody) {
            if (player.getComponent(PhysicsComponent.class).getBody().getLinearVelocity().y <= 0) {
                entity.getComponent(ColliderComponent.class).setSensor(false);
            } else {
                entity.getComponent(ColliderComponent.class).setSensor(true);
            }
        }
    }

    private void handleSpearCollision (Fixture spearFixture, Fixture otherFixture) {
        BodyUserData spearBody = (BodyUserData) spearFixture.getBody().getUserData();
        BodyUserData otherBody = (BodyUserData) otherFixture.getBody().getUserData();

        if (otherBody.entity.getType() == EntityTypes.FIRESPIRIT
                || otherBody.entity.getType() == EntityTypes.SKELETON
                || otherBody.entity.getType() == EntityTypes.WOLF) {

            otherBody.entity.flagDelete();
            spearBody.entity.getEvents().trigger("dispose");
        }
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }
}
