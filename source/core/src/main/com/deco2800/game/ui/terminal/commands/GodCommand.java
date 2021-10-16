package com.deco2800.game.ui.terminal.commands;

import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.components.CombatStatsComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Command to enable god mode
 */
public class GodCommand implements Command {
    private static final Logger logger = LoggerFactory.getLogger(GodCommand.class);

    /**
     * Toggles god mode on or off if no argument. Otherwise, sets it based off the argument received
     *
     * @param args arguments to command; if empty - toggle god mode, otherwise, 'on' will set god
     *             mode, 'off' will clear it.
     */
    public boolean action(ArrayList<String> args) {

        if (!isValid(args)) {
            logger.debug("Invalid arguments received for 'god' command: {}", args);
            return false;
        }

        if (args.size() == 0) {
            ServiceLocator.getAreaService().getManager().getPlayer().getComponent(CombatStatsComponent.class).toggleInvincible();
            return true;
        }

        String arg = args.get(0);
        switch (arg) {
            case "on":
                ServiceLocator.getAreaService().getManager().getPlayer().getComponent(CombatStatsComponent.class).setInvincible(true);
                return true;
            case "off":
                ServiceLocator.getAreaService().getManager().getPlayer().getComponent(CombatStatsComponent.class).setInvincible(false);
                return true;
            default:
                logger.debug("Unrecognised argument received for 'debug' command: {}", args);
                return false;
        }
    }

    /**
     * Validates the command arguments.
     *
     * @param args command arguments
     * @return is valid
     */
    boolean isValid(ArrayList<String> args) {
        return (args.size() == 1 || args.size() == 0);
    }
}
