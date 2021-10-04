package com.deco2800.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.deco2800.game.components.mainmenu.MainMenuDisplay;
import com.deco2800.game.entities.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.deco2800.game.gameScore.gameScore;
import com.deco2800.game.files.UserSettings;
import com.deco2800.game.services.ServiceLocator;
import com.badlogic.gdx.audio.Music;

import java.util.*;

/*
 * A UIpop up class. Can be used to dynamically add pop ups to the game
 * and can be added directly to entities.
 *
 * See wiki for full description and example use
 * @Link -- {'https://github.com/UQdeco2800/2021-studio-1/wiki/UI-Popup'}
 */
public class UIPop extends UIComponent {
    private static final String HELP = "Help Screen";
    private static final String SCORE = "Score Screen";
    private static final String PAUSE = "Pause Menu";
    private static final String LEADERBOARD = "Leaderboard";
    private static final String PAUSE_BACKGROUND = "images/popPauseBack.png";
    private static final String POP_UP_FONT = "popUpFont";

    /*
     * List of background images to be loaded with popUp
     */
    private static final Map<String, String> backGroundImages = new HashMap<>();

    static {
        //background images should ideally be 800 * 500 pixels
<<<<<<< HEAD
        backGroundImages.put("Help Screen", "images/popPauseBack.png");
        backGroundImages.put("Score Screen", "images/popPauseBack.png");
        backGroundImages.put("Pause Menu", "images/popPauseBack.png");
        backGroundImages.put("Game Over", "images/popPauseBack.png");
=======
        backGroundImages.put(HELP, PAUSE_BACKGROUND);
        backGroundImages.put(SCORE, PAUSE_BACKGROUND);
        backGroundImages.put(PAUSE, PAUSE_BACKGROUND);
        backGroundImages.put(LEADERBOARD, PAUSE_BACKGROUND);
>>>>>>> c1550b9b736e4a3486f9ada7f1ad45c8cf4d5b9d
    }

    //the game the popUp will pop onto
    // Can also be thought of as the entity it will become a component of
    private Entity game;

    // the screen name to instantiate
    private String screenName;
    // the title of the pop up screen
    private Label title;
    // the root table to be spawned
    private Table root;
    // the pop up table containing contents
    private Table popUp;
    // the close button for the pop up
    private TextButton closeButton;
    // the texture for the popup background
    private Texture popUpBackGround;
    //Setting screen
    //Back button
    private Button backButton;

    private float volume = 0.8f;
    private Button apply;
    private Slider volumeSlider;
    private CheckBox fullScreenCheck;
    private Music music;
    private Music fire;
    private Music walk;

    // the skin for the popup
    private Skin popUpSkin;

    /*
     * Constructs a new instance of the screenName parsed
     *      @param - screenName - The screenName of the screen to instantiate
     *      @param - game - The game entity to instantiate the popUp
     *                      Every popUp will have a game entity active.
     */
    public UIPop(String screenName, Entity game) {

        music = ServiceLocator.getResourceService().getAsset("sounds/main.mp3", Music.class);
        fire = ServiceLocator.getResourceService().getAsset("sounds/fire.mp3", Music.class);
        walk = ServiceLocator.getResourceService().getAsset("sounds/walk.mp3", Music.class);
        music.setLooping(true);
        fire.setLooping(true);
        walk.setLooping(true);
        music.setVolume(volume);
        fire.setVolume(volume);
        walk.setVolume(volume);
        music.play();
        fire.play();
        walk.play();

        this.game = game;

        if (!backGroundImages.containsKey(screenName)) {
            throw new NoSuchElementException("Wrong Screen Name");
        }

        this.screenName = screenName;
        this.popUpBackGround = new Texture(backGroundImages.get(screenName));

        //the base table the element is depend on
        root = new Table();
        //the popup table for the contents
        popUp = new Table();

        //every popup must have a background
        popUp.setBackground(new TextureRegionDrawable(popUpBackGround));

        // every pop up must have a close button
        closeButton = new TextButton("Close", skin);
        apply = new TextButton("Apply", skin);

        volumeSlider = new Slider(0.2f, 2f, 0.1f, false, skin);
        volumeSlider.setValue(volume);



        apply.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                //there should be an associated trigger event in main game actions
                applyChange();
            }
        });

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                //there should be an associated trigger event in main game actions
                game.getEvents().trigger(screenName);
                entity.dispose();
            }
        });

        backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                root.reset();
                root.add(popUp).expandX().expandY();
            }
        });

        root.setZIndex(0);
        popUp.setZIndex(0);
    }

    @Override
    public void create() {
        super.create();
        addActors(screenName);
    }

    private void applyChange() {
        UserSettings.Settings settings = UserSettings.get();
        settings.fullscreen = fullScreenCheck.isChecked();
        volume = volumeSlider.getValue();
        volumeSlider.setValue(volume);
        music.setVolume(volume);
        fire.setVolume(volume);
        walk.setVolume(volume);
        UserSettings.set(settings, true);
    }

    // Adds actors based on screen name
    private boolean addActors(String screenName) {

        root.setFillParent(true);

        root.add(popUp).expandX().expandY();

        title = new Label(screenName, skin, "popUpTitle");
        title.setFontScale(2.5f);

        popUp.add(title).top();
        popUp.row().padTop(2f);

        getTable();
        return true;
    }

    /*
     * Gets the format of the Table associated with the PopUp name
     *      @param screenName - the screen name of the popUp
     */
    private Table getTable() {

        if (screenName.equals(PAUSE)) {
            popUp = formatPauseScreen();
        }
        if (screenName.equals(SCORE)) {
            popUp = formatScoreScreen();
            popUp.add(closeButton);
        }
        if (screenName.equals(HELP)) {
            popUp = formatHelpScreen();
            popUp.add(closeButton);
        }
        if (screenName.equals(LEADERBOARD)) {
            popUp = formatLeaderboardScreen();
            popUp.add(closeButton);
        }

        if (screenName.equals("Game Over")) {
            popUp = formatGameEndScreen();
        }

        stage.addActor(root);
        return popUp;
    }


    /*
     * Formats the Settings table
     */
    private Table formatSettingsScreen() {
        UserSettings.Settings settings = UserSettings.get();
        //Setting screen
        Table setting = new Table();
        setting.setBackground(new TextureRegionDrawable(popUpBackGround));
        //Title of setting screen
        Label settingTitle;
        settingTitle = new Label("Settings", skin, "popUpTitle");
        settingTitle.setFontScale(2.5f);
        setting.add(settingTitle).top();
        setting.row().padTop(5f);

<<<<<<< HEAD
        Label fullScreenLabel = new Label("Fullscreen:", skin);
        fullScreenCheck = new CheckBox("", skin);
        fullScreenCheck.setChecked(settings.fullscreen);
        setting.add(fullScreenLabel).left();
        setting.add(fullScreenCheck).right();
        setting.row().padTop(20f);


        setting.row().padTop(20f);
        setting.add(new Label("Volume :", skin, "popUpFont")).left();

        setting.add(volumeSlider).right();
=======
        for (int i = 0; i < 3; i++) {
            Label infoTitle = new Label(getInformation(screenName, i), skin, POP_UP_FONT);
            Label info = new Label(String.valueOf(getInfoValues(screenName, i)), skin, POP_UP_FONT);
            infoTitle.setFontScale(1.5f);
            info.setFontScale(1.5f);
            setting.add(infoTitle).left();
            setting.add(info).right();
            setting.row().padTop(20f);
        }

        setting.row().padTop(20f);
        setting.add(new Label("Volume :", skin, POP_UP_FONT)).left();
        setting.add(ScreenSpecialElement(screenName)).right();
>>>>>>> c1550b9b736e4a3486f9ada7f1ad45c8cf4d5b9d

        setting.row().padTop(20f);
        setting.add(closeButton).padRight(30f).left();
        setting.add(apply).padRight(30f).center();
        setting.add(backButton);

        setting.setZIndex(0);
        return setting;
    }

    private Table formatGameEndScreen() {
        //Button - return to main menu
        TextButton returnButton;
        //Button - new game
        TextButton restartButton;

        returnButton = new TextButton("Return to main menu", skin);

        returnButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                game.getEvents().trigger("exit");
            }
        });

        popUp.add(returnButton).center().padTop(30f);

        return popUp;
    }

    /*
     * Formats the Pause table
     */
    private Table formatPauseScreen() {
<<<<<<< HEAD
            // Image button - Setting
            TextButton settingButton;
            // Image button - Resume
            TextButton resumeButton;
            // Image button - Quit
            TextButton quitButton;

            settingButton = new TextButton("Setting", skin);
            resumeButton = new TextButton("Resume", skin);
            quitButton = new TextButton("Return to main menu", skin);

            resumeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    game.getEvents().trigger("Pause Menu");
                }
            });

            quitButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    game.getEvents().trigger("exit");
                }
            });

            settingButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    root.reset();
                    root.add(formatSettingsScreen()).expandX().expandY();
                }
            });

            popUp.add(resumeButton).center().padTop(30f);
            popUp.row();
            popUp.row();
            popUp.add(settingButton).center().padTop(30f);
            popUp.row();
            popUp.row();
            popUp.add(quitButton).center().padTop(30f);
            stage.addActor(root);
            return popUp;
=======

        // texture for button image - Setting
        Texture popUpSettingImage;
        // texture for button image - Resume
        Texture popUpResumeImage;
        // texture for button image - Quit;
        Texture popUpQuitImage;
        popUpSettingImage = new Texture("images/setting.png");
        popUpResumeImage = new Texture("images/resume.png");
        popUpQuitImage = new Texture("images/quit.png");
        // Image button - Setting
        ImageButton settingButton;
        // Image button - Resume
        ImageButton resumeButton;
        // Image button - Quit
        ImageButton quitButton;

        settingButton = new ImageButton(new TextureRegionDrawable(popUpSettingImage));
        resumeButton = new ImageButton(new TextureRegionDrawable(popUpResumeImage));
        quitButton = new ImageButton(new TextureRegionDrawable(popUpQuitImage));

        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                game.getEvents().trigger(PAUSE);
            }
        });

        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                game.getEvents().trigger("exit");
            }
        });

        settingButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                root.reset();
                root.add(formatSettingsScreen()).expandX().expandY();
            }
        });

        popUp.add(resumeButton).center();
        popUp.row().padTop(2.5f);
        popUp.add(settingButton).center();
        popUp.row().padTop(2.5f);
        popUp.add(quitButton).center();
        stage.addActor(root);
        return popUp;
    }

    /*
     * Formats the leaderboard screen with values from current high scores
     */
    private Table formatLeaderboardScreen() {

        String[] highScoreNames = MainMenuDisplay.getHighScoreNames();
        int[] highScoreValues = MainMenuDisplay.getHighScoreValues();

        int numberScores = highScoreNames.length;

        for (int i = 0; i < numberScores; i++) {

            Label scoreLabel = new Label((i + 1) + ". " + highScoreNames[i], skin, POP_UP_FONT);
            Label scoreValue = new Label(highScoreValues[i] + "", skin, POP_UP_FONT);

            popUp.row().padTop(20f);
            popUp.add(scoreLabel).padLeft(100f).align(1).left();
            popUp.add(scoreValue);
        }
        return popUp;
>>>>>>> c1550b9b736e4a3486f9ada7f1ad45c8cf4d5b9d
    }

    /*
     * Formats the Score screen table
     */
    private Table formatScoreScreen() {
        return popUp;
    }

    /*
     * Formats the Default screen table
     */
    private Table formatHelpScreen() {

        SelectBox<String> selections = new SelectBox<>(skin);
        Array<String> selectionText = new Array<>();
        selectionText.add("Select Here");
        selectionText.add("Controls");
        selectionText.add("Goal");
        selections.setItems(selectionText);
        Label informationText = new Label("Hi, welcome to Ragnarok Racer, \n to play the game click start", skin, POP_UP_FONT);


        selections.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                SetHelpLabelText(selections.getSelectedIndex(), informationText);
            }

            private void SetHelpLabelText(int selectedIndex, Label informationText) {
                informationText.setText(getHelpText(selectedIndex));
            }
        });

        popUp.row().padTop(20f);
        popUp.add(selections);
        popUp.row().padTop(20f);
        popUp.add(informationText);
        popUp.row().padTop(20f);
        return popUp;
    }


    /* Creates and returns the unique widget element associated with the
     * unique pop up
     */
    private Widget ScreenSpecialElement(String screenName) {

        Widget element = null;

        if (screenName.equals(PAUSE)) {
            element = new Slider(0.2f, 2f, 0.1f, false, skin);
        } else if (screenName.equals(SCORE)) {
            element = new SelectBox<>(skin);
        }
        return element;

    }

    /* Returns information values associated with pop ups
     * @param screenName -- The screen na
     * @param index -- the relative information index on the screen
     *                 i.e. the first, second or third text item
     */
    private int getInfoValues(String screenName, int index) {
        if (screenName.equals(PAUSE)) {
            switch (index) {
                case 1:
                    return index * 10;
                case 2:
                    return index * 20;
                default:
                    return index * 30;
            }
        }

        if (screenName.equals(SCORE)) {
            switch (index) {
                case 1:
                    return index * 15;
                case 2:
                    return index * 20;
                default:
                    return index * 25;
            }
        }

        if (screenName.equals(HELP)) {
            switch (index) {
                case 1:
                    return index;
                case 2:
                    return index * 2;
                default:
                    return index * 3;
            }
        }

        return -1; //error
    }

    /* Returns information text associated with pop ups
     * @param screenName -- The screen name
     * @param index -- the relative information index on the screen
     *                 i.e. the first, second or third text item
     */
    private String getInformation(String screenName, int index) {

        if (screenName.equals(PAUSE)) {
            switch (index) {
                case 0:
                    return "Sound";
                case 1:
                    return "Difficulty";
                default:
                    return "Resolution";
            }
        }

        if (screenName.equals(SCORE)) {
            switch (index) {
                case 0:
                    return entity.getComponent(gameScore.class).getCurrentScore() + "";
                case 1:
                    return "Score info 2";
                default:
                    return "Score info 3";
            }
        }

        if (screenName.equals(HELP)) {
            switch (index) {
                case 0:
                    return "";
                case 1:
                    return "Main Controls:";
                default:
                    return "Game Goal:";
            }
        }

        return "error";
    }


    private String getHelpText(int i) {

        String text = i == 0 ? "Hi, welcome to Ragnarok Racer, \nto play the game close this window \nand" + " click Run!":
                i == 1 ? " W - Jump \n S - Crouch \n A - Move left \n D - Move right \n P - Pause \n K - Throw Spear \n L - Cast Lightning \n" :
                        "Last as long as you can! \nDestroy as many enemies as you can! \nAvoid as many obstacles as you can! \n";

        return text;
    }

    @Override
    public void draw(SpriteBatch UIIndex) {
        int screenHeight = Gdx.graphics.getHeight();
        int screenWidth = Gdx.graphics.getHeight();
        float offsetX = 10f;
        float offsetY = 30f;
    }

    @Override
    public void dispose() {
        super.dispose();
        root.remove();
    }

    /*
     * Returns the list of possible UI screens
     * Can be used to read and create necessary screen.
     * @returns -- Set<String> of possible screen names
     */
    public static Set<String> GetPossibleUIScreens() {
        return backGroundImages.keySet();
    }

    /*
     * Gets the name of the active pop Up
     * @returns -- String of the pop-up name
     */
    public String GetName() {
        return screenName;
    }
}
