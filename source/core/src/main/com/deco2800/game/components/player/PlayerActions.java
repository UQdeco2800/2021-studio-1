package com.deco2800.game.components.player;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.deco2800.game.components.Component;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.utils.math.Vector2Utils;

/**
 * Action component for interacting with the player. Player events should be initialised in create()
 * and when triggered should call methods within this class.
 */
public class PlayerActions extends Component {
  private static final Vector2 MAX_SPEED = new Vector2(3f, 3f); // Metres
  // per second
  private static final Vector2 CROUCH_SPEED = new Vector2(1f, 1f); // Metres
  // per second

  private PhysicsComponent physicsComponent;

  private Vector2 runDirection = Vector2.Zero.cpy();

  private boolean moving = false;
  private boolean jumping = false;
  private boolean falling = false;
  private boolean crouching = false;

  @Override
  public void create() {
    physicsComponent = entity.getComponent(PhysicsComponent.class);
    entity.getEvents().addListener("run", this::run);
    entity.getEvents().addListener("stop run", this::stopRunning);
    entity.getEvents().addListener("jump", this::jump);
    entity.getEvents().addListener("crouch", this::crouch);
    entity.getEvents().addListener("stop crouch", this::stopCrouching);
    entity.getEvents().addListener("attack", this::attack);
  }

  @Override
  public void update() {
    if (jumping) {
      applyJumpForce();
    } else if (falling) {
      checkFalling();
    } else if (moving) {
      updateRunningSpeed();
    }
  }

  private void updateRunningSpeed() {
    Body body = physicsComponent.getBody();
    Vector2 velocity = body.getLinearVelocity();
    Vector2 desiredVelocity;
    if (crouching) {
      desiredVelocity = runDirection.cpy().scl(CROUCH_SPEED);
    } else {
      desiredVelocity = runDirection.cpy().scl(MAX_SPEED);
    }
    // impulse = (desiredVel - currentVel) * mass
    Vector2 impulse = desiredVelocity.sub(velocity).scl(body.getMass());
    body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
  }

  /**
   * Applies an upwards force to the player for 100ms, then removes the force
   */
  private void applyJumpForce() {
    Body body = physicsComponent.getBody();
    if (moving) {
      if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
        body.applyLinearImpulse(new Vector2(2f, 7f), body.getPosition(),
                true);
      } else {
        body.applyLinearImpulse(new Vector2(-2f, 7f), body.getPosition(),
                true);
      }
    } else {
      body.applyLinearImpulse(new Vector2(0, 5f), body.getPosition(),
              true);
    }
    falling = true;
    jumping = false;
  }

  /**
   * Checks if the player is still falling by comparing their current
   * position to their previously saved position
   */
  private void checkFalling() {
    if (physicsComponent.getBody().getLinearVelocity().y == 0) {
      falling = false;
      entity.getComponent(AnimationRenderComponent.class).stopAnimation();
      if (moving) {
        entity.getComponent(AnimationRenderComponent.class)
                .stopAnimation();
        if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
          entity.getComponent(AnimationRenderComponent.class)
                  .startAnimation("run-right");
        } else {
          entity.getComponent(AnimationRenderComponent.class)
                  .startAnimation("run-left");
        }
      } else {
        entity.getComponent(AnimationRenderComponent.class)
                .stopAnimation();
        if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
          entity.getComponent(AnimationRenderComponent.class)
                  .startAnimation("still-right");
        } else {
          entity.getComponent(AnimationRenderComponent.class)
                  .startAnimation("still-left");
        }
      }
    }
  }


  /**
   * Moves the player towards a given direction.
   *
   * @param direction direction to move in
   */
  void run(Vector2 direction) {
    this.runDirection = direction;
    moving = true;
    if (!jumping || crouching) {
      entity.getComponent(AnimationRenderComponent.class)
              .stopAnimation();
      if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("run-right");
      } else {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("run-left");
      }
    }
  }

  /**
   * Stops the player from running.
   */
  void stopRunning() {
    this.runDirection = Vector2.Zero;
    update();
    moving = false;
    if (!crouching) {
      entity.getComponent(AnimationRenderComponent.class)
              .stopAnimation();
      if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("still-right");
      } else {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("still-left");
      }
    }
  }

  void jump() {
    jumping = true;
    entity.getComponent(AnimationRenderComponent.class).stopAnimation();

    if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
          .startAnimation("jump-right");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
          .startAnimation("jump-left");
    }
  }

  void crouch() {
    crouching = true;
    entity.getComponent(AnimationRenderComponent.class).stopAnimation();
    if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("crouch-right");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("crouch-left");
    }
  }

  void stopCrouching() {
    crouching = false;
    update();
    entity.getComponent(AnimationRenderComponent.class)
            .stopAnimation();
    if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("still-right");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("still-left");
    }
  }

  /**
   * Makes the player attack.
   */
  void attack() {
    Sound attackSound = ServiceLocator.getResourceService().getAsset("sounds/Impact4.ogg", Sound.class);
    attackSound.play();
  }
}
