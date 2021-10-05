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
     * Class to change the speed of the entity depending on the distance of target
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

        float yPos = target.getPosition().y;
        float distance = deathGiant.getPosition().dst(target.getPosition());

        if (distance > 32f) {
            System.out.println("Max speeed = 6");
            entity.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);
            deathGiant.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);
            sfx.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);
        }

        else if (target.getPosition().x<40) {
            System.out.println("Max speeed = 2");
            entity.getComponent(PhysicsMovementComponent.class).setMaxSpeed(2f);
            deathGiant.getComponent(PhysicsMovementComponent.class).setMaxSpeed(2f);
            sfx.getComponent(PhysicsMovementComponent.class).setMaxSpeed(2f);
        } else {
            System.out.println("Max speeed = 4");
            entity.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
            deathGiant.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
            sfx.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
        }
    }
}