package com.deco2800.game.physics;

import com.badlogic.gdx.physics.box2d.*;

import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.EntityTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Box2D collision events fire globally on the physics world, not per-object. The contact listener
 * receives these events, finds the entities involved in the collision, and triggers events on them.
 *
 * <p>On contact start: evt = "collisionStart", params = ({@link Fixture} thisFixture, {@link
 * Fixture} otherFixture)
 *
 * <p>On contact end: evt = "collisionEnd", params = ({@link Fixture} thisFixture, {@link Fixture}
 * otherFixture)
 */
public class PhysicsContactListener implements ContactListener {
  private static final Logger logger = LoggerFactory.getLogger(PhysicsContactListener.class);
  private final ArrayList<EntityTypes> powerUps = new ArrayList<>(Arrays.asList(EntityTypes.LIGHTNINGPOWERUP
      , EntityTypes.SHIELDPOWERUP
      , EntityTypes.SPEARPOWERUP));

  @Override
  public void beginContact(Contact contact) {
    triggerEventOn(contact.getFixtureA(), "collisionStart",
            contact.getFixtureB());
    triggerEventOn(contact.getFixtureB(), "collisionStart",
            contact.getFixtureA());

    BodyUserData bodyA = (BodyUserData) contact.getFixtureA().getBody().getUserData();
    BodyUserData bodyB = (BodyUserData) contact.getFixtureB().getBody().getUserData();

    checkPowerUpCollision(bodyA, bodyB);
    checkProjectileCollision(bodyA, bodyB);
  }

  @Override
  public void endContact(Contact contact) {
    triggerEventOn(contact.getFixtureA(), "collisionEnd", contact.getFixtureB());
    triggerEventOn(contact.getFixtureB(), "collisionEnd", contact.getFixtureA());
  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {
    // Nothing to do before resolving contact
  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {
    // Nothing to do after resolving contact
  }

  private void triggerEventOn(Fixture fixture, String evt, Fixture otherFixture) {
    BodyUserData userData = (BodyUserData) fixture.getBody().getUserData();

    if (userData != null && userData.entity != null) {
      logger.debug("{} on entity {}", evt, userData.entity);
      userData.entity.getEvents().trigger(evt, fixture, otherFixture);
    }
  }

  /**
   * Checks if two bodies involved in a collision are a player and a power up
   * and activates the power up
   *
   * @param bodyA - first body involved in the collision
   * @param bodyB - second body involved in the collision
   */
  private void checkPowerUpCollision(BodyUserData bodyA, BodyUserData bodyB) {
    ArrayList<Entity> playerPower = getPlayerPower(bodyA, bodyB);

    if (!playerPower.isEmpty()) {
      Entity player = getPlayerPower(bodyA, bodyB).get(0);
      Entity powerUp = getPlayerPower(bodyA, bodyB).get(1);

      player.getEvents().trigger("obtainPowerUp", powerUp);

      if (!(powerUp.getType() == EntityTypes.SPEARPOWERUP)) {
        powerUp.getEvents().trigger("dispose");
      }
    }
  }

  /**
   * Checks if two bodies involved in a collision are a player and a power up
   * and add them to an array as player and power up respectively
   *
   * @param bodyA - first body involved in the collision
   * @param bodyB - second body involved in the collision
   * @return array with player and power up respectively
   */
  private ArrayList<Entity> getPlayerPower(BodyUserData bodyA, BodyUserData bodyB) {
    ArrayList<Entity> playerPower = new ArrayList<>();

    if (bodyA.entity.getType() == EntityTypes.PLAYER
        && (powerUps.contains(bodyB.entity.getType()))) {
      playerPower.add(bodyA.entity);
      playerPower.add(bodyB.entity);
    } else if (bodyB.entity.getType() == EntityTypes.PLAYER
        && (powerUps.contains(bodyA.entity.getType()))) {
      playerPower.add(bodyB.entity);
      playerPower.add(bodyA.entity);
    }

    return playerPower;
  }

  /**
   * Checks if out of two bodies involved in a collision, one is a projectile
   *
   * @param bodyA - first body involved in the collision
   * @param bodyB - second body involved in the collision
   */
  private void checkProjectileCollision(BodyUserData bodyA, BodyUserData bodyB) {
    if(bodyA.entity.getType() == EntityTypes.PROJECTILE) {
      bodyA.entity.getEvents().trigger("dispose");
    } else if (bodyB.entity.getType() == EntityTypes.PROJECTILE) {
      bodyB.entity.getEvents().trigger("dispose");
    }
  }
}
