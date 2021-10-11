package com.deco2800.game.components.tutorial;

import com.deco2800.game.GdxGame;
import com.deco2800.game.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class listens to events relevant to the Main Menu Screen and does something when one of the
 * events is triggered.
 */
public class Tutorial2Actions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(Tutorial2Actions.class);
  private GdxGame game;

  public Tutorial2Actions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("next", this::onNext);
    entity.getEvents().addListener("prev", this::onPrev);
    entity.getEvents().addListener("skip", this::onSkip);
  }

  /**
   * Swaps to the Main Game screen.
   */
  private void onNext() {
    logger.info("Load next");
    game.setScreen(GdxGame.ScreenType.MAIN_MENU);
  }

  /**
   * Intended for loading a saved game state.
   * Load functionality is not actually implemented.
   */
  private void onPrev() {
    logger.info("Load previous");
    game.setScreen(GdxGame.ScreenType.TUT1);
  }

  /**
   * Exits the game.
   */
  private void onSkip() {
    logger.info("Skipping to menu");
    game.setScreen(GdxGame.ScreenType.MAIN_MENU);
  }
}