package com.deco2800.game.components.tasks;

import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.ai.tasks.DefaultTask;
import com.deco2800.game.ai.tasks.PriorityTask;
import com.deco2800.game.ai.tasks.Task;
import com.deco2800.game.utils.math.RandomUtils;
import com.deco2800.game.entities.Entity;
//import com.deco2800.game.entities.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fires a projectile at the player. Requires an entity with a PhysicsMovementComponent.
 */
public class ShootTask extends DefaultTask implements PriorityTask {
    private static final Logger logger = LoggerFactory.getLogger(ShootTask.class);

    protected Entity player;
    //protected Entity;
    private Vector2 startPos;
    private MovementTask movementTask;
    private WaitTask waitTask;
    private Task currentTask;


    public ShootTask() {
    }

    @Override
    public int getPriority() {
        return 1; // Low priority task
    }

    @Override
    public void start() {
        super.start();
        startPos = owner.getEntity().getPosition();

        waitTask = new WaitTask(5);
        currentTask = waitTask;

        this.owner.getEntity().getEvents().trigger("Wait for player");
    }

    public void shootPlayer() {
        startPos = owner.getEntity().getPosition();


        movementTask = new MovementTask(player.getPosition());
        movementTask.create(owner);

        movementTask.start();
        currentTask = movementTask;

        this.owner.getEntity().getEvents().trigger("Shoot");
    }


    @Override
    public void update() {
        if (currentTask.getStatus() != Status.ACTIVE) {
            shootPlayer();
            fire();
        }
        currentTask.update();
    }

    /**
     * Sets the target to continuously move at the players position
     */
    private void fire() {
        logger.debug("Shot Fired");

        movementTask.setTarget(new Vector2(player.getPosition()));
    }
}