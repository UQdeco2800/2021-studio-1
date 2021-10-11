package com.deco2800.game.entities.factories;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.TouchAttackComponent;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.PhysicsUtils;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.rendering.BackgroundRenderComponent;
import com.deco2800.game.rendering.TextureRenderComponent;
import com.deco2800.game.components.LevelLoadTriggerComponent;

/**
 * Factory to create obstacle entities.
 *
 * <p>Each obstacle entity type should have a creation method that returns a corresponding entity.
 */
public class ObstacleFactory {

    /**
     * Create and return a background entity having only BackgroundRenderComponent.
     *
     * @param world the world type; if the first word before an underscore is not  one of 'asgard',
     *              'alfheim', 'earth', 'jotunheimr', or 'hel', will default to 'asgard'. Can be
     *              null.
     * @param width width of the image (as given by the terrain coordinate system)
     * @return background entity
     */
    public static Entity createBackground(String world, int width) {
        Entity background = new Entity();
        if (world == null) {
            background.addComponent(new BackgroundRenderComponent("images/Backgrounds/black_back" +
                    ".png"));
        } else { // Choose correct background file. When there is more than one, choose randomly.
            String filename;
            switch (world.split("_", 2)[0]) {
                case "alfheim":
                    filename = MathUtils.random(1) == 0
                            ? "Background Alfheim Day" : "Background Alfheim";
                    break;
                case "earth":
                    filename = MathUtils.random(1) == 0
                            ? "Background Earth Day" : "Background Earth";
                    break;
                case "jotunheimr":
                    filename = MathUtils.random(1) == 0
                            ? "Background Jotunheim Day" : "Background Jotunheim";
                    break;
                case "hel":
                    // Hel background not yet implemented.

                default: // Either is "asgard" or unrecognised.
                    filename = "asgard_bg";
            }
            background.addComponent(new BackgroundRenderComponent("images/Backgrounds/" + filename +
                    ".png"));
        }
        // This 1.5x multiplier ensures 1 'width' value == width of 1 terrain block.
        background.scaleWidth((float) (width * (1.5)));
        return background;
    }

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
        Vector2 size = new Vector2(1, 40);
        levelLoadTrigger.addComponent(new HitboxComponent().setLayer(PhysicsLayer.OBSTACLE));
        levelLoadTrigger.getComponent(HitboxComponent.class).setAsBox(size);
        levelLoadTrigger.addComponent(new LevelLoadTriggerComponent());
        levelLoadTrigger.setType(EntityTypes.OBSTACLE);

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
     * Create and return a platform entity.
     *
     * @param world the world type to load in. Must match the name of a .png file in
     *              assets/images (e.g. assets/images/world.png)
     * @return platform entity
     */
    public static Entity createPlatform(String world) {
        Entity platform = createPlatformNoCollider(world)
                .addComponent(new PhysicsComponent())
                .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));
        platform.getComponent(PhysicsComponent.class).setBodyType(BodyType.StaticBody);
        return platform;
    }

    /**
     * Create and return a platform entity that has no physics or collision component.
     *
     * @param world the world type to load in. Must match the name of a .png file in
     *              assets/images (e.g. assets/images/world.png)
     * @return platform entity
     */
    public static Entity createPlatformNoCollider(String world) {
        Entity platform = new Entity();
        if (world == null) {
            platform.addComponent(new TextureRenderComponent("images/platform_gradient.png"));
        } else {
            platform.addComponent(new TextureRenderComponent("images/worlds/" + world + ".png"));
        }
        platform.getComponent(TextureRenderComponent.class).scaleEntity();
        platform.scaleHeight(0.5f);
        platform.setType(EntityTypes.OBSTACLE);
        return platform;
    }

    /**
     * Creates a platform entity with the default world type.
     *
     * @return platform entity
     */
    public static Entity createPlatform() {
        return createPlatform(null);
    }

    /**
     * Creates a floor entity with Physics and Collider components.
     *
     * @param world the world type corresponding the art style that the platform will mimic. Must
     *              be the same as the name of an image in assets/images/'world'.png.
     * @return floor entity
     */
    public static Entity createFloor(String world) {
        Entity floor = createFloorNoCollider(world)
                .addComponent(new PhysicsComponent())
                .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));
        floor.getComponent(PhysicsComponent.class).setBodyType(BodyType.StaticBody);
        return floor;
    }

    /**
     * Create and return a floor entity that has no Physics or Collider component.
     * <p>
     * The value of 'world' must be the same as the name of a world style with any content following
     * an underscore afterwards. E.g. as of 22/9 the following values are all valid: 'asgard',
     * 'hel_1', 'hel_fdsagdsa', 'jotunheimr', 'earth'. This is to allow variations on platform types
     * while keeping a single floor type.
     *
     * @param world the world type corresponding the art style that the floor will mimic.
     * @return floor entity
     */
    public static Entity createFloorNoCollider(String world) {
        Entity floor = new Entity();
        if (world == null) {
            floor.addComponent(new TextureRenderComponent("images/floor.png"));
        } else {
            // Strip any information including and after an underscore.
            String specificWorld = world.split("_", 2)[0];
            floor.addComponent(new TextureRenderComponent(
                    "images/floors/" + specificWorld + ".png"));
        }
        floor.getComponent(TextureRenderComponent.class).scaleEntity();
        floor.scaleHeight(1.5f);
        floor.setType(EntityTypes.OBSTACLE);
        return floor;
    }

    /**
     * Create and return a floor entity with default world type.
     *
     * @return floor entity
     */
    public static Entity createFloor() {
        return createFloor(null);
    }

    /**
     * Create and return an entity that has a collision hitbox with position given by parameters.
     *
     * @param width  width of the hitbox
     * @param height height of the hitbox
     * @return entity having only PhysicsComponent and ColliderComponent
     */
    public static Entity createCollider(int width, int height) {
        Vector2 scale = new Vector2(0.5f, 0.5f);
        Vector2 size = new Vector2(width, height).scl(scale);
        Entity collider = new Entity()
                .addComponent(new PhysicsComponent().setBodyType(BodyType.StaticBody))
                .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));
        collider.setScale(scale);
        collider.getComponent(ColliderComponent.class).setAsBoxAligned(size,
                PhysicsComponent.AlignX.LEFT, PhysicsComponent.AlignY.BOTTOM);
        collider.setType(EntityTypes.OBSTACLE);
        return collider;
    }

    /**
     * Create an invisible physics wall.
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


    public static Entity createTutorialLightning() {
        Entity TutorialLightning = new Entity()
                .addComponent(new TextureRenderComponent("images/tutorial/lightningTutorial.png"));
        TutorialLightning.getComponent(TextureRenderComponent.class).scaleEntity();
        TutorialLightning.scaleHeight(3f);
        return TutorialLightning;
    }

    public static Entity createTutorialSpear() {
        Entity TutorialSpear = new Entity()
                .addComponent(new TextureRenderComponent("images/tutorial/spearTutorial.png"));
        TutorialSpear.getComponent(TextureRenderComponent.class).scaleEntity();
        TutorialSpear.scaleHeight(3f);
        return TutorialSpear;
    }

    public static Entity createTutorialShield() {
        Entity TutorialShield = new Entity()
                .addComponent(new TextureRenderComponent("images/tutorial/shieldTutorial.png"));
        TutorialShield.getComponent(TextureRenderComponent.class).scaleEntity();
        TutorialShield.scaleHeight(3f);
        return TutorialShield;
    }

    public static Entity createTutorialSpearObstacle() {
        Entity TutorialSpearObstacle = new Entity()
                .addComponent(new TextureRenderComponent("images/tutorial/spearObstacleTutorial.png"));
        TutorialSpearObstacle.getComponent(TextureRenderComponent.class).scaleEntity();
        TutorialSpearObstacle.scaleHeight(3f);
        return TutorialSpearObstacle;
    }


    private ObstacleFactory() {
        throw new IllegalStateException("Instantiating static util class");
    }
}
