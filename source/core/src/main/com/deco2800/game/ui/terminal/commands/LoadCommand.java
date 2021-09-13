package com.deco2800.game.ui.terminal.commands;

import com.deco2800.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * A command for toggling debug mode on and off.
 */
public class LoadCommand implements Command {
    private static final Logger logger = LoggerFactory.getLogger(LoadCommand.class);

    /**
     * Toggles debug mode on or off if the corresponding argument is received.
     * @param args command arguments
     */
    public boolean action(ArrayList<String> args) {

        if (!isValid(args)) {
            logger.debug("Invalid arguments received for 'debug' command: {}", args);
            return false;
        }

        String arg = args.get(0);
        ServiceLocator.getAreaService().load(arg);
        return true;

        }

    /**
     * Validates the command arguments.
     * @param args command arguments
     * @return is valid
     */
    boolean isValid(ArrayList<String> args) {
        return args.size() == 1;
    }
}
