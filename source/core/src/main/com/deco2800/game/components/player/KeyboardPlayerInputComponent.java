package com.deco2800.game.components.player;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.input.InputComponent;
import com.deco2800.game.utils.math.Vector2Utils;

/**
 * Input handler for the player for keyboard and touch (mouse) input.
 * This input handler only uses keyboard input.
 */
public class KeyboardPlayerInputComponent extends InputComponent {
  private final Vector2 runDirection = Vector2.Zero.cpy();

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
        return true;
      case Keys.A:
        runDirection.add(Vector2Utils.LEFT);
        triggerRunEvent();
        return true;
      case Keys.S:
        triggerCrouchEvent();
        return true;
      case Keys.D:
        runDirection.add(Vector2Utils.RIGHT);
        triggerRunEvent();
        return true;
      case Keys.SPACE:
        entity.getEvents().trigger("attack");
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
        triggerStopJumpEvent();
        return true;
      case Keys.A:
        runDirection.sub(Vector2Utils.LEFT);
        triggerRunEvent();
        return true;
      case Keys.S:
        triggerStopCrouchEvent();
        return true;
      case Keys.D:
        runDirection.sub(Vector2Utils.RIGHT);
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
}
