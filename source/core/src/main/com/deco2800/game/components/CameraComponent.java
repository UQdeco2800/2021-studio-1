package com.deco2800.game.components;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.components.player.PlayerActions;
import com.deco2800.game.components.player.KeyboardPlayerInputComponent;

public class CameraComponent extends Component {
  private final Camera camera;
  private Vector2 lastPosition;
  private Vector2 targetCenterPosition;
  private float offset = 0;

  public CameraComponent() {
    this(new OrthographicCamera());
  }

  public CameraComponent(Camera camera) {
    this.camera = camera;
    lastPosition = Vector2.Zero.cpy();
  }

  @Override
  public void update() {
    Vector2 position = entity.getPosition();
    if (!lastPosition.epsilonEquals(entity.getPosition())) {
      camera.position.set(position.x + offset, position.y + offset, 0f);
      lastPosition = position;
      camera.update();

    }

  }
  public void set_offset(float offset) {
    this.offset = offset;
  }
  public float get_offset() {
    return this.offset;
  }


     /* Determine whether current player is moving and which direction
    if (PlayerActions.moving == true ){
      if(KeyboardPlayerInputComponent.isDirection == 1){
        position.y = position.y + 3f;
        camera.position.set(position.x, position.y,0f);
        camera.update();
      }
      if(KeyboardPlayerInputComponent.isDirection == 2){
        position.x = position.x + 3f;
        camera.position.set(position.x, position.y,0f);
        camera.update();
      }
      if(KeyboardPlayerInputComponent.isDirection == 3){
        position.y = position.y - 3f;
        camera.position.set(position.x, position.y,0f);
        camera.update();
      }
      if(KeyboardPlayerInputComponent.isDirection == 4){
        position.x = position.x - 3f;
        camera.position.set(position.x, position.y,0f);
        camera.update();
      }
    }
      */

  public Matrix4 getProjectionMatrix() {
    return camera.combined;
  }

  public Camera getCamera() {
    return camera;
  }

  public void resize(int screenWidth, int screenHeight, float gameWidth) {
    float ratio = (float) screenHeight / screenWidth;
    camera.viewportWidth = gameWidth;
    camera.viewportHeight = gameWidth * ratio;
    camera.update();
  }
}
