package com.deco2800.game.files;

import com.deco2800.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class RagLoader {
    private static final Logger logger = LoggerFactory.getLogger(RagLoader.class);
    private static boolean inConfig;
    private static boolean inTerrain;

    /**
     * Loads file, sending messages to the terminal that are handled and delegated to the AreaManager.
     * It's quite messy, but the true dishpit-code-filthy warriors will know whats up.
     *
     * @param ragFile the ragfile wished to be loaded, in format [ragFile].rag. ** DONT TYPE the .rag**
     */
    public static void createFromFile(String ragFile) {
        String filepath = "configs/rags/" + ragFile + ".rag";

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            inConfig = false;
            inTerrain = false;

            while ((line = br.readLine()) != null) {
                // Ignore blank lines and lines that don't start with $.
                if (line.startsWith("$")) {
                    parseLine(line.replace("$", ""));
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("{} || file not found", filepath);
        } catch (IOException e) {
            logger.error("Error loading file: {}", e.getMessage());
        }
    }

    /**
     * Send command from this line to the terminal.
     *
     * @param line line of a .rag file
     */
    public static void parseLine(String line) {
        if (line.startsWith("_")) { // config line
            inConfig = true;
            String[] args = line.split(" ");
            String config = String.format("-config %s %s", args[0].replace("_", ""), args[1]);

            ServiceLocator.getTerminalService().sendTerminal(config);

        } else if (line.startsWith("#")) { // terrain line
            if (inConfig) {
                inConfig = false;
                ServiceLocator.getTerminalService().sendTerminal("-config close init");
            }
            inTerrain = true;

            String toSend = String.format("-queue %s", line.replace("\n", ""));

            ServiceLocator.getTerminalService().sendTerminal(toSend);

        } else if (line.startsWith("@")) { // atEntity line
            // This line is no longer used to spawn a player since they are most
            // likely already in the world. Its main use is to signify the end of
            // terrain spawning.

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

        } else if (line.startsWith("-")) { // direct command line
            ServiceLocator.getTerminalService().sendTerminal(line);
        }
    }
}