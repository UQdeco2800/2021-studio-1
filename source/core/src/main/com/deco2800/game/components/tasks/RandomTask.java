package com.deco2800.game.components.tasks;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.ai.tasks.DefaultTask;
import com.deco2800.game.ai.tasks.PriorityTask;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.physics.PhysicsEngine;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.raycast.RaycastHit;
import com.deco2800.game.rendering.DebugRenderer;
import com.deco2800.game.services.ServiceLocator;

/** A random task from a list of random movement task configurations implemented custom behaviour */
public class RandomTask extends DefaultTask implements PriorityTask {
    private Entity target;
    private final int priority;
    private final float viewDistance;
    private final float maxChaseDistance;
    private final PhysicsEngine physics;
    private final DebugRenderer debugRenderer;
    private final RaycastHit hit = new RaycastHit();
    private boolean chaseHorizontal = false;
    private MovementTask currentTask;
    private MovementTask movementTaskOne;
    private MovementTask movementTaskTwo;
    private MovementTask movementTaskThree;
    private MovementTask movementTaskFour;

    /**
     * @param target The entity to chase.
     * @param priority Task priority when chasing (0 when not chasing).
     * @param viewDistance Maximum distance from the entity at which chasing can start.
     * @param maxChaseDistance Maximum distance from the entity while chasing before giving up.
     */
    public RandomTask(Entity target, int priority, float viewDistance, float maxChaseDistance) {
        this.target = target;
        this.priority = priority;
        this.viewDistance = viewDistance;
        this.maxChaseDistance = maxChaseDistance;
        physics = ServiceLocator.getPhysicsService().getPhysics();
        debugRenderer = ServiceLocator.getRenderService().getDebug();
    }

    /**
     * @param target The area of the game.
     * @param priority Task priority when chasing (0 when not chasing).
     * @param viewDistance Maximum distance from the entity at which chasing can start.
     * @param maxChaseDistance Maximum distance from the entity while chasing before giving up.
     * @param horizontal Boolean representing only horizontal movement
     */
    public RandomTask(Entity target, int priority, boolean horizontal,  float viewDistance, float maxChaseDistance) {
        this.target = target;
        this.priority = priority;
        this.viewDistance = viewDistance;
        this.chaseHorizontal = horizontal;
        this.maxChaseDistance = maxChaseDistance;
        physics = ServiceLocator.getPhysicsService().getPhysics();
        debugRenderer = ServiceLocator.getRenderService().getDebug();
    }

    @Override
    public void start() {
        super.start();

        float random = MathUtils.random();
        int type = random < 0.25f ? 0
                : random > 0.25 && random < 0.5 ? 1
                : random >= 0.5 && random < 0.75 ? 2
                : 3;

        movementTaskOne = new MovementTask(new Vector2(-1000000, 0));
        movementTaskOne.create(owner);

        movementTaskTwo = new MovementTask(new Vector2(10000000,0));
        movementTaskTwo.create(owner);

        Vector2 targetPosition = target.getPosition();
        movementTaskFour = new MovementTask(new Vector2(0, 25));
        movementTaskFour.create(owner);

        startTask(type);

        this.owner.getEntity().getEvents().trigger("chaseStart");
    }

    private void startTask(int type) {

        if (type == 0) {
            currentTask = movementTaskOne;
            currentTask.start();
            return;
        }
        if (type == 1) {
            currentTask = movementTaskTwo;
            currentTask.start();
            return;
        }
        if (type == 2) {
            currentTask = movementTaskFour;
            currentTask.start();
        }

    }

    @Override
    public void update() {

        if (currentTask != null) {
            if (currentTask.getStatus() != Status.ACTIVE) {
                getNewRandomTask();
            }
            currentTask.update();
        }
    }

    private void getNewRandomTask() {

        float random = MathUtils.random();
        int type = random < 0.25f ? 0
                : random > 0.25 && random < 0.5 ? 1
                : random >= 0.5 && random < 0.75 ? 2
                : 3;

        startTask(type);
    }

    @Override
    public void stop() {
        super.stop();
        currentTask.stop();
    }

    @Override
    public int getPriority() {
        if (status == Status.ACTIVE) {
            return getActivePriority();
        }

        return getInactivePriority();
    }

    private float getDistanceToTarget() {
        if (target != null) {
            return owner.getEntity().getPosition().dst(target.getPosition());
        }
        return 100; // out of range
    }

    private int getActivePriority() {
        float dst = getDistanceToTarget();
        if (dst > maxChaseDistance || !isTargetVisible()) {
            return -1; // Too far, stop chasing
        }
        return priority;
    }

    private int getInactivePriority() {
        float dst = getDistanceToTarget();
        if (dst < viewDistance && isTargetVisible()) {
            return priority;
        }
        return -1;
    }

    private boolean isTargetVisible() {
        Vector2 from = owner.getEntity().getCenterPosition();
        Vector2 to = target.getCenterPosition();

        // If there is an obstacle in the path to the player, not visible.
        if (physics.raycast(from, to, PhysicsLayer.OBSTACLE, hit)) {
            debugRenderer.drawLine(from, hit.point);
            return false;
        }
        debugRenderer.drawLine(from, to);
        return true;
    }
}

