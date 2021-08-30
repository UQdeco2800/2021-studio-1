package com.deco2800.game.areas;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.areas.terrain.TerrainFactory;
import com.deco2800.game.areas.terrain.TerrainFactory.TerrainType;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.NPCFactory;
import com.deco2800.game.entities.factories.ObstacleFactory;
import com.deco2800.game.entities.factories.PlayerFactory;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.entities.factories.ProjectileFactory;
import com.deco2800.game.utils.math.GridPoint2Utils;
import com.deco2800.game.utils.math.RandomUtils;

import com.deco2800.game.services.ResourceService;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.components.gamearea.GameAreaDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Forest area for the demo game with trees, a player, and some enemies. */
public class RacerArea extends GameArea {
    private static final Logger logger = LoggerFactory.getLogger(RacerArea.class);
    private static final int NUM_TREES = 2;
    private static final int NUM_GHOSTS = 2;
    private static final int NUM_SKELETONS = 2;
    private static final int NUM_WOLF = 2;
    private static final int NUM_SPEARS = 3;
    private static final int NUM_ROCKS = 1;
    private static final int NUM_SPIKES = 3;
    private static final int LANE_1 = 9;
    private static final int LANE_2 = 15;
    private static final int LANE_3 = 21;
    private static final GridPoint2 PLAYER_SPAWN = new GridPoint2(10, 15);
    private static final GridPoint2 FLOOR = new GridPoint2(10, 5);
    private static final float WALL_WIDTH = 0.1f;
    private static final String[] forestTextures = {
        "images/box_boy_leaf.png",
        "images/floor.png",
        "images/platform_gradient.png",
        "images/platform_no_gradient.png",
        "images/tree.png",
            "images/skeleton.png",
            "images/wolf_1.png",
            "images/Spear_1.png",
            "images/Rock_1.png",
            "images/Spike_1.png",
        "images/ghost_king.png",
        "images/ghost_1.png",
        "images/grass_1.png",
        "images/grass_2.png",
        "images/grass_3.png",
        "images/hex_grass_1.png",
        "images/hex_grass_2.png",
        "images/hex_grass_3.png",
        "images/iso_grass_1.png",
        "images/iso_grass_2.png",
        "images/iso_grass_3.png"
    };
    private static final String[] forestTextureAtlases = {
        "images/terrain_iso_grass.atlas", "images/ghost.atlas", "images/ghostKing" +
        ".atlas", "images/odin.atlas"
    };
    private static final String[] forestSounds = {"sounds/Impact4.ogg"};
    private static final String backgroundMusic = "sounds/BGM_03_mp3.mp3";
    private static final String[] forestMusic = {backgroundMusic};


    private final TerrainFactory terrainFactory;

    private Entity player;

    public RacerArea(TerrainFactory terrainFactory) {
        super();
        this.terrainFactory = terrainFactory;
    }

    /** Create the game area, including terrain, static entities (trees), dynamic entities (player) */
    @Override
    public void create() {
        loadAssets();

        displayUI();

        spawnTerrain();
        //spawnTrees();
        spawnPlatforms();
        spawnFloor();
        spawnRocks();
        spawnSpikes();
        spawnSkeletons();
        spawnWolf();
        spawnSpears();

        player = spawnPlayer();
        playMusic();
    }

    private void displayUI() {
        Entity ui = new Entity();
        ui.addComponent(new GameAreaDisplay("Box Forest"));
        spawnEntity(ui);
    }

  private void spawnTerrain() {
    // Background terrain
    terrain = terrainFactory.createTerrain(TerrainType.FOREST_DEMO);
    spawnEntity(new Entity().addComponent(terrain));

    // Terrain walls
    float tileSize = terrain.getTileSize();
    GridPoint2 tileBounds = terrain.getMapBounds(0);
    Vector2 worldBounds = new Vector2(tileBounds.x * tileSize, tileBounds.y * tileSize);
    // Left
    spawnEntityAt(
        ObstacleFactory.createWall(WALL_WIDTH, worldBounds.y), GridPoint2Utils.ZERO, false, false);
    // Right
    spawnEntityAt(
        ObstacleFactory.createWall(WALL_WIDTH, worldBounds.y),
        new GridPoint2(tileBounds.x, 0),
        false,
        false);
    // Top
    spawnEntityAt(
        ObstacleFactory.createWall(worldBounds.x, WALL_WIDTH),
        new GridPoint2(0, tileBounds.y),
        false,
        false);
    // Bottom
    spawnEntityAt(
        ObstacleFactory.createWall(worldBounds.x, WALL_WIDTH), GridPoint2Utils.ZERO, false, false);
  }

  private void spawnPlatforms() {
    spawnPlatform(2, LANE_1, 0);
    spawnPlatform(3, LANE_1, 21);
    spawnPlatform(4, LANE_2, 10);
    spawnPlatform(1, LANE_3, 20);
    spawnPlatform(2, LANE_3, 5);
  }

    /**
     * Spawn a platform that is length long with height given by lane and x coordinate given by xCord.
     *
     * @param length the length of the platform to be made
     * @param lane   the y coordinate of the platform in the game area. It is preferable but not
     *               required to use the variables LANE_1, LANE_2, ... for this value
     * @param xCord  the x coordinate of this platform in the game area's grid system
     */
    private void spawnPlatform(int length, int lane, int xCord) {
        // In later implementations, length should create a platform that is (length * platform unit
        // length) long. But at the moment it just creates multiple platforms stacked next to each
        // other.
        Entity platformGradient = ObstacleFactory.createPlatformWithGradient();
        lane = Math.round(lane - platformGradient.getScale().y);
        GridPoint2 platformPos1 = new GridPoint2(Math.round(xCord + (0 * platformGradient.getScale().x) * 2), lane);
        spawnEntityAt(platformGradient, platformPos1, false, false);
        for (int i = 1; i < length; i++) {
            Entity platform = ObstacleFactory.createPlatform();
            // Make the  lane refer to the height of the top of the platform, not the bottom. This breaks
            // when the scale height is set to 1 for some reason, as the scale height does not correlate
            // to actual height.
            lane = Math.round(lane - platform.getScale().y);
            GridPoint2 pos = new GridPoint2(Math.round(xCord + (i * platform.getScale().x) * 2), lane);
            spawnEntityAt(platform, pos, false, false);
        }
    }

    private void spawnFloor() {
        for (int i = 0; i < 15; i++) {
            Entity newFloor = ObstacleFactory.createFloor();
            GridPoint2 position = new GridPoint2(i * 2, 3);
            spawnEntityAt(newFloor, position, false, false);
        }
    }

    /**
     * Spawns the rocks for the game, they can only spawn in lane 1 and the floor.
     */
    private void spawnRocks() {
        GridPoint2 bottomRightMin = new GridPoint2(21, 10);
        GridPoint2 bottomRightMax = new GridPoint2(27, 10);
        GridPoint2 bottomLeftMin = new GridPoint2(1, 10);
        GridPoint2 bottomLeftMax = new GridPoint2(4, 10);
        GridPoint2 floorMin = new GridPoint2(1, 5);
        GridPoint2 floorMax = new GridPoint2(27, 5);

        // Bottom right platform
        for (int i = 0; i < 1; i++) {
            GridPoint2 randomPos = RandomUtils.random(bottomRightMin, bottomRightMax);
            Entity rock = ObstacleFactory.createRock();
            spawnEntityAt(rock, randomPos, false, false);
        }

        // Bottom left platform
        for (int i = 0; i < 1; i++) {
            GridPoint2 randomPos = RandomUtils.random(bottomLeftMin, bottomLeftMax);
            Entity rock = ObstacleFactory.createRock();
            spawnEntityAt(rock, randomPos, false, false);
        }

//        // floor
//        for (int i = 0; i < 2; i++) {
//            GridPoint2 randomPos = RandomUtils.random(floorMin, floorMax);
//            Entity rock = ObstacleFactory.createRock();
//            spawnEntityAt(rock, randomPos, false, false);
//        }
    }

    /**
     * Spawns the spikes for the game, they can only spawn on the floor or in lane 2.
     */
    private void spawnSpikes() {
        GridPoint2 floorMin = new GridPoint2(1, 5);
        GridPoint2 floorMax = new GridPoint2(27, 5);
        GridPoint2 middleMin = new GridPoint2(10, 16);
        GridPoint2 middleMax = new GridPoint2(20, 16);

//        // floor
//        for (int i = 0; i < 1; i++) {
//            GridPoint2 randomPos = RandomUtils.random(floorMin, floorMax);
//            Entity spikes = ObstacleFactory.createSpikes();
//            spawnEntityAt(spikes, randomPos, false, false);
//        }

        // middle platform
        for (int i = 0; i < 1; i++) {
            GridPoint2 randomPos = RandomUtils.random(middleMin, middleMax);
            Entity spikes = ObstacleFactory.createSpikes();
            spawnEntityAt(spikes, randomPos, false, false);
        }
    }

    private Entity spawnPlayer() {
        Entity newPlayer = PlayerFactory.createPlayer();
        spawnEntityAt(newPlayer, PLAYER_SPAWN, true, true);
        return newPlayer;
    }

    public Entity getPlayer() {
        return player;
    }

    /**
     * This spawns skeleton enemies on the platforms.
     */
    private void spawnSkeletons() {
        GridPoint2 bottomRightMin = new GridPoint2(21, 10);
        GridPoint2 bottomRightMax = new GridPoint2(27, 10);
        GridPoint2 bottomLeftMin = new GridPoint2(1, 10);
        GridPoint2 bottomLeftMax = new GridPoint2(4, 10);
        GridPoint2 minPos = new GridPoint2(0, 0);
        GridPoint2 maxPos = terrain.getMapBounds(0).sub(2, 2);

//        for (int i = 0; i < NUM_SKELETONS; i++) {
//            GridPoint2 randomPos = RandomUtils.random(minPos, maxPos);
//            Entity skeleton = NPCFactory.createSkeleton(player);
//            spawnEntityAt(skeleton, randomPos, true, true);
//        }
        // Bottom right platforms
        for (int i = 0; i < 1; i++) {
            GridPoint2 randomPos = RandomUtils.random(bottomRightMin, bottomRightMax);
            Entity skeleton = NPCFactory.createSkeleton(player);
            spawnEntityAt(skeleton, randomPos, true, true);
        }
        // Bottom left platform
        for (int i = 0; i < 1; i++) {
            GridPoint2 randomPos = RandomUtils.random(bottomLeftMin, bottomLeftMax);
            Entity skeleton = NPCFactory.createSkeleton(player);
            spawnEntityAt(skeleton, randomPos, true, true);
        }
    }

    /**
     * This spawns a wolf on the base floor.
     */
        private void spawnWolf() {
        GridPoint2 floor = new GridPoint2(27, 8);

        for (int i = 0; i < 1; i++) {
            Entity wolf = NPCFactory.createWolf(player);
            spawnEntityAt(wolf, floor, true, true);
        }
    }

    /**
     * This spawns the spears in each of the three different lanes.
     */
    private void spawnSpears() {
        spawnSpear(ProjectileFactory.createSpearLane_1(), new GridPoint2(27, 12));
        spawnSpear(ProjectileFactory.createSpearLane_2(), new GridPoint2(27, 17));
        spawnSpear(ProjectileFactory.createSpearLane_3(), new GridPoint2(27, 23));
    }

    /**
     * This creates the spears to be spawned in their respective lanes
     * @param spearLane the lane the spear is assigned to
     * @param startLocation the starting location for the spear
     */
    private void spawnSpear(Entity spearLane, GridPoint2 startLocation) {

        for (int i = 0; i < NUM_SPEARS; i++) {

            spawnEntityAt(spearLane, startLocation, true, true);

        }
    }
    /*
    private void spawnGhosts() {
        GridPoint2 minPos = new GridPoint2(0, 0);
        GridPoint2 maxPos = terrain.getMapBounds(0).sub(2, 2);

        for (int i = 0; i < NUM_GHOSTS; i++) {
            GridPoint2 randomPos = RandomUtils.random(minPos, maxPos);
            Entity ghost = NPCFactory.createGhost(player);
            spawnEntityAt(ghost, randomPos, true, true);
        }
    }

    private void spawnGhostKing() {
        GridPoint2 minPos = new GridPoint2(0, 0);
        GridPoint2 maxPos = terrain.getMapBounds(0).sub(2, 2);

        GridPoint2 randomPos = RandomUtils.random(minPos, maxPos);
        Entity ghostKing = NPCFactory.createGhostKing(player);
        spawnEntityAt(ghostKing, randomPos, true, true);
    }
     */

    private void playMusic() {
        Music music = ServiceLocator.getResourceService().getAsset(backgroundMusic, Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();
    }

    private void loadAssets() {
        logger.debug("Loading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.loadTextures(forestTextures);
        resourceService.loadTextureAtlases(forestTextureAtlases);
        resourceService.loadSounds(forestSounds);
        resourceService.loadMusic(forestMusic);

        while (!resourceService.loadForMillis(10)) {
            // This could be upgraded to a loading screen
            logger.info("Loading... {}%", resourceService.getProgress());
        }
    }

    private void unloadAssets() {
        logger.debug("Unloading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.unloadAssets(forestTextures);
        resourceService.unloadAssets(forestTextureAtlases);
        resourceService.unloadAssets(forestSounds);
        resourceService.unloadAssets(forestMusic);
    }

    @Override
    public void dispose() {
        super.dispose();
        ServiceLocator.getResourceService().getAsset(backgroundMusic, Music.class).stop();
        this.unloadAssets();
    }
}
