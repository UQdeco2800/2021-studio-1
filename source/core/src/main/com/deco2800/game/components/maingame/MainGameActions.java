package com.deco2800.game.components.maingame;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.deco2800.game.GdxGame;
import com.deco2800.game.components.Component;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.screens.MainGameScreen;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.UIPop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class listens to events relevant to the Main Game Screen and does something when one of the
 * events is triggered.
 */
public class MainGameActions extends Component {
    private static final String IMPACT = "sounds/Impact4.ogg"; // Satisfy sonarCloud.
    private static final Logger logger = LoggerFactory.getLogger(MainGameActions.class);
    private GdxGame game;
    private Entity popUp;

    public MainGameActions(GdxGame game) {
        this.game = game;
    }


  @Override
  public void create() {
    entity.getEvents().addListener("exit", this::onExit);
    entity.getEvents().addListener("Pause Menu", this::onPause);
    entity.getEvents().addListener("Score Screen", this::showScore);
    entity.getEvents().addListener("Game Over", this::gameOver);
      entity.getEvents().addListener("start", this::onStart);
  }


  /**
   * Swaps to the Main Menu screen.
   */
  public void onExit() {
    logger.info("Exiting main game screen");
    game.paused = false;
    game.scoreShown = false;
    game.setScreen(GdxGame.ScreenType.MAIN_MENU);
  }

    private void onStart() {
        logger.info("Restart game");
        game.paused = false;
        game.over = false;
        game.setScreen(GdxGame.ScreenType.MAIN_GAME);
    }

    /**
     * Pauses the game -- the trigger function for the event.
     */
    public void onPause() {
        if (game.paused) {
            ServiceLocator.getTimeSource().setTimeScale(1f);
            popUp.dispose();
        } else {
            ServiceLocator.getTimeSource().setTimeScale(0f);

            if (popUp != null) {
                popUp.dispose();
            }

            popUp = new Entity();
            popUp.addComponent(new UIPop("Pause Menu", entity));
            ServiceLocator.getEntityService().register(popUp);
        }
        game.paused = !game.paused;
    }


    /**
     * Pauses the game -- the trigger function for the event.
     */
    private void showScore() {

        Sound scoreScreenSound;

        if (game.scoreShown) {
            popUp.dispose();
            ServiceLocator.getEntityService().unregister(popUp);
            //score screen removed sound
            game.scoreShown = false;
        } else {
            if (popUp != null) {
                popUp.dispose();
            }
            popUp = new Entity();
            popUp.addComponent(new UIPop("Score Screen", entity));
            ServiceLocator.getEntityService().register(popUp);
            //score screen sound
            game.scoreShown = true;
        }
        ServiceLocator.getSoundService().playSound("impact");
    }

    private void gameOver() {

        if (game.over) {
            ServiceLocator.getTimeSource().setTimeScale(1f);
            popUp.dispose();
        } else {
            ServiceLocator.getTimeSource().setTimeScale(0f);

            if (popUp != null) {
                popUp.dispose();
            }

            popUp = new Entity();
            popUp.addComponent(new UIPop("Game Over", entity));
            ServiceLocator.getEntityService().register(popUp);
        }
        game.over = !game.over;

        ServiceLocator.getSoundService().playSound("impact");

    }
}
