package com.deco2800.game.areas;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.areas.terrain.TerrainFactory;
import com.deco2800.game.components.gamearea.GameAreaDisplay;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.NPCFactory;
import com.deco2800.game.entities.factories.ObstacleFactory;
import com.deco2800.game.entities.factories.PlayerFactory;
import com.deco2800.game.services.ResourceService;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.utils.math.GridPoint2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.badlogic.gdx.Gdx.app;

import java.awt.geom.Area;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RagnarokArea extends GameArea {

    private static final Logger logger = LoggerFactory.getLogger(RagnarokArea.class);

    private static final float WALL_WIDTH = 0.1f;
    private final String name; //initiliase in the loader
    private AreaManager manager;

    protected Entity player;

    //TODO: make Json
    private static final String[] racerTextures = {
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
            "images/death_giant.png"
    };

    //TODO: make Json,
    private static final String[] racerTextureAtlases = { //TODO: remove references to Box Boy (forest)
            "images/terrain_iso_grass.atlas", "images/ghostKing" +
            ".atlas", "images/odin.atlas", "images/wall.atlas", "images/skeleton.atlas"
    };

    // get the sounds to work and then move the music & sounds to a json
    //TODO: make Json
    private static final String[] racerSounds = {"sounds/Impact4.ogg"};
    private static final String mainMusic = "sounds/main.mp3";
    private static final String townMusic = "sounds/town.mp3";
    private static final String raiderMusic = "sounds/raider.mp3";
    private static final String[] racerMusic = {mainMusic, townMusic, raiderMusic};

    private final TerrainFactory terrainFactory;

    //have the loader return a level? fuck yeh
    public RagnarokArea(String name, TerrainFactory terrainFactory) {
        super();
        this.name = name;
        this.terrainFactory = terrainFactory;
    }

    public void create() {
        create(0);
    }

    public void create(int xOffset) {
        loadAssets();

        displayUI();

        spawnTerrain();
        //spawnWallOfDeath(); //this is dependant

        //player = spawnPlayer(10, 5);

        //playMusic(); //TODO: eventual move to music
    }

    public void setManager(AreaManager manager) {
        this.manager = manager;
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
        resourceService.loadSounds(racerSounds);
        resourceService.loadMusic(racerMusic);

        while (!resourceService.loadForMillis(10)) {
            // This could be upgraded to a loading screen
            logger.info("Loading... {}%", resourceService.getProgress());
        }
    }

    protected Entity spawnPlayer(int x, int y) {
        Entity newPlayer = PlayerFactory.createPlayer();
        GridPoint2 pos = new GridPoint2(x, y); /*Math.round(lane.y - newPlayer.getScale().y));*/
        spawnEntityAt(newPlayer, pos, true, false);
        // ^ so this will register it to entity service and the activeEntities list
        // in GameArea

        return newPlayer;
    }

    //TODO: KEEP
    private void spawnTerrain() {
        // Background terrain
        terrain = terrainFactory.createTerrain(TerrainFactory.TerrainType.FOREST_DEMO);
        spawnEntity(new Entity().addComponent(terrain));

        // Terrain walls
        float tileSize = terrain.getTileSize();
        GridPoint2 tileBounds = terrain.getMapBounds(0);
        Vector2 worldBounds = new Vector2(tileBounds.x * tileSize, tileBounds.y * tileSize);

        // Left
        // spawnEntityAt(
        //     ObstacleFactory.createWall(WALL_WIDTH, worldBounds.y), GridPoint2Utils.ZERO, false, false);
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

    protected void spawnPlatform(int x, int y) {
        Entity platform = ObstacleFactory.createPlatform();
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(platform, pos, false, false);
    }

    protected void spawnFloor(int x, int y) {
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Entity floor = ObstacleFactory.createFloor();
                GridPoint2 pos = new GridPoint2(x+i, y+j);
                spawnEntityAt(floor, pos, false, false);
            }
        }
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
        Entity wolf = NPCFactory.createWolf(player);
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(wolf, pos, false, false);
    }

    protected void spawnSkeleton(int x, int y) {
        Entity skeleton = NPCFactory.createSkeleton(player);
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(skeleton, pos, false, false);
    }

    public Entity getPlayer() {
        return this.player;
    }

    public void clearPlayer() {
        player.dispose();
    }

    public void makePlayer(int x, int y) {
        this.player = spawnPlayer(x, y);
    }

    protected void deleteEntity() {

    }



}

enum Lanes {
    LANE0(4),
    LANE1(9),
    LANE2(15),
    LANE3(21);

    public int y;

    Lanes(int y_value) {
        this.y = y;
    };
}