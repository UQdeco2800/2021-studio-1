package com.deco2800.game.areas;

import com.deco2800.game.areas.terrain.TerrainFactory;
import com.deco2800.game.files.RagLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.LinkedList;

public class AreaManager extends RagnarokArea {

    private static final Logger logger = LoggerFactory.getLogger(AreaManager.class);

    private LevelType mainType;
    private LinkedList<RagnarokArea> areaInstances;
    private RagnarokArea mainInstance;
    private RagnarokArea loadInstance;

    private boolean loadFlag;

    private String[][] bufferedPlaces;
    private int bPWidth; //this determines the first dimension of bufferedPlaces array (the columns)
    private int bPHeight; //this determines the second dimension of bufferedPlaces array (the chars)
    private int bPIndex;

    private Hashtable<String, String> bufferedSpawns;

    private RagLoader loader; // TODO: remove from this, move to AreaServce

    private TerrainFactory mainTerrainFactory;

    public AreaManager(TerrainFactory terrainFactory) {

        super("Manager", terrainFactory);
        this.mainTerrainFactory = terrainFactory;
        this.mainType = LevelType.SLICE;
        this.areaInstances = new LinkedList<>();

        bufferedSpawns = new Hashtable<>();

        this.loader = new RagLoader(this); //eventually moved to terminal?
        // move RagLoader to terminal because it interfaces to the AreaManger through the commandline

        //spawn(10, 5, "avatar");
    }

    public void create() {

        //load("LVL1");

        int mainArea = 0;

        load("skel");
        mainInstance.makePlayer(10, 5); // has to be here, even tho (should) be called in ragedit
        this.player = mainInstance.getPlayer();
        // System.out.println("load in manager called");

        /*for(RagnarokArea r : areaInstances) {
            r.create();
            mainArea++;
            //r.clearPlayer();
            //this.player = r.getPlayer();
        }*/

        //this.mainInstance = areaInstances.get(mainArea);

        /*for(int x = 0; x < 20; x++) {
            place(x, 0, "floor");
        }

        place(3, 1, "floor");
        place(4, 1, "floor");
        place(4, 2, "floor");

        place(6, 1, "spikes");
        place(7, 1, "rocks");

        spawn(12, 1, "skeleton");
        spawn(13, 1, "wolf");*/
    }

    public void place(int x, int y, String placeType) {

        place(mainInstance, x, y, placeType);

    }

    public void place(RagnarokArea area, int x, int y, String placeType) {
        switch (placeType) {
            case "floor":
                area.spawnFloor(x*3, y*3);
                break;
            case "platform":
                area.spawnPlatform(x*3,y*3);
                break;
            case "rocks":
                area.spawnRocks(x*3, y*3);
                break;
            case "spikes":
                area.spawnSpikes(x*3, y*3);
                break;
            case "null":
                area.clearEntitiesAt(x*3, y*3);
                break;
            default:
                logger.error("place() called in AreaManager without valid placeType");
                break;

        }
    }

    public void spawn(int x, int y, String spawnType) {
        spawn(mainInstance, x, y, spawnType);
    }

    public void spawn(RagnarokArea area, int x, int y, String spawnType) {
        switch (spawnType) {
            case "avatar":
                player = spawnPlayer(x, y);
                break;
            case "skeleton":
                area.spawnSkeleton(x*3, y*3);
                break;
            case "wolf":
                area.spawnWolf(x*3, y*3);
                break;
            case "default":
                logger.error("spawn() called in AreaManger without valid spawnType");
        }
    }

    //
    public void load(String level) {

        /*RagnarokArea testArea = new RagnarokArea("test", mainTerrainFactory);
        testArea.setManager(this); //must do this
        areaInstances.add(testArea);*/

        // currently overrides everyyything

        if (mainInstance != null) {
            mainInstance.clearAllEntities();
        }
        //if (loadInstance != null) loadInstance.clearAllEntities() {
        //    load
        //};
        //mainInstance.dispose(); // TODO: change
        //loadInstance.dispose();

        mainInstance = new RagnarokArea("load test", mainTerrainFactory);
        mainInstance.setManager(this);

        //loadInstance = loadingTestArea;
        mainInstance.create();


        //mainInstance.makePlayer(0, 0); // so setting player in Loader doesn't result in null pointer

        loader.newCreateFromFile(level);

        areaInstances.clear();
        areaInstances.add(mainInstance);

        //loadInstance.makePlayer(10, 5);

        //System.out.println("load create area from file called"); //TODO convert to logger

        //loadInstance.spawnPlatform(0, 0);
        //loadPlace(1, 0, "floor");

        //loadPlace(0, 0, "floor");

        //mainInstance = loadInstance;
        //loadInstance = null;

        //if (mainInstance.getPlayer() != null) mainInstance.setPlayer(10f, 5f);



        //testArea.create();
        //areaInstances.add(testArea);

        //assert areaInstances.peekLast() != null;
        //areaInstances.peekLast().create();

        // add to areaInstances, but if slice, then enforce only 1, so clear the list first
    }

    public void loadPlace(int x, int y, String placeType) {
        place(loadInstance, x, y, placeType);
    }

    public void loadSpawn(int x, int y, String placeType) {
        spawn(loadInstance, x, y, placeType);
    }

    public void loadPlayer(int x, int y) {
        loadInstance.makePlayer(x, y);
    }

    public void loadSetPlayer(float x, float y) {
        mainInstance.movePlayerPos(x, y);
    }

    public void config(String argument, String value) {
        switch (argument) {
            case "title":
                break;
            case "width":
                bPWidth = Integer.parseInt(value);
                break;
            case "height":
                bPHeight = Integer.parseInt(value);
                break;
            case "close":
                switch (value) {
                    case "init":
                        bufferedPlaces = new String[bPWidth][bPHeight];
                        System.out.println(bufferedPlaces);
                        bPIndex = 0;
                        break;
                    case "queue":
                        makeBufferedPlace();
                        break;
                    case "make":
                        break;
                        //makeBufferedSpawn();

                    case "load":
                        break;
                }

        }
    }

    public void queue(String charColumn) {
        for (int y = bPHeight; y > 0; y--) {
            //String coOrd = String.format("[%d,%d]", bPIndex, y);

            String terrainType = "null";
            switch(charColumn.charAt(charColumn.length() - y)) {
                case '.': // null
                    break;
                case 'F': // floor
                    terrainType = "floor";
                    break;
                case 'S': // spikes
                    terrainType = "spikes";
                    break;
                case 'P': // platform
                    terrainType = "platform";
                    break;
            }

            //String place = String.format("-place %s %s", coOrd, terrainType);
            //System.out.printf("Size[] %d Size %d", bufferedPlaces.length, bufferedPlaces[0].length);
            //System.out.printf("%d %d %s\n", bPIndex, y, terrainType);
            bufferedPlaces[bPIndex][y - 1] = terrainType;

        }
        bPIndex++;
    }

    private void makeBufferedPlace() {

        int x = 0;
        for (String[] column : bufferedPlaces) {
            int y = 0;
            for (String placeType : column) {
                place(x, y, placeType);
                y++;
            }
            x++;
        }
    }

    /*private void makeBufferedSpawn() {
        for (String coOrds : bufferedSpawns.keySet()) {

            String spawnType = bufferedSpawns.get(coOrds);

            // do the same fuckn coOrd transformation, fuck my life
            // TODO make static coOrd transform function

            coOrds = coOrds.replace("[","").replace("]","");
            String[] bleg = coOrds.split(",");
            int x = Integer.parseInt(bleg[0]);
            int y = Integer.parseInt(bleg[1]);

            spawn(x, y, spawnType);
        }
    }*/
}

enum LevelType {
    SLICE,          // as in a single level
    ENDLESS;        // as in the final, continous game
}
