package com.deco2800.game.components.tasks;

import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.ai.tasks.DefaultTask;
import com.deco2800.game.ai.tasks.PriorityTask;
import com.deco2800.game.ai.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wander around by moving a random position within a range of the starting position. Wait a little
 * bit between movements. Requires an entity with a PhysicsMovementComponent.
 */
public class MoveLeftTask extends DefaultTask implements PriorityTask {
    private static final Logger logger = LoggerFactory.getLogger(MoveLeftTask.class);

    private MovementTask movementTask;
    private Task currentTask;

    @Override
    public int getPriority() {
        return 1; // Low priority task
    }

    @Override
    public void start() {
        super.start();

        movementTask = new MovementTask(new Vector2(-1000000, 25));
        movementTask.create(owner);

        movementTask.start();
        currentTask = movementTask;

        this.owner.getEntity().getEvents().trigger("moveLeft");
    }

    @Override
    public void update() {
        if (currentTask.getStatus() != Status.ACTIVE) {
            startMoving();
        }
        currentTask.update();
    }

    /**
     * Sets the target to continuously move to the left direction
     */
    private void startMoving() {
        logger.debug("Starting moving");
        movementTask.setTarget(new Vector2(-1000000, 25));
    }
}
