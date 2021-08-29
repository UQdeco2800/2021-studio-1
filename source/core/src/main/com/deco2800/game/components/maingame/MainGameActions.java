package com.deco2800.game.components.maingame;

import com.badlogic.gdx.audio.Sound;
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
    entity.getEvents().addListener("pause", this::onPause);
  }


  /**
   * Swaps to the Main Menu screen.
   */
  private void onExit() {
    logger.info("Exiting main game screen");
    game.setScreen(GdxGame.ScreenType.MAIN_MENU);
  }

  /**
  * Pauses the game -- the trigger function for the event.
  */
    private void onPause() {

        Sound pauseSound;

        if (game.paused) {
            ServiceLocator.getTimeSource().setTimeScale(1f);
            System.out.println("paused");
            popUp.dispose();
            //resume sound
            pauseSound = ServiceLocator.getResourceService().getAsset("sounds/Impact4.ogg", Sound.class);
        } else {
            ServiceLocator.getTimeSource().setTimeScale(0f);
            popUp = new Entity();
            popUp.addComponent(new UIPop("Pause Menu"));
            ServiceLocator.getEntityService().register(popUp);
            //pause sound
            pauseSound = ServiceLocator.getResourceService().getAsset("sounds/Impact4.ogg", Sound.class);
        }
        game.paused = !game.paused;
        pauseSound.play();
    }
}
