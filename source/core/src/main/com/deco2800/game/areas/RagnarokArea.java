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
import com.deco2800.game.components.CameraShakeComponent;

import static com.badlogic.gdx.Gdx.app;

import java.awt.geom.Area;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

public class RagnarokArea extends GameArea {

    private static final Logger logger = LoggerFactory.getLogger(RagnarokArea.class);

    private static final float WALL_WIDTH = 0.1f;
    private final String name; //initiliase in the loader
    private AreaManager manager;

    private Hashtable<GridPoint2, LinkedList<Entity>> entitySignUp;

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
            "images/deathGiant.png"
    };

    //TODO: make Json,
    private static final String[] racerTextureAtlases = { //TODO: remove references to Box Boy (forest)
            "images/terrain_iso_grass.atlas", "images/ghostKing" +
            ".atlas", "images/odin.atlas", "images/wall.atlas", "images/deathGiant.atlas", "images/skeleton.atlas"
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
        this.entitySignUp = new Hashtable<>();
    }

    public void create() {
        create(0);
    }

    public void create(int xOffset) {
        loadAssets();
        displayUI();
        spawnTerrain();

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

        //entitySignUp.
        // ^ so this will register it to entity service and the activeEntities list
        // in GameArea

        return newPlayer;
    }

    /**
     * This spawns the Wall of Death
     */
    protected void spawnWallOfDeath() {
        GridPoint2 leftPos = new GridPoint2(-40,13);
        Entity wallOfDeath = NPCFactory.createWallOfDeath(getPlayer());
        wallOfDeath.addComponent(new CameraShakeComponent(this.player,this.terrainFactory.getCameraComponent()));
        spawnEntityAt(wallOfDeath, leftPos, true, true);
    }

    /**
     * This spawns the Death Giant in front of the Wall of Death
     */
    protected void spawnDeathGiant() {
        GridPoint2 leftPos2 = new GridPoint2(-15, 13);
        Entity deathGiant = NPCFactory.createDeathGiant(getPlayer());
        spawnEntityAt(deathGiant, leftPos2, true, true);
    }


    protected void spawnLevelLoadTrigger(int x) {
        GridPoint2 centrePos = new GridPoint2(x, 6);
        Entity levelLoadTrigger = ObstacleFactory.createLevelLoadTrigger();
        spawnEntityAt(levelLoadTrigger, centrePos, true, true);
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
        GridPoint2 pos = new GridPoint2(x, y+2);
        spawnEntityAt(platform, pos, false, false);

        signup(pos, platform);

        //entitySignUp.put(pos, platform);
    }

    protected void spawnFloor(int x, int y) {
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Entity floor = ObstacleFactory.createFloor();
                GridPoint2 pos = new GridPoint2(x+i, y+j);
                spawnEntityAt(floor, pos, false, false);
                signup(pos, floor);
            }
        }
    }

    protected void spawnRocks(int x, int y) {
        Entity rocks = ObstacleFactory.createRock();
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(rocks, pos, false, false);
        signup(pos, rocks);
    }

    protected void spawnSpikes(int x, int y) {
        Entity spikes = ObstacleFactory.createSpikes();
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(spikes, pos, false, false);
        signup(pos, spikes);
    }

    protected void spawnWolf(int x, int y) {
        Entity wolf = NPCFactory.createWolf(player);
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(wolf, pos, false, false);
        //signup(pos, wolf);
    }

    protected void spawnSkeleton(int x, int y) {
        Entity skeleton = NPCFactory.createSkeleton(player);
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(skeleton, pos, false, false);
        //signup(pos, skeleton);
    }

    public void clearEntitiesAt(int x, int y) {
        // takes the global scale x and y, so mutliply them by 3 in here
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                GridPoint2 index = new GridPoint2(x+i, y+j);

                if (entitySignUp.get(index) != null) {
                    for (Entity e : entitySignUp.get(index)) {
                        e.flagDelete();
                    }
                }

            }
        }
    }

    public void clearAllEntities() {
        for (GridPoint2 g : entitySignUp.keySet()) {
            for (Entity e : entitySignUp.get(g)) {
                e.flagDelete();
            }
        }
    }

    public void signup(GridPoint2 pos, Entity entity) {
        if(!entitySignUp.containsKey(pos)) {

            LinkedList<Entity> posList = new LinkedList<>();
            entitySignUp.put(pos, posList);
        }

        entitySignUp.get(pos).add(entity);
    }

    public Entity getPlayer() {
        return this.player;
    }

    public void clearPlayer() {
        player.flagDelete();
    }

    public void makePlayer(int x, int y) {
        this.player = spawnPlayer(x, y);
    }

    public void movePlayerPos(float x, float y) {
        this.player.setPosition(x, y);
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
        this.y = y_value;
    }
}