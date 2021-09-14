package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.components.powerups.ShieldPowerUpComponent;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.extensions.GameExtension;
import com.deco2800.game.physics.PhysicsService;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.components.CameraShakeComponent;
import com.deco2800.game.areas.terrain.TerrainComponent.TerrainOrientation;
import com.deco2800.game.components.CameraComponent;
import com.deco2800.game.entities.factories.NPCFactory;


import com.deco2800.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

//private TerrainFactory terrainFactory;
//private TerrainComponent terrainComponent;

@ExtendWith(GameExtension.class)
class CameraShakeComponentTest {
  @BeforeEach
  void beforeEach() {
    ServiceLocator.registerPhysicsService(new PhysicsService());
  }

  /**
   * Checks the offsets
   */
  @Test
  void checkOffset() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity target = createTarget(targetLayer,attacker);

    assertEquals(0,attacker.getComponent(CameraComponent.class).getOffset());
  }

  /**
   * Checks the distance
   */
  @Test
  void checkDistance() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity target = createTarget(targetLayer,attacker);

    float distance = target.getPosition().dst(attacker.getPosition());

    assertEquals(0,distance);
  }


  /**
   * Sets the offset and checks it
   */
  @Test
  void setOffset() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity target = createTarget(targetLayer,attacker);

    attacker.getComponent(CameraComponent.class).setOffset(10);

    assertEquals(10,attacker.getComponent(CameraComponent.class).getOffset());
  }



  Entity createAttacker(short targetLayer) {
    Entity entity =
        new Entity()
            .addComponent(new TouchAttackComponent(targetLayer))
            .addComponent(new CombatStatsComponent(0, 10))
            .addComponent(new PhysicsComponent())
                .addComponent(new CameraComponent())
            .addComponent(new HitboxComponent());
    entity.create();
    return entity;
  }

  Entity createTarget(short layer, Entity attacker) {
    Entity sfx = NPCFactory.createScreenFX(attacker);
    Entity target =
        new Entity()
            .addComponent(new CombatStatsComponent(10, 0))
            .addComponent(new PhysicsComponent())
            .addComponent(new HitboxComponent().setLayer(layer))
                .addComponent(new CameraShakeComponent(attacker,
                        attacker.getComponent(CameraComponent.class), sfx));
    target.create();
    return target;
  }
}
