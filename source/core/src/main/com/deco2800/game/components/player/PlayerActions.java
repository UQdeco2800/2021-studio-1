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
 * Action component for interacting with the player. Player events should be
 * initialised in create() and when triggered should call methods within this
 * class.
 */
public class PlayerActions extends Component {
  private static final Vector2 MAX_SPEED = new Vector2(7f, 1f); // Metres
  // per second
  private static final Vector2 CROUCH_SPEED = new Vector2(1f, 1f);
  // Metres per second

  private PhysicsComponent physicsComponent;

  private Vector2 runDirection = Vector2.Zero.cpy();

  public static boolean moving = false;
  private boolean jumping = false;
  private boolean falling = false;
  private boolean crouching = false;
  private Vector2 previousDirection = Vector2.Zero.cpy();

  @Override
  public void create() {
    physicsComponent = entity.getComponent(PhysicsComponent.class);
    entity.getEvents().addListener("run", this::run);
    entity.getEvents().addListener("stop run", this::stopRunning);
    entity.getEvents().addListener("jump", this::jump);
    entity.getEvents().addListener("crouch", this::crouch);
    entity.getEvents().addListener("stop crouch", this::stopCrouching);
    entity.getEvents().addListener("attack", this::attack);
    entity.getEvents().addListener("stop attack",
            this::stopAttack);
    entity.getEvents().addListener("powerAttack", this::powerAttack);
    entity.getEvents().addListener("stop stopPowerAttack",
            this::stopPowerAttack);

    entity.getComponent(AnimationRenderComponent.class)
            .startAnimation("still-right");

  }

  @Override
  public void update() {
    if (falling) {
      checkFalling();
    } else if (jumping) {
      applyJumpForce();
    } else if (moving) {
      updateRunningSpeed();
    }
  }

  private void updateRunningSpeed() {
    Body body = physicsComponent.getBody();
    if (physicsComponent.getBody().getLinearVelocity().y != 0) {
      falling = true;
    }
    Vector2 velocity = body.getLinearVelocity();
    Vector2 desiredVelocity;
    if (crouching) { //Determine speed based on whether crouching or not
      desiredVelocity = runDirection.cpy().scl(CROUCH_SPEED);
    } else {
      desiredVelocity = runDirection.cpy().scl(MAX_SPEED);
    }
    // impulse = (desiredVel - currentVel) * mass
    Vector2 impulse = desiredVelocity.sub(velocity).scl(body.getMass());
    body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
  }

  /**
   * Applies an upwards force to the player
   */
  private void applyJumpForce() {
    Body body = physicsComponent.getBody();
    body.applyLinearImpulse(new Vector2(0f,  40f).scl(body.getMass()), body.getWorldCenter(), true);
    falling = true;
    jumping = false;
  }

  /**
   * Checks if the player is still falling by checking their y velocity
   */
  private void checkFalling() {
    Body body = physicsComponent.getBody();
    entity.getComponent(AnimationRenderComponent.class).stopAnimation();
    whichAnimation();
    if (physicsComponent.getBody().getLinearVelocity().y == 0) {
      falling = false;
      //Determine which animation to play
      entity.getComponent(AnimationRenderComponent.class).stopAnimation();
      whichAnimation();
    } else {
      if (moving) { //Checks if the player is moving and applies respective force
        if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
          body.applyLinearImpulse(new Vector2(0.75f,  0f).scl(body.getMass()),
                  body.getPosition(),
                  true);
        } else {
          body.applyLinearImpulse(new Vector2(-0.75f,  0f).scl(body.getMass()),
                  body.getPosition(),
                  true);
        }
      } else { //Applies force when player is not moving
        body.applyLinearImpulse(new Vector2(0f,  0f).scl(body.getMass()),
                body.getWorldCenter(), true);
      }
    }
  }


  /**
   * Moves the player towards a given direction and copies that direction as
   * the previous direction
   *
   * @param direction direction to move in
   */
  void run(Vector2 direction) {
    this.runDirection = direction;
    moving = true;
    //Determine which animation to play
    entity.getComponent(AnimationRenderComponent.class)
            .stopAnimation();
    whichAnimation();
    previousDirection = this.runDirection.cpy();
  }

  /**
   * Stops the player from running.
   */
  void stopRunning() {
    this.runDirection = Vector2.Zero;
    update();
    moving = false;
    //Determine which animation to play
    entity.getComponent(AnimationRenderComponent.class)
            .stopAnimation();
    whichAnimation();
  }

  void jump() {
    if (entity.getComponent(PhysicsComponent.class).getBody().getLinearVelocity().y == 0) {
      jumping = true;
    }
    //Determine which animation to play
    entity.getComponent(AnimationRenderComponent.class).stopAnimation();
    whichAnimation();
  }

  void crouch() {
    crouching = true;
    entity.getComponent(ColliderComponent.class).setAsBox(entity.getScale()
            .scl(0.5F));
    //Determine which animation to play
    entity.getComponent(AnimationRenderComponent.class).stopAnimation();
    whichAnimation();
  }

  void stopCrouching() {
    crouching = false;
    entity.getComponent(ColliderComponent.class).setAsBox(entity.getScale()
            .scl(2F));
    update();
    //Determine which animation to play
    entity.getComponent(AnimationRenderComponent.class)
            .stopAnimation();
    whichAnimation();
  }

  /**
   * Makes the player attack.
   */
  void attack() {
    Sound attackSound = ServiceLocator.getResourceService().getAsset(
            "sounds/Impact4.ogg", Sound.class);
    attackSound.play();
    if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("attack-right");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("attack-left");
    }
  }

  void stopAttack() {
    entity.getComponent(ColliderComponent.class).setAsBox(entity.getScale()
            .scl(2F));
    update();
    //Determine which animation to play
    entity.getComponent(AnimationRenderComponent.class)
            .stopAnimation();
    if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("still-right");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("still-left");
    }
  }
  /**
   * Player attack for powers up
   */

  void powerAttack() {
    Sound attackSound = ServiceLocator.getResourceService().getAsset(
            "sounds/Impact4.ogg", Sound.class);
    attackSound.play();
    if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("power-right");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("power-left");
    }
  }

  void stopPowerAttack() {
    entity.getComponent(ColliderComponent.class).setAsBox(entity.getScale()
            .scl(2F));
    update();
    //Determine which animation to play
    entity.getComponent(AnimationRenderComponent.class)
            .stopAnimation();
    if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("still-right");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("still-left");
    }
  }

  void whichAnimation() {
    if (jumping || falling) {
      if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("jump-right");
      } else {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("jump-left");
      }
    } else if (moving) {
      if (crouching) {
        if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
          entity.getComponent(AnimationRenderComponent.class)
                  .startAnimation("crouch-right");
        } else {
          entity.getComponent(AnimationRenderComponent.class)
                  .startAnimation("crouch-left");
        }
      } else {
        if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
          entity.getComponent(AnimationRenderComponent.class)
                  .startAnimation("run-right");
        } else {
          entity.getComponent(AnimationRenderComponent.class)
                  .startAnimation("run-left");
        }
      }
    } else {
      if (crouching) {
        if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
          entity.getComponent(AnimationRenderComponent.class)
                  .startAnimation("crouch-still-right");
        } else {
          entity.getComponent(AnimationRenderComponent.class)
                  .startAnimation("crouch-still-left");
        }
      } else {
        if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
          entity.getComponent(AnimationRenderComponent.class)
                  .startAnimation("still-right");
        } else {
          entity.getComponent(AnimationRenderComponent.class)
                  .startAnimation("still-left");
        }
      }
    }
  }
}
