package com.deco2800.game.entities.factories;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.PhysicsUtils;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.rendering.TextureRenderComponent;

/**
 * Factory to create obstacle entities.
 *
 * <p>Each obstacle entity type should have a creation method that returns a corresponding entity.
 */
public class ObstacleFactory {

  /**
   * Creates a rock entity.
   * @return rock entity
   */
  public static Entity createRock() {
    Entity rock =
        new Entity()
            .addComponent(new TextureRenderComponent("images/Rock_1.png"))
            .addComponent(new PhysicsComponent())
            .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));

    rock.getComponent(PhysicsComponent.class).setBodyType(BodyType.StaticBody);
    rock.getComponent(TextureRenderComponent.class).scaleEntity();
    rock.scaleHeight(2.5f);
    PhysicsUtils.setScaledCollider(rock, 0.5f, 0.2f);
    return rock;
  }

  /**
   * Creates a spikes entity
   * @return spikes entity
   */
  public static Entity createSpikes() {
    Entity spikes =
            new Entity()
                    .addComponent(new TextureRenderComponent("images/Spike_1.png"))
                    .addComponent(new PhysicsComponent())
                    .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));

    spikes.getComponent(PhysicsComponent.class).setBodyType(BodyType.StaticBody);
    spikes.getComponent(TextureRenderComponent.class).scaleEntity();
    spikes.scaleHeight(2.5f);
    PhysicsUtils.setScaledCollider(spikes, 0.5f, 0.2f);
    return spikes;
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
