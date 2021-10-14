package com.deco2800.game.components.maingame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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

    Image displayBack = new Image(
        ServiceLocator.getResourceService()
            .getAsset("images/disp_back.png", Texture.class));

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