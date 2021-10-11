package com.deco2800.game.ui.terminal.commands;

import com.deco2800.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * A command for toggling debug mode on and off.
 */
public class DebugCommand implements Command {
  private static final Logger logger = LoggerFactory.getLogger(DebugCommand.class);

  /**
   * Toggles debug mode on or off if the corresponding argument is received. If no argument recieved toggles it.
   * @param args command arguments
   */
  public boolean action(ArrayList<String> args) {
    if (!isValid(args)) {
      logger.debug("Invalid arguments received for '-debug' command: {}", args);
      return false;
    }

    if (args.size() == 0) {
      ServiceLocator.getRenderService().getDebug().toggleActive();
      return true;
    }

    String arg = args.get(0);
    switch (arg) {
      case "on":
        ServiceLocator.getRenderService().getDebug().setActive(true);
        return true;
      case "off":
        ServiceLocator.getRenderService().getDebug().setActive(false);
        return true;
      case "id":
        ServiceLocator.getRenderService().getDebug().toggleId();
        return true;
      default:
        logger.debug("Unrecognised argument received for 'debug' command: {}", args);
        return false;
    }
  }

  /**
   * Validates the command arguments.
   * @param args command arguments
   * @return is valid
   */
  boolean isValid(ArrayList<String> args) {
    return (args.size() == 1 || args.size() == 0);
  }
}
