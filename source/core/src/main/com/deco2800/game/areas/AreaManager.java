package com.deco2800.game.areas;

import com.deco2800.game.areas.terrain.TerrainFactory;
import com.deco2800.game.files.RagLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;

public class AreaManager extends RagnarokArea {

    private static final Logger logger = LoggerFactory.getLogger(AreaManager.class);

    /**
     * each block/tile in the world is actually 3x the game's geometry.
     * don't change this unless you want the *entire* "bricking" of the game
     * to be altered.
     */
    private static final int GRID_SCALE = 3;

    /**
     * this contains a list of GameAreas inside the manager,
     * though it is currently underutilised. BackgroundArea, MainArea?
     */
    private LinkedList<RagnarokArea> areaInstances;

    /**
     * the main instance of the game is where the terrain and enemies are
     * spawned into. where is the player, or wall of death?
     */
    private RagnarokArea terrainInstance;

    //TODO: make this *far* less volatile. currently if either the height/width value loaded
    //      by the loader doesn't equal the amount of lines/colums this thing will go bananas
    //      [unequivolcally crash the game]
    private String[][] bufferedPlaces;

    /**
     * this determines the first dimension of bufferedPlaces array (the columns)
     */
    private int bPWidth;

    /**
     * this determines the second dimension of bufferedPlaces array (the chars)
     */
    private int bPHeight;
    private int bPIndex;

    /**
     * This is the current world to be loaded into the game next
     */
    private String currentWorld;

    /**
     * buffered "spawns" will eventually store a list
     * of coords and the entity to spawn them @
     */
    private HashMap<String, String> bufferedSpawns;

    /**
     * this is passed to all areas inside... I think
     *
     * note on 21/9/21: worth investigating if changing this permits
     * changing of the terrain that is spawned? maybe different
     * terrainFactories can model the different world types?
     *
     * see place() "platform" for usage
     */
    private TerrainFactory mainTerrainFactory;

    /**
     * Holds the ragGrid coordinate of the first column of the next area to be loaded. This is
     * given by the bPWidth of each level, which is added to this value after each load().
     */
    private int startNextArea;

    /**
     * The AreaManager class models the persistant aspects of the game, such as the player and the wall of death.
     * It is a subclass of GameArea, so methods called to GameArea (to spawn) may be called to the AreaManager
     * ( this is considered bad practice, as one should call place() or spawn() either within the mainInstance,
     * or globally, (and they will be handled within this class).
     *
     * @param terrainFactory the terrainFactory to be initilaised in the GameArea class. It is passed the
     *                       mainInstace.
     */
    public AreaManager(TerrainFactory terrainFactory) {

        super("Manager", terrainFactory);
        this.mainTerrainFactory = terrainFactory;
        this.areaInstances = new LinkedList<>();

        bufferedSpawns = new HashMap<>();
        this.startNextArea = 0;
        // eventually moved to terminal?
        // move RagLoader to terminal because it interfaces to the AreaManger through the commandline

        //terrain = terrainFactory.createTerrain(TerrainFactory.TerrainType.FOREST_DEMO);

        //spawn(10, 5, "avatar");
    }

    /**
     * This method is called from outside the class in the MainGameScreen class. The constructor initiliases the real
     * *RAW* stuff of the class. This function is used for clarity.
     * Calls to load and other "set up" functions for the manager should be called here.
     */
    @Override
    public void create() {

        RagnarokArea persistentInstance = new RagnarokArea("load test", mainTerrainFactory);
        persistentInstance.setManager(this);
        persistentInstance.create();
        persistentInstance.makePlayer(10, 5);

        persistentInstance.spawnWallOfDeath();

        this.player = persistentInstance.getPlayer();

        load("asg1");

        logger.debug("Creating AreaManager");

        //mainInstance.makePlayer(10, 5); // has to be here, even tho (should) be called in ragedit
        //this.player = mainInstance.getPlayer();
    }

    /**
     * Convenience method in case place is called to the Manager. See place(RagnarokArea, int, int, String)
     * for details.
     *
     * @param x         ragGrid x coOrdinate
     * @param y         ragGrid y coOrdinate
     * @param placeType what terrain type to place. Is a string, so check the switch statement or
     *                  as we document further, some dics. But there are no docs rn.
     */
    public void place(int x, int y, String placeType) {
        place(terrainInstance, x, y, placeType);

    }

    /**
     * YEWOOOOWOOWOOWOWOWOWOWOWOW
     * place command calls a spawn method on the specified area. Check RagnarokArea.spawn methods for details
     * As of 12/9/21 these are undocumented, but are the same as the spawn methods in the RacerArea from the previous
     * build.
     *
     * @param area      which area to call the spawn method on
     * @param x         ragGrid x coOrdinate
     * @param y         ragGrid y coOrdinate
     * @param placeType type of terrain to place
     */
    public void place(RagnarokArea area, int x, int y, String placeType) {
        logger.debug("Placing element - x:{} y:{} type:{}", x, y, placeType);

        int gx = x * GRID_SCALE;
        int gy = y * GRID_SCALE;
        switch (placeType) {
            case "floor":
                area.spawnFloor(gx, gy);
                break;
            case "platform":
                area.spawnPlatform(gx, gy, this.currentWorld);
                break;
            case "rocks":
                area.spawnRocks(gx, gy);
                break;
            case "spikes":
                area.spawnSpikes(gx, gy);
                break;
            case "null":
                // This should no longer be needed since we are loading one after another
                // area.clearEntitiesAt(gx, gy);
                break;
            default:
                logger.error("place() called in AreaManager without valid placeType");
                break;

        }
    }

    /**
     * This method is a convenience method in case spawn is called on the manager. See spawn(RagnarokArea,
     * int, int, String)
     *
     * @param x         ragGrid x coOrdinate
     * @param y         ragGrid y coOrdinate
     * @param spawnType type to spawn
     */
    public void spawn(int x, int y, String spawnType) {
        spawn(terrainInstance, x, y, spawnType);
    }

    /**
     * Spawn is for "Active Entities" i.e. ones that move around or smt.
     * The definition is somewhat loose, but won't parse arguments for terrain types.
     *
     * @param area      specified area to spawn in
     * @param x         ragGrid x coOrdinate
     * @param y         ragGrid y coOrdinate
     * @param spawnType type to spawn
     */
    public void spawn(RagnarokArea area, int x, int y, String spawnType) {

        int gx = (startNextArea + x) * GRID_SCALE;
        int gy = y * GRID_SCALE;

        switch (spawnType) {
            case "skeleton":
                area.spawnSkeleton(gx, gy);
                break;
            case "wolf":
                area.spawnWolf(gx, gy);
                break;
            case "levelTrigger":
                area.spawnLevelLoadTrigger(gx); //TODO: MAKE THiS DYNAMICALLY BASED ON SCREEN WIDTH
                break;
            case "fireSpirit":
                area.spawnFireSpirit(gx, gy);
                break;
            case "shield":
                area.spawnShield(gx, gy);
                break;
            case "spear":
                area.spawnSpear(gx, gy);
                break;
            case "lightning":
                area.spawnLightning(gx, gy);
                break;
            default:
                logger.error("spawn() called in AreaManger without valid spawnType");
        }
    }

    /**
     * Loads the specified level, the argument is in format [level].rag. ** DO NOT TYPE THE .rag**
     *
     * @param level level to load
     */
    public void load(String level) {
        // This has been changed to stop the removal of all entities after calling load.
        if (terrainInstance == null) {
            terrainInstance = new RagnarokArea("load test", mainTerrainFactory);
            terrainInstance.setManager(this);
            terrainInstance.create();
        }
        RagLoader.createFromFile(level);
        startNextArea += bPWidth; // Set this value to reflect the start of the next area.
    }

    /**
     * Config arguments that are sent to the manager via the terminal. This stuff is pretty dry,
     * and likely you won't need to extend functionality further **UNLESS** there are widespread,
     * SEISMIC changes you'd like to enact on the AreaManager at runtime. (which is not altogether
     * unreasonable).
     *
     * @param argument which config argument is being parsed
     * @param value    the value of said argument.
     */
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
            case "world":
                this.currentWorld = value;
                break;
            case "close":
                switch (value) {
                    case "init":
                        bufferedPlaces = new String[bPWidth][bPHeight];
                        //System.out.println(bufferedPlaces);
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

    /**
     * Queue is called in the loader to create a column of x terrain.
     * This will be further exploited in the procedural generation,
     * as columns of chunked levels are called individually.
     *
     * @param charColumn a String of chars to convert to place calls
     */
    public void queue(String charColumn) {
        for (int y = bPHeight; y > 0; y--) {
            String terrainType = "null";
            switch (charColumn.charAt(charColumn.length() - y)) {
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

            bufferedPlaces[bPIndex][y - 1] = terrainType; // this is fraud

        }
        bPIndex++; // yeh this is fraud
    }

    /**
     * Unspoodles the bufferedPlaces. This is called immediately after the player is set in the loader,
     * But will be intercepted as functionality is developed.
     */
    private void makeBufferedPlace() {
        logger.debug("Placing terrain stored in buffer");
        int x = startNextArea;

        for (int col = 0; col < bPWidth; col++) {
            for (int row = 0; row < bPHeight; row++) {
                int length;
                if (bufferedPlaces[col][row].equals("platform")) {
                    length = replaceDuplicatesInRow(row, col, "platform", "null");
                    int[] xCoordinates = new int[length + 1];
                    for (int i = 0; i < length + 1; i++) {
                        xCoordinates[i] = (x + col + i) * GRID_SCALE;
                    }
                    if (length > 1) terrainInstance.spawnPlatformChunk(xCoordinates,
                            row * GRID_SCALE + 2,
                            this.currentWorld);
                } else if (bufferedPlaces[col][row].equals("floor")) {
                    length = replaceDuplicatesInRow(row, col, "floor", "null");
                    int[] xCoordinates = new int[length + 1];
                    for (int i = 0; i < length + 1; i++) {
                        xCoordinates[i] = (x + col + i) * GRID_SCALE;
                    }
                    if (length > 1) terrainInstance.spawnFloorChunk(xCoordinates,
                            row * GRID_SCALE,
                            this.currentWorld);
                }
            }
        }

        for (String[] column : bufferedPlaces) {
            int y = 0;
            for (String placeType : column) {
                place(x, y, placeType);
                y++;
            }
            x++;
        }
        logger.debug("Finished placing terrain in buffer");
    }

    /**
     * Replace all consecutive instances of 'placeType' in the given row of this.bufferedPlaces
     * with replacement, return the number of replacements made.
     * <p>
     * Will scan columns after the given col at height row until the stored value is no longer
     * placeType, then replace those values it found.
     * <p>
     * Example, in the given world:
     * <p>
     * 5 {        },{        },{        },{        },{        }
     * 4 {platform},{platform},{        },{        },{        }
     * 3 {        },{        },{        },{        },{        }
     * 2 {        },{platform},{platform},{platform},{platform}
     * 1 {        },{        },{        },{        },{        }
     * 0 {  floor },{  floor },{  floor },{  floor },{  floor }
     * --0          1          2          3          4
     * <p>
     * calling replaceDuplicatesInRow(0, 4, 'platform', 'platformNoCollision') would set (4,0) and
     * (4,1) to 'platFormNoCollision'.
     *
     * @param col         column of bufferedPlaces to start looking
     * @param row         row of bufferedPlaces to start looking
     * @param placeType   type of object to search e.g. "platform", "floor"
     * @param replacement word to replace placeType with
     * @return how many values were changed
     */
    private int replaceDuplicatesInRow(int row, int col, String placeType, String replacement) {
        if (!bufferedPlaces[col][row].equals(placeType)) {
            // Original placeType incorrect
            logger.debug("Attempted to replace {} at {},{} but that position does not hold {}.",
                    placeType, row, col, placeType);
            return 0;
        }
        int nextCol = col + 1;
        if (nextCol == bufferedPlaces.length) return 1;
        if (!bufferedPlaces[nextCol][row].equals(placeType)) return 1;
        // Search through the columns until the string stored is no longer placeType, or the end
        // of the world is reached.
        do {
            nextCol++;
        } while (nextCol < bPWidth && bufferedPlaces[nextCol][row].equals(placeType));

        // Replace values from column col to nextCol.
        for (int i = col; i < nextCol; i++) {
            bufferedPlaces[i][row] = replacement;
        }

        return nextCol - col;
    }

}
