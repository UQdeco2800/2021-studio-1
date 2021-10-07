package com.deco2800.game.components.tutorial;

import com.badlogic.gdx.audio.Music;
import com.deco2800.game.GdxGame;
import com.deco2800.game.components.Component;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.services.ResourceService;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.UIPop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class listens to events relevant to the Main Menu Screen and does something when one of the
 * events is triggered.
 */
public class Tutorial1Actions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(Tutorial1Actions.class);
  private GdxGame game;
  private Entity storyPop;
  private boolean muted = false;
  private Music music;
  //  private static final String MAIN_MUSIC = "sounds/main.mp3";

  public Tutorial1Actions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("next", this::onNext);
    entity.getEvents().addListener("prev", this::onPrev);
    entity.getEvents().addListener("skip", this::onSkip);
//    music = ServiceLocator.getResourceService().getAsset(MAIN_MUSIC, Music.class);
//    music.setLooping(true);
//    playMusic();
  }

//  private void playMusic() {
//
//    music.setVolume(0.7f);
//    music.play();
//    logger.info(music.isPlaying() + "music playing");
//  }
//
//  private void stopMusic() {
//    music.setVolume(0f);
//  }

  /**
   * Swaps to the Main Game screen.
   */
  private void onNext() {
    logger.info("Load next");
    game.setScreen(GdxGame.ScreenType.TUT2);
  }

  /**
   * Intended for loading a saved game state.
   * Load functionality is not actually implemented.
   */
  private void onPrev() {
    logger.info("Load previous");
    game.setScreen(GdxGame.ScreenType.STORY);
  }

  /**
   * Exits the game.
   */
  private void onSkip() {
    logger.info("Skipping to menu");
    game.setScreen(GdxGame.ScreenType.MAIN_MENU);
  }
}