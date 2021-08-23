package com.deco2800.game.entities.factories;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.PhysicsUtils;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.physics.components.PhysicsMovementComponent;
import com.deco2800.game.rendering.TextureRenderComponent;

/**
 * Factory to create obstacle entities.
 *
 * <p>Each obstacle entity type should have a creation method that returns a corresponding entity.
 */
public class ObstacleFactory {

  /**
   * Creates a tree entity.
   * @return entity
   */
  public static Entity createTree() {
    Entity tree =
        new Entity()
            .addComponent(new TextureRenderComponent("images/platform.png"))
            .addComponent(new PhysicsComponent())
            .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));

    tree.getComponent(PhysicsComponent.class).setBodyType(BodyType.StaticBody);
    tree.getComponent(TextureRenderComponent.class).scaleEntity();
    tree.scaleHeight(0.8f);
    PhysicsUtils.setScaledCollider(tree, 0.5f, 0.2f);
    return tree;
  }

  /**
   * Creates a platform entity.
   * @return entity
   */
  public static Entity createPlatform() {
    Entity platform =
            new Entity()
            .addComponent(new TextureRenderComponent("images/platform.png"))
            .addComponent(new PhysicsComponent())
            .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));

    platform.getComponent(PhysicsComponent.class).setBodyType(BodyType.StaticBody);
    platform.getComponent(TextureRenderComponent.class).scaleEntity();
    // Be warned, this scale height makes a few of the calculations in RacerArea.spawnPlatform()
    // difficult.
    platform.scaleHeight(0.5f);
    return platform;
  }

  /**
   * Creates a floor entity.
   * @return entity
   */
  public static Entity createFloor() {
    Entity floor =
            new Entity()
                    .addComponent(new TextureRenderComponent("images/floor.png"))
                    .addComponent(new PhysicsComponent())
                    .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));

    floor.getComponent(PhysicsComponent.class).setBodyType(BodyType.StaticBody);
    floor.getComponent(TextureRenderComponent.class).scaleEntity();
    floor.scaleHeight(1f);
    //PhysicsUtils.setScaledCollider(floor, 0.5f, 0.2f);
    return floor;
  }

  /**
   * Creates an invisible physics wall.
   * @param width Wall width in world units
   * @param height Wall height in world units
   * @return Wall entity of given width and height
   */
  public static Entity createWall(float width, float height) {
    Entity wall = new Entity()
        .addComponent(new PhysicsComponent().setBodyType(BodyType.StaticBody))
        .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));
    wall.setScale(width, height);
    return wall;
  }

  private ObstacleFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
