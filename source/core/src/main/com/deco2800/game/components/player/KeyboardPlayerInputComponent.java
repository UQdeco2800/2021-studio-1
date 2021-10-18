package com.deco2800.game.components.player;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.input.InputComponent;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.utils.math.Vector2Utils;

/**
 * Input handler for the player for keyboard and touch (mouse) input.
 * This input handler only uses keyboard input.
 */
public class KeyboardPlayerInputComponent extends InputComponent {
  private final Vector2 runDirection = Vector2.Zero.cpy();
  public static int isDirection = 0;
  public static int isMoving = 0;
  public KeyboardPlayerInputComponent() {
    super(5);
  }

  /**
   * Triggers player events on specific keycodes.
   *
   * @return whether the input was processed
   * @see InputProcessor#keyDown(int)
   */
  @Override
  public boolean keyDown(int keycode) {

    switch (keycode) {
      case Keys.UP:
      case Keys.SPACE:
      case Keys.W:
        triggerJumpEvent();
        return true;
      case Keys.LEFT:
      case Keys.A:
        if (!runDirection.epsilonEquals(Vector2Utils.LEFT) ) {
          runDirection.add(Vector2Utils.LEFT);
        }
        triggerRunEvent();
        return true;
      case Keys.DOWN:
      case Keys.S:
        triggerCrouchEvent();
        return true;
      case Keys.RIGHT:
      case Keys.D:
      if (!runDirection.epsilonEquals(Vector2Utils.RIGHT) ) {
          runDirection.add(Vector2Utils.RIGHT);
        }
        triggerRunEvent();
        return true;
      case Keys.L:
        triggerLightningEvent();
        return true;
      case Keys.K:
        triggerSpearEvent();
        entity.getEvents().trigger("useSpearAttack");
        return true;
      default:
        return false;
    }
  }

  /**
   * Triggers player events on specific keycodes.
   *
   * @return whether the input was processed
   * @see InputProcessor#keyUp(int)
   */
  @Override
  public boolean keyUp(int keycode) {

    triggerAnyMovement();
    switch (keycode) {
      case Keys.UP:
      case Keys.SPACE:
      case Keys.W:
        return true;
      case Keys.LEFT:
      case Keys.A:
        if (runDirection.epsilonEquals(Vector2Utils.LEFT)) {
          runDirection.sub(Vector2Utils.LEFT);
        }
        triggerRunEvent();
        return true;
      case Keys.DOWN:
      case Keys.S:
        triggerStopCrouchEvent();
        return true;
      case Keys.RIGHT:
      case Keys.D:
        if (runDirection.epsilonEquals(Vector2Utils.RIGHT)) {
          runDirection.sub(Vector2Utils.RIGHT);
        }
        triggerRunEvent();
        return true;
      default:
        return false;
    }
  }

  private void triggerRunEvent() {
    if (runDirection.epsilonEquals(Vector2.Zero)) {
      entity.getEvents().trigger("stop run");
    } else {
      entity.getEvents().trigger("run", runDirection);
    }
  }

  /*
   * Returns a boolean representing player input as active and inactive
   *    @returns - true if player input is being handled
   *               false otherwise
   */
  public boolean isPlayerInputEnabled () {
      // current condition for input enabled is that the game is incrementing in time
      // feel free to add / change conditions
      return ServiceLocator.getTimeSource().getDeltaTime() != 0;
  }

  private void triggerJumpEvent() {
    entity.getEvents().trigger("jump");
  }

  private void triggerCrouchEvent() {
    entity.getEvents().trigger("crouch");
  }

  private void triggerStopCrouchEvent() {
    entity.getEvents().trigger("stop crouch");
  }

  private void triggerLightningEvent() {
    entity.getEvents().trigger("usePowerUp", EntityTypes.LIGHTNINGPOWERUP);
    entity.getEvents().trigger("updatePowerUps");
  }

  private void triggerSpearEvent() {
    entity.getEvents().trigger("usePowerUp", EntityTypes.SPEARPOWERUP);
    entity.getEvents().trigger("updatePowerUps");
  }

  private void triggerAnyMovement() {
    entity.getEvents().trigger("anyMovement");
  }
}
