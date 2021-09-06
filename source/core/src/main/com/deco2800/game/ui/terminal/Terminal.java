package com.deco2800.game.ui.terminal;

import com.deco2800.game.areas.AreaManager;
import com.deco2800.game.areas.RagnarokArea;
import com.deco2800.game.components.Component;
import com.deco2800.game.ui.terminal.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// TerminalService has two main functions:
//      1. allow the static calling of strings to the terminal
//      2. interfacing with other services/managers
//
// This terminal class remains, and is where commands are

/**
 * State tracker for a debug terminal. Any commands to be actioned through the terminal input should
 * be added to the map of commands.
 */
public class Terminal extends Component {
  private static final Logger logger = LoggerFactory.getLogger(Terminal.class);
  private final Map<String, Command> commands;
  private String enteredMessage = "";
  private boolean isOpen = false;
  private AreaManager manager;

  public Terminal() {
    this(new HashMap<>());
  }

  public void setManager(AreaManager manager) {
    this.manager = manager;
  }

  public Terminal(Map<String, Command> commands) {
    this.commands = commands;

    addCommand("debug", new DebugCommand());
    addCommand("-spawn", new SpawnCommand()); //TODO: make more Commands
    addCommand("-place", new PlaceCommand());
    addCommand("-load", new LoadCommand());
  }

  /** @return message entered by user */
  public String getEnteredMessage() {
    return enteredMessage;
  }

  /** @return console is open */
  public boolean isOpen() {
    return isOpen;
  }

  /**
   * Toggles between the terminal being open and closed.
   */
  public void toggleIsOpen() {
    if (isOpen) {
      this.setClosed();
    } else {
      this.setOpen();
    }
  }

  /**
   * Opens the terminal.
   */
  public void setOpen() {
    logger.debug("Opening terminal");
    isOpen = true;
  }

  /**
   * Closes the terminal and clears the stored message.
   */
  public void setClosed() {
    logger.debug("Closing terminal");
    isOpen = false;
    setEnteredMessage("");
  }

  /**
   * Adds a command to the list of valid terminal commands.
   *
   * @param name command name
   * @param command command
   */
  public void addCommand(String name, Command command) {
    logger.debug("Adding command: {}", name);
    if (commands.containsKey(name)) {
      logger.error("Command {} is already registered", name);
    }
    commands.put(name, command);
  }

  /**
   * Processes the completed message entered by the user. If the message corresponds to a valid
   * command, the command will be actioned.
   * @return true if command handled, false otherwise
   */
  public boolean processMessage() {
    logger.debug("Processing message");
    // strip leading and trailing whitespace
    String message = enteredMessage.trim();

    // separate command from args
    String[] sections = message.split(" ");
    String command = sections[0];

    ArrayList<String> args = new ArrayList<>(Arrays.asList(sections).subList(1, sections.length));

    if (commands.containsKey(command)) {
      setEnteredMessage("");
      return commands.get(command).action(args);
    }
    return false;
  }

  /**
   * Appends the character to the end of the entered message.
   *
   * @param character character to append
   */
  public void appendToMessage(char character) {
    logger.debug("Appending '{}' to message", character);
    enteredMessage = enteredMessage + character;
  }

  /** Removes the last character of the entered message. */
  public void handleBackspace() {
    logger.debug("Handling backspace");
    int messageLength = enteredMessage.length();
    if (messageLength != 0) {
      enteredMessage = enteredMessage.substring(0, messageLength - 1);
    }
  }

  /**
   * Sets the text shown on the terminal
   * @param text Text to show
   */
  public void setEnteredMessage(String text) {
    enteredMessage = text;
  }

  public void sendTerminal(String text) {
    setEnteredMessage(text);
    processMessage();
  }
}
