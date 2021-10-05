package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.components.powerups.ShieldPowerUpComponent;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.extensions.GameExtension;
import com.deco2800.game.physics.PhysicsService;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.physics.components.*;
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
class VariableSpeedComponentTest {
  @BeforeEach
  void beforeEach() {
    ServiceLocator.registerPhysicsService(new PhysicsService());
  }

  /**
   * Sets and return the maxSpeed for attacker
   */
  @Test
  void setMaxSpeedForAttackerTest() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity wallOfDeath = createWallofDeath(targetLayer);
    Entity target = createTarget(targetLayer,attacker,wallOfDeath);

    attacker.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);
    wallOfDeath.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);
    target.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);

    assertEquals("(6.0,6.0)",attacker.getComponent(PhysicsMovementComponent.class).getMaxSpeed().toString());
  }

  /**
   * Sets and return the maxSpeed for wallOfDeath
   */
  @Test
  void setMaxSpeedForWallofDeathTest() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity wallOfDeath = createWallofDeath(targetLayer);
    Entity target = createTarget(targetLayer,attacker,wallOfDeath);

    attacker.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);
    wallOfDeath.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);
    target.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);

    assertEquals("(6.0,6.0)",wallOfDeath.getComponent(PhysicsMovementComponent.class).getMaxSpeed().toString());
  }

  /**
   * Sets and return the maxSpeed for target
   */
  @Test
  void setMaxSpeedForTargetTest() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity wallOfDeath = createWallofDeath(targetLayer);
    Entity target = createTarget(targetLayer,attacker,wallOfDeath);

    attacker.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);
    wallOfDeath.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);
    target.getComponent(PhysicsMovementComponent.class).setMaxSpeed(6);

    assertEquals("(6.0,6.0)",target.getComponent(PhysicsMovementComponent.class).getMaxSpeed().toString());
  }

  /**
   * Sets and return the minSpeed for attacker
   */
  @Test
  void setMinSpeedForAttackerTest() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity wallOfDeath = createWallofDeath(targetLayer);
    Entity target = createTarget(targetLayer,attacker,wallOfDeath);

    attacker.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
    wallOfDeath.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
    target.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);

    assertEquals("(4.0,4.0)",attacker.getComponent(PhysicsMovementComponent.class).getMaxSpeed().toString());
  }

  /**
   * Sets and return the minSpeed for wall of death
   */
  @Test
  void setMinSpeedForWallofDeathTest() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity wallOfDeath = createWallofDeath(targetLayer);
    Entity target = createTarget(targetLayer,attacker,wallOfDeath);

    attacker.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
    wallOfDeath.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
    target.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);

    assertEquals("(4.0,4.0)",wallOfDeath.getComponent(PhysicsMovementComponent.class).getMaxSpeed().toString());
  }

  /**
   * Sets and return the minSpeed for target
   */
  @Test
  void setMinSpeedForTargetTest() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity wallOfDeath = createWallofDeath(targetLayer);
    Entity target = createTarget(targetLayer,attacker,wallOfDeath);

    attacker.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
    wallOfDeath.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
    target.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);

    assertEquals("(4.0,4.0)",target.getComponent(PhysicsMovementComponent.class).getMaxSpeed().toString());
  }

  /**
   * Sets and return the speed for miniumum edge case
   */
  @Test
  void setMinSpeedForEdgeCaseTest() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity wallOfDeath = createWallofDeath(targetLayer);
    Entity target = createTarget(targetLayer,attacker,wallOfDeath);

    attacker.getComponent(PhysicsMovementComponent.class).setMaxSpeed(0);
    wallOfDeath.getComponent(PhysicsMovementComponent.class).setMaxSpeed(0);
    target.getComponent(PhysicsMovementComponent.class).setMaxSpeed(0);

    assertEquals("(0.0,0.0)",target.getComponent(PhysicsMovementComponent.class).getMaxSpeed().toString());
  }



  Entity createAttacker(short targetLayer) {
    Entity entity =
        new Entity()
            .addComponent(new TouchAttackComponent(targetLayer))
            .addComponent(new CombatStatsComponent(0, 10))
            .addComponent(new PhysicsComponent())
                .addComponent(new PhysicsMovementComponent())
                .addComponent(new CameraComponent())
            .addComponent(new HitboxComponent());
    entity.create();
    return entity;
  }

  Entity createWallofDeath(short targetLayer) {
    Entity entity =
            new Entity()
                    .addComponent(new TouchAttackComponent(targetLayer))
                    .addComponent(new CombatStatsComponent(0, 10))
                    .addComponent(new PhysicsComponent())
                    .addComponent(new PhysicsMovementComponent())
                    .addComponent(new CameraComponent())
                    .addComponent(new HitboxComponent());
    entity.create();
    return entity;
  }

  Entity createTarget(short layer, Entity attacker, Entity wallOfDeath) {
    Entity sfx =
          new Entity()
            .addComponent(new CombatStatsComponent(10, 0))
            .addComponent(new PhysicsComponent())
                  .addComponent(new PhysicsMovementComponent())
            .addComponent(new HitboxComponent().setLayer(layer));
    Entity target =
        new Entity()
            .addComponent(new CombatStatsComponent(10, 0))
            .addComponent(new PhysicsComponent())
                .addComponent(new PhysicsMovementComponent())
            .addComponent(new HitboxComponent().setLayer(layer))
                .addComponent(new VariableSpeedComponent(attacker, wallOfDeath, sfx));
    target.create();
    return target;
  }
}
