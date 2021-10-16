package com.deco2800.game.areas;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.areas.terrain.TerrainFactory;
import com.deco2800.game.areas.terrain.TerrainFactory.TerrainType;
import com.deco2800.game.components.gamearea.GameAreaDisplay;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.*;
import com.deco2800.game.services.ResourceService;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.utils.math.GridPoint2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Forest area for the demo game with trees, a player, and some enemies. */
public class ObstacleArea extends GameArea {
    private static final Logger logger = LoggerFactory.getLogger(ObstacleArea.class);

    private static final float WALL_WIDTH = 0.1f;
    private static final float WALL_HEIGHT = 0.1f;

    private static final String[] forestTextures = {
            "images/box_boy_leaf.png",
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
            "images/deathGiant.png"
    };
    private static final String[] forestTextureAtlases = {
            "images/terrain_iso_grass.atlas",
            "images/ghostKing.atlas",
            "images/odin.atlas",
            "images/wall.atlas",
            "images/skeleton.atlas"
    };
    private static final String[] FOREST_SOUNDS = {"sounds/Impact4.ogg"};
    private static final String BACKGROUND_MUSIC = "sounds/bobjob.mp3";
    private static final String[] FOREST_MUSIC = {BACKGROUND_MUSIC};

    private final TerrainFactory terrainFactory;

    private Entity abstractPlayer;

    public ObstacleArea(TerrainFactory terrainFactory) {
        super();
        this.terrainFactory = terrainFactory;
    }

    /** Create the game area, including terrain, static entities (trees), dynamic entities (player) */
    @Override
    public void create() {
        create(0);
    }

    public void create(int xOffset) {
        loadAssets();

        spawnTerrain();

        spawnFloor(40, 4, xOffset);

        abstractPlayer = spawnAbstractPlayer();

        displayUI(); // done after all stuff cause it gets added, jafeel?

        makeGenerator();

        playMusic();
    }

    private void displayUI() {
        Entity ui = new Entity();

        GameAreaDisplay gameDisplay = new GameAreaDisplay("__obstacleArea");
        ui.addComponent(gameDisplay);

        spawnEntity(ui);
    }

    private void makeGenerator() {

        Entity generator = new Entity();
        GeneratorComponent generatorComponent = new GeneratorComponent(this, abstractPlayer);
        generator.addComponent(generatorComponent);

        spawnEntity(generator);


    }

  private void spawnTerrain() {
    // Background terrain
    terrain = terrainFactory.createTerrain(TerrainType.RAGNAROK_MAIN);
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
        ObstacleFactory.createWall(worldBounds.x, WALL_HEIGHT),
        new GridPoint2(0, tileBounds.y),
        false,
        false);
    // Bottom
    spawnEntityAt(
        ObstacleFactory.createWall(worldBounds.x, WALL_HEIGHT),
            GridPoint2Utils.ZERO, false, false);
  }

  public void spawnFromGenerator(ObstacleTypes obstacleTypes) {

      GridPoint2 spawnAt = new GridPoint2(27, 6);
      Entity entity = null;

      switch (obstacleTypes) {

          case ROCKS:
              entity = GeneratorFactory.createRock();
              break;
          case SPIKES:
              entity = GeneratorFactory.createSpikes();
              break;
          case WOLF:
              entity = GeneratorFactory.createWolf();
              break;
          case SKELETON:
              entity = GeneratorFactory.createSkeleton();
              break;
          default:
              break;

      }

      if (entity != null) {
          spawnEntityAt(entity, spawnAt, true, true);
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

    private Entity spawnAbstractPlayer() {
        Entity newAbstractPlayer = PlayerFactory.createAbstractPlayer();
        spawnEntityAt(newAbstractPlayer, new GridPoint2(0, 0), true, true);
        return newAbstractPlayer;
    }

    private void playMusic() {
        Music music = ServiceLocator.getResourceService().getAsset(BACKGROUND_MUSIC, Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();
    }

    private void loadAssets() {
        logger.debug("Loading assets");

        ResourceService resourceService = ServiceLocator.getResourceService();

        resourceService.loadTextures(forestTextures);
        resourceService.loadTextureAtlases(forestTextureAtlases);
        resourceService.loadSounds(FOREST_SOUNDS);
        resourceService.loadMusic(FOREST_MUSIC);

        while (!resourceService.loadForMillis(10)) {
            // This could be upgraded to a loading screen
            // logger.info("Loading... {}%", resourceService.getProgress());
        }
    }

    private void unloadAssets() {
        logger.debug("Unloading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.unloadAssets(forestTextures);
        resourceService.unloadAssets(forestTextureAtlases);
        resourceService.unloadAssets(FOREST_SOUNDS);
        resourceService.unloadAssets(FOREST_MUSIC);
    }

    @Override
    public void dispose() {
        super.dispose();
        ServiceLocator.getResourceService().getAsset(BACKGROUND_MUSIC, Music.class).stop();
        this.unloadAssets();
    }
}