package com.deco2800.game.components.gamearea;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.deco2800.game.ai.tasks.AITaskComponent;
import com.deco2800.game.components.player.PlayerStatsDisplay;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.GeneratorComponent;
import com.deco2800.game.physics.components.AbstractPlayerMovementComponent;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.UIComponent;

/**
 * Displays the name of the current game area.
 */
public class GameAreaDisplay extends UIComponent {
  private static final String ID_FORMAT = "id: %d, x: %f, y: %f"; // Satisfy SonarCloud.
  private String gameAreaName;
  private Label title;

  private Label debug;

  private boolean isDebug = false;  // SET THIS TO TRUE FOR DEBUG SCREEN
                                    // CURRENTLY VERY MESSY, ASK @NEO-TAKE-LUCY IF YOU WANT MORE
                                    // I CAN ALSO IMPLEMENT THIS BEING SET BY TERMINAL, if ya dig

  public GameAreaDisplay(String gameAreaName) {
    this.gameAreaName = gameAreaName;
  }

  @Override
  public void create() {
    super.create();
    addActors();
  }

  private void addActors() {
    title = new Label(this.gameAreaName, skin, "large");
    debug = new Label("", skin, "small");

    stage.addActor(debug); //remove to remove this if don't want to debug
  }

  @Override
  public void draw(SpriteBatch batch)  {
    int screenHeight = Gdx.graphics.getHeight();
    float offsetX = 200f;
    float offsetY = 400f;

    //title.setPosition(offsetX, screenHeight - offsetY);

    debug.setVisible(isDebug); //turn this to false if don't want debug infromation
    debug.setText(makeDebugString());
    debug.setPosition(20f, 300f);
  }

  private String makeDebugString() {

    boolean isDetailed = false; //detailed mode describes "other" objects aswell

    StringBuilder debugString = new StringBuilder("DE_BUG:\n");

    debugString.append(this.gameAreaName);
    debugString.append("\n");

    Array<Entity> sortedArray = new Array<>(ServiceLocator.getEntityService().getEntityArray());

    for (Entity e : ServiceLocator.getEntityService().getEntityArray()) {

      // basically going to be a really janky sort so that player is at top
      // player is
      if (e.getComponent(PlayerStatsDisplay.class) != null) {

        debugString.append("Player :: ");
        debugString.append(String.format(ID_FORMAT, e.getId(),
                e.getCenterPosition().x, e.getCenterPosition().y));
        debugString.append("\n");
        sortedArray.removeValue(e, true);

      }

      if (e.getComponent(AbstractPlayerMovementComponent.class) != null) {
        debugString.append("Abstract Player :: ");
        debugString.append(String.format(ID_FORMAT, e.getId(),
                e.getCenterPosition().x, e.getCenterPosition().y));
        debugString.append("\n");
        sortedArray.removeValue(e, true);
      }

      if (e.getComponent(AITaskComponent.class) != null) {
        debugString.append("AI Entity :: ");
        debugString.append(String.format("id: %d, x: %f, y: %f%n", e.getId(),
                e.getCenterPosition().x, e.getCenterPosition().y));

        sortedArray.removeValue(e, true);
      }

      if (e.getComponent(GeneratorComponent.class) != null) {
        debugString.append(" *** GENERATOR *** ");
        debugString.append("State: ").append(e.getComponent(GeneratorComponent.class).getState());
        debugString.append("\nDifficulty: ").append(e.getComponent(GeneratorComponent.class).getCurrentDifficulty());
        debugString.append("\n");
        sortedArray.removeValue(e, true);
      }

    }

    if (isDetailed) {
      for (Entity e : sortedArray) {

        debugString.append("Other :: ");
        debugString.append(String.format(ID_FORMAT, e.getId(),
                e.getCenterPosition().x, e.getCenterPosition().y));
        debugString.append("\n");

      }
    }

    return debugString.toString();

  }

  @Override
  public void dispose() {
    super.dispose();
    title.remove();
  }
}
