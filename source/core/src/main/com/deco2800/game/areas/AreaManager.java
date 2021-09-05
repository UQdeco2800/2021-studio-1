package com.deco2800.game.areas;

import com.deco2800.game.areas.terrain.TerrainFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

public class AreaManager extends RagnarokArea {

    private static final Logger logger = LoggerFactory.getLogger(AreaManager.class);

    private LevelType mainType;
    private LinkedList<RagnarokArea> areaInstances;
    private RagnarokArea mainInstance;

    private TerrainFactory mainTerrainFactory;

    public AreaManager(TerrainFactory terrainFactory) {
        super("Manager", terrainFactory);
        this.mainTerrainFactory = terrainFactory;
        this.mainType = LevelType.SLICE;
        this.areaInstances = new LinkedList<>();

        load("LVL1");
        //spawn(10, 5, "avatar");
    }

    public void create() {
        int mainArea = -1;
        for(RagnarokArea r : areaInstances) {
            r.create();
            mainArea++;
            //r.clearPlayer();
            //this.player = r.getPlayer();
        }

        this.mainInstance = areaInstances.get(mainArea);
        mainInstance.makePlayer(10, 5);
        this.player = mainInstance.getPlayer();

        for(int x = 0; x < 20; x++) {
            place(x, 0, "floor");
        }

        place(3, 1, "floor");
        place(4, 1, "floor");
        place(4, 2, "floor");

        place(6, 1, "spikes");
        place(7, 1, "rocks");

        spawn(12, 1, "skeleton");
        spawn(13, 1, "wolf");
    }

    public void place(int x, int y, String placeType) {

        //current geometry for most the game assests is 3x the geometry...
        switch (placeType) {
            case "floor":
                mainInstance.spawnFloor(x*3, y*3);
                break;
            case "platform":
                mainInstance.spawnPlatform(x*3,y*3);
                break;
            case "rocks":
                mainInstance.spawnRocks(x*3, y*3);
                break;
            case "spikes":
                mainInstance.spawnSpikes(x*3, y*3);
                break;
            default:
                logger.error("place() called in AreaManager without valid placeType");
                break;

        }

    }

    public void spawn(int x, int y, String spawnType) {
        switch (spawnType) {
            case "avatar":
                player = spawnPlayer(x, y);
                break;
            case "skeleton":
                mainInstance.spawnSkeleton(x*3, y*3);
                break;
            case "wolf":
                mainInstance.spawnWolf(x*3, y*3);
                break;
            case "default":
                logger.error("spawn() called in AreaManger without valid spawnType");
        }
    }

    //
    public void load(String level) {

        RagnarokArea testArea = new RagnarokArea("test", mainTerrainFactory);
        testArea.setManager(this); //must do this

        areaInstances.add(testArea);

        //testArea.create();
        //areaInstances.add(testArea);

        //assert areaInstances.peekLast() != null;
        //areaInstances.peekLast().create();

        // add to areaInstances, but if slice, then enforce only 1, so clear the list first
    }

}

enum LevelType {
    SLICE,          // as in a single level
    ENDLESS;        // as in the final, continous game
}
