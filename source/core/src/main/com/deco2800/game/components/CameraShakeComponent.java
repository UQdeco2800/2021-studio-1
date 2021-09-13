package com.deco2800.game.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.physics.BodyUserData;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.components.CameraComponent;
import com.badlogic.gdx.graphics.Camera;

public class CameraShakeComponent extends Component {
  private short targetLayer;
  private float knockbackForce = 0f;
  private CombatStatsComponent combatStats;
  private HitboxComponent hitboxComponent;
  private Entity target;
  private CameraComponent cameraComponent;
  private float toggle;

  public CameraShakeComponent(Entity target, CameraComponent cameraComponent) {
    this.target = target;
    this.cameraComponent = cameraComponent;
    this.toggle = 0.1f;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("collisionStart", this::onCollisionStart);
  }

  private void onCollisionStart(Fixture me, Fixture other) {
    float distance = entity.getPosition().dst(target.getPosition());

    if (distance < 30f) {

      if (this.toggle == 0.1f) {
        this.toggle = -0.1f;
      }
      else {
        this.toggle = 0.1f;
      }
      //System.out.print(this.toggle);

      entity.getEvents().trigger("moveRightAngry");
      //System.out.print("close");
      //System.out.print(cameraComponent);
      cameraComponent.set_offset(this.toggle);
      cameraComponent.update();

    }
    else {
      entity.getEvents().trigger("moveRight");
      //System.out.print("far");
     cameraComponent.set_offset(0);
      cameraComponent.update();
    }

  }

}