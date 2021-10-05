package com.deco2800.game.areas;

import com.deco2800.game.areas.terrain.TerrainFactory;
import com.deco2800.game.files.RagLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class AreaManager extends RagnarokArea {
    private static final Logger logger = LoggerFactory.getLogger(AreaManager.class);

    // Some string constants to satisfy SonarCloud
    private static final String FLOOR = "floor";
    private static final String PLATFORM = "platform";
    private static final String SPIKES = "spikes";
    private static final String ROCKS = "rocks";

    /**
     * each block/tile in the world is actually 3x the game's geometry.
     * don't change this unless you want the *entire* "bricking" of the game
     * to be altered.
     */
    private static final int GRID_SCALE = 3;

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
     * this is passed to all areas inside... I think
     * <p>
     * note on 21/9/21: worth investigating if changing this permits
     * changing of the terrain that is spawned? maybe different
     * terrainFactories can model the different world types?
     * <p>
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
        this.startNextArea = -5;
        // eventually moved to terminal?
        // move RagLoader to terminal because it interfaces to the AreaManger through the commandline
    }

    /**
     * Construct a new AreaManager with the given terrainFactory and ragnarokArea.
     *
     * @param terrainFactory the terrainFactory to be initilaised in the GameArea class. It is passed the
     *                       mainInstace.
     * @param ragnarokArea   ragnarokArea to spawn entities into, has already been created()
     */
    public AreaManager(TerrainFactory terrainFactory, RagnarokArea ragnarokArea) {
        super("Manager", terrainFactory);
        this.mainTerrainFactory = terrainFactory;
        this.startNextArea = 0;
        this.terrainInstance = ragnarokArea;
    }

    /**
     * This method is called from outside the class in the MainGameScreen class. The constructor initiliases the real
     * *RAW* stuff of the class. This function is used for clarity.
     * Calls to load and other "set up" functions for the manager should be called here.
     */
    @Override
    public void create() {

        load("tutorial");
        //if above code is uncommented load can introduce duplicate instances
        //conflict with load not above code I don't think
        terrainInstance.makePlayer(10, 5);
        this.player = terrainInstance.getPlayer();
        terrainInstance.spawnWallOfDeath();

        logger.debug("Creating AreaManager");

        terrainInstance.spawnBackground(0, this.bPWidth, "asgard");
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
            case FLOOR:
                area.spawnFloor(gx, gy, this.currentWorld);
                break;
            case PLATFORM:
                area.spawnPlatform(gx, gy, this.currentWorld);
                break;
            case ROCKS:
                area.spawnRocks(gx, gy);
                break;
            case SPIKES:
                area.spawnSpikes(gx, gy);
                break;
            case "null":
                // No longer needed
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
            case "tutorial":
                area.spawnTutorial(gx, gy);
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
            terrainInstance.create();
            player = terrainInstance.getPlayer();
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
                        bPIndex = 0;
                        // Spawn a background at the start of the level with width from the rag
                        // file.
                        terrainInstance.spawnBackground(this.startNextArea * GRID_SCALE,
                                this.bPWidth, "asgard");
                        break;
                    case "queue":
                        makeBufferedPlace(terrainInstance);
                        break;
                    case "make":
                    case "load":
                        // Depreciated.
                        break;
                    default:
                        logger.error("Unknown value in rag: {} {}", argument, value);
                }
                break;
            default:
                logger.error("Unknown argument in rag: {} {}", argument, value);
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
            char c = charColumn.charAt(charColumn.length() - y);
            switch (c) {
                case '.': // null
                    break;
                case 'F': // floor
                    terrainType = FLOOR;
                    break;
                case 'S': // spikes
                    terrainType = SPIKES;
                    break;
                case 'R': // rocks
                    terrainType = ROCKS;
                    break;
                case 'P': // platform
                    terrainType = PLATFORM;
                    break;
                default:
                    logger.error("Unknown character in rag file: {}", c);
            }

            bufferedPlaces[bPIndex][y - 1] = terrainType; // this is fraud

        }
        bPIndex++; // yeh this is fraud
    }

    /**
     * Spawn in all entities listed in this.bufferedPlaces to area.
     * <p>
     * This is called after the terrain in a ragFile has been fully read and a -config line has
     * been reached. Meaning, map parts are all spawned at one time after parsing a file.
     *
     * @param area area to spawn these entities into.
     */
    public void makeBufferedPlace(RagnarokArea area) {
        // Offset the start of this world so it loads after everything else already spawned.
        int x = startNextArea;

        for (int col = 0; col < bPWidth; col++) {
            for (int row = 0; row < bPHeight; row++) {
                switch (this.bufferedPlaces[col][row]) {
                    case PLATFORM:
                        spawnLineOfMap(area, x, row, col, PLATFORM);
                        break;
                    case FLOOR:
                        spawnLineOfMap(area, x, row, col, FLOOR);
                        break;
                    case SPIKES:
                        area.spawnSpikes((col + x) * GRID_SCALE, row * GRID_SCALE);
                        break;
                    case ROCKS:
                        area.spawnRocks((col + x) * GRID_SCALE, row * GRID_SCALE);
                        break;
                    case "null":
                        // Ignore this value
                        break;
                    default:
                        logger.error("Attempted to place unknown element {}",
                                this.bufferedPlaces[col][row]);
                }
            }
        }
    }

    /**
     * Spawn a line of either platforms or floors.
     * <p>
     * This uses the bufferedPlaces array and nullifyDuplicatesInRow to find out how long a
     * particular line of the platform or floor is in the world. Then, along the line it has found,
     * all strings will be set to 'null' from 'type' and a map chunk will be spawned.
     *
     * @param area    area to spawn these entities in
     * @param xOffset starting x coordinate of the world being loaded in
     * @param row     x coordinate (as read from the .rag file)
     * @param col     y coordinate (as read from the .rag file)
     * @param type    type of map, should be either "floor" or "platform"
     */
    private void spawnLineOfMap(RagnarokArea area, int xOffset, int row, int col, String type) {
        int length = nullifyDuplicatesInRow(row, col, type);
        int[] xCoordinates = new int[length];
        for (int i = 0; i < length; i++) {
            xCoordinates[i] = (xOffset + col + i) * GRID_SCALE;
        }
        area.spawnMapChunk(xCoordinates, row * GRID_SCALE, type, this.currentWorld);
    }

    /**
     * Replace all consecutive instances of 'placeType' in the given row of this.bufferedPlaces
     * with "null" and return the number of replacements made. If
     * this.bufferedPlaces[col][row] != 'placeType', return 0 and make no change.
     * <p>
     * Will scan columns after the given col at height row until the stored value is no longer
     * placeType, then replace those values it found.
     * <p>
     * Example, in the given world:
     * <p>
     * 5 {        },{        },{        },{        },{        }
     * 4 {platform},{platform},{        },{platform},{        }
     * 3 {        },{        },{        },{        },{        }
     * 2 {        },{platform},{platform},{platform},{platform}
     * 1 {        },{        },{        },{        },{        }
     * 0 {  floor },{  floor },{  floor },{  floor },{  floor }
     * --0          1          2          3          4
     * <p>
     * Calling replaceDuplicatesInRow(0, 4, 'platform') would set (4,0) and (4,1) to 'null'.
     * <p>
     * Similarly, replaceDuplicatesInRow(3, 4, 'platform') would set (3,4) to 'null'.
     *
     * @param col       column of bufferedPlaces to start looking
     * @param row       row of bufferedPlaces to start looking
     * @param placeType type of object to search e.g. "platform", "floor"
     * @return how many values were changed, only 0 if placeType not present at starting position
     */
    private int nullifyDuplicatesInRow(int row, int col, String placeType) {
        if (!bufferedPlaces[col][row].equals(placeType)) {
            // Original placeType incorrect
            logger.debug("Attempted to replace {} at {},{} but that position does not hold {}.",
                    placeType, row, col, placeType);
            return 0;
        }

        int nextCol = col + 1;
        // Search through the columns until the string stored is no longer 'placeType', or the end
        // of the world is reached.
        while (nextCol < bPWidth && bufferedPlaces[nextCol][row].equals(placeType)) {
            nextCol++;
        }

        // Replace values from column col to nextCol.
        for (int i = col; i < nextCol; i++) {
            bufferedPlaces[i][row] = "null";
        }

        return nextCol - col;
    }

}
