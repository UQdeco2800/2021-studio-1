package com.deco2800.game.files;

import com.deco2800.game.areas.AreaManager;
import com.deco2800.game.areas.RagnarokArea;
import com.deco2800.game.areas.terrain.TerrainFactory;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.terminal.Terminal;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Area;
import java.io.*;
import java.security.Provider;

public class RagLoader {

    private static final Logger logger = LoggerFactory.getLogger(RagLoader.class);
    private final AreaManager manager;

    public RagLoader(AreaManager manager) {
        this.manager = manager;
    }

    public void createAreaFromFile(String ragFile) {
        //ragfile is just the thingy... yknow? from thingy...
        //the rest of the filepath is added here, so people jsut have to pass
        // the name they saved the file as

        //writer.parseString("clear");

        //current key:
        //$     -> line must start with this unless its ignored
        //$_    -> config line (i.e. $_width 5) <- width tells how many buckets to make
                                        //          height tells the size of each bucket
                                        //          why y-buckets? so levels can be loaded and disposed
                                        //          piecewise as opposed to all at once
        //$#    -> terrain map line (i.e $#..P..)
        //$-    -> terminal command line (directly parses it to terminal) (i.e. -spawn)
        //$@    -> "at" line, which configures specific entities (i.e. $@player)

        // psuedocode:
        // (string) must have first char "$"
        //      if second char "_"
        //          configline(string)
        //      elif second char "#"
        //          terrainline(string)
        //      elif second char "-"
        //          terminalline(string)
        //      elif second char "@"
        //          atEntityLine(string)
        //      else
        //          *dispose of line (invalid)*

        // no function returns anything, just interfaces with the terminal
        // terminal has a "load" flag which is opened and must be closed that helps it
        // deal with load requests
        // in area manager, there are only 3 areas: current, next, and last
        // current is complete by time the player enters it, while next is being created w/
        // column-buckets
        // last is being decomposed as it gets closer
        // the only thing that changes is the pointers to each. once last is completely decomposed,
        // and the player enters next, current = next, next = last, last = next (will need to store a temp)

        // also have the area manager manage the player so that they don't go away if/when areas are decomposed



        //filepath = "configs/rags/test1.rag";
        String filepath = "configs/rags/" + ragFile + ".rag";

        BufferedReader br = null;
        boolean loadSuccessful = false;

        try {
            File levelFile = new File(filepath);
            br = new BufferedReader(new FileReader(levelFile));
            loadSuccessful = true;
        } catch (FileNotFoundException e) {
            String erMsg = String.format("%s || File Not Found", filepath);
            logger.error(erMsg);
            // do with a logger instead
        }

        try {

            if (loadSuccessful) {

                // SO THIS FIRST SECTION JUST DEALS WITH THE TILE MAP @ THE TOP

                String line;
                //float lane = 6.8f;

                String title;
                int width = 0;
                int height = 0;

                int i = 0;
                int y = 0;

                while ((line = br.readLine()) != null) {

                    if (line.length() == 0) continue;
                    if (line.charAt(0) == '$') {

                        if (line.charAt(1) == '_') {

                            String[] configArgs = line.split("_")[1].split(" ");
                            //System.out.printf("first: %s, second: %s", configArgs[0], configArgs[1]);

                            switch (configArgs[0]) {
                                case "title":
                                    title = configArgs[1];
                                    break;
                                case "width":
                                    width = Integer.parseInt(configArgs[1]);
                                    break;
                                case "height":
                                    height = Integer.parseInt(configArgs[1]);
                            }

                            continue;
                        }

                        // j = 1 to skip the $ sign
                        for (int j = 1; j < line.length(); j++) {

                            //System.out.printf("%d c:%c", j, line.charAt(j));

                            if (line.charAt(j) == '@') {
                                //System.out.println("2nd char was @");
                                // run @ routine and then break out of reading the line

                                String[] atArgs = line.split("@")[1].split(" ");
                                //System.out.printf("%s %s %s", atArgs[0], atArgs[1] ,atArgs[2]);

                                if (atArgs[0].equals("player") && atArgs[1].equals("set")) {
                                    String[] coOrdinateArgs = atArgs[2].split(",");

                                    //System.out.printf("player is made");

                                    try {
                                        int px = Integer.parseInt(coOrdinateArgs[0]);
                                        int py = Integer.parseInt(coOrdinateArgs[1]);

                                        manager.loadSetPlayer(3*px, 3*py); //TODO: fix this hack
                                    } catch (Exception e) {
                                        String errorMessage = String.format("Couldn't parse String to int when loading file %s" +
                                                "\n on line %d character %d", ragFile, i, j);
                                        logger.error(errorMessage);
                                    }

                                }

                                break; // will (hopefully) break out of the for loop
                                // an continue the next iteration of the while loop its incasulated in
                            }

                            // always 5 lines before the map, so ye

                            // because it skips over blank lines, i dones't get iterated, but be careful g
                            y = height - i - 1; // TODO: set 9 to the "height" value found in .rag file
                            int x = j - 1;

                            //System.out.printf("x%d y%d c%c :: ", x, y, line.charAt(j));

                            String whatToPlace = null;

                            switch (line.charAt(j)) {

                                case '.': //Empty
                                    whatToPlace = "null";
                                    break;
                                case 'P': //Platform
                                    whatToPlace = "platform";
                                    break;
                                case 'F': //Floor
                                    whatToPlace = "floor";
                                    break;
                                case 'S': //Spikes
                                    whatToPlace = "spikes";
                                    break;
                                case 'R': //Rocks
                                    whatToPlace = "rocks";
                                    break;
                                default:
                                    break;
                            }

                            if (whatToPlace != null) {

                                // this should be a line to the terminal not directly to areaservice
                                // remember areaservice is interfaced to by the terminal
                                ServiceLocator.getAreaService().getManager().loadPlace(x, y, whatToPlace);

                            }
                        }
                        i++;
                        y++;
                    }
                }
            }
        } catch (IOException e) {
            // make a logger and log the error
        }
    }

    public void newCreateFromFile(String ragFile) {

        String filepath = "configs/rags/" + ragFile + ".rag";

        ServiceLocator.getTerminalService().sendTerminal("-place [1,1] (platform)");

        BufferedReader br = null;
        boolean loadSuccessful = false;

        try {
            File levelFile = new File(filepath);
            br = new BufferedReader(new FileReader(levelFile));
            loadSuccessful = true;
        } catch (FileNotFoundException e) {
            String erMsg = String.format("%s || File Not Found", filepath);
            logger.error(erMsg);
            // do with a logger instead
        }

        try {

            if (loadSuccessful) {

                // SO THIS FIRST SECTION JUST DEALS WITH THE TILE MAP @ THE TOP

                String line;
                //float lane = 6.8f;

                String title;
                int width = 0;
                int height = 0;

                int i = 0;
                int y = 0;

                boolean inConfig = false;
                boolean inTerrain = false;
                boolean inSpawn = false;

                while ((line = br.readLine()) != null) {

                    if (line.length() == 0) continue;

                    if (line.startsWith("$")) {

                        line = line.replace("$", "");

                        if (line.startsWith("_")) { // config line

                            inConfig = true;
                            String[] args = line.replace("_", "").split(" ");
                            String config = String.format("-config %s %s", args[0], args[1]);

                            //System.out.println(config);

                            ServiceLocator.getTerminalService().sendTerminal(config);
                            continue;

                        } else if (line.startsWith("#")) { // terrain line

                            //System.out.println("LINE STARTED WITH #");

                            if (inConfig) {
                                inConfig = false;
                                //System.out.println("config closed init");
                                ServiceLocator.getTerminalService().sendTerminal("-config close init");
                            }
                            inTerrain = true;

                            String toSend = String.format("-queue %s", line.replace("\n", ""));

                            //System.out.println(toSend);

                            ServiceLocator.getTerminalService().sendTerminal(toSend);

                        } else if (line.startsWith("@")) { // atEntity line

                            String[] args = line.split(" ");
                            String entity = args[0];
                            String command = args[1];
                            String argument = args[2];

                            argument = argument.replace("]", "").replace("[", "");

                            float px = Float.parseFloat(argument.split(",")[0]);
                            float py = Float.parseFloat(argument.split(",")[1]);

                            //TODO REMOVE THIS HACK U FRAUD
                            if (command.equals("set")) {
                                //ServiceLocator.getAreaService().getManager().loadSetPlayer(px, py);
                            }

                        } else if (line.startsWith("-")) { // direct command line

                            if (inTerrain) {
                                inTerrain = false;
                                ServiceLocator.getTerminalService().sendTerminal("-config close queue");
                            }

                            inSpawn = true;

                            ServiceLocator.getTerminalService().sendTerminal(line);
                        }
                    }
                }
            }
        } /*catch (Exception e) {

            System.out.println(e.toString());
            logger.error("Load Unsuccessful");

        }*/ catch (IOException e) {
            e.printStackTrace();
        }

    }
}
