package com.deco2800.game.components.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.components.Component;
import com.deco2800.game.components.powerups.LightningPowerUpComponent;
import com.deco2800.game.components.powerups.ShieldPowerUpComponent;
import com.deco2800.game.components.powerups.SpearPowerUpComponent;

import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.physics.BodyUserData;
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

    // These are some constants to hold the animations in.
    // To access for example, the animation for jumping right with a spear, call
    // animations[SPEAR][JUMPING][NOT_CROUCHING][RIGHT].
    private static final int SHIELD = 0;
    private static final int SPEAR = 1;
    private static final int PLAYER = 2;

    private static final int JUMPING_INT = 0;
    private static final int MOVING_INT = 1;
    private static final int STILL_INT = 2;

    private static final int CROUCHING = 0;
    private static final int NOT_CROUCHING = 1;

    private static final int RIGHT = 0;
    private static final int LEFT = 1;

    private static final String[][][][] animations = new String[3][3][2][2];

    // Set animation array. Change these values to change animation names.
    static {
        // Shield
        animations[SHIELD][JUMPING_INT][CROUCHING][RIGHT] = null;
        animations[SHIELD][JUMPING_INT][CROUCHING][LEFT] = null;
        animations[SHIELD][JUMPING_INT][NOT_CROUCHING][RIGHT] = "shield-jump-right";
        animations[SHIELD][JUMPING_INT][NOT_CROUCHING][LEFT] = "shield-jump-right";

        animations[SHIELD][MOVING_INT][CROUCHING][RIGHT] = "shield-crouch-right";
        animations[SHIELD][MOVING_INT][CROUCHING][LEFT] = "shield-crouch-left";
        animations[SHIELD][MOVING_INT][NOT_CROUCHING][RIGHT] = "shield-run-right";
        animations[SHIELD][MOVING_INT][NOT_CROUCHING][LEFT] = "shield-run-left";

        animations[SHIELD][STILL_INT][CROUCHING][RIGHT] = "shield-crouch-still-right";
        animations[SHIELD][STILL_INT][CROUCHING][LEFT] = "shield-crouch-still-left";
        animations[SHIELD][STILL_INT][NOT_CROUCHING][RIGHT] = "shield-still-right";
        animations[SHIELD][STILL_INT][NOT_CROUCHING][LEFT] = "shield-still-left";

        // Spear
        animations[SPEAR][JUMPING_INT][CROUCHING][RIGHT] = null;
        animations[SPEAR][JUMPING_INT][CROUCHING][LEFT] = null;
        animations[SPEAR][JUMPING_INT][NOT_CROUCHING][RIGHT] = "spear-jump-right";
        animations[SPEAR][JUMPING_INT][NOT_CROUCHING][LEFT] = "spear-jump-left";

        animations[SPEAR][MOVING_INT][CROUCHING][RIGHT] = "spear-crouch-right";
        animations[SPEAR][MOVING_INT][CROUCHING][LEFT] = "spear-crouch-left";
        animations[SPEAR][MOVING_INT][NOT_CROUCHING][RIGHT] = "spear-run-right";
        animations[SPEAR][MOVING_INT][NOT_CROUCHING][LEFT] = "spear-run-left";

        animations[SPEAR][STILL_INT][CROUCHING][RIGHT] = "crouch-still-right";
        animations[SPEAR][STILL_INT][CROUCHING][LEFT] = "spear-crouch-still-left";
        animations[SPEAR][STILL_INT][NOT_CROUCHING][RIGHT] = "spear-still-right";
        animations[SPEAR][STILL_INT][NOT_CROUCHING][LEFT] = "spear-still-left";

        // Player
        animations[PLAYER][JUMPING_INT][CROUCHING][RIGHT] = null;
        animations[PLAYER][JUMPING_INT][CROUCHING][LEFT] = null;
        animations[PLAYER][JUMPING_INT][NOT_CROUCHING][RIGHT] = "jump-right";
        animations[PLAYER][JUMPING_INT][NOT_CROUCHING][LEFT] = "jump-left";

        animations[PLAYER][MOVING_INT][CROUCHING][RIGHT] = "crouch-right";
        animations[PLAYER][MOVING_INT][CROUCHING][LEFT] = "crouch-left";
        animations[PLAYER][MOVING_INT][NOT_CROUCHING][RIGHT] = "run-right";
        animations[PLAYER][MOVING_INT][NOT_CROUCHING][LEFT] = "run-left";

        animations[PLAYER][STILL_INT][CROUCHING][RIGHT] = "crouch-still-right";
        animations[PLAYER][STILL_INT][CROUCHING][LEFT] = "crouch-still-left";
        animations[PLAYER][STILL_INT][NOT_CROUCHING][RIGHT] = "still-right";
        animations[PLAYER][STILL_INT][NOT_CROUCHING][LEFT] = "still-left";
    }

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
        physicsComponent = entity.getComponent(PhysicsComponent.class);
        playerInputComponent = entity.getComponent(KeyboardPlayerInputComponent.class);
        entity.getEvents().addListener("run", this::run);
        entity.getEvents().addListener("stop run", this::stopRunning);
        entity.getEvents().addListener("jump", this::jump);
        entity.getEvents().addListener("crouch", this::crouch);
        entity.getEvents().addListener("stop crouch", this::stopCrouching);
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
        body.applyLinearImpulse(new Vector2(0f, 40f).scl(body.getMass()),
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
        if (currentYVelocity >= -0.01f && currentYVelocity <= 0.01f) {
            falling = false;
            //Determine which animation to play
            whichAnimation();
        } else {
            if (moving) { //Checks if the player is moving and applies respective force
                if (this.runDirection.hasSameDirection(Vector2Utils.RIGHT)) {
                    body.applyLinearImpulse(new Vector2(0.75f, 0f).scl(body.getMass()),
                            body.getPosition(),
                            true);
                } else {
                    body.applyLinearImpulse(new Vector2(-0.75f, 0f).scl(body.getMass()),
                            body.getPosition(),
                            true);
                }
            } else { //Applies no force when player is not moving
                body.applyLinearImpulse(new Vector2(0f, 0f).scl(body.getMass()),
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

        int component;
        if (entity.getComponent(ShieldPowerUpComponent.class).getActive()) {
            component = SHIELD;
        } else if (entity.getComponent(SpearPowerUpComponent.class).getEnabled()
                && !entity.getComponent(SpearPowerUpComponent.class).getActive()) {
            component = SPEAR;
        } else {
            component = PLAYER;
        }

        if (isJumping()) {
            jumpingAnimations(component);
        } else if (isMoving()) {
            movingAnimations(component);
        } else {
            stillAnimations(component);
        }
    }

    /**
     * Animate this.entity with rightAnimation if direction faces right, else animate with
     * leftAnimation.
     *
     * @param direction      direction to test against
     * @param rightAnimation name of animation to use if direction faces right
     * @param leftAnimation  name of animation to use if direction faces left
     */
    private void animate(Vector2 direction, String rightAnimation, String leftAnimation) {
        if (direction.hasSameDirection(Vector2Utils.RIGHT)) {
            entity.getComponent(AnimationRenderComponent.class).startAnimation(rightAnimation);
        } else {
            entity.getComponent(AnimationRenderComponent.class).startAnimation(leftAnimation);
        }
    }

    /**
     * Determine which animation to play if the player is standing still
     *
     * @param component first index in animations array, should be either SHIELD, SPEAR, or PLAYER.
     */
    private void stillAnimations(int component) {
        if (isCrouching()) {
            animate(this.previousDirection,
                    animations[component][STILL_INT][CROUCHING][RIGHT],
                    animations[component][STILL_INT][CROUCHING][LEFT]);
        } else {
            animate(this.previousDirection,
                    animations[component][STILL_INT][NOT_CROUCHING][RIGHT],
                    animations[component][STILL_INT][NOT_CROUCHING][LEFT]);
        }
    }

    /**
     * Determine which animation to play if the player is moving
     *
     * @param component first index in animations array, should be either SHIELD, SPEAR, or PLAYER.
     */
    private void movingAnimations(int component) {
        if (isCrouching()) {
            animate(this.previousDirection,
                    animations[component][MOVING_INT][CROUCHING][RIGHT],
                    animations[component][MOVING_INT][CROUCHING][LEFT]);
        } else {
            animate(this.runDirection,
                    animations[component][MOVING_INT][NOT_CROUCHING][RIGHT],
                    animations[component][MOVING_INT][NOT_CROUCHING][LEFT]);
        }
    }

    /**
     * Determine which animation to play if the player is jumping
     *
     * @param component first index in animations array, should be either SHIELD, SPEAR, or PLAYER.
     */
    private void jumpingAnimations(int component) {
        animate(this.previousDirection,
                animations[component][JUMPING_INT][NOT_CROUCHING][RIGHT],
                animations[component][JUMPING_INT][NOT_CROUCHING][LEFT]);
    }
}
