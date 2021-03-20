package com.deco2800.game.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.deco2800.game.ecs.Component;
import com.deco2800.game.services.ServiceLocator;

/**
 * Lets an entity be controlled by physics. Do not directly modify the position of a physics-enabled
 * entity. Instead, use forces to move it.
 */
public class PhysicsComponent extends Component {
  private final PhysicsEngine physics;
  private final FixtureDef fixtureDef = new FixtureDef();
  private BodyDef.BodyType bodyType = BodyType.DynamicBody;
  private float angle = 0f;
  private Body body;
  private Shape shape;

  /** Create a physics component with default settings. */
  public PhysicsComponent() {
    this.physics = ServiceLocator.getPhysicsService().getPhysics();
  }

  /**
   * Set friction. This affects the object when touching other objects, but does not affect friction
   * with the ground.
   *
   * @param friction friction, default = 0
   * @return self
   */
  public PhysicsComponent setFriction(float friction) {
    fixtureDef.friction = friction;
    return this;
  }

  /**
   * Set whether this physics component is a sensor. Sensors don't collide with other objects but
   * still trigger collision events. See: https://www.iforce2d.net/b2dtut/sensors
   *
   * @param isSensor true if sensor, false if not. default = false.
   * @return self
   */
  public PhysicsComponent setSensor(boolean isSensor) {
    fixtureDef.isSensor = isSensor;
    return this;
  }

  /**
   * Set density
   *
   * @param density Density and size of the physics component determine the object's mass. default =
   *     0
   * @return self
   */
  public PhysicsComponent setDensity(float density) {
    fixtureDef.density = density;
    return this;
  }

  /**
   * Set restitution
   *
   * @param restitution restitution is the 'bounciness' of an object, default = 0
   * @return self
   */
  public PhysicsComponent setRestitution(float restitution) {
    fixtureDef.restitution = restitution;
    return this;
  }

  /**
   * Set shape
   *
   * @param shape shape, default = bounding box the same size as the entity
   * @return self
   */
  public PhysicsComponent setShape(Shape shape) {
    this.shape = shape;
    return this;
  }

  /**
   * Set body type
   *
   * @param bodyType body type, default = dynamic
   * @return self
   */
  public PhysicsComponent setBodyType(BodyType bodyType) {
    this.bodyType = bodyType;
    return this;
  }

  /**
   * Set angle
   *
   * @param angle new angle in degrees, default = 0
   * @return self
   */
  public PhysicsComponent setAngle(float angle) {
    this.angle = angle;
    return this;
  }

  /**
   * Set physics as a box with a given size and local position. Box is centered around the position.
   *
   * @param size size of the box
   * @param position position of the box center relative to the entity.
   * @return self
   */
  public PhysicsComponent setAsBox(Vector2 size, Vector2 position) {
    PolygonShape bbox = new PolygonShape();
    bbox.setAsBox(size.x / 2, size.y / 2, position, 0f);
    shape = bbox;
    return this;
  }

  /**
   * Get the physics body.
   *
   * @return physics body if entity has been created, null otherwise.
   */
  public Body getBody() {
    return body;
  }

  /**
   * Get the fixture definition. Avoid changing this manually.
   *
   * @return fixture definition.
   */
  public FixtureDef getFixtureDef() {
    return fixtureDef;
  }

  @Override
  public void create() {
    // Create physics body
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = bodyType;
    bodyDef.fixedRotation = true;
    bodyDef.angle = angle;
    bodyDef.position.set(entity.getPosition());
    body = physics.createBody(bodyDef);

    // Create fixture
    if (shape == null) {
      shape = makeBoundingBox();
    }
    fixtureDef.shape = shape;
    body.createFixture(fixtureDef);
  }

  private Shape makeBoundingBox() {
    PolygonShape bbox = new PolygonShape();
    Vector2 center = entity.getScale().cpy().scl(0.5f);
    bbox.setAsBox(center.x, center.y, center, 0f);
    return bbox;
  }

  /**
   * Entity position needs to be updated to match the new physics position. This should happen
   * before other updates, which may use the new position.
   */
  @Override
  public void earlyUpdate() {
    Vector2 bodyPos = body.getPosition();
    entity.setPosition(bodyPos);
  }

  @Override
  public void dispose() {
    physics.destroyBody(body);
    if (shape != null) {
      shape.dispose();
    }
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    body.setActive(enabled);
  }
}
