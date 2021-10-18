package com.deco2800.game.components.tasks;

import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.ai.tasks.DefaultTask;
import com.deco2800.game.ai.tasks.PriorityTask;
import com.deco2800.game.areas.RagnarokArea;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.physics.PhysicsEngine;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.raycast.RaycastHit;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.rendering.DebugRenderer;
import com.deco2800.game.services.ServiceLocator;

/** Chases a target entity until they get too far away or line of sight is lost */
public class ChaseTask extends DefaultTask implements PriorityTask {
  private Entity target;
  private final int priority;
  private final float viewDistance;
  private final float maxChaseDistance;
  private final PhysicsEngine physics;
  private final DebugRenderer debugRenderer;
  private final RaycastHit hit = new RaycastHit();
  private boolean chaseHorizontal = false;
  private MovementTask movementTask;
  private Vector2 currentPos;
  private boolean inAnimation;

  /**
   * @param target The entity to chase.
   * @param priority Task priority when chasing (0 when not chasing).
   * @param viewDistance Maximum distance from the entity at which chasing can start.
   * @param maxChaseDistance Maximum distance from the entity while chasing before giving up.
   */
  public ChaseTask(Entity target, int priority, float viewDistance, float maxChaseDistance) {
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
    public ChaseTask(Entity target, int priority, boolean horizontal,  float viewDistance, float maxChaseDistance) {
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

    Vector2 targetPosition = target.getPosition();
    if (chaseHorizontal) {
        targetPosition = new Vector2(targetPosition.x, 0);
    }
    movementTask = new MovementTask(target.getPosition());
    movementTask.create(owner);
    movementTask.start();
    currentPos = owner.getEntity().getPosition();

    if (targetPosition.x > currentPos.x) {
      this.owner.getEntity().getEvents().trigger("chaseStart_right");
    } else {
      this.owner.getEntity().getEvents().trigger("chaseStart");
    }
  }

  @Override
  public void update() {
    Vector2 targetPosition = target.getPosition();
    if (chaseHorizontal) {
      targetPosition = new Vector2(targetPosition.x, 0);
    }

    movementTask.setTarget(targetPosition);
    movementTask.update();
    if (movementTask.getStatus() != Status.ACTIVE) {
      movementTask.start();
    }

    if (owner.getEntity().getComponent(AnimationRenderComponent.class).isFinished()){
      inAnimation = false;
    }

    if (inAnimation == false && !owner.getEntity().getDeath()) {
      if (targetPosition.x > currentPos.x) {
        this.owner.getEntity().getEvents().trigger("chaseStart_right");
      } else {
        this.owner.getEntity().getEvents().trigger("chaseStart");
      }
      inAnimation = true;
    }
  }

  @Override
  public void stop() {
    super.stop();
    movementTask.stop();
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
