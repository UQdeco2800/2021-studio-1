package com.deco2800.game.components.player;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.deco2800.game.components.Component;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.services.ServiceLocator;

import java.security.Provider;

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
  private Vector2 walkDirection = Vector2.Zero.cpy();
  private boolean moving = false;
  private boolean jumping = false;
  private boolean falling = false;
  private boolean crouching = false;
  private long time;

  @Override
  public void create() {
    physicsComponent = entity.getComponent(PhysicsComponent.class);
    entity.getEvents().addListener("walk", this::walk);
    entity.getEvents().addListener("walkStop", this::stopWalking);
    entity.getEvents().addListener("jump", this::jump);
    entity.getEvents().addListener("crouch", this::crouch);
    entity.getEvents().addListener("stop crouch", this::stopCrouching);
    entity.getEvents().addListener("attack", this::attack);
    time = 0;
  }

  @Override
  public void update() {
    if (moving) {
      updateWalkingSpeed();
    } else if (jumping) {
      applyJumpForce();
    }
  }

  private void updateWalkingSpeed() {
    Body body = physicsComponent.getBody();
    Vector2 velocity = body.getLinearVelocity();
    Vector2 desiredVelocity;
    if (crouching) {
      desiredVelocity = walkDirection.cpy().scl(CROUCH_SPEED);
    } else {
      desiredVelocity = walkDirection.cpy().scl(MAX_SPEED);
    }
    // impulse = (desiredVel - currentVel) * mass
    Vector2 impulse = desiredVelocity.sub(velocity).scl(body.getMass());
    body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
  }

  private void applyJumpForce() {
    if (time == 0) {
      time = ServiceLocator.getTimeSource().getTime();
      physicsComponent.getBody().applyForce(new Vector2(0, 100f),
              physicsComponent.getEntity().getPosition(), true);
    } else if (falling) {
      if (ServiceLocator.getTimeSource().getTime() > time + 500f) {
        time = 0;
        physicsComponent.getBody().applyForce(new Vector2(0, 0),
                physicsComponent.getEntity().getPosition(), true);
        jumping = false;
        falling = false;
      }
    } else {
      if (ServiceLocator.getTimeSource().getTime() > time + 500f) {
        time = ServiceLocator.getTimeSource().getTime();
        physicsComponent.getBody().applyForce(new Vector2(0, -100f),
                physicsComponent.getEntity().getPosition(), true);
        falling = true;
      }
    }
  }

  /**
   * Moves the player towards a given direction.
   *
   * @param direction direction to move in
   */
  void walk(Vector2 direction) {
    this.walkDirection = direction;
    moving = true;
    entity.getComponent(AnimationRenderComponent.class).stopAnimation();
    entity.getComponent(AnimationRenderComponent.class)
            .startAnimation("run");
  }

  /**
   * Stops the player from walking.
   */
  void stopWalking() {
    this.walkDirection = Vector2.Zero.cpy();
    updateWalkingSpeed();
    moving = false;
    entity.getComponent(AnimationRenderComponent.class).stopAnimation();
    entity.getComponent(AnimationRenderComponent.class)
            .startAnimation("still");
  }

  void jump() {
    jumping = true;
  }

  void crouch() {
    crouching = true;
  }

  void stopCrouching() {
    crouching = false;
  }

  /**
   * Makes the player attack.
   */
  void attack() {
    Sound attackSound = ServiceLocator.getResourceService().getAsset("sounds/Impact4.ogg", Sound.class);
    attackSound.play();
  }
}
