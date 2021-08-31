package com.deco2800.game.physics.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class AbstractPlayerMovementComponent extends PhysicsMovementComponent {

    private float velocity = 1f; //the speed
    private float difficulty; //how hard the game is

    public void create() {
        physicsComponent = entity.getComponent(PhysicsComponent.class);
        setTarget(new Vector2(0f, 0f));
    }

    @Override
    public void update() {

        Vector2 target = getTarget();

        float tx = target.x + velocity;
        float ty = 0;

        target.set(tx, ty);

        setTarget(target);

        if (movementEnabled && targetPosition != null) {
            Body body = physicsComponent.getBody();
            updateDirection(body);
        }
    }

    public void setVelocity(float velocity) {

    }

    public void setDifficulty(float difficulty) {



    }

}
