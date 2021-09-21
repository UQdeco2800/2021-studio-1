package com.deco2800.game.entities.factories;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
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
import com.deco2800.game.utils.math.Vector2Utils;

/**
 * Factory to create obstacle entities.
 *
 * <p>Each obstacle entity type should have a creation method that returns a corresponding entity.
 */
public class ObstacleFactory {

    /**
     * Creates a rock entity.
     *
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

        //Rock hitbox -  each vector in rockPoints is a vertex of the hitbox
        Vector2[] rockPoints = new Vector2[8];
        rockPoints[0] = new Vector2(0.2f, 0f);
        rockPoints[1] = new Vector2(0.2f, 0.4f);
        rockPoints[2] = new Vector2(0.4f, 0.5f);
        rockPoints[3] = new Vector2(0.6f, 0.5f);
        rockPoints[4] = new Vector2(0.7f, 0.5f);
        rockPoints[5] = new Vector2(0.9f, 0.5f);
        rockPoints[6] = new Vector2(1f, 0.4f);
        rockPoints[7] = new Vector2(1f, 0f);
        PolygonShape rockShape = new PolygonShape();
        rockShape.set(rockPoints);
        rock.getComponent(ColliderComponent.class).setShape(rockShape);

        rock.setType(EntityTypes.OBSTACLE);
        return rock;
    }

    /**
     * Creates a level load Trigger Entity
     * This is an invisible entity that commands the loading of more levels to the right when touched for the first time by the player
     *
     * @return entity
     */
    public static Entity createLevelLoadTrigger() {
        Entity levelLoadTrigger = new Entity();

        levelLoadTrigger.addComponent(new PhysicsComponent());
        levelLoadTrigger.getComponent(PhysicsComponent.class).setBodyType(BodyType.StaticBody);
        Vector2 size = new Vector2(1, 20);
        levelLoadTrigger.addComponent(new HitboxComponent());
        levelLoadTrigger.getComponent(HitboxComponent.class).setAsBox(size);
        levelLoadTrigger.addComponent(new LevelLoadTriggerComponent());

        return levelLoadTrigger;
    }


    /**
     * Creates a spike entity.
     *
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
        spikes.setType(EntityTypes.OBSTACLE);
        return spikes;
    }

    /**
     * Creates a platform entity.
     *
     * note: is the world file not meant to represent a .png?
     *
     * world the world type to load in. Must match the name of .png file (e.g.
     * @param world the world type to load in. Must match the name of a .rag file (e.g. world.rag)
     * @return entity
     */
    public static Entity createPlatform(String world) {
        Entity platform =
                new Entity()
                        .addComponent(new PhysicsComponent())
                        .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));
        if (world == null) {
            platform.addComponent(new TextureRenderComponent("images/platform_gradient.png"));
        } else {
            platform.addComponent(new TextureRenderComponent("images/worlds/" + world + ".png"));
        }
        platform.getComponent(PhysicsComponent.class).setBodyType(BodyType.StaticBody);
        platform.getComponent(TextureRenderComponent.class).scaleEntity();
        // Be warned, this scale height makes a few of the calculations in RacerArea.spawnPlatform()
        // difficult.
        platform.scaleHeight(0.5f);
        platform.setType(EntityTypes.OBSTACLE);
        return platform;
    }

    /**
     * Creates a floor entity
     *
     * @return entity
     */
    public static Entity createPlatform() {
        return createPlatform(null);
    }

    /**
     * Creates a floor entity.
     *
     * @param world String for to a world name
     * @return entity
     */
    public static Entity createFloor(String world) {
        Entity floor =
                new Entity()
                        .addComponent(new PhysicsComponent())
                        .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));
        if (world == null) {
            floor.addComponent(new TextureRenderComponent("images/floor.png"));
        } else {
            floor.addComponent(new TextureRenderComponent("images/" + world + ".png"));
        }
        floor.getComponent(PhysicsComponent.class).setBodyType(BodyType.StaticBody);
        floor.getComponent(TextureRenderComponent.class).scaleEntity();
        floor.scaleHeight(0.5f);
        floor.setType(EntityTypes.OBSTACLE);
        return floor;
    }

    /**
     * Creates a floor entity
     *
     * @return entity
     */
    public static Entity createFloor() {
        return createFloor(null);
    }


    /**
     * Creates an invisible physics wall.
     *
     * @param width  Wall width in world units
     * @param height Wall height in world units
     * @return Wall entity of given width and height
     */
    public static Entity createWall(float width, float height) {
        Entity wall = new Entity()
                .addComponent(new PhysicsComponent().setBodyType(BodyType.StaticBody))
                .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));
        wall.setScale(width, height);
        wall.setType(EntityTypes.OBSTACLE);
        return wall;
    }

    private ObstacleFactory() {
        throw new IllegalStateException("Instantiating static util class");
    }
}
