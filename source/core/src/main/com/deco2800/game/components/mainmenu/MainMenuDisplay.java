package com.deco2800.game.components.mainmenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.events.MouseEvent;

/**
 * A ui component for displaying the Main menu.
 */
public class MainMenuDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(MainMenuDisplay.class);
  private static final float Z_INDEX = 2f;
  private Table table;
  private Table rightTable;
  private Table leftTable;


  @Override
  public void create() {
    super.create();
    addActors();
  }

  private void addActors() {
    table = new Table();
    rightTable = new Table();
    leftTable = new Table();
    table.setFillParent(true);
    rightTable.setFillParent(true);
    leftTable.setFillParent(true);

    Image title =
        new Image(
            ServiceLocator.getResourceService()
                .getAsset("images/box_boy_title.png", Texture.class));

    TextButton startBtn = new TextButton("Run!", skin);
    //This and its descendants are commented out since it could be a button we use in future
    //TextButton loadBtn = new TextButton("Load", skin);
    TextButton settingsBtn = new TextButton("Settings", skin);
    TextButton exitBtn = new TextButton("Exit", skin);
    TextButton helpBtn = new TextButton("Help", skin);

    Image muteImage = new Image(ServiceLocator.getResourceService().getAsset("images/mute_button_on.png", Texture.class));
    muteImage.setScale(2f, 2f);
    // Triggers an event when the button is pressed

    startBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Start button clicked");
            entity.getEvents().trigger("start");
          }
        });

    helpBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Help button clicked");
            entity.getEvents().trigger("help");
          }
        });


    //muteImage.addListener(
      //   new ClickListener() {

        // });

    /*loadBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Load button clicked");
            entity.getEvents().trigger("load");
          }
        });
    */

    leftTable.bottom().left();
    rightTable.bottom().right();

    settingsBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Settings button clicked");
            entity.getEvents().trigger("settings");
          }
        });

    exitBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {

            logger.debug("Exit button clicked");
            entity.getEvents().trigger("exit");
          }
        });

    table.add(title);
    table.row();
    table.add(startBtn).padTop(70f);
    table.row();
    table.row();
    table.add(settingsBtn).padTop(30f);
    table.row();
    table.add(exitBtn).padTop(30f);

    rightTable.add(helpBtn);
    leftTable.add(muteImage);

    stage.addActor(table);
    stage.addActor(rightTable);
    stage.addActor(leftTable);
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
    leftTable.clear();
    rightTable.clear();
    super.dispose();
  }
}
