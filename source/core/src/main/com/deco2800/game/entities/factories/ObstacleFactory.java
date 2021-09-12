package com.deco2800.game.entities.factories;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.TouchAttackComponent;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.PhysicsUtils;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.physics.components.PhysicsMovementComponent;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.rendering.TextureRenderComponent;
import com.deco2800.game.components.LevelLoadTriggerComponent;

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
    rock.getComponent(PhysicsComponent.class).setGravityScale(5.0f);
    rock.getComponent(ColliderComponent.class).setDensity(1.0f);
    rock.setScale(1.1f, 0.6f);
    PhysicsUtils.setScaledCollider(rock, .9f, .9f);
    rock.getComponent(PhysicsComponent.class).getBody().setUserData(EntityTypes.OBSTACLE);
    return rock;
  }

  /**
   * Creates a level load Trigger Entity
   * This is an invisible entity that commands the loading of more levels to the right when touched for the first time by the player
   *
   * @param target entity to chase
   * @return entity
   */
  public static Entity createLevelLoadTrigger(Entity target) {
    Entity levelLoadTrigger = new Entity();
    levelLoadTrigger.addComponent(new HitboxComponent().setAsBox(new Vector2(1,50)));
    levelLoadTrigger.addComponent(new LevelLoadTriggerComponent());
    
    return levelLoadTrigger;
}



  /**
   * Creates a spike entity.
   * @return spikes entity
   */
  public static Entity createSpikes() {
    Entity spikes =
            new Entity()
                    .addComponent(new TextureRenderComponent("images/Spike_1.png"))
                    .addComponent(new PhysicsComponent())
                    .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE))
                    .addComponent(new HitboxComponent().setLayer(PhysicsLayer.OBSTACLE))
                    .addComponent(new CombatStatsComponent(1, 100))
                    .addComponent(new TouchAttackComponent(PhysicsLayer.PLAYER, 0f));

    spikes.getComponent(PhysicsComponent.class).setBodyType(BodyType.StaticBody);
    spikes.getComponent(TextureRenderComponent.class).scaleEntity();
    spikes.getComponent(PhysicsComponent.class).setGravityScale(5.0f);
    spikes.getComponent(ColliderComponent.class).setDensity(1.0f);
    spikes.setScale(1.1f, 0.5f);
    PhysicsUtils.setScaledCollider(spikes, 0.9f, 0.9f);
    spikes.getComponent(PhysicsComponent.class).getBody().setUserData(EntityTypes.OBSTACLE);
    return spikes;
  }

  /**
   * Creates a platform entity.
   * @return entity
   */
  public static Entity createPlatform() {
    Entity platform =
            new Entity()
            .addComponent(new TextureRenderComponent("images/platform_no_gradient.png"))
            .addComponent(new PhysicsComponent())
            .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));

    platform.getComponent(PhysicsComponent.class).setBodyType(BodyType.StaticBody);
    platform.getComponent(TextureRenderComponent.class).scaleEntity();
    // Be warned, this scale height makes a few of the calculations in RacerArea.spawnPlatform()
    // difficult.
    platform.scaleHeight(0.5f);
    platform.getComponent(PhysicsComponent.class).getBody().setUserData(EntityTypes.OBSTACLE);
    return platform;
  }

  /**
   * Creates platform entity with shadow.
   * @return entity
   */
  public static Entity createPlatformWithGradient() {
    Entity platformGradient =
            new Entity()
                    .addComponent(new TextureRenderComponent("images/platform_gradient.png"))
                    .addComponent(new PhysicsComponent())
                    .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));

    platformGradient.getComponent(PhysicsComponent.class).setBodyType(BodyType.StaticBody);
    platformGradient.getComponent(TextureRenderComponent.class).scaleEntity();
    // Be warned, this scale height makes a few of the calculations in RacerArea.spawnPlatform()
    // difficult.
    platformGradient.scaleHeight(0.5f);
    platformGradient.getComponent(PhysicsComponent.class).getBody().setUserData(EntityTypes.OBSTACLE);
    return platformGradient;
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
    floor.scaleHeight(0.5f);
    floor.getComponent(PhysicsComponent.class).getBody().setUserData(EntityTypes.OBSTACLE);
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
    wall.getComponent(PhysicsComponent.class).getBody().setUserData(EntityTypes.OBSTACLE);
    return wall;
  }

  private ObstacleFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
