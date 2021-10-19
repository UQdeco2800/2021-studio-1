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

@ExtendWith(GameExtension.class)
class VariableSpeedComponentTest {
  @BeforeEach
  void beforeEach() {
    ServiceLocator.registerPhysicsService(new PhysicsService());
  }


  /**
   * If the distance between the player and wall of death is less than 25 units
   */
  @Test
  void PlayerAndWallOfDeathLowestDistanceTest() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity wallOfDeath = createWallofDeath(targetLayer);
    Entity target = createTarget(targetLayer,attacker,wallOfDeath);

    float distance = attacker.getPosition().x - wallOfDeath.getPosition().x;

    attacker.setPosition(15,0);
    wallOfDeath.setPosition(10,0);

    target.getComponent(VariableSpeedComponent.class).setTutorialComplete();
    target.getComponent(VariableSpeedComponent.class).changeSpeedStart(false);

    assertEquals("(2.0,2.0)",wallOfDeath.getComponent(PhysicsMovementComponent.class).getMaxSpeed().toString());
  }



  /**
   * If the distance between the player and wall of death is greater than 25 but less than 45 units
   */
  @Test
  void PlayerAndWallOfDeathLowDistanceTest() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity wallOfDeath = createWallofDeath(targetLayer);
    Entity target = createTarget(targetLayer,attacker,wallOfDeath);

    float distance = attacker.getPosition().x - wallOfDeath.getPosition().x;

    attacker.setPosition(40,0);
    wallOfDeath.setPosition(10,0);

    target.getComponent(VariableSpeedComponent.class).setTutorialComplete();
    target.getComponent(VariableSpeedComponent.class).changeSpeedStart(false);

    assertEquals("(3.0,3.0)",wallOfDeath.getComponent(PhysicsMovementComponent.class).getMaxSpeed().toString());
  }

  /**
   * If the distance between the player and wall of death is greater than 45 but less than 65 units
   */
  @Test
  void PlayerAndWallOfDeathMediumDistanceTest() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity wallOfDeath = createWallofDeath(targetLayer);
    Entity target = createTarget(targetLayer,attacker,wallOfDeath);

    float distance = attacker.getPosition().x - wallOfDeath.getPosition().x;

    attacker.setPosition(60,0);
    wallOfDeath.setPosition(10,0);

    target.getComponent(VariableSpeedComponent.class).setTutorialComplete();
    target.getComponent(VariableSpeedComponent.class).changeSpeedStart(false);

    assertEquals("(4.0,4.0)",wallOfDeath.getComponent(PhysicsMovementComponent.class).getMaxSpeed().toString());
  }

  /**
   * If the distance between the player and wall of death is greater than 65 units
   */
  @Test
  void PlayerAndWallOfDeathFarDistanceTest() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity wallOfDeath = createWallofDeath(targetLayer);
    Entity target = createTarget(targetLayer,attacker,wallOfDeath);

    float distance = attacker.getPosition().x - wallOfDeath.getPosition().x;

    attacker.setPosition(80,0);
    wallOfDeath.setPosition(10,0);

    target.getComponent(VariableSpeedComponent.class).setTutorialComplete();
    target.getComponent(VariableSpeedComponent.class).changeSpeedStart(false);

    assertEquals("(6.0,6.0)",wallOfDeath.getComponent(PhysicsMovementComponent.class).getMaxSpeed().toString());
  }

  /**
   * Checks if distance is being calculated and received properly
   */
  @Test
  void CheckDistanceTest() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity wallOfDeath = createWallofDeath(targetLayer);
    Entity target = createTarget(targetLayer,attacker,wallOfDeath);

    float distance = attacker.getPosition().x - wallOfDeath.getPosition().x;

    attacker.setPosition(60,0);
    wallOfDeath.setPosition(0,0);

    target.getComponent(VariableSpeedComponent.class).changeSpeedStart(false);

    attacker.setPosition(70,0);
    wallOfDeath.setPosition(50,0);
    target.getComponent(VariableSpeedComponent.class).changeSpeedStart(false);

    attacker.setPosition(100,0);
    wallOfDeath.setPosition(80,0);
    target.getComponent(VariableSpeedComponent.class).changeSpeedStart(false);

    distance = attacker.getPosition().x - wallOfDeath.getPosition().x;

    assertEquals(20, distance);
    assertEquals("(10.0,10.0)",wallOfDeath.getComponent(PhysicsMovementComponent.class).getMaxSpeed().toString());
  }

  /**
   * When the wall of death is running fast and approaches close to the player, slows down
   */
  @Test
  void SlowDownWallOfDeathTest() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity wallOfDeath = createWallofDeath(targetLayer);
    Entity target = createTarget(targetLayer,attacker,wallOfDeath);

    attacker.setPosition(60,0);
    wallOfDeath.setPosition(10,0);
    target.getComponent(VariableSpeedComponent.class).changeSpeedStart(false);

    attacker.setPosition(70,0);
    wallOfDeath.setPosition(40,0);

    target.getComponent(VariableSpeedComponent.class).changeSpeedStart(false);

    assertEquals("(10.0,10.0)",wallOfDeath.getComponent(PhysicsMovementComponent.class).getMaxSpeed().toString());
  }


  /**
   * When the player has not finished the tutorial yet, wall of death speed should be 0
   */
  @Test
  void TutorialStartTest() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity wallOfDeath = createWallofDeath(targetLayer);
    Entity target = createTarget(targetLayer,attacker,wallOfDeath);

    attacker.setPosition(10,0);
    wallOfDeath.setPosition(12,0);
    target.getComponent(VariableSpeedComponent.class).changeSpeedStart(false);

    assertEquals("(0.0,0.0)",wallOfDeath.getComponent(PhysicsMovementComponent.class).getMaxSpeed().toString());
  }

  /**
   * When the player finishes the tutorial and the Wall of Death speeds up to maxspeed
   */
  @Test
  void FastRunningTest() {
    short targetLayer = (1 << 3);
    Entity attacker = createAttacker(targetLayer);
    Entity wallOfDeath = createWallofDeath(targetLayer);
    Entity target = createTarget(targetLayer,attacker,wallOfDeath);

    attacker.setPosition(60,0);
    wallOfDeath.setPosition(0,0);
    target.getComponent(VariableSpeedComponent.class).changeSpeedStart(false);

    assertEquals("(10.0,10.0)",wallOfDeath.getComponent(PhysicsMovementComponent.class).getMaxSpeed().toString());
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
