package com.deco2800.game.components.tasks;

import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.ai.tasks.DefaultTask;
import com.deco2800.game.ai.tasks.PriorityTask;
import com.deco2800.game.ai.tasks.Task;
import com.deco2800.game.rendering.RenderService;
import com.deco2800.game.utils.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wander around by moving a random position within a range of the starting position. Wait a little
 * bit between movements. Requires an entity with a PhysicsMovementComponent.
 */
public class ObstacleTask extends DefaultTask implements PriorityTask {
    private static final Logger logger = LoggerFactory.getLogger(WanderTask.class);

    //private final Vector2 offScreen;
    //private final float waitTime;
    private Vector2 startPos;
    private MovementTask movementTask;
    //private WaitTask waitTask;
    private Task currentTask;

    private Vector2 lastFramePos;
    private Vector2 currentFramePos;
    private int frameSkips;

    public ObstacleTask() {
        //this.waitTime = waitTime;
    }

    @Override
    public int getPriority() {
        return 10; // high pri
    }

    @Override
    public void start() {
        super.start();
        startPos = owner.getEntity().getPosition();

        movementTask = new MovementTask(new Vector2(-10f, 0f));
        // the 3f will need to be changed to actual y later

        movementTask.create(owner);

        movementTask.start();
        currentTask = movementTask;
        lastFramePos = owner.getEntity().getPosition();

        owner.getEntity().getEvents().addListener("offScreen", owner.getEntity()::flagDelete);

        //this.owner.getEntity().getEvents().trigger("obstacleStart");
    }

    private void disableEntity() {
        owner.getEntity().setEnabled(false);
        //owner.getEntity().getComponent(RenderService.class)
    }

    @Override
    public void update() {

        currentTask.update();

        currentFramePos = owner.getEntity().getPosition();

        if (currentFramePos.epsilonEquals(lastFramePos)) {
            frameSkips++;
        }

        if (frameSkips == 5) {
            owner.getEntity().setPosition(currentFramePos.x - 0.01f, currentFramePos.y);
            frameSkips = 0;
        }

        lastFramePos = owner.getEntity().getPosition();

        if (currentFramePos.x < 2) {
            owner.getEntity().getEvents().trigger("offScreen");
        }
    }


}
