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
        return platform;
    }

    /**
     * Return a platform entity that has no physics or collision component.
     *
     * @param world the world type to load in. Must match the name of a .png file in
     *              assets/images (e.g. assets/images/world.png)
     * @return entity
     */
    public static Entity createPlatformNoCollider(String world) {
        Entity platform = new Entity();
        makeBasePlatform(world, platform);
        return platform;
    }

    /**
     * Creates a platform entity with the default world type.
     *
     * @return entity
     */
    public static Entity createPlatform() {
        return createPlatform(null);
    }

    /**
     * Add the required components and properties to the given entity to make it a platform.
     *
     * @param world  the world type corresponding the art style that the platform will mimic. Must
     *               be the same as the name of an image in assets/images/'world'.png.
     * @param entity entity to transform into a platform
     */
    private static void makeBasePlatform(String world, Entity entity) {
        if (world == null) {
            entity.addComponent(new TextureRenderComponent("images/platform_gradient.png"));
        } else {
            entity.addComponent(new TextureRenderComponent("images/" + world + ".png"));
        }
        entity.getComponent(TextureRenderComponent.class).scaleEntity();
        entity.scaleHeight(0.5f);
        entity.setType(EntityTypes.OBSTACLE);
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
            floor.addComponent(new TextureRenderComponent("images/floors" + world + ".png"));
        }
        floor.getComponent(PhysicsComponent.class).setBodyType(BodyType.StaticBody);
        return floor;
    }

    public static Entity createFloorNoCollider(String world) {
        Entity floor = new Entity();
        makeBaseFloor(world, floor);
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
     * Add the required components and properties to entity to make it a floor entity.
     *
     * @param world  the world type corresponding the art style that the platform will mimic. Must
     *               be the same as the name of an image in assets/images/'world'.png.
     * @param entity entity to transform into a platform
     */
    private static void makeBaseFloor(String world, Entity entity) {
        if (world == null) {
            entity.addComponent(new TextureRenderComponent("images/floor.png"));
        } else {
            entity.addComponent(new TextureRenderComponent("images/" + world + ".png"));
        }
        entity.getComponent(TextureRenderComponent.class).scaleEntity();
        entity.scaleHeight(0.5f);
        entity.setType(EntityTypes.OBSTACLE);
    }

    public static Entity createCollider(int x1, int x2, int height) {
        Vector2 scale = new Vector2(0.5f, 0.5f);
        Vector2 size = new Vector2(x2 - x1, height).scl(scale);
        Entity collider = new Entity()
                .addComponent(new PhysicsComponent().setBodyType(BodyType.StaticBody))
                .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));
        collider.setScale(scale);
        collider.getComponent(ColliderComponent.class).setAsBoxAligned(size,
                PhysicsComponent.AlignX.LEFT, PhysicsComponent.AlignY.BOTTOM);
        return collider;
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
