package com.deco2800.game.components.tasks;

import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.ai.tasks.DefaultTask;
import com.deco2800.game.ai.tasks.PriorityTask;
import com.deco2800.game.ai.tasks.Task;
import com.deco2800.game.entities.Entity;

import com.deco2800.game.entities.factories.ProjectileFactory;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Fires a projectile at the player. Requires an entity with a PhysicsMovementComponent.
 */
public class ShootTask extends DefaultTask implements PriorityTask {
    private static final Logger logger = LoggerFactory.getLogger(ShootTask.class);

    protected Entity player;
    protected Entity fireball;
    private Vector2 startPos;
    private MovementTask movementTask;
    private final float waitTime;
    private WaitTask waitTask;
    private ShootTask shootTask;
    private Task currentTask;
    private Entity fire;


    public ShootTask(Entity player, float waitTime) { // I removed Entity fireball from here and instead just created the fireball when shoot it called
        this.player = player;
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

        shootTask = new ShootTask(player, waitTime);
        shootTask.create(owner);

        currentTask = shootTask;

        this.owner.getEntity().getEvents().trigger("Wait for player");
    }


    @Override
    public void update() {
        if (currentTask.getStatus() != Status.ACTIVE) {
            if (currentTask == waitTask && waitTime == 0) {
                waiting();
            } else {
                fire();
            }
        }
        currentTask.update();
    }

    /**
     * Sets the target to continuously move at the players position
     */
    private void fire() {
        Vector2 playerPos = this.player.getPosition();
        Vector2 entityPos = owner.getEntity().getPosition();

        logger.debug("Shot Fired");
        fireball = ProjectileFactory.fireBall();
        Body body = fireball.getComponent(PhysicsComponent.class).getBody();
        ServiceLocator.getEntityService().register(fireball);
        fireball.setPosition(owner.getEntity().getPosition().x + 1, owner.getEntity().getPosition().y);

        if (playerPos.x < entityPos.x) {
            fireball.getComponent(PhysicsComponent.class).getBody().setLinearVelocity(-50, 2);
            fireball.getEvents().trigger("fireball");
        } else {
            fireball.getComponent(PhysicsComponent.class).getBody().setLinearVelocity(50, 2);
            fireball.getEvents().trigger("fireball_back");
        }

        swapTask(waitTask);
    }

    private void waiting() {
        logger.debug("Don't Fire");
        swapTask(shootTask);
    }

    private void swapTask(Task newtask) {
        if (currentTask != null) {
            currentTask.stop();
        }
        currentTask = newtask;
        currentTask.start();
    }
}