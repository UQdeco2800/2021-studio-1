package com.deco2800.game.ui.terminal.commands;

import com.deco2800.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * A command that is sent when a new area is being configured
 */
public class ConfigCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(ConfigCommand.class);

    /**
     * Sends a configure message to the AreaService if valid
     */
    public boolean action(ArrayList<String> args) {
        if (!isValid(args)) {
            logger.debug("Invalid arguments recieved for '-config'");
            return false;
        }

        //this is the argument that decides what type of config is being done
        ServiceLocator.getAreaService().config(args.get(0), args.get(1));
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
