package com.deco2800.game.components.tasks;

import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.ai.tasks.DefaultTask;
import com.deco2800.game.ai.tasks.PriorityTask;
import com.deco2800.game.ai.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.badlogic.gdx.physics.box2d.Body;
import com.deco2800.game.physics.components.PhysicsComponent;

/**
 * Wander around by moving a random position within a range of the starting position. Wait a little
 * bit between movements. Requires an entity with a PhysicsMovementComponent.
 */
public class RunningTask extends DefaultTask implements PriorityTask {
  private static final Logger logger = LoggerFactory.getLogger(RunningTask.class);

  private Vector2 startPos;
  private MovementTask movementTask;
  private WaitTask waitTask;
  private Task currentTask;


  public RunningTask() {
  }

  @Override
  public int getPriority() {
    return 1; // Low priority task
  }

  @Override
  public void start() {
    super.start();
    startPos = owner.getEntity().getPosition();

    // CHECK CODE HERE
    Body body = owner.getEntity().getComponent(PhysicsComponent.class).getBody();
    if (body.getLinearVelocity().isZero()) {
      movementTask = new MovementTask(new Vector2(-10000, 25));
      this.owner.getEntity().getEvents().trigger("moveLeft");
    } else {
      movementTask.setTarget(new Vector2(+10000,25));
      this.owner.getEntity().getEvents().trigger("moveRight");
    }

    movementTask.create(owner);
    movementTask.start();
    currentTask = movementTask;
  }

  @Override
  public void update() {
    if (currentTask.getStatus() != Status.ACTIVE) {
        start();
    }
    currentTask.update();
  }
}
