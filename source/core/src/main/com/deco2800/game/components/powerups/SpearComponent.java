package com.deco2800.game.components.powerups;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.components.Component;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.physics.BodyUserData;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;

public class SpearComponent extends Component {
    /**
     * Add an event to the spear when it is created
     */
    @Override
    public void create() {
        entity.getEvents().addListener("collisionStart",
                this::handleSpearCollision);
        entity.getEvents().addListener("collision",
                this::spearCollisionWithPlayer);
    }

    /**
     * Check if the spear and player are colliding while the spear is static and
     * the player is jumping. If they are, then disable the contact to allow
     * the player to jump through the spear
     *
     * @param contact - the contact between the spear and another entity
     * @param spear - BodyUserData of the spear entity
     * @param other - BodyUserData of the other entity
     */
    private void spearCollisionWithPlayer (Contact contact, BodyUserData spear,
                                       BodyUserData other) {
        if ((entity.getComponent(PhysicsComponent.class).getBody().getType() ==
                BodyDef.BodyType.StaticBody) && other.entity.getType() ==
                EntityTypes.PLAYER && other.entity
                .getComponent(PhysicsComponent.class).getBody()
                .getLinearVelocity().y > 0) {
            contact.setEnabled(false);
        }
    }

    /**
     * If the spear is stationary (i.e. stuck to a wall) and it collides with
     * another spear, remove the other spear. This prevents spears from
     * stacking up or being placed too close together. Otherwise, if the spear
     * collides with an enemy, kill the enemy and delete the spear.
     *
     * @param spearFixture - fixture for the spear entity
     * @param otherFixture - fixture for the other entity
     */
    private void handleSpearCollision (Fixture spearFixture,
                                       Fixture otherFixture) {
        BodyUserData spear =
                (BodyUserData) spearFixture.getBody().getUserData();
        BodyUserData other =
                (BodyUserData) otherFixture.getBody().getUserData();
        if (entity.getComponent(PhysicsComponent.class).getBody().getType() == BodyDef.BodyType.StaticBody) {
            if (other.entity.getType() == EntityTypes.PLAYERSPEAR) {
                spear.entity.getEvents().trigger("dispose");
            }
        } else {
            if (other.entity.getType() == EntityTypes.FIRESPIRIT
                    || other.entity.getType() == EntityTypes.SKELETON
                    || other.entity.getType() == EntityTypes.WOLF) {
                other.entity.getEvents().trigger("death");
                if(entity.getComponent(AnimationRenderComponent.class).isFinished()) {
                    other.entity.flagDelete();
                }
                spear.entity.getEvents().trigger("dispose");
            }
        }
    }
}
