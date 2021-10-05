package com.deco2800.game.components.maingame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Displays a button to exit the Main Game screen to the Main Menu screen.
 */
public class MainGamePannelDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(MainGamePannelDisplay.class);
  private static final float Z_INDEX = 2f;
  private Table table;

    @Override
  public void create() {
    super.create();
    addActors();
  }

  private void addActors() {
    table = new Table();
    Table pannelDisplayBack = new Table();
    table.top().right();
    pannelDisplayBack.top().left();
    table.setFillParent(true);
    pannelDisplayBack.setFillParent(true);

    TextButton mainMenuBtn = new TextButton("Exit", skin);
    TextButton mainScoreBtn = new TextButton("Score", skin);
    TextButton mainPauseBtn = new TextButton("Pause", skin);

    Image displayBack = new Image(
          ServiceLocator.getResourceService()
                  .getAsset("images/disp_back.png", Texture.class));

    // Triggers an event when the button is pressed.
    mainMenuBtn.addListener(
      new ChangeListener() {
        @Override
        public void changed(ChangeEvent changeEvent, Actor actor) {
          logger.debug("Exit button clicked");
          entity.getEvents().trigger("exit");
        }
      });

      // Triggers an event when the button is pressed.
    mainScoreBtn.addListener(
          new ChangeListener() {
              @Override
              public void changed(ChangeEvent changeEvent, Actor actor) {
                  logger.debug("Score button clicked");
                  entity.getEvents().trigger("Score Screen");
              }
          });

      // Triggers an event when the button is pressed.
    mainPauseBtn.addListener(
          new ChangeListener() {
              @Override
              public void changed(ChangeEvent changeEvent, Actor actor) {
                  logger.debug("Pause button clicked");
                  entity.getEvents().trigger("Pause Menu");
              }
          });


    table.add(mainMenuBtn).padRight(170).padTop(10f).row();
    table.add(mainPauseBtn).padRight(150).padTop(10f).row();
    pannelDisplayBack.add(displayBack);

    table.add(pannelDisplayBack);
    stage.addActor(pannelDisplayBack);
    stage.addActor(table);
  }

  @Override
  public void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    table.clear();
    super.dispose();
  }
}
