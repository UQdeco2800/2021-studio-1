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
import com.deco2800.game.utils.math.GridPoint2Utils;
import com.deco2800.game.utils.math.RandomUtils;
import java.io.*;
import static com.badlogic.gdx.Gdx.app;

import com.deco2800.game.services.ResourceService;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.components.gamearea.GameAreaDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Forest area for the demo game with trees, a player, and some enemies. */
public class RacerArea extends GameArea {
    private static final Logger logger = LoggerFactory.getLogger(RacerArea.class);
    private static final int LANE_0 = 4;
    private static final int LANE_1 = 9;
    private static final int LANE_2 = 15;
    private static final int LANE_3 = 21;
    private static final int[] LANES = new int[]{LANE_0, LANE_1, LANE_2, LANE_3};
    private static final GridPoint2 PLAYER_SPAWN = new GridPoint2(10, 15);
    private static final float WALL_WIDTH = 0.1f;
    private static final String[] forestTextures = {
        "images/box_boy_leaf.png",
        "images/floor.png",
        "images/platform.png",
        "images/tree.png",
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
        "images/terrain_iso_grass.atlas", "images/ghost.atlas", "images/odin.atlas"
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

        try {
            spawnWorld();
        } catch (IOException ex) {
            System.out.println("File Not Found Quitting");
            app.exit();
        }
        player = spawnPlayer();
        playMusic();
    }

    private void displayUI() {
        Entity ui = new Entity();
        ui.addComponent(new GameAreaDisplay("Box Forest"));
        spawnEntity(ui);
    }



    private void spawnWorld() throws IOException{
        // URL url = getClass().getResource("com/deco2800/game/entities/configs/LVL1.txt");
        // spawnPlatform(1, LANE_0, 3);
        // spawnPlatform(1, LANE_1, 4);
        // spawnPlatform(1, LANE_2, 5);
        
        
        File levelFile = new File("configs/LVL1.txt");
        FileReader fr = new FileReader(levelFile);
        BufferedReader br = new BufferedReader(fr);
        String line;
        Integer lane = 7;
        while ((line = br.readLine()) != null) {
            // TODO: CHECK LINES ARE ALL THE SAME LENGTH
            for (int i = 0; i < line.length(); i++) {
                switch ((line.charAt(i))) {
                    case 'P':
                        // PLATFORM
                        spawnPlatform(1, LANES[Math.round(lane/2)], i*3);
                        break;
                    case 'F':
                        // FLOOR
                        spawnFloor(1, LANES[Math.round(lane/2)], i*3);
                        spawnFloor(1, LANES[Math.round(lane/2)], i*3+1);
                        spawnFloor(1, LANES[Math.round(lane/2)], i*3+2);
                        break;
                    default:
                        break;
                }
            }
            lane = lane -1;
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
        spawnEntityAt(
            ObstacleFactory.createWall(WALL_WIDTH, worldBounds.y), GridPoint2Utils.ZERO, false, false);
        // Right
        // spawnEntityAt(
        //     ObstacleFactory.createWall(WALL_WIDTH, worldBounds.y),
        //     new GridPoint2(tileBounds.x, 0),
        //     false,
        //     false);
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

    /**
     * Spawn a platform that is length long with height given by lane and x coordinate given by xCord.
     *
     * @param length the length of the platform to be made
     * @param lane   the y coordinate of the platform in the game area. It is preferable but not
     *               required to use the variables LANE_0, LANE_1, LANE_2, ... for this value
     * @param xCord  the x coordinate of this platform in the game area's grid system
     */
    private void spawnPlatform(int length, int lane, int xCord) {
        // In later implementations, length should create a platform that is (length * platform unit
        // length) long. But at the moment it just creates multiple platforms stacked next to each
        // other.
        
        for (int i = 0; i < length; i++) {
            
            // Make the  lane refer to the height of the top of the platform, not the bottom. This breaks
            // when the scale height is set to 1 for some reason, as the scale height does not correlate
            // to actual height.
            Entity platform = ObstacleFactory.createPlatform();
            GridPoint2 pos = new GridPoint2(Math.round(xCord + (i * platform.getScale().x) * 2), Math.round(lane - platform.getScale().y));
            spawnEntityAt(platform, pos, false, false);
        }
    }

    /**
     * Spawn a floor that is length long with height given by lane and x coordinate given by xCord.
     *
     * @param length the length of the floor to be made
     * @param lane   the y coordinate of the floor in the game area. It is preferable but not
     *               required to use the variables LANE_0, LANE_1, LANE_2, ... for this value
     * @param xCord  the x coordinate of this floor in the game area's grid system
     */
    private void spawnFloor(int length, int lane, int xCord) {
        
        for (int i = 0; i < length; i++) {
            // Make the  lane refer to the height of the top of the platform, not the bottom. This breaks
            // when the scale height is set to 1 for some reason, as the scale height does not correlate
            // to actual height.
            Entity floor = ObstacleFactory.createFloor();
            GridPoint2 pos = new GridPoint2(Math.round(xCord + (i * floor.getScale().x) * 2), Math.round(lane - floor.getScale().y));
            spawnEntityAt(floor, pos, false, false);
        }
    }

    private Entity spawnPlayer() {
        Entity newPlayer = PlayerFactory.createPlayer();
        spawnEntityAt(newPlayer, PLAYER_SPAWN, true, true);
        return newPlayer;
    }

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
