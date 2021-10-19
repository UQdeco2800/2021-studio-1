package com.deco2800.game.components.mainmenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.deco2800.game.files.UserSettings;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * A ui component for displaying the Main menu.
 */
public class MainMenuDisplay extends UIComponent {

  private static final Logger logger = LoggerFactory.getLogger(MainMenuDisplay.class);
  private static final float Z_INDEX = 2f;
  private Table rootTable;
  private Table table;
  private Table helpTable;
  private Table highScoreTable;
  private Table starOne;
  private Image shootingStarOne;
  private Table starTwo;
  private Image shootingStarTwo;
  private Table starThree;
  private Image shootingStarThree;
  private float effectsXOne = -200;
  private float effectsXTwo = -100;
  private float effectsXThree = -500;
  private float effectsYOne = 300;
  private float effectsYTwo = 450;
  private float effectsYThree = 350;
  private float frameCountOne = 0;
  private float frameCountTwo = 0;
  private float frameCountThree = 0;
  private float shootOneSpeed = 25;
  private float shootTwoSpeed = 25;
  private float shootThreeSpeed = 25;
  private boolean starOneShot = false;
  private boolean starTwoShot = false;
  private boolean starThreeShot = false;
  private float shootOneWait = 1;
  private float shootTwoWait = 1;
  private float shootThreeWait = 1;
  private Table backgroundTable;
  private Table muteTable;
  private ImageButton muteButtonOn;
  private ImageButton muteButtonOff;
  private boolean muted = false;
  private Dimension screenSize;
  private Label playerNameText;
  private TextArea inputBox;
  private TextButton inputBoxButton;

  private String YOUR_NAME_TEXT = "Your Name: ";
  private static final String PLAY_LINE = "Play to get here!";
  private static final String POP_UP_FONT = "popUpFont";

  private static String playerName = "Random";
  private static int[] scoreValues = {0, 0, 0, 0, 0};
  private static String[] scoreNames = {PLAY_LINE, PLAY_LINE, PLAY_LINE, PLAY_LINE, PLAY_LINE};
  private static String highScoreName = "";
  private static int highScoreValue = 0;

  @Override
  public void create() {
    super.create();
    addActors();
  }

  private void addActors() {
    rootTable = new Table();
    table = new Table();
    helpTable = new Table();
    highScoreTable = new Table();
    backgroundTable = new Table();
    starOne = new Table();
    starTwo = new Table();
    starThree = new Table();
    muteTable = new Table();
    starOne.setFillParent(true);
    starTwo.setFillParent(true);
    starThree.setFillParent(true);
    rootTable.setFillParent(true);
    backgroundTable.setFillParent(true);
    table.setFillParent(true);
    helpTable.setFillParent(true);
    muteTable.setFillParent(true);
    highScoreTable.setFillParent(true);

    shootingStarOne = new Image(
            ServiceLocator.getResourceService()
                    .getAsset("images/star.png", Texture.class));
    shootingStarOne.setScale(0.2f, 0.2f);

    shootingStarTwo =
          new Image(
                  ServiceLocator.getResourceService()
                          .getAsset("images/star2.png", Texture.class));
    shootingStarTwo.setScale(0.2f, 0.2f);

    shootingStarThree =
          new Image(
                  ServiceLocator.getResourceService()
                          .getAsset("images/star3.png", Texture.class));
    shootingStarThree.setScale(0.2f, 0.2f);

    TextButton startBtn = new TextButton("Run!", skin);
    TextButton settingsBtn = new TextButton("Settings", skin);
    TextButton exitBtn = new TextButton("Exit", skin);
    TextButton helpBtn = new TextButton("Help", skin);
    TextButton leaderBoardButton = new TextButton("Leaderboard", skin);
    inputBoxButton = new TextButton("Enter", skin);

    muteButtonOn = new ImageButton(new TextureRegionDrawable(
            ServiceLocator.getResourceService().getAsset(
                    "images/mute_button_on.png", Texture.class)));

    muteButtonOff = new ImageButton(new TextureRegionDrawable(
              ServiceLocator.getResourceService().getAsset(
                      "images/mute_button_off.png", Texture.class)));

    highScoreName = readHighScores();
    playerNameText = new Label(YOUR_NAME_TEXT + playerName, skin, POP_UP_FONT);
    Label highScorePreText = new Label("Best Runner", skin, POP_UP_FONT);
    Label highScoreNameText = new Label(highScoreName, skin, POP_UP_FONT);
    Label highScoreValueText = new Label("" + highScoreValue, skin, POP_UP_FONT);
    SelectBox<String> characterSelections = new SelectBox<>(skin);
    inputBox = new TextArea("Enter Name", skin);
    addCharacterSelections(characterSelections);

    //screenSize = Toolkit.getDefaultToolkit().getScreenSize();

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
                    entity.getEvents().trigger("Help Screen");
                }
            });

    leaderBoardButton.addListener(
            new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    logger.debug("leaderboard clicked");
                    entity.getEvents().trigger("Leaderboard");
                }
            });

    muteButtonOn.addListener(
            new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    logger.debug("mute button clicked");
                    switchMuteGraphic();
                    entity.getEvents().trigger("mute");
                }
            });

    muteButtonOff.addListener(
          new ChangeListener() {
              @Override
              public void changed(ChangeEvent changeEvent, Actor actor) {
                  logger.debug("mute button clicked");
                  switchMuteGraphic();
                  entity.getEvents().trigger("mute");
              }
          });

    muteTable.bottom().left();
    helpTable.bottom().right();
    highScoreTable.top().right();

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

    inputBoxButton.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent changeEvent, Actor actor) {
            playerName = inputBox.getText();
            logger.debug("{} players name", playerName);
            playerNameText.setText(YOUR_NAME_TEXT + playerName);
        }
    });

    rootTable.setBackground(new TextureRegionDrawable(new Texture("images/plainBack.png")));
    setBackground();
    table.add(playerNameText).padTop(150f);
    table.row();
    table.add(characterSelections).padTop(20f);
    table.row();
    table.add(inputBox).padTop(10f);
    table.add(inputBoxButton);
    inputBox.setVisible(false);
    inputBoxButton.setVisible(false);
    table.row();
    table.add(startBtn).padTop(20f);
    table.row();
    table.row();
    table.add(settingsBtn).padTop(30f);
    table.row();
    table.add(exitBtn).padTop(30f);

    helpTable.add(helpBtn);
    muteTable.add(muteButtonOn);
    muteTable.add(muteButtonOff);
    muteButtonOff.setVisible(false);
    highScoreTable.add(highScorePreText);
    highScoreTable.row();
    highScoreTable.add(highScoreNameText).bottom().right();
    highScoreTable.row();
    highScoreTable.add(highScoreValueText).bottom().right();
    highScoreTable.row();
    highScoreTable.add(leaderBoardButton).bottom().right();
    starOne.add(shootingStarOne).top().left();
    starOne.row();
    starTwo.add(shootingStarTwo);
    starOne.row();
    starThree.add(shootingStarThree);
    starOne.setPosition(effectsXOne, effectsYOne);
    starTwo.setPosition(effectsXTwo, effectsYTwo);
    starThree.setPosition(effectsXThree, effectsYThree);

    stage.addActor(rootTable);
    stage.addActor(backgroundTable);
    stage.addActor(starOne);
    stage.addActor(starTwo);
    stage.addActor(starThree);
    stage.addActor(table);
    stage.addActor(helpTable);
    stage.addActor(muteTable);
    stage.addActor(highScoreTable);
  }

  private void addCharacterSelections(SelectBox<String> characterSelections) {

      String[] selections = new String[] {"Select Name",  "Random",  "Thor", "Loki", "Bjorn", "Floki", "Odin", "Ironside", "Uber", "Frejya", "Njoror", "Aesir", "Mjolnir", "Custom"};
      Array<String> selectionText = new Array<>();

      for (String selection : selections) {

          selectionText.add(selection);
      }

      characterSelections.setItems(selectionText);

      characterSelections.addListener(new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
              playerName = characterSelections.getSelected();
              if (playerName.equals("Custom")) {
                  showCustomBox();
              } else {
                  inputBox.setVisible(false);
                  inputBoxButton.setVisible(false);
                  playerNameText.setText(YOUR_NAME_TEXT + playerName);
              }
          }
      });
  }

    /*
     * Shows the custom box beneath the selection box on the main menu screen
     */
    private void showCustomBox() {

      inputBox.setVisible(true);
      inputBoxButton.setVisible(true);
    }

    /*
     * Reads the high scores from highScores.txt in gameinfo folder
     *      @returns - the name of the player with the highest score
     */
    private static String readHighScores() {
        scoreValues = new int[]{0, 0, 0, 0, 0};
        scoreNames = new String[]{PLAY_LINE, PLAY_LINE, PLAY_LINE, PLAY_LINE, PLAY_LINE};


        File highScoresFile = new File("gameinfo/highScores.txt");
        Scanner highScoresScanner;
        try {
            highScoresScanner = new Scanner(highScoresFile);
        } catch (FileNotFoundException f) {
            return "High Score file not found";
        }

        int numberScoreRead = 0;
        highScoreValue = 0;

        while (highScoresScanner.hasNextLine()) {

            String line = highScoresScanner.nextLine();
            String[] lineResult = line.split(",");
            String readName = lineResult[0];
            int scoreValue = Integer.parseInt(lineResult[1]);

            logger.debug(readName);

            // sort while reading
            for (int i = 0; i < 5; i++) {

                if (scoreValues[i] <= scoreValue) {

                    for (int j = 4; j > i; j--) {
                        scoreValues[j] = scoreValues[j - 1];
                        scoreNames[j] = scoreNames[j - 1];
                    }
                    scoreValues[i] = scoreValue;
                    scoreNames[i] = readName;
                    break;
                    }
            numberScoreRead += 1;
            }
            numberScoreRead += 1;
        }

        highScoreValue = scoreValues[0];
        highScoreName = scoreNames[0];
        highScoresScanner.close();

        return highScoreName;
    }

    private void switchMuteGraphic() {

        if (muted) {
            muteButtonOn.setVisible(true);
            muteButtonOff.setVisible(false);
            muted = false;
        } else {
            muteButtonOn.setVisible(false);
            muteButtonOff.setVisible(true);
            muted = true;
        }
    }

    /*
     * Returns the players selected name
     */
    public static String getPlayerName() {
        return playerName;
    }

    /*
     * Returns the highest score value
     */
    public static int getHighScore() {
        return highScoreValue;
    }

    /*
     * Returns the name of the player with the highest score
     */
    public static String getHighScoreName() {
        return highScoreName;
    }


    /*
     * Returns the current high score names in order
     */
    public static String[] getHighScoreNames() {
        //cloning to prevent public modification
        return scoreNames.clone();
    }

    /*
     * Returns the current high score values in order
     */
    public static int[] getHighScoreValues() {
        // cloning to prevent public modification
        return scoreValues.clone();
    }


    @Override
    public void draw(SpriteBatch batch) {

        frameCountOne += 1;
        frameCountTwo += 1;
        frameCountThree += 1;

        int offset = UserSettings.get().fullscreen ? 200 : 0;

        MoveStars();

        RotateStars();

        SetStarPositions();

        if (!starOneShot && effectsXOne >= offset) {
            starOneShot = true;
            frameCountOne = 0;
            shootOneWait = (float) Math.random() * 800 + 800;
        }

        if (!starTwoShot && effectsXTwo >= offset) {
            starTwoShot = true;
            frameCountTwo = 0;
            shootTwoWait = (float) Math.random() * 300 + 300;
        }

        if (!starThreeShot && effectsXThree >= offset) {
            starThreeShot = true;
            frameCountThree = 0;
            shootThreeWait = (float) Math.random() * 500 + 500;
        }
    }

    private void SetStarPositions() {

        if (starOneShot && frameCountOne > shootOneWait) {
            SetStarOnePosition();
            starOneShot = false;
        }

        if (starTwoShot && frameCountTwo > shootTwoWait) {
            SetStarTwoPosition();
            starTwoShot = false;
        }

        if (starThreeShot && frameCountThree > shootThreeWait) {
            SetStarThreePosition();
            starThreeShot = false;
        }
    }

    private void SetStarOnePosition() {
        float rand = (float) Math.random() * 1000 + 1000;
        float rand2 = (float) Math.random() * 150 + 10;
        effectsXOne = 200 - rand;
        effectsYOne = 300 + rand2;

        shootOneSpeed = (float) Math.random() * 25 + 25;

        float scale = (float)(Math.random() * 0.2);
        shootingStarOne.setScale(scale, scale);

        starOne.setPosition(effectsXOne, effectsYOne);
    }

    private void SetStarTwoPosition() {
        float rand = (float) Math.random() * 1000 + 1000;
        float rand2 = (float) Math.random() * 1000 + 1000;
        effectsXTwo = 500 - rand;
        effectsYTwo = rand2;

        shootTwoSpeed = (float) Math.random() * 25 + 25;

        float scale = (float)(Math.random() * 0.2f);
        shootingStarTwo.setScale(scale, scale);

        starTwo.setPosition(effectsXTwo, effectsYTwo);
    }

    private void SetStarThreePosition() {
        float rand = (float) Math.random() * 1000 + 1000;
        float rand2 = (float) Math.random() * 1000 + 1000;
        effectsXThree = 300 - rand;
        effectsYThree = rand2;

        shootThreeSpeed = (float) Math.random() * 25 + 25;

        float scale = (float)(Math.random() * 0.2);
        shootingStarThree.setScale(scale, scale);

        starThree.setPosition(effectsXThree, effectsYThree);
    }

    private void RotateStars() {

        if (starOneShot) {
            RotateStarOne();
        }
    }

    private void RotateStarOne() {

        float rand = (float)(Math.random() * 10);
        shootingStarOne.rotateBy(rand);
    }

    private void RotateStarTwo() {

        float rand = (float)(Math.random() * 10);
        starTwo.rotateBy(rand * -1);
    }

    private void RotateStarThree() {

        float rand = (float)(Math.random() * 10);
        shootingStarThree.rotateBy(rand * -1);
    }

    private void MoveStars() {

        if (!starOneShot) {
            MoveStarOne();
        }

        if (!starTwoShot) {
            MoveStarTwo();
        }

        if (!starThreeShot) {
            MoveStarThree();
        }
    }

    private void MoveStarOne() {

        effectsXOne += shootOneSpeed;
        effectsYOne = shootOneWait != 1 ? 200 : effectsYOne - shootOneSpeed;

        starOne.setPosition(effectsXOne, effectsYOne);
    }

    private void MoveStarTwo() {

        effectsXTwo += shootTwoSpeed;
        effectsYTwo = ((int)shootTwoWait) % 2 == 0 ? 200 :
                effectsYTwo - shootTwoSpeed;

        starTwo.setPosition(effectsXTwo, effectsYTwo);
    }

    private void MoveStarThree() {

        effectsXThree += shootThreeSpeed;
        effectsYThree -= shootThreeSpeed;

        starThree.setPosition(effectsXThree, effectsYThree);
    }

    @Override
    public float getZIndex() {
        return Z_INDEX;
    }

    @Override
    public void dispose() {
        table.clear();
        muteTable.clear();
        helpTable.clear();
        highScoreTable.clear();
        super.dispose();
    }

    private void setBackground() {
        if (MathUtils.random() <= 0.5) {
            backgroundTable.setBackground(new TextureRegionDrawable(new Texture("images/main_back.png")));
        } else {
            backgroundTable.setBackground(new TextureRegionDrawable(new Texture("images/main_back2.png")));
        }
    }

}

