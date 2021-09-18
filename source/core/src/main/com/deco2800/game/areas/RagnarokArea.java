package com.deco2800.game.areas;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.areas.terrain.TerrainFactory;
import com.deco2800.game.components.gamearea.GameAreaDisplay;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.*;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.services.ResourceService;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.utils.math.GridPoint2Utils;
import com.deco2800.game.utils.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.deco2800.game.components.CameraShakeComponent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class RagnarokArea extends GameArea {

    private static final Logger logger = LoggerFactory.getLogger(RagnarokArea.class);

    private static final float WALL_HEIGHT = 0.1f;
    private final String name; //initiliase in the loader
    private Vector2 lastPos;

    private HashMap<GridPoint2, LinkedList<Entity>> entitySignUp;

    protected Entity player;

    //TODO: make Json
    private static final String[] racerTextures = {
            "images/floor.png",
            "images/platform_gradient.png",
            "images/platform_no_gradient.png",
            "images/fire_spirit.png",
            "images/skeleton.png",
            "images/Spear_1.png",
            "images/Rock_1.png",
            "images/Spike_1.png",
            "images/deathGiant.png",
            "images/sfx.png",
            "images/earth_1.png",
            "images/earth_2.png",
            "images/earth_3.png",
            "images/earth_4.png",
            "images/asgard_1.png",
            "images/asgard_2.png",
            "images/hel_1.png",
            "images/hel_2.png",
            "images/jotunheimr_1.png",
            "images/jotunheimr_2.png",
            "images/powerup-shield.png",
            "images/blue_bck.png"
    };

    //TODO: make Json,
    private static final String[] racerTextureAtlases = { //TODO: remove references to Box Boy (forest)
            "images/wolf.atlas",
            "images/odin.atlas",
            "images/wall.atlas",
            "images/deathGiant.atlas",
            "images/skeleton.atlas",
            "images/sfx.atlas",
            "images/lightning-animation.atlas",
            "images/player-spear.atlas"
    };

    // get the sounds to work and then move the music & sounds to a json
    //TODO: make Json
    private static final String[] racerSounds = {"sounds/Impact4.ogg"};
    private static final String mainMusic = "sounds/main.mp3";
    private static final String townMusic = "sounds/town.mp3";
    private static final String raiderMusic = "sounds/raider.mp3";
    // sound effect of fire/burning behind giant *fwoom* *crackle*
    private static final String fireMusic = "sounds/fire.mp3";
    // sound effects of giant walking (still to be tested)
    private static final String walkMusic = "sounds/walk.mp3";
    private static final String loudWalkMusic = "sounds/giant_walk.mp3";
    private static final String[] racerMusic = {mainMusic, townMusic, raiderMusic, fireMusic, walkMusic, loudWalkMusic};

    private final TerrainFactory terrainFactory;

    //have the loader return a level? fuck yeh
    public RagnarokArea(String name, TerrainFactory terrainFactory) {
        super();
        this.name = name;
        this.terrainFactory = terrainFactory;
        this.entitySignUp = new HashMap<>();
    }

    public void create() {
        create(0);
    }

    public void create(int xOffset) {
        loadAssets();
        displayUI();
        spawnTerrain();

        // TODO: Add power ups to RagEdit and reformat spawn method
        spawnPowerUps();

        playMusic(); //TODO: eventual move to music

        logger.debug("Creating new RagnarokArea");
    }

    public void setManager(AreaManager manager) {
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

//        while (!resourceService.loadForMillis(10)) {
            // This could be upgraded to a loading screen
            // logger.info("Loading... {}%", resourceService.getProgress());
//        }
    }

    private void spawnPowerUps() {
        GridPoint2 start = new GridPoint2(10, 10);
        GridPoint2 end = new GridPoint2(1000, 10);

        Entity powerUp;

        for (int i = 0; i < 31; i++) {
            GridPoint2 posLeft = RandomUtils.random(start, end);

            switch (i % 3) {
                case 0:
                    powerUp = PowerUpFactory.createLightningPowerUp();
                    break;

                case 1:
                    powerUp = PowerUpFactory.createShieldPowerUp();
                    break;

                default:
                    powerUp = PowerUpFactory.createSpearPowerUp();
                    break;
            }

            spawnEntityAt(powerUp, posLeft, false, false);
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
        GridPoint2 leftPos = new GridPoint2(-40, 13);
        GridPoint2 leftPos2 = new GridPoint2(-5, 13);
        Entity wallOfDeath = NPCFactory.createWallOfDeath(getPlayer());
        Entity sfx = NPCFactory.createScreenFX(getPlayer());
        wallOfDeath.addComponent(new CameraShakeComponent(this.player, this.terrainFactory.getCameraComponent(), sfx));
        spawnEntityAt(wallOfDeath, leftPos, true, true);
        spawnEntityAt(sfx, leftPos2, true, true);
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

    protected void spawnPlatform(int x, int y, String world) {
        Entity platform = ObstacleFactory.createPlatform(world);
        GridPoint2 pos = new GridPoint2(x, y + 2);
        spawnEntityAt(platform, pos, false, false);
        signup(pos, platform);
    }

    protected void spawnPlatformNoCollision(int x, int y, String world) {
        Entity platform = ObstacleFactory.createPlatformNoCollider(world);
        GridPoint2 pos = new GridPoint2(x, y + 2);
        spawnEntityAt(platform, pos, false, false);
        signup(pos, platform);
    }

    protected void spawnPlatform(int x, int y) {
        spawnPlatform(x, y, null);
    }

    protected void spawnFloor(int x, int y, String world) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Entity floor = ObstacleFactory.createFloor(world);
                GridPoint2 pos = new GridPoint2(x + i, y + j);
                spawnEntityAt(floor, pos, false, false);
                signup(pos, floor);
            }
        }
    }

    /**
     * Spawn a floor with no physics or collision component, with world style given by world.
     *
     * @param x     x coordinate in Area
     * @param y     y coordinate in Area
     * @param world the world type to load in. Must match the name of a .png file in
     *              assets/images (e.g. assets/images/world.png)
     */
    protected void spawnFloorNoCollision(int x, int y, String world) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Entity floor = ObstacleFactory.createFloorNoCollider(world);
                GridPoint2 pos = new GridPoint2(x + i, y + j);
                spawnEntityAt(floor, pos, false, false);
                signup(pos, floor);
            }
        }
    }

    /**
     * Spawn a floor with no physics or collision component, with the default world style.
     *
     * @param x x coordinate in Area
     * @param y y coordinate in Area
     */
    protected void spawnFloorNoCollision(int x, int y) {
        spawnFloorNoCollision(x, y, null);
    }

    protected void spawnFloor(int x, int y) {
        spawnFloor(x, y, null);
    }

    protected void spawnCollider(int x1, int x2, int y, int height) {
        Entity collider = ObstacleFactory.createCollider(x1, x2, height);
        logger.debug("Spawning Collider at {},{} with height: {}", x1, y, height);
        GridPoint2 pos = new GridPoint2(x1, y);
        spawnEntityAt(collider, pos, false, false);
        signup(pos, collider);
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

    protected void spawnFireSpirit(int x, int y) {
        Entity fireSpirit = NPCFactory.createFireSpirit(player);
        GridPoint2 pos = new GridPoint2(x, y);
        spawnEntityAt(fireSpirit, pos, false, false);
        //signup(pos, skeleton);
    }

    protected void projectileFromEnemy(GridPoint2 enemy) {
        Entity fireBall = ProjectileFactory.fireBall();
        spawnEntityAt(fireBall, enemy, false, false);
    }

    public void clearEntitiesAt(int x, int y) {
        // takes the global scale x and y, so mutliply them by 3 in here
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                GridPoint2 index = new GridPoint2(x + i, y + j);

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
        if (!entitySignUp.containsKey(pos)) {

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
        music.setVolume(0.7f);
        fire.setVolume(0.7f);
        walk.setVolume(0.8f);
        music.play();
        fire.play();
        walk.play();
    }

    private void unloadAssets() {
        logger.debug("Unloading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.unloadAssets(racerTextures);
        resourceService.unloadAssets(racerTextureAtlases);
        resourceService.unloadAssets(racerSounds);
        resourceService.unloadAssets(racerMusic);
    }

    @Override
    public void dispose() {
        super.dispose();
        ServiceLocator.getResourceService().getAsset(mainMusic, Music.class).stop();
        this.unloadAssets();
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