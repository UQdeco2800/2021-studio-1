package com.deco2800.game.components.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.gameScore.gameScore;
import com.deco2800.game.ui.UIComponent;

/**
 * A ui component for displaying player stats, e.g. health.
 */
public class PlayerStatsDisplay extends UIComponent {
  Table table;
  Table tableTwo;
  private Image heartImage;
  private Label healthLabel;
  private Label scoreLabel;
  public static boolean deadFlag = false;
  gameScore scoring = new gameScore();



  /**
   * Creates reusable ui styles and adds actors to the stage.
   */
  @Override
  public void create() {
    super.create();
    addActors();
    entity.getEvents().addListener("updateHealth", this::updatePlayerHealthUI);
    entity.getEvents().addListener("updateScore", this::updatePlayerScoreUI);
  }

  /**
   * Creates actors and positions them on the stage using a table.
   * @see Table for positioning options
   */
  private void addActors() {
    table = new Table();

   // tableTwo = new Table();


    table.top().left();
    table.setFillParent(true);
    table.padTop(30f).padLeft(-200f);

    tableTwo = new Table();
    tableTwo.top().left();
    tableTwo.setFillParent(true);
    tableTwo.padTop(60f).padLeft(20f);
    // Heart image
    float heartSideLength = 30f;

    // Health text
    int health = entity.getComponent(CombatStatsComponent.class).getHealth();
    //int health = 100;
    CharSequence healthText = String.format("Health: %d", health);
    healthLabel = new Label(healthText, skin, "large");

    table.add(heartImage).size(heartSideLength).pad(5);
    table.add(healthLabel);
    stage.addActor(table);

    // Score Text
    long score = scoring.getCurrentScore();

    CharSequence scoreText = String.format("Score: %d",score);
    scoreLabel = new Label(scoreText,skin,"large");

    table.add(healthLabel);
    tableTwo.add(scoreLabel);
    stage.addActor(table);
    stage.addActor(tableTwo);

  }

  @Override
  public void draw(SpriteBatch batch)  {
    // draw is handled by the stage
  }

  @Override
  public void update(){
    entity.getEvents().trigger("updateScore", scoring.getCurrentScore());
    if (entity.getComponent(CombatStatsComponent.class).isDead() == true){
      deadFlag = true;
    }
  }


  /**
   * Updates the player's health on the ui.
   * @param health player health
   */
  public void updatePlayerHealthUI(int health) {
    CharSequence text = String.format("Health: %d", health);
    healthLabel.setText(text);
  }

  public void updatePlayerScoreUI(long score){
    CharSequence text = String.format("Score %d",score);
    scoreLabel.setText(text);

  }

  public long getPlayerScore() {
      return scoring.getCurrentScore();
  }

  @Override
  public void dispose() {
    super.dispose();
    healthLabel.remove();
    scoreLabel.remove();
  }
}