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


  public CameraShakeComponent(Entity target) {
    this.target = target;
  }


  @Override
  public void create() {
    entity.getEvents().addListener("collisionStart", this::onCollisionStart);
  }

  private void onCollisionStart(Fixture me, Fixture other) {

    float dis = entity.getPosition().dst(target.getPosition());
    //System.out.print(dis);

    if (dis < 14f) {
      //System.out.print("mayday");
      Vector2 position = entity.getPosition();
      //System.out.print(target.getComponent(CameraComponent.class));
      target.getComponent(CameraComponent.class).getCamera().position.set(position.x-10f, position.y-10f, 0f);
      target.getComponent(CameraComponent.class).getCamera().update();
      //camera.positionW
    }

  }

}
