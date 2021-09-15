package com.deco2800.game.components.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import com.deco2800.game.components.Component;
import com.deco2800.game.components.powerups.LightningPowerUpComponent;
import com.deco2800.game.components.powerups.ShieldPowerUpComponent;
import com.deco2800.game.components.powerups.SpearPowerUpComponent;

import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
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

  private KeyboardPlayerInputComponent playerInputComponent;

  private Vector2 runDirection = Vector2.Zero.cpy();
  private Vector2 previousDirection = Vector2.Zero.cpy();

  //private final float runAnimationlength = 0.5f;
  private float lastRunAnimationTime;

  public static boolean moving = false;
  private boolean jumping = false;
  private boolean falling = false;
  private boolean crouching = false;

  @Override
  public void create() {
    physicsComponent = entity.getComponent(PhysicsComponent.class);
    playerInputComponent = entity.getComponent(KeyboardPlayerInputComponent.class);
    entity.getEvents().addListener("run", this::run);
    entity.getEvents().addListener("stop run", this::stopRunning);
    entity.getEvents().addListener("jump", this::jump);
    entity.getEvents().addListener("crouch", this::crouch);
    entity.getEvents().addListener("stop crouch", this::stopCrouching);
    entity.getEvents().addListener("obtainPowerUp", this::obtainPowerUp);
    entity.getEvents().addListener("usePowerUp", this::usePowerUp);
  }

  @Override
  public void update() {

    // for pause condition
    if (!playerInputComponent.isPlayerInputEnabled()) {
        return;}

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
    float currentYVelocity = physicsComponent.getBody().getLinearVelocity().y;
    if (currentYVelocity > 0.01f || currentYVelocity < -0.01f) {
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
                  body.getPosition(),
                  true);
        }
      } else { //Applies no force when player is not moving
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

  void crouch() {
    crouching = true;
    entity.getComponent(ColliderComponent.class).setAsBox(entity.getScale()
            .scl(0.5F));
    //Determine which animation to play
    whichAnimation();
  }

  void stopCrouching() {
    crouching = false;
    entity.getComponent(ColliderComponent.class).setAsBox(entity.getScale()
            .scl(2F));
    update();
    //Determine which animation to play
    whichAnimation();
  }

  public void obtainPowerUp(Entity powerUp) {
    switch (powerUp.getType()) {
      case LIGHTNINGPOWERUP:
        powerUp.getComponent(ColliderComponent.class).setEnabled(false);
        powerUp.getComponent(PhysicsComponent.class).getBody().setGravityScale(0);

        entity.getComponent(LightningPowerUpComponent.class).setEnabled(true);
        powerUp.getComponent(ColliderComponent.class).setSensor(true);
        entity.getComponent(LightningPowerUpComponent.class).obtainPowerUp(powerUp);

        powerUp.getComponent(AnimationRenderComponent.class).stopAnimation();
        powerUp.getComponent(AnimationRenderComponent.class).startAnimation("blank");
        break;

      case SPEARPOWERUP:
        powerUp.getComponent(ColliderComponent.class).setSensor(true);
        entity.getComponent(SpearPowerUpComponent.class).setEnabled(true);
        entity.getComponent(SpearPowerUpComponent.class).obtainSpear(powerUp);
        break;

      case SHIELDPOWERUP:
        entity.getEvents().trigger("pickUpShield");
        entity.getComponent(ShieldPowerUpComponent.class).setEnabled(true);
        break;

      default:
        break;
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
          entity.getComponent(AnimationRenderComponent.class).stopAnimation();
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
