package com.deco2800.game.files;

import com.deco2800.game.areas.AreaManager;
import com.deco2800.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class RagLoader {

    private static final Logger logger = LoggerFactory.getLogger(RagLoader.class);
    private final AreaManager manager;

    public RagLoader(AreaManager manager) {
        this.manager = manager;
    }

    // the createAreaFromFile method has been depreciated,
    // and is now replaced by the newCreateFromFile method... maybe a name change is in order

    /**
     * Loads file, sending messages to the terminal that are handled and delegated to the AreaManager.
     * It's quite messy, but the true dishpit-code-filthy warriors will know whats up.
     *
     * @param ragFile the ragfile wished to be loaded, in format [ragFile].rag. ** DONT TYPE the .rag**
     */
    public void newCreateFromFile(String ragFile) {

        String filepath = "configs/rags/" + ragFile + ".rag";

        ServiceLocator.getTerminalService().sendTerminal("-place [1,1] (platform)");

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
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

                        // there needs to be an @ on an entity for this to be called (so yeh kinna broken)
                        if (inTerrain) {
                            inTerrain = false;
                            ServiceLocator.getTerminalService().sendTerminal("-config close queue");
                        }

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

                        inSpawn = true;

                        ServiceLocator.getTerminalService().sendTerminal(line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            String erMsg = String.format("%s || File Not Found", filepath);
            logger.error(erMsg);
            // do with a logger instead
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
