package com.deco2800.game.physics;

import com.badlogic.gdx.physics.box2d.*;

import com.deco2800.game.entities.factories.EntityTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  @Override
  public void beginContact(Contact contact) {
    triggerEventOn(contact.getFixtureA(), "collisionStart",
            contact.getFixtureB());
    triggerEventOn(contact.getFixtureB(), "collisionStart",
            contact.getFixtureA());
  }

  @Override
  public void endContact(Contact contact) {
    triggerEventOn(contact.getFixtureA(), "collisionEnd", contact.getFixtureB());
    triggerEventOn(contact.getFixtureB(), "collisionEnd", contact.getFixtureA());
  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {
    BodyUserData userDataA =
            (BodyUserData) contact.getFixtureA().getBody().getUserData();
    BodyUserData userDataB =
            (BodyUserData) contact.getFixtureB().getBody().getUserData();
    if (userDataA.entity.getType() == EntityTypes.PLAYERSPEAR) {
      userDataA.entity.getEvents().trigger("collision", contact, userDataA,
              userDataB);
    } else if (userDataB.entity.getType() == EntityTypes.PLAYERSPEAR){
      userDataB.entity.getEvents().trigger("collision", contact, userDataB,
              userDataA);
    }
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
}
