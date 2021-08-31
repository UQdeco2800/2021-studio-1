package com.deco2800.game.components.mainmenu;

import com.deco2800.game.GdxGame;
import com.deco2800.game.components.Component;
import com.deco2800.game.components.maingame.MainGameActions;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.UIComponent;
import com.deco2800.game.ui.UIPop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class listens to events relevant to the Main Menu Screen and does something when one of the
 * events is triggered.
 */
public class MainMenuActions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(MainMenuActions.class);
  private GdxGame game;
  private Entity mainMenuPop;

  public MainMenuActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("start", this::onStart);
    entity.getEvents().addListener("load", this::onLoad);
    entity.getEvents().addListener("exit", this::onExit);
    entity.getEvents().addListener("settings", this::onSettings);
    entity.getEvents().addListener("help", this::onHelp);
    entity.getEvents().addListener("mute", this::onMute);
  }

  /**
   * Swaps to the Main Game screen.
   */
  private void onStart() {
    logger.info("Start game");
    game.setScreen(GdxGame.ScreenType.MAIN_GAME);
  }

  /**
   * Intended for loading a saved game state.
   * Load functionality is not actually implemented.
   */
  private void onLoad() {
    logger.info("Load game");
  }

  /**
   * Exits the game.
   */
  private void onExit() {
    logger.info("Exit game");
    game.exit();
  }

  /**
   * Swaps to the Settings screen.
   */
  private void onSettings() {
    logger.info("Launching settings screen");
    game.setScreen(GdxGame.ScreenType.SETTINGS);
  }
    /**
     * Creates help popUP on main menu.
     */
  private void onHelp() {
    logger.info("Launching help popUp");
    if (mainMenuPop == null) {
        mainMenuPop = new Entity();

        mainMenuPop.addComponent(new UIPop("Default Pop", entity));

        ServiceLocator.getEntityService().register(mainMenuPop);
    } else {
        mainMenuPop.dispose();
        mainMenuPop = null;
    }
  }

  /**
    * Mutes main menu sound.
    */
  private void onMute() {
    logger.info("muting game");
  }
}
