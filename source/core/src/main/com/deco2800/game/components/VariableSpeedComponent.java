package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.physics.components.PhysicsMovementComponent;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.services.SoundService;

import java.security.Provider;

public class VariableSpeedComponent extends Component {
    private Entity target;
    private Entity deathGiant;
    private Entity sfx;
    private int tutorialCompleted = 0;
    private int stopRunning = 0;

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
        target.getEvents().addListener("collisionStart", this::onCollisionStart);
        target.getEvents().addListener("anyMovement", this::changeSpeedStart);
    }

    /**
     * Handles collision
     *
     * @param me    first collision object
     * @param other second collision object
     */
    private void onCollisionStart(Fixture me, Fixture other) {
        changeSpeedStart();
    }

    private void changeSpeedStart() {

        Vector2 playerPos = target.getPosition();
        Vector2 entityPos = entity.getPosition();

        float distance = playerPos.x - entityPos.x;
        ServiceLocator.getSoundService().setGiantDistance(distance);

        if (playerPos.x < 40 && tutorialCompleted == 0) {
            entity.getComponent(PhysicsMovementComponent.class).setMaxSpeed(0);
            deathGiant.getComponent(PhysicsMovementComponent.class).setMaxSpeed(0);
            sfx.getComponent(PhysicsMovementComponent.class).setMaxSpeed(0);
        } else if (stopRunning == 0) {

            tutorialCompleted = 1;

            ServiceLocator.getSoundService().playSound("onstomp");

            entity.getComponent(PhysicsMovementComponent.class).setMaxSpeed(13);
            deathGiant.getComponent(PhysicsMovementComponent.class).setMaxSpeed(13);
            sfx.getComponent(PhysicsMovementComponent.class).setMaxSpeed(13);

            if (distance < 30f)  {

                entity.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
                deathGiant.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
                sfx.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);

                stopRunning = 1;

            }
        }

        if (tutorialCompleted == 1 && stopRunning == 1) {

            if (distance > 50f) {
                entity.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);
                deathGiant.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);
                sfx.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);

            } else {
                entity.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
                deathGiant.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
                sfx.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
            }

        }

    }


}