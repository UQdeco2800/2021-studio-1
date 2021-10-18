package com.deco2800.game.areas;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.areas.terrain.TerrainFactory;
import com.deco2800.game.components.GroupDisposeComponent;
import com.deco2800.game.components.gamearea.GameAreaDisplay;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.*;
import com.deco2800.game.services.ResourceService;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.utils.math.GridPoint2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.deco2800.game.components.CameraShakeComponent;
import com.deco2800.game.components.VariableSpeedComponent;
import com.deco2800.game.components.FallDamageComponent;
import com.deco2800.game.physics.components.PhysicsMovementComponent;

import java.util.function.Function;

public class RagnarokArea extends GameArea {

    private static final Logger logger = LoggerFactory.getLogger(RagnarokArea.class);

    private static final float WALL_HEIGHT = 0.1f;
    private final String name; //initialise in the loader
    private static final int DEFAULT_SPEED =3;

    protected Entity player;

    private static final String[] racerTextures = {
            "images/floor.png",
            "images/platform_gradient.png",
            "images/platform_no_gradient.png",
            "images/fire_spirit.png",
            "images/skeleton.png",
            "images/Spear_1.png",
            "images/fireball.png",
            "images/Rock_1.png",
            "images/Spike_1.png",
            "images/deathGiant.png",
            "images/sfx.png",
            "images/worlds/earth_1.png",
            "images/worlds/earth_2.png",
            "images/worlds/earth_3.png",
            "images/worlds/earth_4.png",
            "images/worlds/asgard_1.png",
            "images/worlds/asgard_2.png",
            "images/worlds/hel_1.png",
            "images/worlds/hel_2.png",
            "images/worlds/jotunheimr_1.png",
            "images/worlds/jotunheimr_2.png",
            "images/floors/alfheim.png",
            "images/floors/asgard.png",
            "images/floors/earth.png",
            "images/floors/hel.png",
            "images/floors/jotunheimr.png",
            "images/powerup-shield.png",
            "images/powerup-spear.png",
            "images/blue_bck.png",
            "images/Backgrounds/black_back.png",
            "images/Backgrounds/asgard_bg.png",
            "images/Backgrounds/Background Alfheim.png",
            "images/Backgrounds/Background Alfheim Day.png",
            "images/Backgrounds/Background Earth.png",
            "images/Backgrounds/Background Earth Day.png",
            "images/Backgrounds/Background Jotunheim.png",
            "images/Backgrounds/Background Jotunheim Day.png",
            "images/Backgrounds/Background Hel.png",
            "images/tutorial/lightningTutorial.png",
            "images/tutorial/shieldTutorial.png",
            "images/tutorial/spearTutorial.png",
            "images/tutorial/spearObstacleTutorial.png",
            "images/tutorial/run.png",
            "images/bifrost.png",
            "images/bfx.png"
    };

    private static final String[] racerTextureAtlases = {
            "images/wolf.atlas",
            "images/odin.atlas",
            "images/wall.atlas",
            "images/deathGiant.atlas",
            "images/fire_spirit.atlas",
            "images/skeleton.atlas",
            "images/sfx.atlas",
            "images/lightning-animation.atlas",
            "images/player-spear.atlas",
            "images/bifrost.atlas",
            "images/bfx.atlas",
            "particles/particles.atlas",
            "images/fireball.atlas",
            "images/deathFade.atlas",
            "particles/particles.atlas"
    };

    private final TerrainFactory terrainFactory;

    public RagnarokArea(String name, TerrainFactory terrainFactory) {
        super();
        this.name = name;
        this.terrainFactory = terrainFactory;

    }

    @Override
    public void create() {
        create(0);
    }

    @Override
    public void create(int xOffset) {
        loadAssets();
        displayUI();
        spawnTerrain();


        logger.debug("Creating new RagnarokArea");
    }

    private void displayUI() {
        Entity ui = new Entity();
        ui.addComponent(new GameAreaDisplay("Ragnarok Area: " + name));
        spawnEntity(ui);
    }

    private void loadAssets() {
        logger.debug("Loading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.loadTextures(racerTextures);
        resourceService.loadTextureAtlases(racerTextureAtlases);

        while (!resourceService.loadForMillis(10)) {
            // This could be upgraded to a loading screen
        }
    }

    protected void spawnTutorial(int x, int y) {
        GridPoint2 spearSpawn = new GridPoint2(x, y);
        GridPoint2 lightningSpawn = new GridPoint2(x+18,y);
        GridPoint2 spearObstacleSpawn = new GridPoint2(x+46,y);
        GridPoint2 runSpawn = new GridPoint2(x+80,y);

        GridPoint2 textOffset = new GridPoint2(0,5);

        spawnSpear(spearSpawn.x, spearSpawn.y);
        spawnSpear(spearObstacleSpawn.x + 10, spearObstacleSpawn.y);
        Entity spearTutorial = ObstacleFactory.createTutorialSpear();

        // Spawn enemies to test spear on
        spawnWolf(spearSpawn.x+8, spearSpawn.y, 0);

        spawnLightning(lightningSpawn.x, lightningSpawn.y);
        Entity lightningTutorial = ObstacleFactory.createTutorialLightning();
        
        // Spawn enemies to test lightning on
        spawnSkeleton(lightningSpawn.x+12, lightningSpawn.y, 0);
        spawnFireSpirit(lightningSpawn.x+14, lightningSpawn.y);

        // Offset text and spawn it in
        lightningSpawn.add(textOffset);
        spawnEntityAt(lightningTutorial, lightningSpawn, true, false);
        spearSpawn.add(textOffset);
        spawnEntityAt(spearTutorial, spearSpawn, true, false);

        runSpawn.add(textOffset);
        Entity runTutorial = ObstacleFactory.createTutorialRun();
        spawnEntityAt(runTutorial, runSpawn, true, false);

        spearObstacleSpawn.add(textOffset);
        Entity spearObstacleTutorial = ObstacleFactory.createTutorialSpearObstacle();
        spawnEntityAt(spearObstacleTutorial, spearObstacleSpawn, true, false);

    }

    protected Entity spawnPlayer(int x, int y) {
        Entity newPlayer = PlayerFactory.createPlayer();
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(newPlayer, pos, true, false);

        return newPlayer;
    }

    /**
     * Spawn a background image starting at x and a bifrost split.
     *
     * @param x     starting coordinate
     * @param width width of the image using scaleWidth(width)
     * @param world world type, must match the first word of a .png file in
     *              assets/images/Backgrounds/'world'_bg.png, any information after an underscore
     *              in world is ignored i.e. asgard and asgard_3 are both the same.
     */
    protected void spawnBackground(int x, int width, String world) {
        logger.debug("Spawning background with world {}", world);
        Entity background = ObstacleFactory.createBackground(world, width);
        GridPoint2 pos = new GridPoint2(x, -1);
        spawnEntityAt(background, pos, false, false);

        spawnBifrost(x);
    }

    /*
    * Spawns a biforst level transition
    */
    protected void spawnBifrost(int x) {
        Entity bifrost = ObstacleFactory.createBifrost();
        GridPoint2 pos2 = new GridPoint2(x, 10);
        spawnEntityAt(bifrost, pos2, true, true);
    }

    /**
     * This spawns the Wall of Death
     */
    protected void spawnWallOfDeath() {
        GridPoint2 leftPos = new GridPoint2(-45, 13);
        GridPoint2 leftPos2 = new GridPoint2(-10, 13);
        Entity wallOfDeath = NPCFactory.createWallOfDeath(getPlayer());
        Entity sfx = NPCFactory.createScreenFX(getPlayer());
        wallOfDeath.addComponent(new CameraShakeComponent(getPlayer(), this.terrainFactory.getCameraComponent(), sfx));
        wallOfDeath.addComponent(new FallDamageComponent(getPlayer()));

        GridPoint2 leftPos3 = new GridPoint2(-20, 13);
        Entity deathGiant = NPCFactory.createDeathGiant(getPlayer());

        wallOfDeath.addComponent(new VariableSpeedComponent(getPlayer(), deathGiant, sfx));

        spawnEntityAt(wallOfDeath, leftPos, true, true);
        spawnEntityAt(sfx, leftPos2, true, true);
        spawnEntityAt(deathGiant, leftPos3, true, true);
    }

    protected void spawnBifrostFX(int x, int y) {
        Entity bfx = NPCFactory.createBifrostFX();
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(bfx, pos, false, false);
    }

    protected void spawnLevelLoadTrigger(int x) {
        GridPoint2 centrePos = new GridPoint2(x, 11);
        Entity levelLoadTrigger = ObstacleFactory.createLevelLoadTrigger();
        spawnEntityAt(levelLoadTrigger, centrePos, true, true);
    }


    // this has to get kept otherwise calls to spawn stuff
    // gets messed, as terrain has not been initialised
    private void spawnTerrain() {
        // Background terrain
        terrain = terrainFactory.createTerrain(TerrainFactory.TerrainType.RAGNAROK_MAIN);
        spawnEntity(new Entity().addComponent(terrain));

        // Terrain walls
        float tileSize = terrain.getTileSize();
        GridPoint2 tileBounds = terrain.getMapBounds(0);
        Vector2 worldBounds = new Vector2(tileBounds.x * tileSize, tileBounds.y * tileSize);

        // Top
        spawnEntityAt(
                ObstacleFactory.createWall(worldBounds.x, WALL_HEIGHT),
                new GridPoint2(0, tileBounds.y),
                false,
                false);

        // Bottom
        spawnEntityAt(
                ObstacleFactory.createWall(worldBounds.x, WALL_HEIGHT),
                GridPoint2Utils.ZERO, false, false);

    }

    /**
     * Spawn a single platform at (x,y) with texture given by world.
     *
     * @param x     left coordinate
     * @param y     bottom coordinate
     * @param world the world type to load in
     */
    protected void spawnPlatform(int x, int y, String world) {
        Entity platform = ObstacleFactory.createPlatform(world);
        // y + 2 is on this line so the platform spawns at the top of a tile, not the bottom.
        GridPoint2 pos = new GridPoint2(x, y + 2);
        spawnEntityAt(platform, pos, false, false);
    }

    /**
     * Spawn a single floor entity at (x,y) with texture given by world.
     *
     * @param x     left coordinate
     * @param y     bottom coordinate
     * @param world the world type to load in
     */
    protected void spawnFloor(int x, int y, String world) {
        Entity floor = ObstacleFactory.createFloor(world);
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(floor, pos, false, false);
    }

    /**
     * Spawn a line of 'type' at x[0], x[1], ..., x[n] having only a single collision entity that
     * spans from x[0] to the end of the map chunk at x[n + 3].
     * <p>
     * On disposal of the single collision entity, all map pieces created are disposed of.
     *
     * @param x     game x coordinates to spawn floors at
     * @param y     game y coordinates to spawn floors at
     * @param type  type of map piece, should only be either 'floor' or 'platform'
     * @param world the world type to load in. Must match the name of a .png file in
     *              assets/images/worlds (e.g. assets/images/worlds/world.png)
     */
    protected void spawnMapChunk(int[] x, int y, String type, String world) {
        // Define a functional interface to create an entity of either a floor or platform type.
        Function<String, Entity> createMapEntity;
        // The height of the map piece.
        int height;
        if (type.equals("floor")) {
            createMapEntity = ObstacleFactory::createFloorNoCollider;
            height = 3;
        } else if (type.equals("platform")) {
            createMapEntity = ObstacleFactory::createPlatformNoCollider;
            height = 1;
            // Platforms should be sitting at the top of a tile.
            y += 2;
        } else {
            logger.error("Tried to spawn map chunk with incorrect type {}", type);
            return;
        }

        Entity[] entities = new Entity[x.length];
        for (int i = 0; i < x.length; i++) {
            Entity mapPart = createMapEntity.apply(world);
            // Add to array of entities so that they can all be disposed of at once.
            entities[i] = mapPart;
            GridPoint2 pos = new GridPoint2(x[i], y);
            spawnEntityAt(mapPart, pos, false, false);
        }

        /* Calculate width by getting taking away the starting position of the first platform from
           the starting position of the last platform. This is still one map piece too short, so
           add 3 which is the width of a single tile. */
        int width = x[x.length - 1] - x[0] + 3;
        Entity collider = ObstacleFactory.createCollider(width, height);
        collider.addComponent(new GroupDisposeComponent(entities));

        GridPoint2 pos = new GridPoint2(x[0], y);
        spawnEntityAt(collider, pos, false, false);
    }

    protected void spawnRocks(int x, int y) {
        Entity rocks = ObstacleFactory.createRock();
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(rocks, pos, false, false);
    }

    protected void spawnSpikes(int x, int y) {
        Entity spikes = ObstacleFactory.createSpikes();
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(spikes, pos, false, false);
    }

    protected void spawnWolf(int x, int y) {
        spawnWolf(x,y,DEFAULT_SPEED);
    }

    protected void spawnWolf(int x, int y, int speed) {
        Entity wolf = NPCFactory.createWolf(player);
        wolf.getComponent(PhysicsMovementComponent.class).setMaxSpeed(speed);
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(wolf, pos, false, false);
    }

    protected void spawnSkeleton(int x, int y) {
        spawnSkeleton(x,y,DEFAULT_SPEED);
    }

    protected void spawnSkeleton(int x, int y, int speed) {
        Entity skeleton = NPCFactory.createSkeleton(player);
        skeleton.getComponent(PhysicsMovementComponent.class).setMaxSpeed(speed);
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(skeleton, pos, false, false);
    }

    protected void spawnFireSpirit(int x, int y) {
        Entity fireSpirit = NPCFactory.createFireSpirit(player);
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(fireSpirit, pos, false, false);
    }

    protected void spawnShield(int x, int y) {
        Entity shield = PowerUpFactory.createShieldPowerUp();
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(shield, pos, false, false);
    }

    protected void spawnSpear(int x, int y) {
        Entity spear = PowerUpFactory.createSpearPowerUp();
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(spear, pos, false, false);
    }

    protected void spawnLightning(int x, int y) {
        Entity lightning = PowerUpFactory.createLightningPowerUp();
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(lightning, pos, false, false);
    }

    public Entity getPlayer() {
        return this.player;
    }

    public void makePlayer(int x, int y) {
        this.player = spawnPlayer(x, y);
    }

    private void unloadAssets() {
        logger.debug("Unloading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.unloadAssets(racerTextures);
        resourceService.unloadAssets(racerTextureAtlases);

        ServiceLocator.getSoundService().unloadAssets();
    }

    @Override
    public void dispose() {
        super.dispose();
        this.unloadAssets();
    }

}