package com.deco2800.game.components.powerups;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.components.Component;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.physics.BodyUserData;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.PhysicsComponent;

public class SpearComponent extends Component {
    private Entity player;

    /**
     * Add an event to the spear when it is created
     */
    public void create() {
        entity.getEvents().addListener("collisionStart", this::handleSpearCollision);
    }

    /**
     * If the spear is stationary (i.e. stuck to a wall) and the player is falling or on a surface, the spear can be
     * collided with, otherwise it cannot. This allows for the player to jump through the spear from underneath and
     * land on top of it and use it as a platform
     */
    public void update() {
        if (entity.getComponent(PhysicsComponent.class).getBody().getType() == BodyDef.BodyType.StaticBody) {
            if (player.getComponent(PhysicsComponent.class).getBody().getLinearVelocity().y <= 0) {
                entity.getComponent(ColliderComponent.class).setSensor(false);
            } else {
                entity.getComponent(ColliderComponent.class).setSensor(true);
            }
        }
    }

    /**
     * If the spear is stationary (i.e. stuck to a wall) and it collides with another spear, remove the other spear.
     * This prevents spears from stacking up or being placed too close together.
     * Otherwise, if the spear collides with an enemy, kill the enemy and delete the spear
     *
     * @param spearFixture - the fixture for the spear in the collision
     * @param otherFixture - the fixture for the other entity in the collision with the spear
     */
    private void handleSpearCollision (Fixture spearFixture, Fixture otherFixture) {
        BodyUserData spearBody = (BodyUserData) spearFixture.getBody().getUserData();
        BodyUserData otherBody = (BodyUserData) otherFixture.getBody().getUserData();
        if (entity.getComponent(PhysicsComponent.class).getBody().getType() == BodyDef.BodyType.StaticBody) {
            if (otherBody.entity.getType() == EntityTypes.PLAYERSPEAR) {
                otherBody.entity.flagDelete();
            }
        } else {
            if (otherBody.entity.getType() == EntityTypes.FIRESPIRIT
                    || otherBody.entity.getType() == EntityTypes.SKELETON
                    || otherBody.entity.getType() == EntityTypes.WOLF) {

                otherBody.entity.flagDelete();
                spearBody.entity.getEvents().trigger("dispose");
            }
        }
    }

    /**
     * Sets the player entity for the spear (used in the logic to allow the player to collide with spear (see update())
     *
     * @param player - the player entity
     */
    public void setPlayer(Entity player) {
        this.player = player;
    }
}
