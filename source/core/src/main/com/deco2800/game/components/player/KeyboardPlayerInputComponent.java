package com.deco2800.game.components.player;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.components.powerups.LightningPowerUpComponent;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.input.InputComponent;
import com.deco2800.game.utils.math.Vector2Utils;

/**
 * Input handler for the player for keyboard and touch (mouse) input.
 * This input handler only uses keyboard input.
 */
public class KeyboardPlayerInputComponent extends InputComponent {
  private final Vector2 runDirection = Vector2.Zero.cpy();
  public static int isDirection = 0;
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
      case Keys.W:
        triggerJumpEvent();
        isDirection = 1;
        return true;
      case Keys.A:
        runDirection.add(Vector2Utils.LEFT);
        triggerRunEvent();
        isDirection = 2;
        return true;
      case Keys.S:
        triggerCrouchEvent();
        isDirection = 3;
        return true;
      case Keys.D:
        runDirection.add(Vector2Utils.RIGHT);
        triggerRunEvent();
        isDirection = 4;
        return true;
      case Keys.SPACE:
        entity.getEvents().trigger("attack");
        return true;
      /* Keys.J to test powers up attack */
      case Keys.J:
        entity.getEvents().trigger("powerAttack");
        return true;
      case Keys.L:
        triggerLightningEvent();
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
    switch (keycode) {
      case Keys.W:
        isDirection = 1;
        return true;
      case Keys.A:
        runDirection.sub(Vector2Utils.LEFT);
        triggerRunEvent();
        isDirection = 2;
        return true;
      case Keys.S:
        triggerStopCrouchEvent();
        isDirection = 3;
        return true;
      case Keys.D:
        runDirection.sub(Vector2Utils.RIGHT);
        triggerRunEvent();
        isDirection = 4;
        return true;
      case Keys.SPACE:
        triggerStopAttackEvent();
        return true;
      /* Keys.J to test powers up attack */
      case Keys.J:
        triggerStopPowerAttackEvent();
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

  private void triggerJumpEvent() {
    entity.getEvents().trigger("jump");
  }

  private void triggerStopJumpEvent() {
    entity.getEvents().trigger("stop jump");
  }

  private void triggerCrouchEvent() {
    entity.getEvents().trigger("crouch");
  }

  private void triggerStopCrouchEvent() {
    entity.getEvents().trigger("stop crouch");
  }

  private void triggerStopAttackEvent() {
    entity.getEvents().trigger("stop attack");
  }

  private void triggerStopPowerAttackEvent() {
    entity.getEvents().trigger("stop stopPowerAttack");
  }

  private void triggerLightningEvent() {
    entity.getEvents().trigger("usePowerUp",
        EntityTypes.LIGHTNINGPOWERUP);
  }
}
