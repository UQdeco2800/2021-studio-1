package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.physics.components.PhysicsMovementComponent;

public class VariableSpeedComponent extends Component {
    private Entity target;
    private Entity deathGiant;
    private Entity sfx;

    /**
     * Class to shake the camera and start event listener for animation
     *
     * @param target     the target of the camera
     * @param deathGiant the death giant whose speed is changing
     * @param sfx        screen effects entity
     */
    public VariableSpeedComponent(Entity target, Entity deathGiant, Entity sfx) {
        this.target = target;
        this.deathGiant = deathGiant;
        this.sfx = sfx;
    }

    /**
     * Create event listener for collision
     */
    @Override
    public void create() {
        entity.getEvents().addListener("collisionStart", this::onCollisionStart);
    }

    /**
     * Handles collision
     *
     * @param me    first collision object
     * @param other second collision object
     */
    private void onCollisionStart(Fixture me, Fixture other) {

        float distance = entity.getPosition().dst(target.getPosition());

        if (distance < 32f) {

            entity.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
            deathGiant.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
            sfx.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);

        } else {
            entity.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);
            deathGiant.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);
            sfx.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);

        }
    }
}