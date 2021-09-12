package com.deco2800.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deco2800.game.entities.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.*;

/*
 * A UIpop up class. Can be used to dynamically add pop ups to the game
 * and can be added directly to entities.
 *
 * See wiki for full description and example use
 * @Link -- {'https://github.com/UQdeco2800/2021-studio-1/wiki/UI-Popup'}
 */
public class UIPop extends UIComponent {

    /*
     * List of background images to be loaded with popUp
     */
    private static final Map<String, String> backGroundImages =
            new HashMap<>();
    static {
        //background images should ideally be 800 * 500 pixels
        backGroundImages.put("Default Pop", "images/popPauseBack.png");
        backGroundImages.put("Score Screen", "images/run_ended.png");
        backGroundImages.put("Pause Menu", "images/popPauseBack.png");
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

    // the skin for the popup
    private Skin popUpSkin;

    /*
     * Constructs a new instance of the screenName parsed
     *      @param - screenName - The screenName of the screen to instantiate
     *      @param - game - The game entity to instantiate the popUp
     *                      Every popUp will have a game entity active.
     */
    public UIPop(String screenName, Entity game) {

        this.game = game;

        if (!backGroundImages.containsKey(screenName)){
            throw new NoSuchElementException("Wrong Screen Name");
        };

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

        if (screenName.equals("Pause Menu")) {
            popUp = formatPauseScreen();
        }
        if (screenName.equals("Score Screen")) {
            popUp = formatScoreScreen();
            popUp.add(closeButton);
        }
        if (screenName.equals("Default Pop")) {
            popUp = formatDefaultScreen();
            popUp.add(closeButton);
        }

        stage.addActor(root);
        return popUp;
    }

    /*
     * Formats the Settings table
     */
    private Table formatSettingsScreen() {

        //Setting screen
        Table setting = new Table();
        setting.setBackground(new TextureRegionDrawable(popUpBackGround));
        //Title of setting screen
        Label settingTitle;
        settingTitle = new Label("Settings", skin, "popUpTitle");
        settingTitle.setFontScale(2.5f);
        setting.add(settingTitle).top();
        setting.row().padTop(5f);

        for (int i = 0; i < 3; i++) {
            Label infoTitle = new Label(getInformation(screenName, i), skin, "popUpFont");
            Label info = new Label(String.valueOf(getInfoValues(screenName, i)), skin, "popUpFont");
            infoTitle.setFontScale(1.5f);
            info.setFontScale(1.5f);
            setting.add(infoTitle).left();
            setting.add(info).right();
            setting.row().padTop(20f);
        }

        setting.row().padTop(20f);
        setting.add(new Label("Volume :", skin, "popUpFont")).left();
        setting.add(ScreenSpecialElement(screenName)).right();

        setting.row().padTop(20f);
        setting.add(closeButton).bottom().right();
        setting.add(backButton).bottom().left();

        setting.setZIndex(0);
        return setting;
    }

    /*
     * Formats the Pause table
     */
    private Table formatPauseScreen() {

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

            popUp.add(resumeButton).center();
            popUp.row().padTop(2.5f);
            popUp.add(settingButton).center();
            popUp.row().padTop(2.5f);
            popUp.add(quitButton).center();
            stage.addActor(root);
            return popUp;
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
    private Table formatDefaultScreen() {
        for (int i = 0; i < 3; i++) {
            Label infoTitle = new Label(getInformation(screenName, i), skin, "popUpFont");
            Label info = new Label(String.valueOf(getInfoValues(screenName, i)), skin, "popUpFont");
            infoTitle.setFontScale(1.5f);
            info.setFontScale(1.5f);
            popUp.add(infoTitle).left();
            popUp.add(info).right();
            popUp.row().padTop(20f);
        }
        return popUp;
    }


    /* Creates and returns the unique widget element associated with the
     * unique pop up
     */
    private Widget ScreenSpecialElement(String screenName) {

        Widget element = null;

        if (screenName.equals("Pause Menu")) {
            element = new Slider(0.2f, 2f, 0.1f, false, skin);
        } else if (screenName.equals("Score Screen")) {
            element = new SelectBox<>(skin);
        }
        return element;

    }

    /* Returns information values associated with pop ups
    * @param screenName -- The screen name
    * @param index -- the relative information index on the screen
    *                 i.e. the first, second or third text item
    */
    private int getInfoValues(String screenName, int index) {

        if (screenName.equals("Pause Menu")) {
            return index == 1 ? index * 10 :
                    index == 2 ? index * 20 :
                            index * 30;
        }

        if (screenName.equals("Score Screen")) {
            return index == 1 ? index * 15 :
                    index == 2 ? index * 20 :
                            index * 25;
        }

        if (screenName.equals("Default Pop")) {
            return index == 1 ? index :
                    index == 2 ? index * 2 :
                            index * 3;
        }

        return -1; //error
    }

    /* Returns information text associated with pop ups
     * @param screenName -- The screen name
     * @param index -- the relative information index on the screen
     *                 i.e. the first, second or third text item
     */
    private String getInformation(String screenName, int index) {

        if (screenName.equals("Pause Menu")) {
            return index == 0 ? "Sound" :
                   index == 1 ? "Difficulty" :
                   "Resolution";
        }

        if (screenName.equals("Score Screen")) {
            return index == 0 ? "Score info 1" :
                    index == 1 ? "Score info 2" :
                            "Score info 3";
        }

        if (screenName.equals("Default Pop")) {
            return index == 0 ? "Information 1" :
                    index == 1 ? "Information 2" :
                            "Information 3";
        }

        return "error";
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
    public static Set<String> GetPossibleUIScreens () {
        Set<String> s;
        s = backGroundImages.keySet();
        return s;
    }

    /*
     * Gets the name of the active pop Up
     * @returns -- String of the pop Up name
     */
    public String GetName() {
        String s = "";
        s = screenName;
        return s;
    }
}
