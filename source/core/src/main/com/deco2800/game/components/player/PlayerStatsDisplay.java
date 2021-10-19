package com.deco2800.game.components.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.powerups.LightningPowerUpComponent;
import com.deco2800.game.files.UserSettings;
import com.deco2800.game.score.GameScore;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.UIComponent;

import java.awt.*;

/**
 * A ui component for displaying player stats, e.g. health.
 */
public class PlayerStatsDisplay extends UIComponent {

  private Table table;
  private Table tableTwo;

  private Image heartImage;
  private Image healthBar;

  private Label healthLabel;
  private Label scoreLabel;

  public static boolean deadFlag = false;
  public static boolean lightningActive = false;

  GameScore scoring = new GameScore();
  private int health;

  /**
   * Creates reusable ui styles and adds actors to the stage.
   */
  @Override
  public void create() {
    super.create();
    addActors();
    entity.getEvents().addListener("updateHealth", this::updatePlayerHealthUI);
    entity.getEvents().addListener("updateScore", this::updatePlayerScoreUI);
    deadFlag = false;
  }

  /**
   * Creates actors and positions them on the stage using a table.
   * @see Table for positioning options
   */
  private void addActors() {

    table = new Table();
    table.top().left();
    table.setFillParent(true);

    //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    float windowHeight = UserSettings.get().fullscreen ? 900 :
            UserSettings.getWindowHeight();

    tableTwo = new Table();
    tableTwo.top().left();
    tableTwo.setFillParent(true);

    // Health text
    health = entity.getComponent(CombatStatsComponent.class).getHealth();

    //int health = 100;
    CharSequence healthText = String.format("%d", health);
    healthLabel = new Label("HP:" + healthText, skin);

    healthBar =  new Image(
          ServiceLocator.getResourceService()
                  .getAsset("images/healthBar.png", Texture.class));

    Image healthBarBack = new Image(
          ServiceLocator.getResourceService()
                  .getAsset("images/healthBarBack.png", Texture.class));

    // Score Text
    long score = scoring.getCurrentScore();

    CharSequence scoreText = String.format("Score: %d",score);
    scoreLabel = new Label(scoreText,skin,"popUpFont");

    float healthBarScale = 0.5f;
    float healthBarOffset = healthBarScale;
    float healthBarScreenRatio = 0.950f;
    float healthLabelScreenRatio = 0.950f;
    float scoreLabelScreenRatio = 0.915f;


    table.add(healthLabel).left();
    table.add(healthBarBack);
    tableTwo.add(scoreLabel);

    stage.addActor(healthBarBack);
    healthBarBack.setPosition(20, windowHeight * healthBarScreenRatio);
    healthBarBack.setScale(healthBarScale, healthBarScale);

    stage.addActor(healthBar);
    healthBar.setPosition(20 + healthBarOffset, windowHeight * healthBarScreenRatio + healthBarOffset);
    healthBar.setScale(healthBarScale, healthBarScale);

    healthLabel.setPosition(25 + healthBarOffset, windowHeight * healthLabelScreenRatio + healthBarOffset + 3);
    scoreLabel.setPosition(20 + healthBarOffset, windowHeight * scoreLabelScreenRatio + healthBarOffset);

    stage.addActor(healthLabel);
    stage.addActor(scoreLabel);
  }

  @Override
  public void draw(SpriteBatch batch)  {
    // draw is handled by the stage
  }

  @Override
  public void update(){
    entity.getEvents().trigger("updateScore", scoring.getCurrentScore());
    // flags that the player is dead
    if (entity.getComponent(CombatStatsComponent.class).isDead()){
      deadFlag = true;
      entity.getEvents().trigger("start");
    }
    if(entity.getComponent(LightningPowerUpComponent.class).getActive()){
      lightningActive = true;
    }else{
      lightningActive = false;
    }
  }

  /**
   * Updates the player's health on the ui.
   * @param health player health
   */
  public void updatePlayerHealthUI(int health) {
    CharSequence text = String.format("HP: %d", health);
    healthBar.setScale(health/100f * 0.5f, 0.5f);
    healthLabel.setText(text);
  }

  public void updatePlayerScoreUI(long score){
    CharSequence text = String.format("Score: %d",score);
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