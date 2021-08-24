package com.deco2800.game.components.player;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.deco2800.game.components.Component;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.utils.math.Vector2Utils;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;


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
  private Vector2 position;

  private boolean moving = false;
  private boolean jumping = false;
  private boolean falling = false;
  private boolean crouching = false;
  private long timeJumping;

  @Override
  public void create() {
    physicsComponent = entity.getComponent(PhysicsComponent.class);
    entity.getEvents().addListener("run", this::run);
    entity.getEvents().addListener("stop run", this::stopRunning);
    entity.getEvents().addListener("jump", this::jump);
    entity.getEvents().addListener("stop jump", this::stopJumping);
    entity.getEvents().addListener("crouch", this::crouch);
    entity.getEvents().addListener("stop crouch", this::stopCrouching);
    entity.getEvents().addListener("attack", this::attack);
    timeJumping = 0;
  }

  @Override
  public void update() {
    if (moving) {
      updateRunningSpeed();
    } else if (jumping) {
      applyJumpForce();
    }
  }

  private void updateRunningSpeed() {
    if (jumping) {
      applyJumpForce();
    }

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
    if (!falling) {
      if (timeJumping == 0) {
        //Get the time when the force begins to be applied
        timeJumping = ServiceLocator.getTimeSource().getTime();
        //Apply the force to the player
        body.applyLinearImpulse(new Vector2(0, 20f)
                .scl(body.getMass()), body.getWorldCenter(), true);
        //Check if 100ms has passed
      } else if (ServiceLocator.getTimeSource().getTimeSince(timeJumping)
              >= 100) {
        //Remove the force from the player and reset the time
        body.applyLinearImpulse(new Vector2(0, 0)
                .scl(body.getMass()), body.getWorldCenter(), true);
        timeJumping = 0;
        falling = true;
        jumping = false;
        position = physicsComponent.getEntity().getPosition();
      }
    } else {
      checkFalling();
    }
  }

  /**
   * Checks if the player is still falling by comparing their current
   * position to their previously saved position
   */
  private void checkFalling() {
    if (position.epsilonEquals(physicsComponent.getEntity().getPosition())) {
      jumping = false;
      falling = false;
    } else {
      position = physicsComponent.getEntity().getPosition();
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
    entity.getComponent(AnimationRenderComponent.class).stopAnimation();

    if (direction.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
          .startAnimation("run-right");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
          .startAnimation("run-left");
    }
  }

  /**
   * Stops the player from running.
   */
  void stopRunning() {
    this.runDirection = Vector2.Zero;
    update();
    moving = false;
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

  void stopJumping() {
    jumping = false;
    update();
    entity.getComponent(AnimationRenderComponent.class).stopAnimation();
    entity.getComponent(AnimationRenderComponent.class).startAnimation("still-right");
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
    entity.getComponent(AnimationRenderComponent.class).stopAnimation();
    entity.getComponent(AnimationRenderComponent.class).startAnimation("still-right");
  }

  /**
   * Makes the player attack.
   */
  void attack() {
    Sound attackSound = ServiceLocator.getResourceService().getAsset("sounds/Impact4.ogg", Sound.class);
    attackSound.play();
  }
}
