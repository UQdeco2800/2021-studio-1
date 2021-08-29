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
public class MoveRightTask extends DefaultTask implements PriorityTask {
  private static final Logger logger = LoggerFactory.getLogger(MoveRightTask.class);

  private final Vector2 wanderRange;
  private final float waitTime;
  private Vector2 startPos;
  private MovementTask movementTask;
  private WaitTask waitTask;
  private Task currentTask;

  /**
   * @param wanderRange Distance in X and Y the entity can move from its position when start() is
   *     called.
   * @param waitTime How long in seconds to wait between wandering.
   */
  public MoveRightTask(Vector2 wanderRange, float waitTime) {
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
    movementTask = new MovementTask(getRandomPosInRange());
    movementTask.create(owner);

    movementTask.start();
    currentTask = movementTask;

    this.owner.getEntity().getEvents().trigger("wanderStart");
  }

  @Override
  public void update() {
    if (currentTask.getStatus() != Status.ACTIVE) {

        startMoving();

    }
    currentTask.update();
  }

  private void startWaiting() {
    logger.debug("Starting waiting");
    swapTask(waitTask);
  }

  private void startMoving() {
    logger.debug("Starting moving");
    movementTask.setTarget(getRandomPosInRange());
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
    Vector2 halfRange = wanderRange.cpy().scl(3f);
    Vector2 min = startPos.cpy().sub(halfRange);

    Vector2 max = startPos.cpy().add(halfRange);
    Vector2 right = new Vector2(10000000,500);
    //return RandomUtils.random(min, max);
    return right;
  }
}
