package com.deco2800.game.components.mainmenu;

import com.deco2800.game.GdxGame;
import com.deco2800.game.components.Component;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.services.ServiceLocator;
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
  private Music music;
  private boolean muted = false;
  private static final String MAIN_MUSIC = "sounds/main.mp3";

  public MainMenuActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("start", this::onStart);
    entity.getEvents().addListener("load", this::onLoad);
    entity.getEvents().addListener("exit", this::onExit);
    entity.getEvents().addListener("settings", this::onSettings);
    entity.getEvents().addListener("Help Screen", this::onHelp);
    entity.getEvents().addListener("Leaderboard", this::onLeaderBoard);
    entity.getEvents().addListener("mute", this::onMute);
  }

    private void playMusic() {
      music = ServiceLocator.getResourceService().getAsset(MAIN_MUSIC, Music.class);
      music.setVolume(0.7f);
      music.play();
      logger.info(music.isPlaying() + "music playing");
    }

    private void stopMusic() {
        music.setVolume(0f);
    }

    /**
   * Swaps to the Main Game screen.
   */
  private void onStart() {
    logger.info("Start game");
    game.paused = false;
    game.over = false;
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
        mainMenuPop.addComponent(new UIPop("Help Screen", entity));
        ServiceLocator.getEntityService().register(mainMenuPop);
    } else {
        mainMenuPop.dispose();
        mainMenuPop = null;
        ServiceLocator.getEntityService().unregister(mainMenuPop);
    }
  }

    /**
     * Creates leaderboard popUP on main menu.
     */
    private void onLeaderBoard() {
        logger.info("Launching leaderboard popUp");
        if (mainMenuPop == null) {
            mainMenuPop = new Entity();
            mainMenuPop.addComponent(new UIPop("Leaderboard", entity));
            ServiceLocator.getEntityService().register(mainMenuPop);
        } else {
            mainMenuPop.dispose();
            mainMenuPop = null;
            ServiceLocator.getEntityService().unregister(mainMenuPop);
        }
    }

  /**
    * Mutes main menu sound.
    */
  private void onMute() {
    logger.info("muting game");

    muted = !muted;
    if (muted) {
        ServiceLocator.getSoundService().setMusicVolume(0f);
    } else {
        ServiceLocator.getSoundService().setMusicVolume(1f);
    }

    }
}
