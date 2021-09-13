package com.deco2800.game.components.maingame;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.deco2800.game.GdxGame;
import com.deco2800.game.components.Component;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.UIPop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class listens to events relevant to the Main Game Screen and does something when one of the
 * events is triggered.
 */
public class MainGameActions extends Component {
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

  /**
  * Pauses the game -- the trigger function for the event.
  */
    public void onPause() {

        Sound pauseSound;
        //Music walkSound = ServiceLocator.getResourceService().getAsset("sounds/walk.mp3", Music.class);
        //walkSound.setLooping(true);
        //walkSound.setVolume(0.8f);

        if (game.paused) {
            ServiceLocator.getTimeSource().setTimeScale(1f);
            popUp.dispose();
            //resume sound
            //pauseSound = ServiceLocator.getResourceService().getAsset("sounds/Impact4.ogg", Sound.class);
            //walkSound.play();
        } else {
            ServiceLocator.getTimeSource().setTimeScale(0f);

            if (popUp != null) {
                popUp.dispose();
            }

            popUp = new Entity();
            popUp.addComponent(new UIPop("Pause Menu", entity));
            ServiceLocator.getEntityService().register(popUp);
            //pause sound
            //pauseSound = ServiceLocator.getResourceService().getAsset("sounds/Impact4.ogg", Sound.class);
            //walkSound.pause();
        }
        game.paused = !game.paused;
        //pauseSound.play();
    }


    /**
     * Pauses the game -- the trigger function for the event.
     */
    private void showScore() {

        Sound scoreScreenSound;

        if (game.scoreShown) {
            popUp.dispose();
            //score screen removed sound
            scoreScreenSound = ServiceLocator.getResourceService().getAsset("sounds/Impact4.ogg", Sound.class);
            game.scoreShown = false;
        } else {
            if (popUp != null) {
                popUp.dispose();
            }
            popUp = new Entity();
            popUp.addComponent(new UIPop("Score Screen", entity));
            ServiceLocator.getEntityService().register(popUp);
            //score screen sound
            scoreScreenSound = ServiceLocator.getResourceService().getAsset("sounds/Impact4.ogg", Sound.class);
            game.scoreShown = true;
        }
        scoreScreenSound.play();
    }

}
