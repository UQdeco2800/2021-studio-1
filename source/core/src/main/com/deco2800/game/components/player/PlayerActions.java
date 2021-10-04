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

    // These are some constants to hold the animations in.
    // To access for example, the animation for jumping right with a spear, call
    // animations[SPEAR][JUMPING][NOT_CROUCHING][RIGHT].
    private static final int SHIELD = 0;
    private static final int SPEAR = 1;
    private static final int PLAYER = 2;

    private static final int JUMPING = 0;
    private static final int MOVING = 1;
    private static final int STILL = 2;

    private static final int CROUCHING = 0;
    private static final int NOT_CROUCHING = 1;

    private static final int RIGHT = 0;
    private static final int LEFT = 1;

    private static final String[][][][] animations = new String[3][3][2][2];

    // Set animation array. Change these values to change animation names.
    static {
        // Shield
        animations[SHIELD][JUMPING][CROUCHING][RIGHT] = null;
        animations[SHIELD][JUMPING][CROUCHING][LEFT] = null;
        animations[SHIELD][JUMPING][NOT_CROUCHING][RIGHT] = "shield-jump-right";
        animations[SHIELD][JUMPING][NOT_CROUCHING][LEFT] = "shield-jump-right";

        animations[SHIELD][MOVING][CROUCHING][RIGHT] = "shield-crouch-right";
        animations[SHIELD][MOVING][CROUCHING][LEFT] = "shield-crouch-left";
        animations[SHIELD][MOVING][NOT_CROUCHING][RIGHT] = "shield-run-right";
        animations[SHIELD][MOVING][NOT_CROUCHING][LEFT] = "shield-run-left";

        animations[SHIELD][STILL][CROUCHING][RIGHT] = "shield-crouch-still-right";
        animations[SHIELD][STILL][CROUCHING][LEFT] = "shield-crouch-still-left";
        animations[SHIELD][STILL][NOT_CROUCHING][RIGHT] = "shield-still-right";
        animations[SHIELD][STILL][NOT_CROUCHING][LEFT] = "shield-still-left";

        // Spear
        animations[SPEAR][JUMPING][CROUCHING][RIGHT] = null;
        animations[SPEAR][JUMPING][CROUCHING][LEFT] = null;
        animations[SPEAR][JUMPING][NOT_CROUCHING][RIGHT] = "spear-jump-right";
        animations[SPEAR][JUMPING][NOT_CROUCHING][LEFT] = "spear-jump-left";

        animations[SPEAR][MOVING][CROUCHING][RIGHT] = "spear-crouch-right";
        animations[SPEAR][MOVING][CROUCHING][LEFT] = "spear-crouch-left";
        animations[SPEAR][MOVING][NOT_CROUCHING][RIGHT] = "spear-run-right";
        animations[SPEAR][MOVING][NOT_CROUCHING][LEFT] = "spear-run-left";

        animations[SPEAR][STILL][CROUCHING][RIGHT] = "crouch-still-right";
        animations[SPEAR][STILL][CROUCHING][LEFT] = "spear-crouch-still-left";
        animations[SPEAR][STILL][NOT_CROUCHING][RIGHT] = "spear-still-right";
        animations[SPEAR][STILL][NOT_CROUCHING][LEFT] = "spear-still-left";

        // Player
        animations[PLAYER][JUMPING][CROUCHING][RIGHT] = null;
        animations[PLAYER][JUMPING][CROUCHING][LEFT] = null;
        animations[PLAYER][JUMPING][NOT_CROUCHING][RIGHT] = "jump-right";
        animations[PLAYER][JUMPING][NOT_CROUCHING][LEFT] = "jump-left";

        animations[PLAYER][MOVING][CROUCHING][RIGHT] = "crouch-right";
        animations[PLAYER][MOVING][CROUCHING][LEFT] = "crouch-left";
        animations[PLAYER][MOVING][NOT_CROUCHING][RIGHT] = "run-right";
        animations[PLAYER][MOVING][NOT_CROUCHING][LEFT] = "run-left";

        animations[PLAYER][STILL][CROUCHING][RIGHT] = "crouch-still-right";
        animations[PLAYER][STILL][CROUCHING][LEFT] = "crouch-still-left";
        animations[PLAYER][STILL][NOT_CROUCHING][RIGHT] = "still-right";
        animations[PLAYER][STILL][NOT_CROUCHING][LEFT] = "still-left";
    }

    private PhysicsComponent physicsComponent;

    private KeyboardPlayerInputComponent playerInputComponent;

  private Vector2 runDirection = Vector2.Zero.cpy();
  private Vector2 previousDirection = Vector2.Zero.cpy();
  public static boolean moving = false;
  private boolean jumping = false;
  private boolean falling = false;

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
    entity.getEvents().addListener("useSpearAttack", this::useSpearAttack);
  }

  @Override
  public void update() {
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
    body.applyLinearImpulse(new Vector2(0f, 40f).scl(body.getMass()),
            body.getWorldCenter(), true);
    falling = true;
    jumping = false;
    whichAnimation();
  }

  /**
   * Checks if the player is still falling by checking their y velocity
   */
  private void checkFalling() {
    Body body = physicsComponent.getBody();
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

  public Vector2 getPreviousDirection() {
    return previousDirection.cpy();
  }

    public boolean isMoving() {
        return moving;
    }

  public boolean isJumping() {
    return jumping;
  }

  public boolean isFalling() {
    return falling;
  }

  /**
   * Determine which animation to play based off of which triggers are active
   */
  private void whichAnimation() {
      entity.getComponent(AnimationRenderComponent.class).stopAnimation();
      if (entity.getComponent(ShieldPowerUpComponent.class).getActive()
              && entity.getComponent(SpearPowerUpComponent.class).getEnabled()) {
          if (isJumping()) {
              spearShieldJumpingAnimations();
          } else if (isFalling()) {
              spearShieldFallingAnimations();
          } else if (isMoving()) {
              spearShieldMovingAnimations();
          } else {
              spearShieldStillAnimations();
          }
      } else if (entity.getComponent(ShieldPowerUpComponent.class).getActive()) {
          if (isJumping()) {
              shieldJumpingAnimations();
          } else if (isFalling()) {
              shieldFallingAnimations();
          } else if (isMoving()) {
              shieldMovingAnimations();
          } else {
              shieldStillAnimations();
          }
      } else if (entity.getComponent(SpearPowerUpComponent.class).getEnabled()
              && !entity.getComponent(SpearPowerUpComponent.class).getActive()) {
          if (isJumping()) {
              spearJumpingAnimations();
          } else if (isFalling()) {
              spearFallingAnimations();
          } else if (isMoving()) {
              spearMovingAnimations();
          } else {
              spearStillAnimations();
          }
      } else {
          if (isJumping()) {
              jumpingAnimations();
          } else if (isFalling()) {
              fallingAnimations();
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
          if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
              entity.getComponent(AnimationRenderComponent.class)
                      .startAnimation("still-right");
          } else {
              entity.getComponent(AnimationRenderComponent.class)
                      .startAnimation("still-left");
          }
      }

  /**
   * Determine which animation to play if the player is moving
   */
  private void movingAnimations() {
              if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
                  entity.getComponent(AnimationRenderComponent.class)
                          .startAnimation("run-right");
              } else {
                  entity.getComponent(AnimationRenderComponent.class)
                          .startAnimation("run-left");
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
   * Determine which animation to play if the player is falling
   */
  private void fallingAnimations() {
    if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("fall-right");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("fall-left");
    }
  }

  /**
   * Determine which animation to play if the player is standing still with
   * the spear power up
   */
  private void spearStillAnimations() {
    if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("still-right-spear");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("still-left-spear");
    }
  }

  /**
   * Determine which animation to play if the player is moving with
   * the spear power up
   */
  private void spearMovingAnimations() {
    if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("run-right-spear");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("run-left-spear");
    }
  }

  /**
   * Determine which animation to play if the player is jumping with
   * the spear power up
   */
  private void spearJumpingAnimations() {
    if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("jump-right-spear");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("jump-left-spear");
    }
  }

  /**
   * Determine which animation to play if the player is falling with
   * the spear power up
   */
  private void spearFallingAnimations() {
    if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("fall-right-spear");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("fall-left-spear");
    }
  }

  /**
   * Determine which animation to play if the player is still with the shield
   * power up
   */
  private void shieldStillAnimations() {
    if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("still-right-shield");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("still-left-shield");
    }
  }

  /**
   * Determine which animation to play if the player is moving with the shield
   * power up
   */
  private void shieldMovingAnimations() {
    if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("run-right-shield");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("run-left-shield");
    }
  }

  /**
   * Determine which animation to play if the player is jumping with the shield
   * power up
   */
  private void shieldJumpingAnimations() {
    if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("jump-right-shield");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("jump-left-shield");
    }
  }

  /**
   * Determine which animation to play if the player is falling with the shield
   * power up
   */
  private void shieldFallingAnimations() {
    if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("fall-right-shield");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("fall-left-shield");
    }
  }

  /**
   * Determine which animation to play if the player is still with the spear and shield
   * power up
   */
  private void spearShieldStillAnimations() {
    if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("still-right-spear-shield");
    } else {
      entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("still-left-spear-shield");
    }
  }

  /**
   * Determine which animation to play if the player is moving with the spear and shield
   * power up
   */
  private void spearShieldMovingAnimations() {
      if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
          entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("run-right-spear-shield");
      } else {
          entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("run-left-spear-shield");
      }
  }

  /**
   * Determine which animation to play if the player is jumping with the spear and shield
   * power up
   */
  private void spearShieldJumpingAnimations() {
      if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
          entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("jump-right-spear-shield");
      } else {
          entity.getComponent(AnimationRenderComponent.class)
              .startAnimation("jump-left-spear-shield");
      }
  }

  /**
   * Determine which animation to play if the player is falling with the spear and shield
   * power up
   */
  private void spearShieldFallingAnimations() {
      if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
          entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("fall-right-spear-shield");
      } else {
          entity.getComponent(AnimationRenderComponent.class)
                .startAnimation("fall-left-spear-shield");
      }
  }

  /**
   * Determine which animation to play if the player is going to use spear
   * power up
   */
  private void useSpearAttack() {
      if (entity.getComponent(ShieldPowerUpComponent.class).getActive()
              && entity.getComponent(SpearPowerUpComponent.class).getEnabled()) {
          if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
              entity.getComponent(AnimationRenderComponent.class)
                      .startAnimation("throwing-spear-with-shield-right");
          } else {
              entity.getComponent(AnimationRenderComponent.class)
                      .startAnimation("throwing-spear-with-shield-left");
          }

      } else if (entity.getComponent(SpearPowerUpComponent.class).getEnabled()) {
          if (this.previousDirection.hasSameDirection(Vector2Utils.RIGHT)) {
              entity.getComponent(AnimationRenderComponent.class)
                      .startAnimation("throwing-spear-right");
          } else {
              entity.getComponent(AnimationRenderComponent.class)
                      .startAnimation("throwing-spear-left");
          }
      }
  }
}

enum SpeedTypes {
  RUN_SPEED,
  MEDIUM_SPEED,
  WALK_SPEED
}