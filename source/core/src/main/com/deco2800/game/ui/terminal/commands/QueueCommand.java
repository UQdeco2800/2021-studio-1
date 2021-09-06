package com.deco2800.game.ui.terminal.commands;

import com.deco2800.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class QueueCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(QueueCommand.class);

    public boolean action(ArrayList<String> args) {
        if (!isValid(args)) {
            //TODO: add log
            return false;
        }

        ServiceLocator.getAreaService().queue(args.get(0));
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
