package com.deco2800.game.entities.factories;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.PhysicsUtils;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.rendering.TextureRenderComponent;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.TouchAttackComponent;
import com.deco2800.game.physics.components.HitboxComponent;

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
    rock.setScale(1.5f, 1.5f);
    PhysicsUtils.setScaledCollider(rock, 0.5f, 0.5f);
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
                    .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE))
                    .addComponent(new HitboxComponent().setLayer(PhysicsLayer.OBSTACLE))
                    .addComponent(new TouchAttackComponent(PhysicsLayer.PLAYER, 0f))
                    .addComponent(new CombatStatsComponent(1, 100));

    spikes.getComponent(PhysicsComponent.class).setBodyType(BodyType.StaticBody);
    spikes.getComponent(TextureRenderComponent.class).scaleEntity();
    spikes.setScale(2f, 1.5f);
    PhysicsUtils.setScaledCollider(spikes, 0.7f, 0.2f);
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
