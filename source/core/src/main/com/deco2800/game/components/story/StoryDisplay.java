package com.deco2800.game.components.story;

import com.badlogic.gdx.Gdx;
import com.deco2800.game.GdxGame;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.UIComponent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A ui component for displaying the Main menu.
 */
public class StoryDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(StoryDisplay.class);
  private static final float Z_INDEX = 2f;
  private Table table;
  private Table rootTable;
  private Texture texture;

  @Override
  public void create() {
    super.create();
    addActors();
  }

  private void addActors() {
    table = new Table();
    rootTable = new Table();
    rootTable.bottom().right();
    table.setFillParent(true);
    rootTable.setFillParent(true);

    Image story =
        new Image(
            ServiceLocator.getResourceService()
                .getAsset("images/story/storyline.png", Texture.class));

    Image topLayer = new Image(new TextureRegion(texture=GdxGame.getTexture()));
    topLayer.setColor(Color.BLACK);

    TextButton nextBtn = new TextButton("Next", skin);
    TextButton skipBtn = new TextButton("Skip", skin);

    // Triggers an event when the button is pressed

    nextBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Next button clicked");
            entity.getEvents().trigger("next");
          }
        });
    // Triggers an event when the button is pressed


    skipBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Skip button clicked");
            entity.getEvents().trigger("skip");
          }
        });
    topLayer.addAction(Actions.fadeOut(2));
    table.stack(story, topLayer).width(Gdx.graphics.getWidth()).height(Gdx.graphics.getHeight());
    rootTable.add(nextBtn).pad(30f);
    rootTable.add(skipBtn).pad(30f);

    stage.addActor(table);
    stage.addActor(rootTable);
  }


  @Override
  public void draw(SpriteBatch batch) {
    // Draw is handled by the stage.
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