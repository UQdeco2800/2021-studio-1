package com.deco2800.game.components.player;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.input.InputComponent;
import com.deco2800.game.utils.math.Vector2Utils;
import com.badlogic.gdx.InputProcessor;

/**
 * Input handler for the player for keyboard and touch (mouse) input.
 * This input handler uses keyboard and touch input.
 */
public class TouchPlayerInputComponent extends InputComponent {
  private final Vector2 runDirection = Vector2.Zero.cpy();

  public TouchPlayerInputComponent() {
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
      case Input.Keys.UP:
        runDirection.add(Vector2Utils.UP);
        triggerRunEvent();
        return true;
      case Input.Keys.LEFT:
        runDirection.add(Vector2Utils.LEFT);
        triggerRunEvent();
        return true;
      case Input.Keys.DOWN:
        runDirection.add(Vector2Utils.DOWN);
        triggerRunEvent();
        return true;
      case Input.Keys.RIGHT:
        runDirection.add(Vector2Utils.RIGHT);
        triggerRunEvent();
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
      case Input.Keys.UP:
        runDirection.sub(Vector2Utils.UP);
        triggerRunEvent();
        return true;
      case Input.Keys.LEFT:
        runDirection.sub(Vector2Utils.LEFT);
        triggerRunEvent();
        return true;
      case Input.Keys.DOWN:
        runDirection.sub(Vector2Utils.DOWN);
        triggerRunEvent();
        return true;
      case Input.Keys.RIGHT:
        runDirection.sub(Vector2Utils.RIGHT);
        triggerRunEvent();
        return true;
      default:
        return false;
    }
  }

  /**
   * Triggers the player attack.
   * @return whether the input was processed
   * @see InputProcessor#touchDown(int, int, int, int)
   */
  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    entity.getEvents().trigger("attack");
    return true;
  }

  private void triggerRunEvent() {
    if (runDirection.epsilonEquals(Vector2.Zero)) {
      entity.getEvents().trigger("stop run");
    } else {
      entity.getEvents().trigger("run", runDirection);
    }
  }
}
