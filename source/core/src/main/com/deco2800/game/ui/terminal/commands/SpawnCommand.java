package com.deco2800.game.ui.terminal.commands;

import com.deco2800.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

// format : spawn [x,y] (activeEntity)
// to spawn walls/floors, use the PlaceCommand... but this one is more fun for now

public class SpawnCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(SpawnCommand.class);

    /**
     *
     * @param args command arguments
     */
    public boolean action(ArrayList<String> args) {

        if (!isValid(args)) {
            logger.debug("Invalid arguments received for 'spawn' command: {}", args);
            return false;
        }

        String arg0 = args.get(0).replace("[","").replace("]","");
        String arg1 = args.get(1).replace("(","").replace(")","");
        String[] coOrds = arg0.split(",");
        int x = Integer.parseInt(coOrds[0]);
        int y = Integer.parseInt(coOrds[1]);

        ServiceLocator.getAreaService().spawn(x, y, arg1);

        return true;
    }

    /**
     * Validates the command arguments.
     * @param args command arguments
     * @return is valid
     */
    boolean isValid(ArrayList<String> args) {
        return args.size() == 2;
    }

}
