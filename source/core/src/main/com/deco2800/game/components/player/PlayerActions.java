package com.deco2800.game.components.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.components.Component;
import com.deco2800.game.components.powerups.LightningPowerUpComponent;
import com.deco2800.game.components.powerups.PowerUpGUIComponent;
import com.deco2800.game.components.powerups.ShieldPowerUpComponent;
import com.deco2800.game.components.powerups.SpearPowerUpComponent;

import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.physics.BodyUserData;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.services.GameTime;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.utils.math.Vector2Utils;

/**
 * Action component for interacting with the player. Player events should be
 * initialised in create() and when triggered should call methods within this
 * class.
 */
public class PlayerActions extends Component {
  private SpeedTypes playerSpeed;
  private long walkTime;
  private long gameTime;
  private Vector2 maxSpeed = new Vector2(8f, 1f); // Metres
  // per second
  private Vector2 mediumSpeed = new Vector2(6f, 1f);
  // Metres per second
  private Vector2 walkSpeed = new Vector2(4f, 1f);
  // Metres per second

  private PhysicsComponent physicsComponent;

  private KeyboardPlayerInputComponent playerInputComponent;

  private Vector2 runDirection = Vector2.Zero.cpy();
  private Vector2 previousDirection = Vector2.Zero.cpy();
  public static boolean moving = false;
  private boolean jumping = false;
  private boolean falling = false;
  private boolean crouching = false;

  @Override
  public void create() {
    walkTime = ServiceLocator.getTimeSource().getTime();
    gameTime = ServiceLocator.getTimeSource().getTime();
    playerSpeed = SpeedTypes.WALK_SPEED;
    physicsComponent = entity.getComponent(PhysicsComponent.class);
    playerInputComponent = entity.getComponent(KeyboardPlayerInputComponent.class);
    entity.getEvents().addListener("run", this::run);
    entity.getEvents().addListener("stop run", this::stopRunning);
    entity.getEvents().addListener("jump", this::jump);
    entity.getEvents().addListener("collisionStart", this::obtainPowerUp);
    entity.getEvents().addListener("usePowerUp", this::usePowerUp);
  }

  @Override
  public void update() {
    whichAnimation();
    // for pause condition
    if (!playerInputComponent.isPlayerInputEnabled()) {
        return;
    }
    if (falling) {
      checkFalling();
    } else if (jumping) {
      applyJumpForce();
    } else if (moving) {
      updateRunningSpeed();
    }

    checkGameTime();
  }

  private void updateRunningSpeed() {
    Body body = physicsComponent.getBody();
    float currentYVelocity = physicsComponent.getBody().getLinearVelocity().y;
    if (currentYVelocity > 0.01f || currentYVelocity < -0.01f) {
      falling = true;
    }

    //Set the player speed
    Vector2 velocity = body.getLinearVelocity();
    Vector2 desiredVelocity;
    switch(playerSpeed) {
      case WALK_SPEED:
        desiredVelocity = runDirection.cpy().scl(walkSpeed);
        if (ServiceLocator.getTimeSource().getTimeSince(walkTime) > 250L) {
          playerSpeed = SpeedTypes.MEDIUM_SPEED;
          walkTime = ServiceLocator.getTimeSource().getTime();
        }
        break;
      case MEDIUM_SPEED:
        desiredVelocity = runDirection.cpy().scl(mediumSpeed);
        if (ServiceLocator.getTimeSource().getTimeSince(walkTime) > 500L) {
          playerSpeed = SpeedTypes.RUN_SPEED;
          walkTime = ServiceLocator.getTimeSource().getTime();
        }
        break;
      case RUN_SPEED:
        desiredVelocity = runDirection.cpy().scl(maxSpeed);
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + playerSpeed);

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
    body.applyLinearImpulse(new Vector2(0f,  40f).scl(body.getMass()),
            body.getWorldCenter(), true);
    falling = true;
    jumping = false;
  }

  /**
   * Checks if the player is still falling by checking their y velocity
   */
  private void checkFalling() {
    Body body = physicsComponent.getBody();
    whichAnimation();
    float currentYVelocity = physicsComponent.getBody().getLinearVelocity().y;
    if (currentYVelocity >= -0.01f  && currentYVelocity <= 0.01f ) {
      falling = false;
      //Determine which animation to play
      whichAnimation();
    } else {
      if (moving) { //Checks if the player is moving and applies respective force
        if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
          body.applyLinearImpulse(new Vector2(0.75f,  0f).scl(body.getMass()),
                  body.getPosition(),
                  true);
        } else {
          body.applyLinearImpulse(new Vector2(-0.75f,  0f).scl(body.getMass()),
                  body.getPosition(), true);
        }
      } else { //Applies no force when player is not moving
        body.applyLinearImpulse(new Vector2(0f,  0f).scl(body.getMass()),
                body.getWorldCenter(), true);
      }
    }
  }

  private void checkGameTime() {
    if (ServiceLocator.getTimeSource().getTimeSince(gameTime) >= 5000L) {
      gameTime = ServiceLocator.getTimeSource().getTime();
      walkSpeed.add(new Vector2(0.1f,0f));
      mediumSpeed.add(new Vector2(0.2f,0f));
      maxSpeed.add(new Vector2(0.3f,0f));
    }
  }

  /**
   * Moves the player towards a given direction and copies that direction as
   * the previous direction
   *
   * @param direction direction to move in
   */
  void run(Vector2 direction) {
    if (!moving) {
      playerSpeed = SpeedTypes.WALK_SPEED;
      walkTime = ServiceLocator.getTimeSource().getTime();
    }
    this.runDirection = direction;
    moving = true;
    //Determine which animation to play
    whichAnimation();
    previousDirection = this.runDirection.cpy();
  }

  /**
   * Stops the player from running.
   */
  void stopRunning() {
    this.runDirection = Vector2.Zero;
    moving = false;
    //Determine which animation to play
    whichAnimation();
  }

  void jump() {
    if (entity.getComponent(PhysicsComponent.class).getBody()
            .getLinearVelocity().y == 0) {
      jumping = true;
    }
    //Determine which animation to play
    whichAnimation();
  }

  public void obtainPowerUp(Fixture playerFixture, Fixture other) {
    BodyUserData otherBody = (BodyUserData) other.getBody().getUserData();

    if (otherBody.entity.getType() == EntityTypes.SPEARPOWERUP
          || otherBody.entity.getType() == EntityTypes.LIGHTNINGPOWERUP
          || otherBody.entity.getType() == EntityTypes.SHIELDPOWERUP) {

      Entity powerUp = otherBody.entity;

      switch (powerUp.getType()) {
        case LIGHTNINGPOWERUP:
          entity.getComponent(LightningPowerUpComponent.class).setEnabled(true);
          break;

        case SPEARPOWERUP:
          entity.getComponent(SpearPowerUpComponent.class).setEnabled(true);
          entity.getComponent(SpearPowerUpComponent.class).obtainSpear();
          break;

        case SHIELDPOWERUP:
          entity.getEvents().trigger("pickUpShield");
          entity.getComponent(ShieldPowerUpComponent.class).setEnabled(true);
          break;

        default:
          break;
      }
      powerUp.flagDelete();
    }
  }

  public void usePowerUp(EntityTypes powerUp) {
    switch (powerUp) {
      case LIGHTNINGPOWERUP:
        if (entity.getComponent(LightningPowerUpComponent.class).getEnabled()) {
          entity.getComponent(LightningPowerUpComponent.class).activate();
        }
        break;

      case SPEARPOWERUP:
        if (entity.getComponent(SpearPowerUpComponent.class).getEnabled()) {
          entity.getComponent(SpearPowerUpComponent.class).activate();
          whichAnimation();
        }
        break;

      case SHIELDPOWERUP:
        if (entity.getComponent(ShieldPowerUpComponent.class).getEnabled()) {
          entity.getComponent(ShieldPowerUpComponent.class).activate();
        }
        break;

      default:
        break;
    }
  }

  public Vector2 getRunDirection() {
    return runDirection.cpy();
  }

  public Vector2 getPreviousDirection() {
    return previousDirection.cpy();
  }

  public boolean isMoving() {
    return moving;
  }

  public boolean isJumping() {
    return (jumping || falling);
  }

  public boolean isCrouching() {
    return crouching;
  }

  /**
   * Determine which animation to play based off of which triggers are active
   */
  private void whichAnimation() {
    entity.getComponent(AnimationRenderComponent.class).stopAnimation();
    if (entity.getComponent(ShieldPowerUpComponent.class).getActive()) {
      if (isJumping()) {
        shieldJumpingAnimations();
      } else if (isMoving()) {
        shieldMovingAnimations();
      } else {
        shieldStillAnimations();
      }
    } else if (entity.getComponent(SpearPowerUpComponent.class).getEnabled()
            && !entity.getComponent(SpearPowerUpComponent.class).getActive()) {
      if (isJumping()) {
        spearJumpingAnimations();
      } else if (isMoving()) {
        spearMovingAnimations();
      } else {
        spearStillAnimations();
      }
    } else {
      if (isJumping()) {
        jumpingAnimations();
      } else if (isMoving()) {
        movingAnimations();
      } else {
        stillAnimations();
      }
    }
  }

  /**
   * Determine which animation to play if the player is standing still
   */
  private void stillAnimations() {
    if (isCrouching()) {
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

  /**
   * Determine which animation to play if the player is moving
   */
  private void movingAnimations() {
    if (isCrouching()) {
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
  }

  /**
   * Determine which animation to play if the player is jumping
   */
  private void jumpingAnimations() {
    if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("jump-right");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("jump-left");
    }
  }

  /**
   * Determine which animation to play if the player is standing still with
   * the spear power up
   */
  private void spearStillAnimations() {
    if (isCrouching()) {
      if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("crouch-still-right");
      } else {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("spear-crouch-still-left");
      }
    } else {
      if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("spear-still-right");
      } else {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("spear-still-left");
      }
    }
  }

  /**
   * Determine which animation to play if the player is moving with
   * the spear power up
   */
  private void spearMovingAnimations() {
    if (isCrouching()) {
      if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("spear-crouch-right");
      } else {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("spear-crouch-left");
      }
    } else {
      if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("spear-run-right");
      } else {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("spear-run-left");
      }
    }
  }

  /**
   * Determine which animation to play if the player is jumping with
   * the spear power up
   */
  private void spearJumpingAnimations() {
    if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("spear-jump-right");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("spear-jump-left");
    }
  }

  /**
   * Determine which animation to play if the player is shield with the spear
   * power up
   */
  private void shieldStillAnimations() {
    if (isCrouching()) {
      if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("shield-crouch-still-right");
      } else {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("shield-crouch-still-left");
      }
    } else {
      if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("shield-still-right");
      } else {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("shield-still-left");
      }
    }
  }

  /**
   * Determine which animation to play if the player is shield with the spear
   * power up
   */
  private void shieldMovingAnimations() {
    if (isCrouching()) {
      if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("shield-crouch-right");
      } else {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("shield-crouch-left");
      }
    } else {
      if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("shield-run-right");
      } else {
        entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("shield-run-left");
      }
    }
  }

  /**
   * Determine which animation to play if the player is shield with the spear
   * power up
   */
  private void shieldJumpingAnimations() {
    if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("shield-jump-right");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("shield-jump-left");
    }
  }
}

enum SpeedTypes {
  RUN_SPEED,
  MEDIUM_SPEED,
  WALK_SPEED
}