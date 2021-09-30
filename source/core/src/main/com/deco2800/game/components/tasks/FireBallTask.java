package com.deco2800.game.components.tasks;

import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.ai.tasks.DefaultTask;
import com.deco2800.game.ai.tasks.PriorityTask;
import com.deco2800.game.ai.tasks.Task;
import com.deco2800.game.utils.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.entities.factories.ProjectileFactory;
import com.deco2800.game.physics.BodyUserData;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.utils.math.Vector2Utils;

/**
 * Wander around by moving a random position within a range of the starting position. Wait a little
 * bit between movements. Requires an entity with a PhysicsMovementComponent.
 */
public class FireBallTask extends DefaultTask implements PriorityTask {
    private static final Logger logger = LoggerFactory.getLogger(FireBallTask.class);

    private final Vector2 wanderRange;
    private final float waitTime;
    private Vector2 startPos;
    private MovementTask movementTask;
    private WaitTask waitTask;
    private Task currentTask;
    private Vector2 newPos;

    /**
     * @param wanderRange Distance in X and Y the entity can move from its position when start() is
     *     called.
     * @param waitTime How long in seconds to wait between wandering.
     */
    public FireBallTask(Vector2 wanderRange, float waitTime) {
        this.wanderRange = wanderRange;
        this.waitTime = waitTime;
    }

    @Override
    public int getPriority() {
        return 1; // Low priority task
    }

    @Override
    public void start() {
        super.start();
        startPos = owner.getEntity().getPosition();

        waitTask = new WaitTask(waitTime);
        waitTask.create(owner);
        newPos = getRandomPosInRange();
        movementTask = new MovementTask(newPos);
        movementTask.create(owner);

        movementTask.start();
        currentTask = movementTask;

        this.owner.getEntity().getEvents().trigger("wanderStart");
    }

    @Override
    public void update() {
        if (currentTask.getStatus() != Status.ACTIVE) {
            if (currentTask == movementTask) {
                startWaiting();
            } else {
                startShooting();
            }
        }
        currentTask.update();
    }

    private void startWaiting() {
        logger.debug("Starting waiting");
        swapTask(waitTask);
    }

    private void startShooting() {
        logger.debug("Starting Shooting");
        movementTask.setTarget(getRandomPosInRange());
        newPos = getRandomPosInRange();
        startPos = owner.getEntity().getPosition();

        ////////////////
        Entity fireBall = ProjectileFactory.createFireBall();
        ServiceLocator.getEntityService().register(fireBall);
        fireBall.setPosition(startPos.x + 1f, startPos.y + 1f);

        /*
        fireBall.getComponent(PhysicsComponent.class).getBody().applyLinearImpulse(
                new Vector2(5f, 0f),
                fireBall.getComponent(PhysicsComponent.class).getBody().getWorldCenter(),
                true);
        */

        swapTask(movementTask);
    }

    private void swapTask(Task newTask) {
        if (currentTask != null) {
            currentTask.stop();
        }
        currentTask = newTask;
        currentTask.start();
    }

    private Vector2 getRandomPosInRange() {
        Vector2 halfRange = wanderRange.cpy().scl(0.5f);
        Vector2 min = startPos.cpy().sub(halfRange);
        Vector2 max = startPos.cpy().add(halfRange);
        return RandomUtils.random(min, max);
    }
}
