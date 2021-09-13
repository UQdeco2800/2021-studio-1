package com.deco2800.game.areas;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.areas.terrain.TerrainFactory;
import com.deco2800.game.areas.terrain.TerrainFactory.TerrainType;
import com.deco2800.game.components.TouchDisposeComponent;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.*;
import com.deco2800.game.events.listeners.EventListener1;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.utils.math.GridPoint2Utils;
import com.deco2800.game.utils.math.RandomUtils;
import java.io.*;
import java.util.Random;

import static com.badlogic.gdx.Gdx.app;

import com.deco2800.game.services.ResourceService;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.components.gamearea.GameAreaDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Forest area for the demo game with trees, a player, and some enemies. */
public class RacerArea extends GameArea {
    private static final Logger logger = LoggerFactory.getLogger(RacerArea.class);
    private static final int NUM_SKELETONS = 2;
    private static final int NUM_WOLF = 2;
    private static final int NUM_SPEARS = 1;
    private static final int LANE_0 = 4;
    private static final int LANE_1 = 9;
    private static final int LANE_2 = 15;
    private static final int LANE_3 = 21;
    private static final int[] LANES = new int[] { LANE_0, LANE_1, LANE_2, LANE_3 };
    private static final float WALL_WIDTH = 0.1f;
    private static final String[] forestTextures = {
        "images/box_boy_leaf.png",
        "images/deathGiant.png",
        "images/floor.png",
        "images/platform_gradient.png",
        "images/platform_no_gradient.png",
        "images/tree.png",
        "images/skeleton.png",
        "images/Spear_1.png",
        "images/Rock_1.png",
        "images/Spike_1.png",
        "images/ghost_1.png",
        "images/grass_1.png",
        "images/grass_2.png",
        "images/grass_3.png",
        "images/hex_grass_1.png",
        "images/hex_grass_2.png",
        "images/hex_grass_3.png",
        "images/iso_grass_1.png",
        "images/iso_grass_2.png",
        "images/iso_grass_3.png",
        "images/powerup-lightning.png",
        "images/powerup-spear.png",
        "images/powerup-shield.png",
        "images/wallOfDeath.png",
        "images/powerup.png"
    };
    private static final String[] forestTextureAtlases = {
        "images/terrain_iso_grass.atlas",
        "images/ghostKing.atlas",
        "images/odin.atlas",
        "images/wall.atlas",
        "images/deathGiant.atlas",
        "images/skeleton.atlas",
        "images/playerspear.atlas",
        "images/lightning-animation.atlas"
    };
    private static final String[] forestSounds = {"sounds/Impact4.ogg"};
    private static final String mainMusic = "sounds/main.mp3";
    private static final String townMusic = "sounds/town.mp3";
    private static final String raiderMusic = "sounds/raider.mp3";
    // sound effect of fire/burning behind giant *fwoom* *crackle*
    private static final String fireMusic = "sounds/fire.mp3";
    // sound effects of giant walking (still to be tested)
    private static final String walkMusic = "sounds/walk.mp3";
    private static final String[] forestMusic = { mainMusic, townMusic, raiderMusic, fireMusic, walkMusic };

    private Entity player;

    private Entity deathGiant;

    private Entity wallOfDeath;

    private final TerrainFactory terrainFactory;

    public RacerArea(TerrainFactory terrainFactory) {
        super();
        this.terrainFactory = terrainFactory;
    }

    /**
     * Create the game area, including terrain, static entities (trees), dynamic
     * entities (player)
     */
    @Override
    public void create(int xOffset) {
        loadAssets();

        displayUI();

        spawnTerrain();

        spawnDeathGiant();

        spawnWallOfDeath();

        try {
            spawnWorld();
        } catch (IOException ex) {
            System.out.println("File Not Found Quitting"); // BRUUUUUUUUUUUUUUUH
            // ^ WHOEVER DID THIS, THIS IS THE POINT OF THE LOOOOOOGER
            // ALSO SYSTEM.OUT??? NOT EVEN THE ERROR FILE
            // BRUUUUUUUUUH
            app.exit();
        }

        spawnSkeletons();
        spawnWolf();
        spawnSpears();
        spawnPowerUps();

        playMusic();
    }

    private void displayUI() {
        Entity ui = new Entity();
        ui.addComponent(new GameAreaDisplay("Box Forest"));
        spawnEntity(ui);
    }

    private void spawnWorld() throws IOException {
        // URL url =
        // getClass().getResource("com/deco2800/game/entities/configs/LVL1.txt");
        // spawnPlatform(1, LANE_0, 3);
        // spawnPlatform(1, LANE_1, 4);
        // spawnPlatform(1, LANE_2, 5);

        try (BufferedReader br = new BufferedReader(new FileReader("configs/LVL1.txt"))) {
            String line;
            float lane = 6.8f;
            while ((line = br.readLine()) != null) {
                // TODO: PERFORM INPUT CHECKING ON LVL FILE
                for (int i = 0; i < line.length(); i++) {
                    switch ((line.charAt(i))) {
                        case 'P':
                            // PLATFORM
                            spawnPlatform(1, LANES[Math.round(lane / 2)], (i * 3));
                            break;
                        case 'F':
                            // FLOOR
                            spawnFloor(1, LANES[Math.round(lane / 2)], (i * 3));
                            spawnFloor(1, LANES[Math.round(lane / 2)], (i * 3) + 1);
                            spawnFloor(1, LANES[Math.round(lane / 2)], (i * 3) + 2);
                            break;
                        case 'A':
                            // A for Avatar :)
                            player = spawnPlayer(LANES[Math.round(lane / 2)] + 1, (i * 3) + 1);
                            break;
                        case 'S':
                            // SPIKE
                            spawnSpike(LANES[Math.round(lane / 2)] + 1, (i * 3) + 1);
                            break;
                        case 'R':
                            // ROCK
                            spawnRock(LANES[Math.round(lane / 2)] + 1, (i * 3) + 1);
                        default:
                            break;
                    }
                }
                lane = lane - 1;
            }
        } catch (IOException ioe) {
            throw new IOException("Level could not be loaded.");
        }
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
        // spawnEntityAt(
        // ObstacleFactory.createWall(WALL_WIDTH, worldBounds.y), GridPoint2Utils.ZERO,
        // false, false);
        // Right
        // spawnEntityAt(
        // ObstacleFactory.createWall(WALL_WIDTH, worldBounds.y),
        // new GridPoint2(tileBounds.x, 0),
        // false,
        // false);
        // Top
        spawnEntityAt(ObstacleFactory.createWall(worldBounds.x, WALL_WIDTH), new GridPoint2(0, tileBounds.y), false,
                false);
        // Bottom
        spawnEntityAt(ObstacleFactory.createWall(worldBounds.x, WALL_WIDTH), GridPoint2Utils.ZERO, false, false);
    }

    /**
     * Spawn a platform that is length long with height given by lane and x
     * coordinate given by xCord.
     *
     * @param length the length of the platform to be made
     * @param lane   the y coordinate of the platform in the game area. It is
     *               preferable but not required to use the variables LANE_0,
     *               LANE_1, LANE_2, ... for this value
     * @param xCord  the x coordinate of this platform in the game area's grid
     *               system
     */
    private void spawnPlatform(int length, int lane, int xCord) {
        // In later implementations, length should create a platform that is (length *
        // platform unit
        // length) long. But at the moment it just creates multiple platforms stacked
        // next to each
        // other.
        Entity platformGradient = ObstacleFactory.createPlatformWithGradient();
        lane = Math.round(lane - platformGradient.getScale().y);
        GridPoint2 platformPos1 = new GridPoint2(Math.round(xCord + (0 * platformGradient.getScale().x) * 2), lane);
        spawnEntityAt(platformGradient, platformPos1, false, false);
        for (int i = 0; i < length; i++) {
            Entity platform = ObstacleFactory.createPlatform();
            // Make the lane refer to the height of the top of the platform, not the bottom.
            // This breaks
            // when the scale height is set to 1 for some reason, as the scale height does
            // not correlate
            // to actual height.
            GridPoint2 pos = new GridPoint2(Math.round(xCord + (i * platform.getScale().x) * 2),
                    Math.round(lane - platform.getScale().y));
            spawnEntityAt(platform, pos, false, false);
        }
    }

    /**
     * Spawn a floor that is length long with height given by lane and x coordinate
     * given by xCord.
     *
     * @param length the length of the floor to be made
     * @param lane   the y coordinate of the floor in the game area. It is
     *               preferable but not required to use the variables LANE_0,
     *               LANE_1, LANE_2, ... for this value
     * @param xCord  the x coordinate of this floor in the game area's grid system
     */
    private void spawnFloor(int length, int lane, int xCord) {

        for (int i = 0; i < length; i++) {
            // Make the lane refer to the height of the top of the platform, not the bottom.
            // This breaks
            // when the scale height is set to 1 for some reason, as the scale height does
            // not correlate
            // to actual height.
            Entity floor = ObstacleFactory.createFloor();
            GridPoint2 pos = new GridPoint2(Math.round(xCord + (i * floor.getScale().x) * 2),
                    Math.round(lane - floor.getScale().y));
            spawnEntityAt(floor, pos, false, false);
        }
    }

    private void spawnRock(int lane, int xCord) {
        Entity rock = ObstacleFactory.createRock();
        // GridPoint2 pos = new GridPoint2(xCord, Math.round(lane - rock.getScale().y));
        GridPoint2 pos = new GridPoint2(xCord, Math.round(lane));
        spawnEntityAt(rock, pos, true, false);
    }

    private void spawnSpike(int lane, int xCord) {
        Entity spike = ObstacleFactory.createSpikes();
        GridPoint2 pos = new GridPoint2(xCord, Math.round(lane - spike.getScale().y));
        spawnEntityAt(spike, pos, true, false);
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

        // // floor
        // for (int i = 0; i < 2; i++) {
        // GridPoint2 randomPos = RandomUtils.random(floorMin, floorMax);
        // Entity rock = ObstacleFactory.createRock();
        // spawnEntityAt(rock, randomPos, false, false);
        // }
    }

    /**
     * Spawns the spikes for the game, they can only spawn on the floor or in lane
     * 2.
     */
    private void spawnSpikes() {
        GridPoint2 floorMin = new GridPoint2(1, 5);
        GridPoint2 floorMax = new GridPoint2(27, 5);
        GridPoint2 middleMin = new GridPoint2(10, 16);
        GridPoint2 middleMax = new GridPoint2(20, 16);

        // // floor
        // for (int i = 0; i < 1; i++) {
        // GridPoint2 randomPos = RandomUtils.random(floorMin, floorMax);
        // Entity spikes = ObstacleFactory.createSpikes();
        // spawnEntityAt(spikes, randomPos, false, false);
        // }

        // middle platform
        for (int i = 0; i < 1; i++) {
            GridPoint2 randomPos = RandomUtils.random(middleMin, middleMax);
            Entity spikes = ObstacleFactory.createSpikes();
            spawnEntityAt(spikes, randomPos, false, false);
        }
    }

    private void spawnPowerUps() {
        GridPoint2 bottomRightMin = new GridPoint2(21, 10);
        GridPoint2 bottomRightMax = new GridPoint2(27, 10);
        GridPoint2 bottomLeftMin = new GridPoint2(1, 10);
        GridPoint2 bottomLeftMax = new GridPoint2(4, 10);

        GridPoint2 randomPos1 = RandomUtils.random(bottomLeftMin, bottomRightMin);
        GridPoint2 randomPos2 = RandomUtils.random(bottomLeftMin, bottomRightMin);
        GridPoint2 randomPos3 = new GridPoint2(30, 5);
        GridPoint2 playerPos = new GridPoint2(12,5);

        Entity lightningPowerUp = PowerUpFactory.createLightningPowerUp();
        Entity shieldPowerUp = PowerUpFactory.createShieldPowerUp();

        Entity spearPowerUp1 = PowerUpFactory.createSpearPowerUp();
        spearPowerUp1.getComponent(AnimationRenderComponent.class).startAnimation("static");

        Entity spearPowerUp2 = PowerUpFactory.createSpearPowerUp();
        spearPowerUp2.getComponent(AnimationRenderComponent.class).startAnimation("static");

        spawnEntityAt(lightningPowerUp, randomPos1, false, false);
        spawnEntityAt(shieldPowerUp, randomPos2, false, false);
        spawnEntityAt(spearPowerUp1, playerPos, false, false);
        spawnEntityAt(spearPowerUp2, randomPos3, false, false);
    }

    private Entity spawnPlayer(int lane, int xCord) {
        Entity newPlayer = PlayerFactory.createPlayer();
        GridPoint2 pos = new GridPoint2(xCord, Math.round(lane - newPlayer.getScale().y) + 1);
        spawnEntityAt(newPlayer, pos, true, false);
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

        // for (int i = 0; i < NUM_SKELETONS; i++) {
        // GridPoint2 randomPos = RandomUtils.random(minPos, maxPos);
        // Entity skeleton = NPCFactory.createSkeleton(player);
        // spawnEntityAt(skeleton, randomPos, true, true);
        // }
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
        spawnSpear(1, new GridPoint2(60, 12));
        spawnSpear(2, new GridPoint2(40, 17));
        spawnSpear(3, new GridPoint2(80, 23));
        spawnSpear(1, new GridPoint2(100, 12));
        spawnSpear(2, new GridPoint2(120, 17));
        spawnSpear(3, new GridPoint2(100, 23));
    }

    /**
     * This creates spears to be spawned in their respective lanes. The number of
     * spears spawned is given by NUM_SPEARS.
     *
     * @param spearLane     the lane the spear is assigned to, must be in range [1,
     *                      3]
     * @param startLocation the starting location for the spear
     */
    private void spawnSpear(int spearLane, GridPoint2 startLocation) {
        float lane;
        switch (spearLane) {
            case 1:
                lane = 6f;
                break;
            case 2:
                lane = 8.5f;
                break;
            case 3:
                lane = 11.5f;
                break;
            default:
                logger.error("Attempt at spawning spear in unknown lane {}, will be ignored", spearLane);
                return;
        }
        for (int i = 0; i < NUM_SPEARS; i++) {
            Entity spear = ProjectileFactory.createSpearAtHeight(lane);
            spawnEntityAt(spear, startLocation, true, true);
        }
    }

    /**
     * This spawns the Wall of Death
     */
    private void spawnWallOfDeath() {
        GridPoint2 leftPos = new GridPoint2(-40, 14);
        wallOfDeath = NPCFactory.createWallOfDeath(player);
        spawnEntityAt(wallOfDeath, leftPos, true, true);
    }

    /**
     * This spawns the Death Giant in front of the Wall of Death
     */
    private void spawnDeathGiant() {
        GridPoint2 leftPos2 = new GridPoint2(-15, 15);
        deathGiant = NPCFactory.createDeathGiant(player);
        spawnEntityAt(deathGiant, leftPos2, true, true);
    }

    /**
     * Play all SFX in the game.
     */
    private void playMusic() {

        String witchMusic;

        Random rand = new Random();
        switch (rand.nextInt(3)) {
            case 1:
                witchMusic = townMusic;
                break;
            case 2:
                witchMusic = raiderMusic;
                break;
            default:
                witchMusic = mainMusic;
        }
        Music music = ServiceLocator.getResourceService().getAsset(witchMusic, Music.class);
        Music fire = ServiceLocator.getResourceService().getAsset(fireMusic, Music.class);
        Music walk = ServiceLocator.getResourceService().getAsset(walkMusic, Music.class);
        music.setLooping(true);
        fire.setLooping(true);
        walk.setLooping(true);
        music.setVolume(0.3f);
        fire.setVolume(0.8f);
        walk.setVolume(0.8f);
        music.play();
        fire.play();
        walk.play();
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
        ServiceLocator.getResourceService().getAsset(mainMusic, Music.class).stop();
        this.unloadAssets();
    }
}
